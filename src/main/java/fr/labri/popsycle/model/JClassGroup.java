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

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.labri.popsycle.model.utils.JavaUtils;

public class JClassGroup {

	private Set<JClass> classes;

	public JClassGroup() {
		this.classes = new HashSet<JClass>();
	}

	public JClassGroup(Collection<JClass> classes) {
		this();
		this.classes.addAll(classes);
	}

	public Set<JClass> getJClasses() {
		return classes;
	}

	public JPackageGroup extractJPackageGroup() {
		Map<String,JPackage> jPkgMap = new HashMap<String, JPackage>();

		for( JClass cls: classes ) {
			String pkgName = JavaUtils.getPackageName(cls.getName());

			JPackage pkg = null; 
			if ( jPkgMap.containsKey(pkgName) )
				pkg = jPkgMap.get(pkgName);
			else {
				pkg = new JPackage(pkgName);
				jPkgMap.put(pkgName,pkg);
			}

			pkg.getJClasses().add(cls);
			cls.setJPackage(pkg);
		}

		for( JClass cls: classes ) {
			for(JClassDep dep: cls.getJClassDeps() ) {
				JPackage source = jPkgMap.get(JavaUtils.getPackageName(cls.getName()));
				JPackage target = jPkgMap.get(JavaUtils.getPackageName(dep.getTarget().getName()));
				if ( source != target)
					source.handleJPackageDepKindMap(target, dep.getKindMap());
			}
		}

		return new JPackageGroup(jPkgMap.values());
	}

	public int getJClassesNb() {
		return this.classes.size();
	}
	
	public int getJClassDepsNb() {
		int n = 0;
		for(JClass cls : classes)
			n += cls.getJClassDeps().size();
		return n;
	}
	
	public void outputToFile(String path) throws IOException {
		FileWriter fw = new FileWriter(path);
		for ( JClass cls: classes ) {
			fw.append(cls.getName() + "\n");
			for ( JClassDep dep: cls.getJClassDeps() )
				fw.append("\t" + dep.getTarget().getName() + "\n");
		}
		fw.close();
	}
	
	public String toString() {
		return "ClassGroup with " + classes.size() + " and " + getJClassDepsNb() + " dependencies.";
	}
	
}
