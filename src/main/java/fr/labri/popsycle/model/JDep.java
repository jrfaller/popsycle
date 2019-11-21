/*
 * Copyright 2009-2011 Jean-R��my Falleri
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JDep {
	protected Map<JDepKind, Integer> kindMap;

	public JDep() {
		kindMap = new HashMap<>();
	}

	public Map<JDepKind, Integer> getKindMap() {
		return kindMap;
	}
	public void setKindMap(Map<JDepKind, Integer> kindMap) {
		this.kindMap = kindMap;
	}

	public void incrementKind(JDepKind kind) {
		if ( !kindMap.containsKey(kind) )
			kindMap.put(kind,1);
		else
			kindMap.put(kind,kindMap.get(kind)+1);
	}

	public void integrateKindMap(Map<JDepKind, Integer> kindMap) {
		for( JDepKind kind : kindMap.keySet() ) {
			if ( !this.kindMap.containsKey(kind) )
				this.kindMap.put(kind,0);

			this.kindMap.put(kind,this.kindMap.get(kind) + kindMap.get(kind));
		}
	}

	public JDepKind getWorseDepKind() {
		JDepKind worse = null;

		for( JDepKind kind : kindMap.keySet() ) {
			if ( worse == null )
				worse = kind;
			else
				worse = JDepKind.getWorseDepKind(worse,kind);
		}

		return worse;
	}

	public int occurences() {
		int occ = 0;
		for( int val : kindMap.values() )
			occ += val;
		return occ;
	}

	public String toString() {
		StringBuffer b = new StringBuffer();

		Iterator<JDepKind> kindIt = kindMap.keySet().iterator();
		while ( kindIt.hasNext() ) {
			JDepKind kind = kindIt.next();
			
			b.append(kind.toString() + " " + kindMap.get(kind));
			if ( kindIt.hasNext() )
				b.append(", ");
		}

		return b.toString();
	}
}
