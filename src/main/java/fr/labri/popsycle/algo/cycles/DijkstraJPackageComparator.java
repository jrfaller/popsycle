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

import java.util.Comparator;
import java.util.Map;

import fr.labri.popsycle.model.JPackage;

public class DijkstraJPackageComparator implements Comparator<JPackage> {
	
	private Map<JPackage,Integer> distMap;
	
	public DijkstraJPackageComparator(Map<JPackage,Integer> distMap) {
		this.distMap = distMap;
	}

	@Override
	public int compare(JPackage p1, JPackage p2) {
		int d1 = distMap.get(p1);
		int d2 = distMap.get(p2);
		return d1 - d2;
	}

}
