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

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import fr.labri.popsycle.algo.cycles.AllShortestCycleFinder;
import fr.labri.popsycle.algo.cycles.CycleFinder;
import fr.labri.popsycle.model.*;

public class CycleDepsFinder extends JPackageDepsFinder {

	private Set<JPackageCycle> cycles;

	private Map<JPackageDep,Integer> deps;

	public CycleDepsFinder(JPackageGroup pkgGrp) {
		super(pkgGrp);
	}

	private void count() {
		deps = new HashMap<JPackageDep, Integer>();
		for( JPackageCycle cycle: cycles) {
			for(int i = 0 ; i < cycle.size() ; i++) {
				JPackage src = cycle.get(i);
				JPackage tgt = null;
				if ( i == cycle.size() - 1 )
					tgt = cycle.get(0);
				else
					tgt = cycle.get(i+1);

				JPackageDep dep = src.getJPackageDepFor(tgt);
				increment(dep);
			}
		}
	}

	private void increment(JPackageDep dep) {
		if ( !deps.containsKey(dep) )
			deps.put(dep,0);
		deps.put(dep,deps.get(dep) + 1);
	}

	@SuppressWarnings("unused")
	private void debug() {
		List<Entry<JPackageDep,Integer>> list = sortByValue(deps);
		for(Entry<JPackageDep,Integer> e: list )  
			System.out.println(e.getKey() + ";" + e.getValue());
	}

	private List<Entry<JPackageDep,Integer>> sortByValue(Map<JPackageDep,Integer> map) {
		List<Entry<JPackageDep,Integer>> list = new LinkedList<Entry<JPackageDep,Integer>>(map.entrySet());
		Collections.sort(list, new Comparator<Entry<JPackageDep,Integer>>() {
			public int compare(Entry<JPackageDep,Integer> o1, Entry<JPackageDep,Integer> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});
		Collections.reverse(list);
		return list;	
	}

	@Override
	public Set<JPackageDep> extractJPackageDeps() {
		CycleFinder f = new AllShortestCycleFinder(pkgGrp);
		this.cycles = f.extractJPackageCycles();
		count();
		//debug();
		
		List<Integer> values = new LinkedList<Integer>();
		for(JPackageDep d: deps.keySet())
			values.add(deps.get(d));
		
		ParetoThresholdFinder t = new ParetoThresholdFinder(values);
		double threshold = t.getThreshold();
		
		Set<JPackageDep> finalDeps = new HashSet<JPackageDep>();
		
		for(JPackageDep d: deps.keySet())
			if ( (double) deps.get(d) > threshold )
			finalDeps.add(d);
		
		return finalDeps;
	}


}
