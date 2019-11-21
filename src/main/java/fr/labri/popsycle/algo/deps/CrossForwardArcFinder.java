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

import java.util.HashSet;
import java.util.Set;

import fr.labri.popsycle.model.JPackageDep;
import fr.labri.popsycle.model.JPackageGroup;
import fr.labri.popsycle.model.rank.DistanceUtils;

public class CrossForwardArcFinder extends JPackageDepsFinder {

	public CrossForwardArcFinder(JPackageGroup pkgGrp) {
		super(pkgGrp);
	}

	@Override
	public Set<JPackageDep> extractJPackageDeps() {
		Set<JPackageDep> deps = new HashSet<JPackageDep>();
		for( JPackageDep dep: pkgGrp.getAllJPackageDeps() ) {
			if ( DistanceUtils.areSiblings(dep.getSource(),dep.getTarget())) {
				JPackageDep inv = pkgGrp.getJPackageDep(dep.getTarget(),dep.getSource());
				
				if ( inv != null ) {
					if ( 2 * dep.occurences() < inv.occurences() )
						deps.add(dep);
				}
			}
			else if ( DistanceUtils.isParent(dep.getSource(),dep.getTarget()) ) {
					deps.add(dep);
			}
		}
		
		return deps;
	}

}
