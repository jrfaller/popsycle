package fr.labri.popsycle.tests.benchmark;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import fr.labri.popsycle.algo.deps.*;
import fr.labri.popsycle.model.*;

import static fr.labri.popsycle.tests.benchmark.BenchmarkUtils.*;

public class ComputeDepsPrecision {

	public static void main(String[] args) throws IOException {
		String cdg = args[0];
		String deps = args[0] + ".deps.csv";
		String met = args[0] + ".deps.res.csv";

		FileWriter fw = new FileWriter(met);
		
		fw.append("ALGO" + ";" + "CORRECT" + ";" + "PREC" + ";" + "REC" + "\n");

		Map<String,Set<String>> gUndesiredDeps = BenchmarkUtils.getJPackageDeps(deps,JDepImportance.UNDESIRED);
		
		JPackageDepsFinder f1 = new MFASFinder(loadJPackageGroup(cdg));
		Set<JPackageDep> tUndesiredDeps = f1.extractJPackageDeps();
		appendResults("MFAS", tUndesiredDeps, gUndesiredDeps, fw);
		
		JPackageDepsFinder f2 = new PastaFinder(loadJPackageGroup(cdg));
		tUndesiredDeps = f2.extractJPackageDeps();
		appendResults("PASTA", tUndesiredDeps, gUndesiredDeps, fw);
		
		JPackageDepsFinder f3 = new OzoneFinder(loadJPackageGroup(cdg));
		tUndesiredDeps = f3.extractJPackageDeps();
		appendResults("OZONE", tUndesiredDeps, gUndesiredDeps, fw);
		
		JPackageDepsFinder f4 = new BriandFinder(loadJPackageGroup(cdg));
		tUndesiredDeps = f4.extractJPackageDeps();
		appendResults("BRIAND", tUndesiredDeps, gUndesiredDeps, fw);
		
		JPackageDepsFinder f6 = new TaiFinder(loadJPackageGroup(cdg));
		tUndesiredDeps = f6.extractJPackageDeps();
		appendResults("TAI", tUndesiredDeps, gUndesiredDeps, fw);
		
		fw.close();
	}
	
	private static void appendResults(String algo,Set<JPackageDep> tUndesiredDeps,Map<String,Set<String>> gUndesiredDeps,FileWriter fw) throws IOException {
		int correct = computeCorrectJPackageDeps(tUndesiredDeps, gUndesiredDeps);
		double prec = (double) correct / (double) tUndesiredDeps.size();
		int gSize = 0;
		for(String k : gUndesiredDeps.keySet())
			gSize += gUndesiredDeps.get(k).size();
		double rec = (double) correct / (double) gSize;
		
		fw.append(algo + ";" + correct + ";" + prec + ";" + rec + "\n");
		
	}

	public static int computeCorrectJPackageDeps(Set<JPackageDep> tUndesiredDeps,Map<String,Set<String>> gUndesiredDeps) {
		int correct = 0;
		for(JPackageDep tdep : tUndesiredDeps )
			if ( gUndesiredDeps.containsKey(tdep.getSource().getName()))
				if ( gUndesiredDeps.get(tdep.getSource().getName()).contains(tdep.getTarget().getName()))
					correct++;

		return correct;
	}



}