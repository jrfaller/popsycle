package fr.labri.popsycle.tests.benchmark;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import fr.labri.popsycle.algo.DecomposeSCCs;
import fr.labri.popsycle.algo.cycles.AllShortestCycleFinder;
import fr.labri.popsycle.algo.cycles.FastShortestCycleFinder;
import fr.labri.popsycle.io.CDGReader;
import fr.labri.popsycle.model.JClassGroup;
import fr.labri.popsycle.model.JDepImportance;
import fr.labri.popsycle.model.JPackage;
import fr.labri.popsycle.model.JPackageCycle;
import fr.labri.popsycle.model.JPackageDep;
import fr.labri.popsycle.model.JPackageGroup;

import static fr.labri.popsycle.tests.benchmark.BenchmarkUtils.*;

public class GenerateCdgMetrics {
	
	public static void main(String[] args) throws IOException {
		String cdg = args[0];
		String met = args[0] + ".met";
		String deps = args[0] + ".deps.csv";
		
		FileWriter fw = new FileWriter(met);
		fw.append("Metrics for: " + cdg + "\n");
		CDGReader r = new CDGReader(cdg);
		JClassGroup cg = r.read();
		fw.append("Number of classes: " + cg.getJClassesNb() + "\n");
		JPackageGroup pg = cg.extractJPackageGroup();
		fw.append("Number of packages: " + pg.getJPackagesNb() + "\n");
		fw.append("Number of distinct class dependencies: " + cg.getJClassDepsNb() + "\n");
		fw.append("Number of distinct packages dependencies: " + pg.getJPackageDepsNb() + "\n");
		
		Map<String,Set<String>> undesiredDepsMap = BenchmarkUtils.getJPackageDeps(deps,JDepImportance.UNDESIRED);
		int size = 0;
		for(String key: undesiredDepsMap.keySet())
			size += undesiredDepsMap.get(key).size();
		fw.append("Number of undesired dependencies: " + size + "\n");
		
		Map<String,Set<String>> desiredDepsMap = BenchmarkUtils.getJPackageDeps(deps,JDepImportance.DESIRED);
		size = 0;
		for(String key: desiredDepsMap.keySet())
			size += desiredDepsMap.get(key).size();
		fw.append("Number of desired dependencies: " + size + "\n");
		
		Map<String,Set<String>> unknownDepsMap = BenchmarkUtils.getJPackageDeps(deps,JDepImportance.UNKNOWN);
		size = 0;
		for(String key: unknownDepsMap.keySet())
			size += unknownDepsMap.get(key).size();
		fw.append("Number of unknown dependencies: " + size + "\n");
		
		int nbUndesiredInSCC = 0;
		
		DecomposeSCCs f = new DecomposeSCCs(pg);
		fw.append("SCCs:\n");
		
		Set<JPackageGroup> sccs = f.extractSCCs();
		for(JPackageGroup scc : sccs ) {
			if ( scc.getJPackagesNb() > 1 )
				fw.append("\t" + scc.getJPackagesNb() + "\n");
			
			for(JPackageDep sccDep: scc.getAllJPackageDeps() ) {
				if ( undesiredDepsMap.containsKey(sccDep.getSource().getName())) {
					if ( undesiredDepsMap.get(sccDep.getSource().getName()).contains(sccDep.getTarget().getName()) ) {
						nbUndesiredInSCC++;
					}
				}
			}
		}
		
		fw.append("Undesired deps in a SCC: " + nbUndesiredInSCC + "\n");
		
		pg = loadJPackageGroup(cdg);
		
		FastShortestCycleFinder sf = new FastShortestCycleFinder(pg);
		long start = System.currentTimeMillis();
		sf.extractJPackageCycles();
		long end = System.currentTimeMillis();
		fw.append("Fast Short Cycle Extraction Time (ms): " + (end - start) + "\n");
		
		pg = loadJPackageGroup(cdg);
		
		AllShortestCycleFinder af = new AllShortestCycleFinder(pg);
		start = System.currentTimeMillis();
		Set<JPackageCycle> cycles = af.extractJPackageCycles();
		end = System.currentTimeMillis();
		fw.append("Naive Short Cycle Extraction Time (ms): " + (end - start) + "\n");
		fw.append("Number of short cycles: " + cycles.size() + "\n");
		
		pg = loadJPackageGroup(cdg);
		for(String k: undesiredDepsMap.keySet()) {
			JPackage src = pg.getJPackage(k);
			for(String t: undesiredDepsMap.get(k) )
				src.removeJPackageDep(pg.getJPackage(t));
		}
		
		f = new DecomposeSCCs(pg);
		fw.append("SCCs without undesired deps:\n");
		
		sccs = f.extractSCCs();
		for(JPackageGroup scc : sccs )
			if ( scc.getJPackagesNb() > 1 )
				fw.append("\t" + scc.getJPackagesNb() + "\n");
		
		fw.close();
	}

}
