package fr.labri.popsycle.tests.benchmark;

import java.io.FileWriter;
import java.io.IOException;

import fr.labri.popsycle.algo.cycles.AllShortestCycleFinder;
import fr.labri.popsycle.model.JPackageCycle;

import static fr.labri.popsycle.tests.benchmark.BenchmarkUtils.*;

public class GenerateCyclesDist {
	
	public static void main(String[] args) throws IOException {
		String cdg = args[0];
		FileWriter fw = new FileWriter(cdg + ".cycles.dist.csv");

		AllShortestCycleFinder f = new AllShortestCycleFinder(loadJPackageGroup(cdg));
		for( JPackageCycle c : f.extractJPackageCycles())
			fw.append(c.size() + "\n");
		fw.close();
	}

}
