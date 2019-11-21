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

import java.util.Collection;
import java.util.Comparator;

import fr.labri.popsycle.model.JPackageCycle;

public class DiameterComparator implements Comparator<JPackageCycle>{

	double maxDiameter = Double.MIN_VALUE;

	public DiameterComparator(Collection<JPackageCycle> cycles) {
		for( JPackageCycle cycle : cycles ) {
			double d = DistanceUtils.getDiameter(cycle);
			if ( d > maxDiameter )
				maxDiameter = d;
		}
	}

	@Override
	public int compare(JPackageCycle c1, JPackageCycle c2) {
		int cmp = Double.compare(getDiameter(c1),getDiameter(c2));
		if ( cmp != 0 )
			return cmp;
		else 
			return (new SizeComparator()).compare(c1,c2);
	}
	
	public double getDiameter(JPackageCycle cycle) {
		return 1D - (DistanceUtils.getDiameter(cycle) / maxDiameter);
	}

}
