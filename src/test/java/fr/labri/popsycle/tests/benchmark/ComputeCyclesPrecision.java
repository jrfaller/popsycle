package fr.labri.popsycle.tests.benchmark;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.labri.popsycle.algo.cycles.AllShortestCycleFinder;
import fr.labri.popsycle.model.*;
import fr.labri.popsycle.model.rank.*;


import static fr.labri.popsycle.tests.benchmark.BenchmarkUtils.*;

public class ComputeCyclesPrecision {

	public static void main(String[] args) throws IOException {
		String cdg = args[0];
		String deps = args[0] + ".deps.csv";
		String met = args[0] + ".cycles.met";

		FileWriter fw = new FileWriter(met);

		AllShortestCycleFinder f = new AllShortestCycleFinder(loadJPackageGroup(cdg));

		Map<String,Set<String>> undesiredDepsMap = BenchmarkUtils.getJPackageDeps(deps,JDepImportance.UNDESIRED);
		Set<JPackageCycle> cycles = f.extractJPackageCycles();

		int undesiredCycles = 0;
		for(JPackageCycle c : cycles)
			if ( isUndesired(c, undesiredDepsMap))
				undesiredCycles++;

		fw.append("Number of cycles: " + cycles.size() + "\n");
		fw.append("Number of undesired cycles: " + undesiredCycles + "\n");


		List<JPackageCycle> cyclesDiameter = new LinkedList<JPackageCycle>(cycles);
		Collections.sort(cyclesDiameter,new DiameterComparator(cycles));
		fw.append("FP10 - Diameter: " + getFirstPrecision(10,cyclesDiameter, undesiredDepsMap) + "\n");
		fw.append("LP10 - Diameter: " + getLastPrecision(10,cyclesDiameter, undesiredDepsMap) + "\n");
		//fw.append("LURR - Diameter: " + getLastRank(cyclesDiameter, undesiredDepsMap) + "\n");

		List<JPackageCycle> cyclesEqWeight = new LinkedList<JPackageCycle>(cycles);
		Collections.sort(cyclesEqWeight,new EqWeightComparator(cycles));
		fw.append("FP10 - Weight-eq: " + getFirstPrecision(10,cyclesEqWeight, undesiredDepsMap) + "\n");
		fw.append("LP10 - Weight-eq: " + getLastPrecision(10,cyclesEqWeight, undesiredDepsMap) + "\n");
		//fw.append("LURR - Weight-eq: " + getLastRank(cyclesEqWeight, undesiredDepsMap) + "\n");

		List<JPackageCycle> cyclesExpWeight = new LinkedList<JPackageCycle>(cycles);
		Collections.sort(cyclesExpWeight,new ExpWeightComparator(cycles));
		fw.append("FP10 - Weight-exp: " + getFirstPrecision(10,cyclesExpWeight, undesiredDepsMap) + "\n");
		fw.append("LP10 - Weight-exp: " + getLastPrecision(10,cyclesExpWeight, undesiredDepsMap) + "\n");
		//fw.append("LURR - Weight-exp: " + getLastRank(cyclesExpWeight, undesiredDepsMap) + "\n");

		List<JPackageCycle> cyclesDiameterExpWeight = new LinkedList<JPackageCycle>(cycles);
		Collections.sort(cyclesDiameterExpWeight,new DiameterExpWeightComparator(cycles));
		fw.append("FP10 - DiameterWeight-exp: " + getFirstPrecision(10,cyclesDiameterExpWeight, undesiredDepsMap) + "\n");
		fw.append("LP10 - DiameterWeight-exp: " + getLastPrecision(10,cyclesDiameterExpWeight, undesiredDepsMap) + "\n");
		//fw.append("LURR - DiameterWeight-exp: " + getLastRank(cyclesDiameterExpWeight, undesiredDepsMap) + "\n");
		
		List<JPackageCycle> cyclesName = new LinkedList<JPackageCycle>(cycles);
		Collections.sort(cyclesName,new NameComparator());
		fw.append("FP10 - Name: " + getFirstPrecision(10,cyclesName, undesiredDepsMap) + "\n");
		fw.append("LP10 - Name: " + getLastPrecision(10,cyclesName, undesiredDepsMap) + "\n");
		//fw.append("LURR - Name: " + getLastRank(cyclesName, undesiredDepsMap) + "\n");
		
		fw.close();
	}

	public static double getFirstPrecision(int n,List<JPackageCycle> cycles,Map<String,Set<String>> undesiredDepsMap) {
		int undesired = 0;
		for(int i = 0 ; i < n ; i++) {
			JPackageCycle cycle = cycles.get(i);
			if ( isUndesired(cycle, undesiredDepsMap) )
				undesired++;
		}

		return (double) undesired / (double) n;
	}

	public static double getLastPrecision(int n,List<JPackageCycle> cycles,Map<String,Set<String>> undesiredDepsMap) {
		int desired = 0;
		for(int i = cycles.size() - 1; i > cycles.size() - 1 - n ; i--) {
			JPackageCycle cycle = cycles.get(i);
			if ( !isUndesired(cycle, undesiredDepsMap) )
				desired++;
		}

		return (double) desired / (double) n;
	}

	public static double getLastRank(List<JPackageCycle> cycles,Map<String,Set<String>> undesiredDepsMap) {
		int last = 0;
		int current = 0;
		for(JPackageCycle cycle : cycles) {
			if ( isUndesired(cycle, undesiredDepsMap))
				last = current;
			current++;
		}

		return (double) (last + 1) / (double) cycles.size();
	}

	public static boolean isUndesired(JPackageCycle cycle,Map<String,Set<String>> undesiredDepsMap) {
		for( JPackageDep dep : cycle.getJPackageDeps() ) 
			if ( undesiredDepsMap.containsKey(dep.getSource().getName()) )
				if ( undesiredDepsMap.get(dep.getSource().getName()).contains(dep.getTarget().getName())) 
					return true;

		return false;
	}

}