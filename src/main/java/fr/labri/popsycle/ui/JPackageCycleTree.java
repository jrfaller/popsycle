/*
 * Copyright 2009-2011 Jean-Rémy Falleri
 * 
 * This file is part of Popsycle.
 * Popsycle is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Popsycle is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Popsycle.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.labri.popsycle.ui;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.draw2d.ColorConstants;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fr.labri.popsycle.algo.cycles.*;
import fr.labri.popsycle.model.*;
import fr.labri.popsycle.model.rank.*;

public class JPackageCycleTree extends Composite implements SelectionListener {

	private Tree tree;

	private List<JPackageCycle> cycles;

	@SuppressWarnings("unused")
	private JPackageGroup pkgGrp;
	
	private Map<TreeItem, JPackageCycle> itemMap;
	private Map<JPackageCycle, TreeItem> cycleMap;

	private Menu popup;

	private MenuItem menuShowDetails;

	private MenuItem menuFixSoon;
	private MenuItem menuFixOneDay;
	private MenuItem menuNoFix;

	private Map<JPackageCycle,JCycleKind> cycleKindMap;

	public JPackageCycleTree(Composite parent,int style) {
		super(parent,style);

		this.cycles = new LinkedList<JPackageCycle>();
		this.itemMap = new HashMap<TreeItem, JPackageCycle>();
		this.cycleMap = new HashMap<JPackageCycle, TreeItem>();

		this.setLayout(new FillLayout());

		tree = new Tree(this, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE );
		tree.setLinesVisible (true);
		tree.setHeaderVisible (false);

		popup =  new Menu(this.getShell(),SWT.POP_UP);

		menuShowDetails = new MenuItem(popup,SWT.PUSH);
		menuShowDetails.setText("Show details");
		menuShowDetails.addSelectionListener(this);

		new MenuItem(popup,SWT.SEPARATOR);

		menuFixSoon = new MenuItem(popup,SWT.PUSH);
		menuFixSoon.setText("Set As: FIX_SOON");
		menuFixSoon.addSelectionListener(this);

		menuFixOneDay = new MenuItem(popup,SWT.PUSH);
		menuFixOneDay.setText("Set As: FIX_ONEDAY");
		menuFixOneDay.addSelectionListener(this);

		menuNoFix = new MenuItem(popup,SWT.PUSH);
		menuNoFix.setText("Set As: NO_FIX");
		menuNoFix.addSelectionListener(this);

		tree.setMenu(popup);
	}
	
	public void setJPackageGroup(JPackageGroup pkgGrp) {
		this.pkgGrp = pkgGrp;
		
		this.itemMap = new HashMap<TreeItem, JPackageCycle>();
		this.cycleMap = new HashMap<JPackageCycle, TreeItem>();
		this.cycleKindMap = new HashMap<JPackageCycle, JCycleKind>();

		tree.removeAll();
		
		//CycleFinder f = new GreedyCycleFinder(pkgGrp);
		CycleFinder f = new FastShortestCycleFinder(pkgGrp);
		List<JPackageCycle> cycles = new LinkedList<JPackageCycle>(f.extractJPackageCycles());

		Collections.sort(cycles,new DiameterComparator(cycles));
		
		this.cycles = cycles;
		
		int n = 1;

		for ( JPackageCycle c: this.cycles ) {
			drawJPackageCycle(c,n);
			n++;
		}
	}
	
	public List<JPackageCycle> getJPackageCycles() {
		return cycles;
	}

	private void drawJPackageCycle(JPackageCycle c,int n) {
		TreeItem cItem = new TreeItem(tree,SWT.NONE);
		link(cItem,c);

		cItem.setText("Cycle " + n);

		for( JPackage pkg: c )
			drawNode(cItem,pkg);

		cItem.setExpanded(true);
	}

	private void drawNode(TreeItem dcItem,JPackage p) {
		TreeItem srcItem = new TreeItem(dcItem,SWT.NONE);
		srcItem.setText(p.getName());
	}

	private JPackageCycle getSelectedJPackageCycle() {
		TreeItem i = tree.getSelection()[0];
		if ( itemMap.containsKey(i) )
			return itemMap.get(i);

		return null;
	}

	private void handleDetailsSelected() {
		JPackageCycle cycle = getSelectedJPackageCycle();

		if ( cycle != null ) {
			final Shell dialog = new Shell(this.getShell(), SWT.APPLICATION_MODAL | SWT.YES | SWT.RESIZE );
			dialog.setLayout(new FillLayout());

			JPackageCycleUI ui = new JPackageCycleUI(dialog,SWT.NONE);
			ui.setJPackageCycle(cycle);

			dialog.setSize(800,500);
			dialog.open();
		}
	} 

	private void setJPackageCycleKind(JPackageCycle cycle,JCycleKind kind) {
		cycleKindMap.put(cycle,kind);
		TreeItem i = cycleMap.get(cycle);
		if ( kind == JCycleKind.FIX_SOON)
			i.setBackground(ColorConstants.red);
		else if ( kind == JCycleKind.FIX_ONEDAY)
			i.setBackground(ColorConstants.yellow);
		else if ( kind == JCycleKind.NO_FIX)
			i.setBackground(ColorConstants.gray);

		if ( kind != JCycleKind.NOT_ASSIGNED )
			i.setExpanded(false);
	}

	private void setCurrentJPackageCycleKind(JCycleKind kind) {
		setJPackageCycleKind(getSelectedJPackageCycle(), kind);
	}

	private void link(TreeItem i,JPackageCycle c) {
		itemMap.put(i,c);
		cycleMap.put(c,i);
	}

	public void writeResultsInFile(String file) throws IOException {
		FileWriter fw = new FileWriter(file);
		fw.append("<results>\n");
		for( JPackageCycle c: cycles ) {
			JCycleKind kind = JCycleKind.NOT_ASSIGNED;
			if ( cycleKindMap.containsKey(c) )
				kind = cycleKindMap.get(c);
			fw.append("\t<cycle state=\"" + kind + "\">\n");
			for( JPackage p: c )
				fw.append("\t\t<package name=\"" + p.getName() +  "\"/>\n");
			fw.append("\t</cycle>\n");
		}
		fw.append("</results>");
		fw.close();
	}

	public void loadResultsFromFile(String file) throws IOException, SAXException, ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(file);
		doc.getDocumentElement().normalize();
		NodeList cycleLst = doc.getElementsByTagName("cycle");
		Iterator<JPackageCycle> cIt = cycles.iterator();
		for (int s = 0 ; s < cycleLst.getLength() ; s++) {
			Node cycleNode = cycleLst.item(s);
			String state = cycleNode.getAttributes().getNamedItem("state").getNodeValue();
			JPackageCycle c = cIt.next();
			setJPackageCycleKind(c,JCycleKind.valueOf(state));
		}
	}

	public boolean isEmpty() {
		return cycles.size() == 0;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {}

	@Override
	public void widgetSelected(SelectionEvent e) {
		if ( e.getSource() == menuShowDetails )
			handleDetailsSelected();
		else if ( e.getSource() == menuFixSoon)
			setCurrentJPackageCycleKind(JCycleKind.FIX_SOON);
		else if ( e.getSource() == menuFixOneDay )
			setCurrentJPackageCycleKind(JCycleKind.FIX_ONEDAY);
		else if ( e.getSource() == menuNoFix)
			setCurrentJPackageCycleKind(JCycleKind.NO_FIX);
	}

}
