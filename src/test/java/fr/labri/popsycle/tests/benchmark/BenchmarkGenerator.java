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

package fr.labri.popsycle.tests.benchmark;

import java.io.File;
import java.io.IOException;

import org.apache.bcel.classfile.ClassFormatException;

import fr.labri.popsycle.algo.deps.*;
import fr.labri.popsycle.io.CDGReader;
import fr.labri.popsycle.io.JClassGroupReader;
import fr.labri.popsycle.model.utils.JPackageDepsExporter;

public class BenchmarkGenerator {

	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 * @throws ClassFormatException 
	 */
	public static void main(String[] args) throws ClassFormatException, IOException, ClassNotFoundException {
		File fold = new File("cdg");

		for( File cdg: fold.listFiles() ) {

			if ( cdg.getName().endsWith(".cdg")) {
				System.out.println(cdg.getAbsolutePath());
				
				String name = cdg.getName().replace(".cdg","");
				
				System.out.println(name);
				JClassGroupReader imp1 = new CDGReader(cdg.getAbsolutePath());
				JPackageDepsFinder f1 = new MFASFinder(imp1.read().extractJPackageGroup());
				JPackageDepsExporter.exportToFile(f1.extractJPackageDeps(),"tmp" + File.separator + name + "_mfas.csv");

				JClassGroupReader imp2 = new CDGReader(cdg.getAbsolutePath());
				JPackageDepsFinder f2 = new CycleDepsFinder(imp2.read().extractJPackageGroup());
				JPackageDepsExporter.exportToFile(f2.extractJPackageDeps(),"tmp" + File.separator + name + "_cd.csv");

				JClassGroupReader imp3 = new CDGReader(cdg.getAbsolutePath());
				JPackageDepsFinder f3 = new PastaFinder(imp3.read().extractJPackageGroup());
				JPackageDepsExporter.exportToFile(f3.extractJPackageDeps(),"tmp" + File.separator + name + "_pasta.csv");
				
				JClassGroupReader imp4 = new CDGReader(cdg.getAbsolutePath());
				JPackageDepsFinder f4 = new DistanceFinder(imp4.read().extractJPackageGroup());
				JPackageDepsExporter.exportToFile(f4.extractJPackageDeps(),"tmp" + File.separator + name + "_distance.csv");
				
				JClassGroupReader imp5 = new CDGReader(cdg.getAbsolutePath());
				JPackageDepsFinder f5 = new CrossArcFinder(imp5.read().extractJPackageGroup());
				JPackageDepsExporter.exportToFile(f5.extractJPackageDeps(),"tmp" + File.separator + name + "_crossarc.csv");
				
				JClassGroupReader imp6 = new CDGReader(cdg.getAbsolutePath());
				JPackageDepsFinder f6 = new CrossForwardArcFinder(imp6.read().extractJPackageGroup());
				JPackageDepsExporter.exportToFile(f6.extractJPackageDeps(),"tmp" + File.separator + name + "_crossforwardarc.csv");
				
				JClassGroupReader imp7 = new CDGReader(cdg.getAbsolutePath());
				JPackageDepsFinder f7 = new AllCrossArcFinder(imp7.read().extractJPackageGroup());
				JPackageDepsExporter.exportToFile(f7.extractJPackageDeps(),"tmp" + File.separator + name + "_allcrossarc.csv");
			}
		}
	}

}
