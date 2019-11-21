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

public class JClassDep extends JDep {
	private JClass source;
	private JClass target;

	public JClassDep(JClass source, JClass target) {
		this.source = source;
		this.target = target;
	}
	
	public JClass getSource() {
		return source;
	}
	public void setSource(JClass source) {
		this.source = source;
	}

	public void setTarget(JClass target) {
		this.target = target;
	}
	public JClass getTarget() {
		return target;
	}
	
	public int hashCode() {
		return source.getName().hashCode() + target.getName().hashCode();
	}
	
	public boolean equals(Object o) {
		if ( ! ( o instanceof JClassDep ) )
			return false;
		else {
			JClassDep dep = (JClassDep) o;
			return (this.source.getName().equals(dep.source.getName()) && this.target.getName().equals(dep.target.getName()));
		}
	}
}
