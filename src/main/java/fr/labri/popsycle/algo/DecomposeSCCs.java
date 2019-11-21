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

package fr.labri.popsycle.algo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import fr.labri.popsycle.model.JPackage;
import fr.labri.popsycle.model.JPackageDep;
import fr.labri.popsycle.model.JPackageGroup;

public class DecomposeSCCs {
	private JPackageGroup pkgGrp;
	
	private Set<JPackageGroup> components;

	private int index;
	
	private Map<JPackage,Integer> indexMap;
	
	private Map<JPackage,Integer> lowLinkMap;
	
	private Set<JPackage> onStack;
	
	private Stack<JPackage> nodeStack;
	
	public DecomposeSCCs(JPackageGroup pkgGrp) {
		this.pkgGrp = pkgGrp;
	}
	
	public Set<JPackageGroup> extractSCCs() {
		components = new HashSet<>();
		index = 0;
		nodeStack = new Stack<>();
		indexMap = new HashMap<>();
		lowLinkMap = new HashMap<>();
		onStack = new HashSet<>();
		for( JPackage v: pkgGrp.getJPackages() )
			if ( !indexMap.containsKey(v) )
				tarjan(v);
		
		for( JPackageGroup pkgGrp: components) {
			for( JPackage pkg: pkgGrp.getJPackages() ) {
				Iterator<JPackageDep> depIt = pkg.getJPackageDeps().iterator();
				while ( depIt.hasNext() ) {
					JPackageDep dep = depIt.next();
					if ( !pkgGrp.getJPackages().contains(dep.getTarget()) )
						depIt.remove();
				}
			}
		}
		
		return components;
	}
	
	private void tarjan(JPackage v) {
		indexMap.put(v,index);
		lowLinkMap.put(v, index);
		index++;
		nodeStack.push(v);
		onStack.add(v);
		for ( JPackageDep dep: v.getJPackageDeps() ) {
			JPackage v_p = dep.getTarget();
			if ( !indexMap.containsKey(v_p) ) {
				tarjan(v_p);
				lowLinkMap.put(v,Math.min(lowLinkMap.get(v),lowLinkMap.get(v_p)));
			}
			else if ( onStack.contains(v_p) )
				lowLinkMap.put(v,Math.min(lowLinkMap.get(v),indexMap.get(v_p)));
		}
		if ( lowLinkMap.get(v) == indexMap.get(v) ) {
			// Handle SCC
			JPackageGroup c = new JPackageGroup();
			JPackage v_p;
			do {
				v_p = nodeStack.pop();
				onStack.remove(v_p);
				c.getJPackages().add(v_p);
			}
			while ( !v.equals(v_p) );
			components.add(c);
		}
	}	
}
