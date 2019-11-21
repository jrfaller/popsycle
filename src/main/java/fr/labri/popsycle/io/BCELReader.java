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

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantString;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.Type;

import fr.labri.popsycle.model.JClass;
import fr.labri.popsycle.model.JClassDep;
import fr.labri.popsycle.model.JClassGroup;
import fr.labri.popsycle.model.JDepKind;
import fr.labri.popsycle.model.utils.JavaUtils;

public class BCELReader extends JClassGroupReader {

	private static final String[] ZIP_FILE_TYPES = new String[] {".zip", ".jar", ".war", ".ear"}; 

	private String[] resources;

	private Map<String,JClass> unresolvedClassesMap;

	public BCELReader(String[] resources) {
		super();
		this.resources = resources;
	}

	public JClassGroup read() {
		reset();
		try {
			readResources();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		resolveClasses();
		return new JClassGroup(classes);
	}
	
	protected void reset() {
		super.reset();
		this.unresolvedClassesMap = new HashMap<String, JClass>();
	}

	private void resolveClasses() {
		for( JClass cls: classes ) {
			Iterator<JClassDep> depIt = cls.getJClassDeps().iterator();
			while ( depIt.hasNext() ) {
				JClassDep dep = depIt.next();
				String clsName = dep.getTarget().getName();
				if ( namesMap.containsKey(clsName) ) {
					//System.out.println("Resolved: " + clsName);
					dep.setTarget(namesMap.get(clsName));
				}
				else {
					//System.out.println("Not resolved: " + clsName);
					depIt.remove();
				}
			}
		}
	}
	
	private void readResources() throws IOException, ClassFormatException, ClassNotFoundException {
		for (int i = 0; i < resources.length; i++) {
			String resource = resources[i];
			File file = new File(resource);
			if ( file.isDirectory() ) {
				analyseClassFile(file, resource);
				File[] files = file.listFiles( new FileFilter() {public boolean accept(File file){return isZipFile(file);}} );
				for (int j = 0; j < files.length; j++) {
					String source = createSourceName(resource, files[j].getName());
					analyseClassFiles(new ZipFile(files[j].getAbsoluteFile()), source);
				}
			} 
			else if (file.getName().endsWith(".class"))
				analyseClassFile(file, null);
			else if (isZipFile(file))
				analyseClassFiles(new ZipFile(file.getAbsoluteFile()), resource);
			else 
				throw new IOException(resource + " is an invalid file.");
		}
	}

	private void analyseClassFile(File file, String source) throws IOException, ClassFormatException, ClassNotFoundException {
		if (file.isDirectory()) {
			String[] files = file.list();
			for (int i = 0; i < files.length; i++) {
				File child = new File(file, files[i]);
				if (child.isDirectory() || files[i].endsWith(".class"))
					analyseClassFile(child, source);
			}
		} 
		else 
			createJClass(new FileInputStream(file),source);
	}

	private void analyseClassFiles(ZipFile zipFile, String source) throws IOException, ClassFormatException, ClassNotFoundException {
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while ( entries.hasMoreElements() ) {
			ZipEntry entry = entries.nextElement();
			if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
				InputStream stream = zipFile.getInputStream(entry);
				createJClass(stream,source);
			}
		}
	}

	private void createJClass(InputStream stream,String source) throws ClassFormatException, IOException, ClassNotFoundException {
		ClassParser p = new ClassParser(stream, source);
		JavaClass c = p.parse();

		String clsName = fixName(c.getClassName());

		JClass cls = retrieveClass(clsName);
		
		if ( c.isInterface() ) {
			cls.setInterface(true);
		}
		else if ( c.isAbstract() )
			cls.setAbstract(true);

		cls.handleJClassDep(retrieveUnresolvedClass(fixName(c.getSuperclassName())), JDepKind.INH);

		for ( String itfName: c.getInterfaceNames() )
			cls.handleJClassDep(retrieveUnresolvedClass(fixName(itfName)),JDepKind.INH);

		for( Field f: c.getFields() ) {
			String fldTypeName = fixName(f.getType().toString());
			if (!clsName.equals(fldTypeName)) {
				JDepKind depKind = JDepKind.ITF;
				if (f.isPrivate())
					depKind = JDepKind.REF;
				cls.handleJClassDep(retrieveUnresolvedClass(fldTypeName), depKind);
			}
		}	

		for( Method m: c.getMethods() ) {
			JDepKind kind = JDepKind.ITF;
			if (m.isPrivate())
				kind = JDepKind.REF;
			String mtdRetTypeName = fixName(m.getReturnType().toString());
			if ( !clsName.equals(mtdRetTypeName) )
				cls.handleJClassDep(retrieveUnresolvedClass(mtdRetTypeName),kind);

			for (Type t: m.getArgumentTypes() ) {
				String mtdParTypeName = fixName(t.toString());
				if (!clsName.equals(mtdParTypeName))
					cls.handleJClassDep(retrieveUnresolvedClass(mtdParTypeName),kind);
			}
		}
		
		ConstantPool cPool = c.getConstantPool();
		for( int i = 0; i < cPool.getLength() ; i++ ) {
			Constant cst = cPool.getConstant(i);
			if ( cst != null ) {
				if ( cst instanceof ConstantUtf8 || cst instanceof ConstantClass || cst instanceof ConstantString ) {
					
					String rawValue = "";
					
					if ( cst instanceof ConstantUtf8 )
						rawValue = ((ConstantUtf8) cst).getBytes();
					else if ( cst instanceof ConstantClass )
						rawValue = ((ConstantClass) cst).getBytes(cPool);
					else 
						rawValue = ((ConstantString) cst).getBytes(cPool);
					
					for( String name: JavaUtils.fixUtf8ConstantValue(rawValue) ) {
						name = JavaUtils.removeInnerClass(name);
						cls.handleJClassDep(retrieveUnresolvedClass(name),JDepKind.REF);
					}	
				}
			}
		}

	}
	
	private JClass retrieveUnresolvedClass(String name) {
		if ( unresolvedClassesMap.containsKey(name) )
			return unresolvedClassesMap.get(name);
		else {
			JClass cls = new JClass(name,null,false);
			unresolvedClassesMap.put(name,cls);
			return cls;
		}
	}

	private static String fixName(String name) {
		return name.replaceAll("\\$\\w*","");
	}

	private static String createSourceName(String classFile, String name) {
		return classFile + (classFile.endsWith(File.separator) ? name : File.separatorChar + name);
	}

	private static boolean isZipFile(File file) {
		String name = file.getName();
		for (int i = 0; i < ZIP_FILE_TYPES.length; i++) 
			if (name.endsWith(ZIP_FILE_TYPES[i]))
				return true;

		return false;
	}

}
