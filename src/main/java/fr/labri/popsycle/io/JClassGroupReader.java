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

package fr.labri.popsycle.io;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.labri.popsycle.model.JClass;
import fr.labri.popsycle.model.JClassGroup;

public abstract class JClassGroupReader {
	
	protected Set<JClass> classes;

	protected Map<String,JClass> namesMap;
	
	protected void reset() {
		this.classes = new HashSet<>();
		this.namesMap = new HashMap<>();
	}
	
	public abstract JClassGroup read();
	
	protected JClass retrieveClass(String name) {
		if ( namesMap.containsKey(name) )
			return namesMap.get(name);
		else {
			JClass cls = new JClass(name,null,true);
			namesMap.put(name,cls);
			classes.add(cls);
			return cls;
		}
	}

}
