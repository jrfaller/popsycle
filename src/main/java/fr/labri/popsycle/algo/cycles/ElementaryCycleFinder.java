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

package fr.labri.popsycle.algo.cycles;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import fr.labri.popsycle.model.JPackage;
import fr.labri.popsycle.model.JPackageCycle;
import fr.labri.popsycle.model.JPackageDep;
import fr.labri.popsycle.model.JPackageGroup;

public class ElementaryCycleFinder extends CycleFinder {
	
	private Set<JPackage> markedNodes;

	private Stack<JPackage> markedStack;

	private Stack<JPackage> pointStack;
	
	public ElementaryCycleFinder(JPackageGroup pkgGrp) {
		super(pkgGrp);
	}
	
	public Set<JPackageCycle> extractJPackageCycles() {
		markedNodes = new HashSet<JPackage>();
		markedStack = new Stack<JPackage>();
		pointStack = new Stack<JPackage>();
		cycles = new HashSet<JPackageCycle>();

		for ( JPackage pkg: pkgGrp.getJPackages() ) {
			backtrack(pkg);
			while ( !markedStack.isEmpty() )
				markedNodes.remove(markedStack.pop());
		}
		
		return cycles;
	}

	private boolean backtrack(JPackage pkg) {
		boolean f = false;

		pointStack.push(pkg);
		markedStack.push(pkg);
		markedNodes.add(pkg);

		JPackage sPkg = pointStack.firstElement();
		// Potential bug
		
		Set<JPackage> adjPkgs = new HashSet<JPackage>();
		for( JPackageDep dep: pkg.getJPackageDeps() )
			adjPkgs.add(dep.getTarget());

		Iterator<JPackage> adjPkgIt = adjPkgs.iterator();
		
		while ( adjPkgIt.hasNext() ) {
			JPackage adjPkg = adjPkgIt.next();
			if ( adjPkg.getName().compareTo(sPkg.getName()) < 0 )
				adjPkgIt.remove();
			else if ( adjPkg == sPkg ) {
				// Handle circuit
				List<JPackage> l = new LinkedList<JPackage>();
				for( JPackage cPkg: pointStack )
					l.add(cPkg);
				cycles.add(new JPackageCycle(l));
				// End handle circuit
				f = true;
			}
			else if ( !markedNodes.contains(adjPkg) ) {
				boolean g = backtrack(adjPkg);
				f = f || g;
			}
		}
		if ( f == true ) {
			while( ! (markedStack.lastElement() == pkg) )
				markedNodes.remove(markedStack.pop());
			
			markedStack.pop(); // removes v from marked stack
			markedNodes.remove(pkg);
		}
		pointStack.remove(pkg);

		return f;
	}
	
	
}
