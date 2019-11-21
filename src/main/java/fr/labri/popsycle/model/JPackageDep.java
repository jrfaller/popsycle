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

import java.util.HashMap;
import java.util.Map;

public class JPackageDep extends JDep {
	
	private JPackage source;

	private JPackage target;

	public JPackageDep(JPackage source,JPackage target) {
		this.source = source;
		this.target = target;
	}
	
	public JPackage getTarget() {
		return target;
	}
	
	public JPackage getSource() {
		return source;
	}

	public String toString() {
		return source.getName() + " -> " + target.getName() + " (" + super.toString() + ")";
	}
	
	public int hashCode() {
		return source.getName().hashCode() + target.getName().hashCode();
	}
	
	public boolean equals(Object o) {
		if ( ! (o instanceof JPackageDep) )
			return false;
		else {
			JPackageDep dep = (JPackageDep) o;
			return ( this.source == dep.source && this.target == dep.target );
		}
	}
	
	public JClassGroup extractJClassDeps() {
		Map<String,JClass> clsMap = new HashMap<String, JClass>();
		JClassGroup clsGrp = new JClassGroup();
		for( JClass firstCls: source.getJClasses() ) {
			JClass clsCopy = firstCls.definitionCopy();
			for( JClassDep dep: firstCls.getJClassDeps() ) {
				JClass adjCls = dep.getTarget();
				if ( target.getJClasses().contains(adjCls) ) {					
					JClass adjClsCopy = null;
					if ( clsMap.containsKey(adjCls.getName()))
						adjClsCopy = clsMap.get(adjCls.getName());
					else {
						adjClsCopy = adjCls.definitionCopy();
						clsGrp.getJClasses().add(adjClsCopy);
						clsMap.put(adjCls.getName(),adjClsCopy);
					}
					clsCopy.setJClassDep(adjClsCopy,dep.getKindMap());
					clsGrp.getJClasses().add(clsCopy);
				}
			}
			if ( clsCopy.getJClassDeps().size() > 0 )
				clsGrp.getJClasses().add(clsCopy);
		}

		return clsGrp;
	}
	
}
