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

import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import fr.labri.popsycle.model.JPackageDep;

public class JPackageDepsExporter {
	
	public static void exportToFile(Set<JPackageDep> deps,String path) throws IOException {
		FileWriter w = new FileWriter(path);
		
		for(JPackageDep dep: deps)
			w.append(dep.getSource().getName() + ";" + dep.getTarget().getName() + "\n");
		
		w.close();
	}

}
