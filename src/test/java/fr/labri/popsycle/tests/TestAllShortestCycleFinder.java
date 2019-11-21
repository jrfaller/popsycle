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

import org.junit.Test;

import fr.labri.popsycle.algo.cycles.AllShortestCycleFinder;
import fr.labri.popsycle.algo.cycles.CycleFinder;
import fr.labri.popsycle.model.JDepKind;
import fr.labri.popsycle.model.JPackage;
import fr.labri.popsycle.model.JPackageCycle;
import fr.labri.popsycle.model.JPackageGroup;

import static org.junit.Assert.*;

public class TestAllShortestCycleFinder {
	
	@Test
	public void testAllShortestCycleFinder() {
		JPackageGroup pkgGrp = getTestJPackageGroup();
		
		CycleFinder f1 = new AllShortestCycleFinder(pkgGrp);
		Set<JPackageCycle> cycles = f1.extractJPackageCycles();
		
		System.out.println(cycles);
		
		assertTrue(cycles.size() == 3);
	}
	
	public static JPackageGroup getTestJPackageGroup() {
		JPackage a = new JPackage("a");
		JPackage b = new JPackage("b");
		JPackage c = new JPackage("c");
		JPackage d = new JPackage("d");
		
		a.handleJPackageDep(b,JDepKind.ITF);
		b.handleJPackageDep(c,JDepKind.ITF);
		b.handleJPackageDep(d,JDepKind.ITF);
		c.handleJPackageDep(a,JDepKind.ITF);
		c.handleJPackageDep(d,JDepKind.ITF);
		d.handleJPackageDep(b,JDepKind.ITF);
		
		JPackageGroup  pkgGrp = new JPackageGroup();
		pkgGrp.getJPackages().add(a);
		pkgGrp.getJPackages().add(b);
		pkgGrp.getJPackages().add(c);
		pkgGrp.getJPackages().add(d);
		
		return pkgGrp;
	}

}
