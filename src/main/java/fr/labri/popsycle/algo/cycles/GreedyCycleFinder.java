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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import fr.labri.popsycle.algo.Dijkstra;
import fr.labri.popsycle.algo.DecomposeSCCs;
import fr.labri.popsycle.model.JPackage;
import fr.labri.popsycle.model.JPackageCycle;
import fr.labri.popsycle.model.JPackageDep;
import fr.labri.popsycle.model.JPackageGroup;

public class GreedyCycleFinder extends CycleFinder {

	public GreedyCycleFinder(JPackageGroup pkgGrp) {
		super(pkgGrp);
	}

	public Set<JPackageCycle> extractJPackageCycles() {
		this.cycles = new HashSet<JPackageCycle>();

		Set<JPackageGroup> pkgGrps = (new DecomposeSCCs(pkgGrp)).extractSCCs();

		for(JPackageGroup pkgGrp: pkgGrps ) {
			if ( pkgGrp.getJPackages().size() > 1 ) {

				Set<JPackageDep> firstEdges = getFirstEdges(pkgGrp);
				Set<JPackageDep> allEdges = pkgGrp.getAllJPackageDeps();
				Set<JPackageDep> visitedEdges = new HashSet<JPackageDep>();

				List<JPackageDep> orderedEdges = new LinkedList<JPackageDep>();

				orderedEdges.addAll(firstEdges);
				allEdges.removeAll(firstEdges);
				orderedEdges.addAll(allEdges);

				for( JPackageDep dep: orderedEdges ) {
					if ( !visitedEdges.contains(dep) ) {
						Dijkstra d = new Dijkstra(pkgGrp);
						List<JPackage> l = new LinkedList<JPackage>();
						List<JPackage> path = d.getShortestPath(dep.getTarget(),dep.getSource());
						if ( path.size() > 1 ) {
							for(int i = 1; i < path.size() ; i++ ) {
								JPackage pathSource = path.get(i-1);
								JPackage pathTarget = path.get(i);
								visitedEdges.add(pathSource.getJPackageDepFor(pathTarget));
							}
							l.addAll(path);
							cycles.add(new JPackageCycle(l));
						}
					}
				}
			}
		}
		return cycles;
	}

	private Set<JPackageDep> getFirstEdges(JPackageGroup pkgGrp) {
		Set<JPackageDep> firstEdges = new HashSet<JPackageDep>();
		for( JPackage pkg: pkgGrp.getJPackages() ) {
			for( JPackageDep dep: pkg.getJPackageDeps() ) {
				JPackage adjPkg = dep.getTarget();
				for( JPackageDep adjDep: adjPkg.getJPackageDeps() ) {
					JPackage adjAdjPkg = adjDep.getTarget();
					if ( pkg == adjAdjPkg ) {
						firstEdges.add(dep);
						break;
					}
				}
			}
		}
		return firstEdges;
	}

}
