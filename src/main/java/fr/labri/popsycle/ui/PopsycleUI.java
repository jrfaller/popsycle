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

import org.apache.bcel.classfile.ClassFormatException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import fr.labri.popsycle.io.BCELReader;
import fr.labri.popsycle.io.CDGReader;
import fr.labri.popsycle.model.JClassGroup;
import fr.labri.popsycle.model.JPackageGroup;

public class PopsycleUI extends Composite implements SelectionListener {
	private JPackageCycleTree tree;

	private JPackageGroup pkgGrp;

	private Label status;

	private Menu menuBar, fileMenu, infoMenu;

	private MenuItem fileMenuHeader, infoMenuHeader;

	private MenuItem fileOpenDirItem, fileOpenCdgItem, fileWriteResultsItem, 
		fileLoadResultsItem, infoMetricsItem, infoAboutItem;
	
	public static void main (String [] args) {
		Display display = new Display ();
		Shell shell = new Shell(display);
		shell.setText("Popsycle");
		shell.setLayout(new FillLayout());
		new PopsycleUI(shell,SWT.NONE);
		shell.setSize(400,600);
		shell.open ();
		while (!shell.isDisposed())
			if (!display.readAndDispatch ()) display.sleep ();

		display.dispose();
	}

	public PopsycleUI(Composite parent,int style) {
		super(parent,style);

		this.setLayout(new GridLayout(1,true));

		tree = new JPackageCycleTree(this, SWT.BORDER);
		tree.setLayoutData(new GridData(GridData.FILL_BOTH));

		status = new Label(this, SWT.BORDER);
		status.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		initMenu();

		this.getShell().setMenuBar(menuBar);
	}

	private void initMenu() {
		menuBar = new Menu(this.getShell(), SWT.BAR);

		fileMenu = new Menu(this.getShell(), SWT.DROP_DOWN);

		fileMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
		fileMenuHeader.setText("&File");
		fileMenuHeader.setMenu(fileMenu);

		fileOpenDirItem = new MenuItem(fileMenu, SWT.PUSH);
		fileOpenDirItem.setText("&Open directory");
		fileOpenDirItem.addSelectionListener(this);
		
		fileOpenCdgItem = new MenuItem(fileMenu, SWT.PUSH);
		fileOpenCdgItem.setText("Open cdg file");
		fileOpenCdgItem.addSelectionListener(this);
		
		new MenuItem(fileMenu,SWT.SEPARATOR);
		
		fileLoadResultsItem = new MenuItem(fileMenu, SWT.PUSH);
		fileLoadResultsItem.setText("&Load results");
		fileLoadResultsItem.addSelectionListener(this);
		
		fileWriteResultsItem = new MenuItem(fileMenu, SWT.PUSH);
		fileWriteResultsItem.setText("&Write results");
		fileWriteResultsItem.addSelectionListener(this);

		infoMenu = new Menu(this.getShell(), SWT.DROP_DOWN);

		infoMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
		infoMenuHeader.setText("&Information");
		infoMenuHeader.setMenu(infoMenu);

		infoMetricsItem = new MenuItem(infoMenu, SWT.PUSH);
		infoMetricsItem.setText("View &metrics");
		infoMetricsItem.addSelectionListener(this);
		
		new MenuItem(infoMenu,SWT.SEPARATOR);
		
		infoAboutItem = new MenuItem(infoMenu, SWT.PUSH);
		infoAboutItem.setText("&About Popsycle");
		infoAboutItem.addSelectionListener(this);
	}

	public void setJavaResource(String[] res) throws ClassFormatException {
		BCELReader importer = new BCELReader(res);

		JClassGroup clsGrp = importer.read();
		JPackageGroup pkgGrp = clsGrp.extractJPackageGroup();

		this.pkgGrp = pkgGrp;

		this.tree.setJPackageGroup(pkgGrp);
		
		updateStatusBar();
	}
	
