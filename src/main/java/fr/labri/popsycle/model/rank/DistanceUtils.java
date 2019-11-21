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

package fr.labri.popsycle.model.rank;

import fr.labri.popsycle.model.JPackage;
import fr.labri.popsycle.model.JPackageCycle;
import fr.labri.popsycle.model.utils.JavaUtils;

public class DistanceUtils {
	
	public static double getDiameter(JPackageCycle cycle) {
		double max = 0;

		for( JPackage p1: cycle )
			for( JPackage p2: cycle )
				if ( p1 != p2 ) {
					double dist = DistanceUtils.getDistance(p1,p2);
					if ( dist > max )
						max = dist;
				}
		
		return max;
	}
	
	public static double getDistance(JPackage p1,JPackage p2) {
		String[] p1tok = JavaUtils.split(p1.getName());
		String[] p2tok = JavaUtils.split(p2.getName());
		
		double dist = 0;
		
		int start = getLowestAncesterLevel(p1, p2);
				
		for(int i = start ; i < p1tok.length ; i++ )
			dist += 1D / (Math.pow(2D,(double) i));
		
		for(int i = start ; i < p2tok.length ; i++ )
			dist += 1D / (Math.pow(2D,(double) i));
		
		return dist;
	}
	
	public static boolean areSiblings(JPackage p1,JPackage p2) {
		if ( p1.getName().startsWith(p2.getName()) || p2.getName().startsWith(p1.getName()))
			return false;
		else
			return true;
	}
	
	public static boolean isParent(JPackage p1,JPackage p2) {
		return p2.getName().startsWith(p1.getName());
	}
	
	private static int getLowestAncesterLevel(JPackage p1,JPackage p2) {
		String[] p1tok = JavaUtils.split(p1.getName());
		String[] p2tok = JavaUtils.split(p2.getName());
		
		int min = Math.min(p1tok.length,p2tok.length);
		
		for(int i = 0 ; i < min ; i++ )
			if ( !p1tok[i].equals(p2tok[i]) )
				return i;
		
		return min;
	}

}
