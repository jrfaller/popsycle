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

public class JClass extends JElement {
	
	private JPackage jPackage;
	
	private boolean isResolved;
	
	private boolean isInterface;
	
	private boolean isAbstract;
	
	public boolean isInterface() {
		return isInterface;
	}

	public void setInterface(boolean isInterface) {
		this.isInterface = isInterface;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	private Set<JClassDep> dependencies;

	public JClass(String name, JPackage jPackage, boolean isResolved) {
		super(name);
		this.dependencies = new HashSet<>();
		this.jPackage = jPackage;
		this.isResolved = isResolved;
		this.isAbstract = false;
		this.isInterface = false;
	}
	
	public Set<JClassDep> getJClassDeps() {
		return dependencies;
	}
	
	public void handleJClassDep(JClass target, JDepKind kind) {
		JClassDep dep = getJClassDepFor(target);
		
		if ( dep == null ) {
			dep = new JClassDep(this,target);
			this.dependencies.add(dep);
		}
		
		dep.incrementKind(kind);
	}
	
	public void setJClassDep(JClass target,Map<JDepKind,Integer> kindMap) {
		JClassDep dep = new JClassDep(this,target);
		dep.setKindMap(kindMap);
		this.dependencies.add(dep);
	}
	
	public JClassDep getJClassDepFor(JClass target) {
		for(JClassDep dep: this.getJClassDeps())
			if ( dep.getTarget().equals(target) )
				return dep;
			
		return null;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public JPackage getJPackage() {
		return jPackage;
	}

	public void setJPackage(JPackage jPackage) {
		this.jPackage = jPackage;
	}
	
	public boolean isResolved() {
		return isResolved;
	}
	
	public JClass definitionCopy() {
		return new JClass(this.getName(),this.getJPackage(),this.isResolved());
	}

}
