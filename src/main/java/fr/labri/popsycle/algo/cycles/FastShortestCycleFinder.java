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

public class FastShortestCycleFinder extends CycleFinder {

	private Map<JPackage,Set<JPackage>> precMap;

	public FastShortestCycleFinder(JPackageGroup pkgGrp) {
		super(pkgGrp);
	}

	@Override
	public Set<JPackageCycle> extractJPackageCycles() {
		this.cycles = new HashSet<JPackageCycle>();

		Set<JPackageGroup> pkgGrps = (new DecomposeSCCs(pkgGrp)).extractSCCs();

		for(JPackageGroup pkgGrp: pkgGrps ) {
			if ( pkgGrp.getJPackages().size() > 1 ) {
				precMap = new HashMap<JPackage, Set<JPackage>>();
				for( JPackage pkg: pkgGrp.getJPackages() ) {
					for(JPackageDep dep: pkg.getJPackageDeps() ) {
						JPackage target = dep.getTarget();
						if ( !precMap.containsKey(target) )
							precMap.put(target,new HashSet<JPackage>());
						precMap.get(target).add(pkg);
					}
				}
				for( JPackage pkg: pkgGrp.getJPackages() )
					cycles.addAll(getShortestCycles(pkg));
			}
		}

		return cycles;
	}

	private Set<JPackageCycle> getShortestCycles(JPackage pkg) {
		Set<JPackageCycle> cycles = new HashSet<JPackageCycle>();
		Map<JPackage,JPackage> prevMap = new HashMap<JPackage, JPackage>();

		List<JPackage> stack = new LinkedList<JPackage>();

		Set<JPackage> closed = new HashSet<JPackage>();
		Set<JPackage> opened = new HashSet<JPackage>();

		Set<JPackage> ancestors = new HashSet<JPackage>(precMap.get(pkg));

		stack.add(pkg);
		opened.add(pkg);

		while( ancestors.size() > 0 ) {
			JPackage visiting = stack.remove(0);
			closed.add(visiting);
			opened.remove(visiting);
			for( JPackageDep curDep: visiting.getJPackageDeps() ) {
				JPackage next = curDep.getTarget();
				if ( !closed.contains(next) && !opened.contains(next)) {
					prevMap.put(next,visiting);
					stack.add(next);
					opened.add(next);
				}
			}

			if ( ancestors.contains(visiting) ) {
				List<JPackage> path = new LinkedList<JPackage>();
				for (JPackage current = visiting; current != null; current = prevMap.get(current) )
					path.add(current);
				Collections.reverse(path);
				cycles.add(new JPackageCycle(path));
				ancestors.remove(visiting);
			}

		}
		return cycles;
	}

}
