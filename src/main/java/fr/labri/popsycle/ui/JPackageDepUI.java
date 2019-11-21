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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import fr.labri.popsycle.model.JClassGroup;
import fr.labri.popsycle.model.JPackage;

public class JPackageDepUI extends Composite {
	
	@SuppressWarnings("unused")
	private JClassGroup clsGrp;

	@SuppressWarnings("unused")
	private JPackage source, target;
	
	private StyledText label;
	
	private JClassGroupUI clsGrpUI;
	
	public JPackageDepUI(Composite parent,int style,boolean compact) {
		super(parent,style);
		this.setLayout(new GridLayout(1,false));
		this.label = new StyledText(this,SWT.NONE);
		this.label.setBackground(this.getBackground());
		this.label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.label.setEditable(false);
		this.clsGrpUI = new JClassGroupUI(this,SWT.BORDER,compact);
		this.clsGrpUI.setLayoutData(new GridData(GridData.FILL_BOTH));
	}
	
	public void setJPackageDep(JClassGroup clsGrp,JPackage source,JPackage target) {
		this.clsGrp = clsGrp;
		this.source = source;
		this.target = target;
		this.label.setText("Dependencies from: " + source.getName() + " to: " + target.getName());
		
		StyleRange r1 = new StyleRange();
		r1.start = 0;
		r1.length = 19;
		r1.fontStyle = SWT.ITALIC;
		
		StyleRange r2 = new StyleRange();
		r2.start = 19;
		r2.length = source.getName().length();
		r2.fontStyle = SWT.BOLD;
		
		StyleRange r3 = new StyleRange();
		r3.start = 19 + source.getName().length();
		r3.length = 5;
		r3.fontStyle = SWT.ITALIC;
		
		StyleRange r4 = new StyleRange();
		r4.start = 19 + source.getName().length() + 5;
		r4.length = target.getName().length();
		r4.fontStyle = SWT.BOLD;
		
		this.label.setStyleRanges(new StyleRange[] {r1,r2,r3,r4});
		
		this.clsGrpUI.setJClassGroup(clsGrp);
	}

}
