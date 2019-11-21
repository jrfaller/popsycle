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

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import fr.labri.popsycle.algo.cycles.DirectCycleFinder;
import fr.labri.popsycle.model.utils.JavaUtils;

public class JPackageGroup implements Iterable<JPackage> {
	
	private Set<JPackage> packages;
	
	public JPackageGroup() {
		this.packages = new HashSet<JPackage>();
	}
	
	public JPackageGroup(Collection<JPackage> packages) {
		this();
		this.packages.addAll(packages);
	}

	public Set<JPackage> getJPackages() {
		return packages;
	}
	
	public void outputToFile(String path) throws IOException {
		FileWriter fw = new FileWriter(path);
		for ( JPackage pkg: packages ) {
			fw.append(pkg.getName() + "\n");
			for ( JPackageDep dep: pkg.getJPackageDeps() )
				fw.append("\t" + dep.getTarget().getName() + "\n");
		}
		fw.close();
	}
	
	public void outputToDotFile(String path) throws IOException {
		FileWriter fw = new FileWriter(path);
		fw.append("digraph G{\n");
		for ( JPackage pkg: packages ) {
			String n1 = pkg.getName().replaceAll("\\.","_");
			System.out.println(n1);
			for ( JPackageDep dep: pkg.getJPackageDeps() ) {
				String n2 = dep.getTarget().getName().replaceAll("\\.","_");
				fw.append(n1 + " -> " + n2 + ";\n");
				
			}
		}
		fw.append("}");
		fw.close();
	}
	
	public JPackageDep getJPackageDep(JPackage source,JPackage target) {
		for( JPackage pkg: packages ) {
			if ( pkg == source )
				return pkg.getJPackageDepFor(target);
		}
		return null;
	}
	
	public Set<JPackageDep> getAllJPackageDeps() {
		Set<JPackageDep> allEdges = new HashSet<JPackageDep>();
		for( JPackage pkg: this.getJPackages() )
			allEdges.addAll(pkg.getJPackageDeps());
		return allEdges;
	}
	
	public int getJPackagesNb() {
		return this.packages.size();
	}
	
	public int getJPackageDepsNb() {
		int n = 0;
		for ( JPackage pkg: packages )
			n += pkg.getJPackageDeps().size();
		return n;
	}
	
	public Set<JClass> getAllJClasses() {
		Set<JClass> classes = new HashSet<JClass>();
		for ( JPackage pkg: packages )
			classes.addAll(pkg.getJClasses());
		return classes;
	}
	
	public int getAllJClassesNb() {
		int n = 0;
		for ( JPackage pkg: packages )
			n += pkg.getJClasses().size();
		return n;
	}

	public int getMaxDepth() {
		int max = 0;
		for(JPackage pkg: packages) {
			int d = JavaUtils.split(pkg.getName()).length;
			if ( d > max )
				max = d;
			
		}
		return max;
	}
	
	public JPackageGroup copy() {
		JPackageGroup g = new JPackageGroup();
		for ( JPackage p: this ) {
			JPackage cp = new JPackage(p.getName());
			g.getJPackages().add(cp);
		}
		
		for( JPackage p: this ) {
			for( JPackageDep dep: p.getJPackageDeps() ) {
				JPackage src = g.getJPackage(p.getName());
				JPackage tgt = g.getJPackage(dep.getTarget().getName());
				JPackageDep cdep = new JPackageDep(src,tgt);
				cdep.setKindMap(dep.getKindMap());
				src.getJPackageDeps().add(cdep);
			}
		}
		
		return g;
	}
	
	public int getAllJClassDepsNb() {
		int n = 0;
		for(JPackage pkg: packages)
			n += new JClassGroup(pkg.getJClasses()).getJClassDepsNb();
		return n;
	}
	
	public String toString() {
		return "JPackageGroup with " + packages.size() + " packages, " + getJPackageDepsNb() + " dependencies";
	}
	
	public void removeJPackage(JPackage pkg) {
		this.packages.remove(pkg);
		for( JPackage p: packages )
			p.removeJPackageDep(pkg);
	}
	
	public int inDegree(JPackage pkg) {
		int d = 0;
		for( JPackage p: packages )
			if ( p != pkg ) 
				if ( p.getJPackageDepFor(pkg) != null )
					d++;
		
		return d;
	}
	
	public int outDegree(JPackage pkg) {
		return pkg.getJPackageDeps().size();
	}
	
	public JPackage getJPackage(String name) {
		for(JPackage p: packages)
			if ( p.getName().equals(name) )
				return p;
		
		return null;
	}

	@Override
	public Iterator<JPackage> iterator() {
		return packages.iterator();
	}

}
