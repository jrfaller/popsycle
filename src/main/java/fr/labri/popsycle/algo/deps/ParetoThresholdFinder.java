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
import java.util.List;

public class ParetoThresholdFinder {
	
	private List<Integer> values;
	
	public ParetoThresholdFinder(List<Integer> values) {
		this.values = values;	
	}
	
	private int sum() {
		int sum = 0;
		for(int v : values)
			sum += v;
		return sum;
	}
	
	public double getThreshold() {
		Collections.sort(values);
		int globalSum = sum();
		int partialSum = 0;
		for(int i = values.size() - 1 ; i > 0 ; i-- ) {
			int v = values.get(i);
			partialSum += v;
			
			double r = (double)partialSum/(double)globalSum;
			if ( r >= 0.8D ) {
				return v; 
			}
		}
		return 0D;
	}

}
