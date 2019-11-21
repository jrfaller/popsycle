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

package fr.labri.popsycle.model.utils;

import java.util.Set;

public class JavaUtils {

	public static String removeInnerClass(String clsName) {
		return clsName.replaceAll("\\$\\w*","");
	}
	
	public static boolean isClassNameValid(String className) {
		boolean valid = true;
		boolean firstCharacter = true;
		for (int i = 0, n = className.length(); valid && i < n; i++) {
			char c = className.charAt(i);
			if (firstCharacter) {
				firstCharacter = false;
				valid = Character.isJavaIdentifierStart(c);
			}
			else {
				if (c == '.')
					firstCharacter = true;
				else 
					valid = Character.isJavaIdentifierPart(c);
			}
		}
		return valid && firstCharacter == false;
	}
	
	public static String getPackageName(String clsName) {
		if ( !clsName.contains(".") ) {
			return "#root";
		}
		else {
			String[] tokens = split(clsName);
			String pkg = "";
			for(int i = 0; i < tokens.length - 1 ; i++ ) {
				if ( i == tokens.length - 2)
					pkg += tokens[i];
				else
					pkg += tokens[i] + ".";
			}
			return pkg;
		}
	}
	
	public static Set<String> fixUtf8ConstantValue(String value) {
		ClassNameExtractor extr = new ClassNameExtractor(value);
		return extr.extract();
	}
	
	public static String getSimpleClassName(String clsName) {
		String[] tokens = split(clsName);
		return tokens[tokens.length - 1];
	}
	
	public static String[] split(String s) {
		return s.split("\\.");
	}
	
	public static String join(String[] t) {
		String s = "";
		for(int i = 0; i < t.length - 1 ; i++ ) {
			if ( i == t.length - 2)
				s += t[i];
			else
				s += t[i] + ".";
		}
		return s;
	}
	
}
