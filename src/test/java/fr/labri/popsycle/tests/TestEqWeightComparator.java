package fr.labri.popsycle.tests;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import fr.labri.popsycle.algo.cycles.AllShortestCycleFinder;
import fr.labri.popsycle.model.JDepKind;
import fr.labri.popsycle.model.JPackage;
import fr.labri.popsycle.model.JPackageCycle;
import fr.labri.popsycle.model.JPackageDep;
import fr.labri.popsycle.model.JPackageGroup;
import fr.labri.popsycle.model.rank.EqWeightComparator;

import static org.junit.Assert.*;

public class TestEqWeightComparator {
	
	@Test
	public void testCompareTo() {
		JPackageGroup pg = getJPackageGroup();
		AllShortestCycleFinder f = new AllShortestCycleFinder(pg);
		Set<JPackageCycle> cycles = f.extractJPackageCycles();
		
		assertTrue(cycles.size() == 2);

		
		List<JPackageCycle> sort = new LinkedList<JPackageCycle>(cycles);
		
		System.out.println(sort);
		
		EqWeightComparator eq = new EqWeightComparator(cycles);
		Collections.sort(sort,eq);
		
		System.out.println(eq.getWeight(sort.get(0)));
		System.out.println(eq.getWeight(sort.get(1)));
		
		System.out.println(sort);
		
	}
	
	public static JPackageGroup getJPackageGroup() {
		JPackage p1 = new JPackage("p1");
		JPackage p2 = new JPackage("p2");
		JPackage p3 = new JPackage("p3");
		
		JPackageDep d12 = new JPackageDep(p1,p2);
		d12.incrementKind(JDepKind.INH);
		d12.incrementKind(JDepKind.INH);
		d12.incrementKind(JDepKind.INH);
		d12.incrementKind(JDepKind.INH);
		d12.incrementKind(JDepKind.ITF);
		d12.incrementKind(JDepKind.REF);
		d12.incrementKind(JDepKind.REF);
		p1.getJPackageDeps().add(d12);
		
		JPackageDep d21 = new JPackageDep(p2,p1);
		d21.incrementKind(JDepKind.INH);
		p2.getJPackageDeps().add(d21);
		
		JPackageDep d23 = new JPackageDep(p2,p3);
		d23.incrementKind(JDepKind.INH);
		d23.incrementKind(JDepKind.INH);
		d23.incrementKind(JDepKind.ITF);
		d23.incrementKind(JDepKind.ITF);
		p2.getJPackageDeps().add(d23);
		
		JPackageDep d32 = new JPackageDep(p3,p2);
		d32.incrementKind(JDepKind.ITF);
		d32.incrementKind(JDepKind.ITF);	
		p3.getJPackageDeps().add(d32);
		
		JPackageGroup pg = new JPackageGroup();
		pg.getJPackages().add(p1);
		pg.getJPackages().add(p2);
		pg.getJPackages().add(p3);
		
		return pg;
	}

}
