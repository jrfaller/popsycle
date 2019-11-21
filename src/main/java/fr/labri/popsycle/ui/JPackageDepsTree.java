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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.draw2d.ColorConstants;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import fr.labri.popsycle.model.*;
import fr.labri.popsycle.model.rank.*;

public class JPackageDepsTree extends Composite implements SelectionListener {

	private Tree tree;

	private JPackageGroup pkgGrp;
	
	private Map<TreeItem, JPackageDep> itemMap;
	private Map<JPackageDep, TreeItem> jPackageDepMap;

	private Menu popup;

	private MenuItem menuShowDetails;

	private MenuItem menuDesired;
	private MenuItem menuUnknown;
	private MenuItem menuUndesired;

	private Map<JPackageDep,JDepImportance> jPackageDepImportanceMap;
	
	private Color defaultColor;

	public JPackageDepsTree(Composite parent,int style) {
		super(parent,style);

		this.itemMap = new HashMap<TreeItem, JPackageDep>();
		this.jPackageDepMap = new HashMap<JPackageDep, TreeItem>();

		this.setLayout(new FillLayout());

		tree = new Tree(this, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE );
		tree.setLinesVisible (true);
		tree.setHeaderVisible (false);

		popup =  new Menu(this.getShell(),SWT.POP_UP);

		menuShowDetails = new MenuItem(popup,SWT.PUSH);
		menuShowDetails.setText("Show details");
		menuShowDetails.addSelectionListener(this);

		new MenuItem(popup,SWT.SEPARATOR);

		menuDesired = new MenuItem(popup,SWT.PUSH);
		menuDesired.setText("Set As: DESIRED");
		menuDesired.addSelectionListener(this);

		menuUnknown = new MenuItem(popup,SWT.PUSH);
		menuUnknown.setText("Set As: UNKNOWN");
		menuUnknown.addSelectionListener(this);

		menuUndesired = new MenuItem(popup,SWT.PUSH);
		menuUndesired.setText("Set As: UNDESIRED");
		menuUndesired.addSelectionListener(this);
		
		tree.setMenu(popup);
	}
	
	public void setJPackageGroup(JPackageGroup pkgGrp) {
		this.pkgGrp = pkgGrp;
		
		this.itemMap = new HashMap<TreeItem, JPackageDep>();
		this.jPackageDepMap = new HashMap<JPackageDep, TreeItem>();
		this.jPackageDepImportanceMap = new HashMap<JPackageDep,JDepImportance>();

		tree.removeAll();
		
		List<JPackageDep> deps = new LinkedList<JPackageDep>(this.pkgGrp.getAllJPackageDeps());
		Collections.sort(deps,new JDepComparator());

		for ( JPackageDep d: deps ) {
			drawJPackageDep(d);
			setJPackageDepImportance(d,JDepImportance.UNKNOWN);
		}
	}
	

	private void drawJPackageDep(JPackageDep d) {
		TreeItem cItem = new TreeItem(tree,SWT.NONE);
		link(cItem,d);
		
		if ( defaultColor == null )
			defaultColor = cItem.getBackground();

		cItem.setText(d.toString());
	}

	private JPackageDep getSelectedJPackageDep() {
		TreeItem i = tree.getSelection()[0];
		if ( itemMap.containsKey(i) )
			return itemMap.get(i);

		return null;
	}

	private void handleDetailsSelected() {
		JPackageDep dep = getSelectedJPackageDep();

		if ( dep != null ) {
			final Shell dialog = new Shell(this.getShell(), SWT.APPLICATION_MODAL | SWT.YES | SWT.RESIZE | SWT.CLOSE );
			dialog.setLayout(new FillLayout());

			JPackageDepUI ui = new JPackageDepUI(dialog,SWT.NONE,false);
			ui.setJPackageDep(dep.extractJClassDeps(),dep.getSource(),dep.getTarget());
			
			dialog.setSize(800,500);
			dialog.open();
		}
	} 

	private void setJPackageDepImportance(JPackageDep dep,JDepImportance importance) {
		jPackageDepImportanceMap.put(dep,importance);
		TreeItem i = jPackageDepMap.get(dep);
		
		if ( importance == JDepImportance.DESIRED )
			i.setBackground(ColorConstants.green);
		else if ( importance == JDepImportance.UNKNOWN)
			i.setBackground(defaultColor);
		else if ( importance == JDepImportance.UNDESIRED)
			i.setBackground(ColorConstants.red);
	}

	private void setCurrentJPackageDepImportance(JDepImportance importance) {
		setJPackageDepImportance(getSelectedJPackageDep(), importance);
	}

	private void link(TreeItem i,JPackageDep d) {
		itemMap.put(i,d);
		jPackageDepMap.put(d,i);
	}
	
	public int getNotSureJPackageDepNb() {
		int n = 0;
		for( Entry<JPackageDep,JDepImportance> entry: jPackageDepImportanceMap.entrySet() )
			if ( entry.getValue() == JDepImportance.UNKNOWN )
				n++;
		
		return n;
	}

	public void writeResultsInFile(String file) throws IOException {
		FileWriter fw = new FileWriter(file);
		for(TreeItem i: tree.getItems()) {
			JPackageDep d = itemMap.get(i);
			String source = d.getSource().getName();
			String target = d.getTarget().getName();
			fw.append(source + ";" + target + ";" + jPackageDepImportanceMap.get(d).toString() + "\n");
		}
		fw.close();
	}

	public void loadResultsFromFile(String file) throws IOException {
		BufferedReader r = new BufferedReader(new FileReader(file));
		int i = 0;
		while ( r.ready() ) {
			String line = r.readLine();
			String[] tokens = line.split("\\;");
			String importance = tokens[2].trim();
			JPackageDep d = itemMap.get(tree.getItem(i));
			setJPackageDepImportance(d,JDepImportance.valueOf(importance));
			i++;
		}
		r.close();
		
	}

	public boolean isEmpty() {
		return pkgGrp.getAllJPackageDeps().size() == 0;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {}

	@Override
	public void widgetSelected(SelectionEvent e) {
		if ( e.getSource() == menuShowDetails )
			handleDetailsSelected();
		else if ( e.getSource() == menuDesired)
			setCurrentJPackageDepImportance(JDepImportance.DESIRED);
		else if ( e.getSource() == menuUnknown )
			setCurrentJPackageDepImportance(JDepImportance.UNKNOWN);
		else if ( e.getSource() == menuUndesired)
			setCurrentJPackageDepImportance(JDepImportance.UNDESIRED);
	}

}
