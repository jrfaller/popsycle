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

import fr.labri.popsycle.model.JClass;
import fr.labri.popsycle.model.JClassGroup;
import fr.labri.popsycle.model.JDepKind;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.AbstractFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SpoonReader extends JClassGroupReader {
	private String[] srcPaths;

	private Set<String> managedTypeNames = new HashSet<>();
	private CtModel model;

	public SpoonReader(String[] srcPaths) {
		super();
		this.srcPaths = srcPaths;
	}

	public JClassGroup read() {
		reset();
		readResources();
		return new JClassGroup(classes);
	}
	
	protected void reset() {
		super.reset();
		this.managedTypeNames = new HashSet<>();
	}

	private void readResources() {
		Launcher launcher = new Launcher();
		for (String srcPath: srcPaths)
			launcher.addInputResource(srcPath);
		launcher.buildModel();
		this.model = launcher.getModel();
		scanModel(launcher.getModel());
	}

	private void extractManagedTypes() {
		for (CtType spType : model.getAllTypes())
			managedTypeNames.add(spType.getQualifiedName());
	}

	private void scanModel(CtModel model) {
		extractManagedTypes();

		for (CtType spType : model.getAllTypes()) {
			JClass klass = this.retrieveClass(spType.getQualifiedName());
			extractInheritanceDependencies(klass, spType);
			extractFieldDependencies(klass, spType);
			extractMethodDependencies(klass, spType);
		}
	}

	private void extractInheritanceDependencies(JClass klass, CtType spType) {
		extractDependency(klass, spType.getSuperclass(), JDepKind.INH);
		for (CtTypeReference spItf : spType.getSuperInterfaces())
			extractDependency(klass, spItf, JDepKind.INH);
	}

	private void extractFieldDependencies(JClass klass, CtType spType) {
		List<CtField> publicFields = spType.filterChildren(new PublicFieldFilter()).list();
		for (CtField publicField : publicFields)
			extractDependency(klass, publicField.getType(), JDepKind.REF);

		List<CtField> nonPublicFields = spType.filterChildren(new NonPublicFieldFilter()).list();
		for (CtField nonPublicField : nonPublicFields)
			extractDependency(klass, nonPublicField.getType(), JDepKind.REF);
	}

	private void extractMethodDependencies(JClass klass, CtType spType) {
		List<CtMethod> publicMethods = spType.filterChildren(new PublicMethodFilter()).list();
		for (CtMethod publicMethod : publicMethods) {
			extractDependency(klass, publicMethod.getType(), JDepKind.ITF);
			for (CtParameter param : (List<CtParameter>) publicMethod.getParameters())
				extractDependency(klass, param.getType(), JDepKind.ITF);
			if (publicMethod.getBody() != null )
				for (CtTypeReference ref : publicMethod.getBody().getReferencedTypes())
					extractDependency(klass, ref, JDepKind.REF);
		}

		List<CtMethod> nonPublicMethods = spType.filterChildren(new NonPublicMethodFilter()).list();
		for (CtMethod nonPublicMethod : nonPublicMethods) {
			extractDependency(klass, nonPublicMethod.getType(), JDepKind.REF);
			for (CtParameter param : (List<CtParameter>) nonPublicMethod.getParameters())
				extractDependency(klass, param.getType(), JDepKind.REF);
			if (nonPublicMethod.getBody() != null)
				for (CtTypeReference ref : nonPublicMethod.getBody().getReferencedTypes())
					extractDependency(klass, ref, JDepKind.REF);
		}
	}

	private void extractDependency(JClass klass, CtTypeReference spDependencyType, JDepKind dependencyKind) {
		if (this.isManaged(spDependencyType)) {
			JClass dependencyType = this.retrieveClass(spDependencyType.getQualifiedName());
			klass.handleJClassDep(dependencyType, dependencyKind);
		}
	}

	private boolean isManaged(CtTypeReference ref) {
		if (ref == null)
			return false;

		return managedTypeNames.contains(ref.getQualifiedName());
	}

	private static class PublicMethodFilter extends AbstractFilter<CtMethod> {
		@Override
		public boolean matches(CtMethod method) {
			return method.getModifiers().contains(ModifierKind.PUBLIC);
		}
	}

	private static class NonPublicMethodFilter extends AbstractFilter<CtMethod> {
		@Override
		public boolean matches(CtMethod method) {
			return !method.getModifiers().contains(ModifierKind.PUBLIC);
		}
	}

	private static class PublicFieldFilter extends AbstractFilter<CtField> {
		@Override
		public boolean matches(CtField field) {
			return field.getModifiers().contains(ModifierKind.PUBLIC);
		}
	}

	private static class NonPublicFieldFilter extends AbstractFilter<CtField> {
		@Override
		public boolean matches(CtField field) {
			return !field.getModifiers().contains(ModifierKind.PUBLIC);
		}
	}
}
