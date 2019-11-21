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

package fr.labri.popsycle.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

public class JPackageCycle extends LinkedList<JPackage> {

	private static final long serialVersionUID = 1L;

	public JPackageCycle(Collection<JPackage> packages) { 
		super(packages);
		normalize();
	}
	
	private void normalize() {
		String min = this.get(0).getName();
		int minId = -1;
		int cur = 0;
		for(JPackage p: this) {
			if ( p.getName().compareTo(min) <= 0 ) {
				minId = cur;
				min = p.getName();
			}
			cur++;
		}
		if ( minId > 0 )
			Collections.rotate(this,this.size() - minId);
	}

	public Set<JPackageDep> getJPackageDeps() {
		Set<JPackageDep> deps = new HashSet<JPackageDep>();

		if ( this.size() == 2 ) {
			JPackageDep d1 = new JPackageDep(this.get(0),this.get(1));
			d1.setKindMap(this.get(0).getJPackageDepFor(this.get(1)).getKindMap());
			
			JPackageDep d2 = new JPackageDep(this.get(1),this.get(0));
			d2.setKindMap(this.get(1).getJPackageDepFor(this.get(0)).getKindMap());
			
			deps.add(d1);deps.add(d2);
			return deps;
		}
		else {
			JPackageDep dinit = new JPackageDep(this.get(this.size()-1),this.get(0));
			dinit.setKindMap(this.get(this.size()-1).getJPackageDepFor(this.get(0)).getKindMap());
			
			deps.add(dinit);
			
			for(int i = 0 ; i < this.size() - 1 ; i++ ) {
				JPackageDep di = new JPackageDep(this.get(i),this.get(i+1));
				di.setKindMap(this.get(i).getJPackageDepFor(this.get(i+1)).getKindMap());
				deps.add(di);
			}
			return deps;
		}
	}
	
	public boolean equals(Object o) {
		if ( ! (o instanceof JPackageCycle) )
			return false;
		else {
			JPackageCycle c = (JPackageCycle) o;
			if ( this.size() != c.size() )
				return false;
			else
				return isIdentical(c);
		}
	}

	public String toString() {
		String res = "";
		Iterator<JPackage> pkgIt = this.iterator();
		while ( pkgIt.hasNext() ) {
			JPackage pkg = pkgIt.next();
			res += pkg.getName();
			if ( pkgIt.hasNext() )
				res += " -> ";
		}
		return res;
	}
	
	private boolean isIdentical(JPackageCycle c) {
		Iterator<JPackage> thisPkgIt = this.iterator();
		Iterator<JPackage> cPkgIt = c.iterator();
		while( thisPkgIt.hasNext() ) {
			JPackage thisPkg = thisPkgIt.next();
			JPackage cPkg = cPkgIt.next();
			if ( thisPkg != cPkg )
				return false;
		}
		return true;
	}

}
