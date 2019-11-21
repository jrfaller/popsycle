package fr.labri.popsycle.tests;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import fr.labri.popsycle.model.JPackage;
import fr.labri.popsycle.model.JPackageCycle;
import fr.labri.popsycle.model.rank.DiameterComparator;

public class TestDiameterComparator {
	
	
	@Test
	public void testCompareTo() {
		List<JPackageCycle> sort = getCycles();
		System.out.println(sort);
		
		DiameterComparator diam = new DiameterComparator(sort);
		
		System.out.println(diam.getDiameter(sort.get(0)));
		System.out.println(diam.getDiameter(sort.get(1)));
		System.out.println(diam.getDiameter(sort.get(2)));
		System.out.println(diam.getDiameter(sort.get(3)));
		
		Collections.sort(sort,diam);
		
		System.out.println(sort);
	}
	
	public static List<JPackageCycle> getCycles() {
		List<JPackageCycle> cycles = new LinkedList<JPackageCycle>();
		JPackage a = new JPackage("core");
		JPackage b = new JPackage("core.utils");
		
		JPackage c = new JPackage("ui");
		JPackage d = new JPackage("ui.utils.graph");
		
		cycles.add(new JPackageCycle(Arrays.asList(a,b)));
		cycles.add(new JPackageCycle(Arrays.asList(c,d)));
		cycles.add(new JPackageCycle(Arrays.asList(a,c)));
		cycles.add(new JPackageCycle(Arrays.asList(b,d)));
		
		return cycles;
	}
}
