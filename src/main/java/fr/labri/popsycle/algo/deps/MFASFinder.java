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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import fr.labri.popsycle.model.*;

public class MFASFinder extends JPackageDepsFinder {

	public MFASFinder(JPackageGroup pkgGrp) {
		super(pkgGrp);
	}

	@Override
	public Set<JPackageDep> extractJPackageDeps() {
		JPackageGroup g = pkgGrp.copy();
		
		List<JPackage> s1 = new LinkedList<JPackage>();
		List<JPackage> s2 = new LinkedList<JPackage>();

		while ( g.getJPackages().size() > 0 ) {
			while ( nextSink(g) != null ) {
				JPackage u = nextSink(g); 
				s2.add(0,u);
				g.removeJPackage(u);
			}
			while ( nextSource(g) != null ) {
				JPackage u = nextSource(g); 
				s1.add(u);
				g.removeJPackage(u);
			}
			
			if ( g.getJPackages().size() > 0 ){
				int max = Integer.MIN_VALUE;
				JPackage best = null;
				for(JPackage p: g.getJPackages()) {
					int cur = g.outDegree(p) - g.inDegree(p);
					if ( cur > max ) {
						max = cur;
						best = p;
					}
				}
				s1.add(best);
				g.removeJPackage(best);
			}
		}
		s1.addAll(s2);

		Set<JPackageDep> mfas = new HashSet<JPackageDep>();

		for (int j = s1.size() - 1 ; j >= 1 ; j--) {
			for( int i = j - 1 ; i >= 0 ; i-- ) {
				JPackage vj = pkgGrp.getJPackage(s1.get(j).getName());
				JPackage vi = pkgGrp.getJPackage(s1.get(i).getName());
				if ( vj.getJPackageDepFor(vi) != null )
					mfas.add(vj.getJPackageDepFor(vi));
			}

		}

		return mfas;
	}
	
	private JPackage nextSink(JPackageGroup pkgGroup) {
		for(JPackage p: pkgGroup.getJPackages())
			if ( pkgGroup.outDegree(p) == 0 )
				return p;

		return null;
	}

	private JPackage nextSource(JPackageGroup pkgGroup) {
		for(JPackage p: pkgGroup.getJPackages())
			if ( pkgGroup.inDegree(p) == 0 )
				return p;

		return null;
	}

}
