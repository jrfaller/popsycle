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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.labri.popsycle.algo.DecomposeSCCs;
import fr.labri.popsycle.model.JPackage;
import fr.labri.popsycle.model.JPackageCycle;
import fr.labri.popsycle.model.JPackageDep;
import fr.labri.popsycle.model.JPackageGroup;

public class AllShortestCycleFinder extends CycleFinder {

	public AllShortestCycleFinder(JPackageGroup pkgGrp) {
		super(pkgGrp);
	}

	@Override
	public Set<JPackageCycle> extractJPackageCycles() {
		this.cycles = new HashSet<JPackageCycle>();

		Set<JPackageGroup> pkgGrps = (new DecomposeSCCs(pkgGrp)).extractSCCs();

		for(JPackageGroup pkgGrp: pkgGrps ) {
			if ( pkgGrp.getJPackages().size() > 1 ) {
				Set<JPackageDep> allEdges = pkgGrp.getAllJPackageDeps();
				for( JPackageDep dep: allEdges ) {
					JPackageCycle depCycle =  getOneShortestCycle(dep);
					cycles.addAll(getAllShortestCycles(dep,depCycle.size()));
				}
			}
		}

		return cycles;
	}

	private Set<JPackageCycle> getAllShortestCycles(JPackageDep dep,int maxLength) {
		Set<JPackageCycle> cycles = new HashSet<JPackageCycle>();
		JPackage source = dep.getTarget();
		JPackage target = dep.getSource();
		Map<JPackage,JPackage> prevMap = new HashMap<JPackage, JPackage>();
		prevMap.put(source,null);
		visit(source,target,new HashSet<JPackage>(),0,maxLength,cycles,new HashMap<JPackage, JPackage>());
		return cycles;
	}

	private void visit(JPackage node, JPackage target, Set<JPackage> visited,int curLength, int maxLength,Set<JPackageCycle> cycles,Map<JPackage,JPackage> prevMap) {
		if ( curLength < maxLength - 2 ) {
			visited.add(node);
			for( JPackageDep dep : node.getJPackageDeps() ) {
				JPackage child = dep.getTarget();
				if ( !visited.contains(child)) {
					prevMap.put(child,node);
					visit(child,target,visited,curLength + 1,maxLength,cycles,prevMap);
				}
			}
		}
		else {
			for( JPackageDep dep : node.getJPackageDeps() ) {
				JPackage child = dep.getTarget();
				if ( child == target ) {
					List<JPackage> path = new LinkedList<JPackage>();
					path.add(target);
					JPackage current = node;
					while ( current != null ) {
						path.add(current);
						current = prevMap.get(current);
					}
					Collections.reverse(path);
					JPackageCycle cycle = new JPackageCycle(path);
					cycles.add(cycle);
				}
			}
		}

	}

	private JPackageCycle getOneShortestCycle(JPackageDep dep) {
		Map<JPackage,JPackage> prevMap = new HashMap<JPackage, JPackage>();
		List<JPackage> stack = new LinkedList<JPackage>();
		Set<JPackage> closed = new HashSet<JPackage>();
		Set<JPackage> opened = new HashSet<JPackage>();
		JPackage source = dep.getTarget();
		JPackage target = dep.getSource();

		stack.add(source);
		opened.add(source);

		while( stack.get(0) != target ) {
			JPackage current = stack.remove(0);
			opened.remove(current);
			closed.add(current);
			for( JPackageDep curDep: current.getJPackageDeps() ) {
				JPackage next = curDep.getTarget();
				if ( !closed.contains(next) && !stack.contains(next) ) {
					prevMap.put(next,current);
					stack.add(next);
				}
			}

		}

		List<JPackage> path = new LinkedList<JPackage>();
		JPackage current = target;
		while ( current != null ) {
			path.add(current);
			current = prevMap.get(current);
		}
		Collections.reverse(path);

		JPackageCycle cycle = new JPackageCycle(path);

		return cycle;
	}

}
