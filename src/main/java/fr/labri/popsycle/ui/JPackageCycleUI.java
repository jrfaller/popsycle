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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import fr.labri.popsycle.model.JClassGroup;
import fr.labri.popsycle.model.JPackage;
import fr.labri.popsycle.model.JPackageCycle;
import fr.labri.popsycle.model.JPackageDep;

public class JPackageCycleUI extends Composite {
	
	@SuppressWarnings("unused")
	private JPackageCycle cycle;

	public JPackageCycleUI(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new GridLayout(1,false));
	}
	
	
	public void setJPackageCycle(JPackageCycle cycle) {
		this.cycle = cycle;
		for(int i = 1; i < cycle.size() ; i++ ) {
			JPackage srcPkg = cycle.get(i-1);
			JPackage tgtPkg = cycle.get(i);
			JPackageDep dep = srcPkg.getJPackageDepFor(tgtPkg);
			JClassGroup clsGrp = dep.extractJClassDeps();
			addJPackageEdgeUI(clsGrp, srcPkg, tgtPkg);
		}
		JPackage srcPkg = cycle.get(cycle.size() - 1);
		JPackage tgtPkg = cycle.get(0);
		JPackageDep dep = srcPkg.getJPackageDepFor(tgtPkg);
		JClassGroup clsGrp = dep.extractJClassDeps();
		addJPackageEdgeUI(clsGrp, srcPkg, tgtPkg);
	}
	
	public void addJPackageEdgeUI(JClassGroup clsGrp,JPackage srcPkg,JPackage tgtPkg) {
		JPackageDepUI ui = new JPackageDepUI(this,SWT.BORDER,true);	
		ui.setLayoutData(new GridData(GridData.FILL_BOTH));
		ui.setJPackageDep(clsGrp,srcPkg,tgtPkg);
	}

}
