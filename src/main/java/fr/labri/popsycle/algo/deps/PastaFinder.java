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

package fr.labri.popsycle.algo.deps;

import java.util.HashSet;
import java.util.Set;

import fr.labri.popsycle.algo.DecomposeSCCs;
import fr.labri.popsycle.model.JPackageDep;
import fr.labri.popsycle.model.JPackageGroup;

public class PastaFinder extends JPackageDepsFinder {

	public PastaFinder(JPackageGroup pkgGrp) {
		super(pkgGrp);
	}

	@Override
	public Set<JPackageDep> extractJPackageDeps() {
		Set<JPackageDep> pasta = new HashSet<JPackageDep>();
		
		while( hasCycle() ) {
			JPackageDep dep = nextDep();
			pasta.add(dep);
			pkgGrp.getJPackage(dep.getSource().getName()).removeJPackageDep(dep.getTarget());
		}
		
		return pasta;
	}
	
	private JPackageDep nextDep() {
		int min = Integer.MAX_VALUE;
		JPackageDep minDep = null;
		for( JPackageDep dep: pkgGrp.getAllJPackageDeps() ) {
			int cur = dep.occurences();
			if ( cur < min ) {
				min = cur;
				minDep = dep;
			}
		}
		return minDep;
	}
	
	private boolean hasCycle() {
		DecomposeSCCs f = new DecomposeSCCs(pkgGrp);
		for( JPackageGroup g : f.extractSCCs() )
			if ( g.getJPackages().size() > 1 )
				return true;
		
		return false;
	}

}
