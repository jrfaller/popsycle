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

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import fr.labri.popsycle.algo.cycles.DijkstraJPackageComparator;
import fr.labri.popsycle.model.JPackage;
import fr.labri.popsycle.model.JPackageDep;
import fr.labri.popsycle.model.JPackageGroup;

public class Dijkstra {

	private Map<JPackage,Integer> distMap;

	private Map<JPackage,JPackage> previousMap;
	
	private JPackageGroup pkgGrp;
	
	public Dijkstra(JPackageGroup pkgGrp) {
		this.pkgGrp = pkgGrp;
	}

	public List<JPackage> getShortestPath(JPackage source,JPackage target) {
		distMap = new HashMap<>();
		previousMap = new HashMap<>();
		for(JPackage pkg: pkgGrp.getJPackages())
			distMap.put(pkg,Integer.MAX_VALUE);
		computePaths(source);
		return getShortestPathTo(target);
	}
	
	private List<JPackage> getShortestPathTo(JPackage target) {
		List<JPackage> path = new LinkedList<>();
		for (JPackage vertex = target; vertex != null; vertex = previousMap.get(vertex) )
			path.add(vertex);

		Collections.reverse(path);
		return path;
	}

	private void computePaths(JPackage source) {
		distMap.put(source,0);
		PriorityQueue<JPackage> vertexQueue = new PriorityQueue<>(6,new DijkstraJPackageComparator(distMap));
		vertexQueue.add(source);

		while (!vertexQueue.isEmpty()) {
			JPackage u = vertexQueue.poll();

			for (JPackageDep dep : u.getJPackageDeps()) {
				JPackage v = dep.getTarget();
				int weight = 1;
				int distanceThroughU = distMap.get(u) + weight;
/*				int distV = Integer.MAX_VALUE;
				if ( distMap.containsKey(v) )
					distV = distMap.get(v);*/
				if ( distanceThroughU < distMap.get(v) ) {
					vertexQueue.remove(v);
					distMap.put(v,distanceThroughU);
					previousMap.put(v,u);
					vertexQueue.add(v);
				}
			}
		}
	}

}
