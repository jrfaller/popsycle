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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JPackage extends JElement {
	private Set<JClass> classes;
	
	private Set<JPackageDep> dependencies;
	
	public JPackage(String name) {
		super(name);
		this.dependencies = new HashSet<>();
		this.classes = new HashSet<>();
	}
	
	public JPackageDep getJPackageDepFor(JPackage target) {
		for(JPackageDep dep: dependencies )
			if ( dep.getTarget() == target )
				return dep;
		
		return null;
	}
	
	public void handleJPackageDep(JPackage target,JDepKind depKind) {
		JPackageDep dep = getJPackageDepFor(target);
		
		if ( dep == null ) {
			dep = new JPackageDep(this,target);
			this.dependencies.add(dep);
		}
		
		dep.incrementKind(depKind);
	}
	
	public void handleJPackageDepKindMap(JPackage target,Map<JDepKind,Integer> kindMap) {
		JPackageDep dep = getJPackageDepFor(target);
		
		if ( dep == null ) {
			dep = new JPackageDep(this,target);
			this.dependencies.add(dep);
		}
		
		dep.integrateKindMap(kindMap);
	}
	
	public void setJPackageDep(JPackage target,Map<JDepKind,Integer> kindMap) {
		JPackageDep dep = new JPackageDep(this, target);
		dep.setKindMap(kindMap);
		this.dependencies.add(dep);
	}
	
	public String toString() {
		return this.name;
	}
	
	public Set<JPackageDep> getJPackageDeps() {
		return dependencies;
	}

	public Set<JClass> getJClasses() {
		return this.classes;
	}

	public void removeJPackageDep(JPackage tgt) {
		this.dependencies.remove(new JPackageDep(this,tgt));
	}
}
