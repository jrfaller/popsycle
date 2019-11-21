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

package fr.labri.popsycle.tests;

import java.util.Set;

import fr.labri.popsycle.algo.cycles.ElementaryCycleFinder;
import fr.labri.popsycle.model.JDepKind;
import fr.labri.popsycle.model.JPackage;
import fr.labri.popsycle.model.JPackageCycle;
import fr.labri.popsycle.model.JPackageGroup;

public class TestGreedyCycles {
	
	public static void main(String[] args) throws Exception {
		JPackageGroup pkgGrp = getTestJPackageGroup();

		ElementaryCycleFinder f = new ElementaryCycleFinder(pkgGrp);
		Set<JPackageCycle> cycles = f.extractJPackageCycles();
		for( JPackageCycle cycle: cycles)
			System.out.println(cycle);
	}
	
	public static JPackageGroup getTestJPackageGroup() {
		JPackage a = new JPackage("a");
		JPackage b = new JPackage("b");
		JPackage c = new JPackage("c");
		JPackage d = new JPackage("d");
		JPackage e = new JPackage("e");
		
		a.handleJPackageDep(b,JDepKind.ITF);
		b.handleJPackageDep(a,JDepKind.ITF);
		b.handleJPackageDep(c,JDepKind.ITF);
		b.handleJPackageDep(d,JDepKind.ITF);
		c.handleJPackageDep(d,JDepKind.ITF);
		d.handleJPackageDep(b,JDepKind.ITF);
		d.handleJPackageDep(e,JDepKind.ITF);
		e.handleJPackageDep(a,JDepKind.ITF);
		
		JPackageGroup  pkgGrp = new JPackageGroup();
		pkgGrp.getJPackages().add(a);
		pkgGrp.getJPackages().add(b);
		pkgGrp.getJPackages().add(c);
		pkgGrp.getJPackages().add(d);
		pkgGrp.getJPackages().add(e);
		
		return pkgGrp;
	}
	
}