	public void setCdgFile(String file) throws ClassFormatException {
		CDGReader importer = new CDGReader(file);

		JClassGroup clsGrp = importer.read();
		JPackageGroup pkgGrp = clsGrp.extractJPackageGroup();

		this.pkgGrp = pkgGrp;

		this.tree.setJPackageGroup(pkgGrp);

		updateStatusBar();
	}

	private void updateStatusBar() {
		status.setText(pkgGrp.toString() + ", found " + tree.getJPackageCycles().size() + " cycles.");	
	}
	
	private void showException(Exception e) {
		MessageBox box = new MessageBox(this.getShell());
		box.setText("Error");
		e.printStackTrace();
		box.setMessage(e.getMessage());
		box.open();
	}

	private void handleOpenCdgSelected() {
		FileDialog dlg = new FileDialog(this.getShell(),SWT.OPEN);
		dlg.setText("Select cdg file:");
		String resource = dlg.open();

		try {
			if ( !"".equals(resource) && !(resource == null) )
				this.setCdgFile(resource);
		}
		catch(Exception e) {
			showException(e);
		}
	}

	
	private void handleOpenDirSelected() {
		DirectoryDialog dlg = new DirectoryDialog(this.getShell(),SWT.OPEN);
		dlg.setText("Select resource directory:");
		String resource = dlg.open();

		try {
			if ( !"".equals(resource) && !(resource == null) )
				this.setJavaResource(new String[] { resource });
		}
		catch(Exception e) {
			showException(e);
		}
	}

	private void handleMetricsSelected() {
		if ( pkgGrp != null ) {
			MessageBox box = new MessageBox(this.getShell());
			box.setText("Metrics");
			box.setMessage(pkgGrp.getJPackages().size() + " packages.\n" + 
					pkgGrp.getJPackageDepsNb() + " package dependencies.\n" + 
					pkgGrp.getAllJClassesNb() + " classes.\n" +
					pkgGrp.getAllJClassDepsNb() + " class dependencies."
			);
			box.open();
		}
	}
	
	private void handleLoadResultsSelected() {
		if ( !tree.isEmpty() ) {
			FileDialog dlg = new FileDialog(this.getShell(),SWT.OPEN);
			dlg.setText("Select the file containing the results.");
			String resource = dlg.open();
			try {
				if ( !"".equals(resource) && !(resource == null) )
					this.tree.loadResultsFromFile(resource);
			}
			catch( Exception e ) {
				showException(e);
			}	
		}
	}
	
	private void handleWriteResultsSelected() {
		if ( !tree.isEmpty() ) {
			FileDialog dlg = new FileDialog(this.getShell(),SWT.SAVE);
			dlg.setText("Select the file to write the results.");
			String resource = dlg.open();
			try {
				if ( !"".equals(resource) && !(resource == null) )
					this.tree.writeResultsInFile(resource);
			}
			catch( Exception e ) {
				showException(e);
			}
		}
	}
	
	private void handleAboutSelected() {
		MessageBox box = new MessageBox(this.getShell());
		box.setText("About Popsycle");
		box.setMessage("Popsycle, a Java package cycle detector\n\n" +
				"(c) Copyright 2010 Jean-Rémy Falleri <jr.falleri@laposte.net>\n"
		);
		box.open();
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		if ( e.getSource() == fileOpenDirItem )
			handleOpenDirSelected();
		else if ( e.getSource() == fileOpenCdgItem )
			handleOpenCdgSelected();
		else if ( e.getSource() == fileWriteResultsItem )
			handleWriteResultsSelected();
		else if ( e.getSource() == fileLoadResultsItem )
			handleLoadResultsSelected();
		else if ( e.getSource() == infoMetricsItem )
			handleMetricsSelected();
		else if ( e.getSource() == infoAboutItem )
			handleAboutSelected();
	}
	
	@Override
	public void widgetDefaultSelected(SelectionEvent arg0) {}
}
