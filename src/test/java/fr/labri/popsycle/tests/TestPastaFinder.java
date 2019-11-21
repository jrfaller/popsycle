package fr.labri.popsycle.tests;

import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import fr.labri.popsycle.algo.deps.PastaFinder;
import fr.labri.popsycle.model.JDepKind;
import fr.labri.popsycle.model.JPackage;
import fr.labri.popsycle.model.JPackageDep;
import fr.labri.popsycle.model.JPackageGroup;

public class TestPastaFinder {

	@Test
	public void testPastaFinder() {
		JPackageGroup pkgGrp = getTestJPackageGroup();
		
		PastaFinder f1 = new PastaFinder(pkgGrp);
		Set<JPackageDep> deps = f1.extractJPackageDeps();

		System.out.println("Should be: [PackA -> UI (ITF 1), Kernel -> UI (ITF 1)]");
		System.out.println(deps);
		
		assertTrue(deps.size() == 2);
	}
	
	public static JPackageGroup getTestJPackageGroup() {
		JPackage e = new JPackage("PackE");
		JPackage ui = new JPackage("UI");
		JPackage a = new JPackage("PackA");
		JPackage b = new JPackage("PackB");
		JPackage k = new JPackage("Kernel");
		JPackage d = new JPackage("PackD");
		
		e.handleJPackageDep(ui,JDepKind.ITF);
		e.handleJPackageDep(ui,JDepKind.ITF);
		e.handleJPackageDep(ui,JDepKind.ITF);
		e.handleJPackageDep(ui,JDepKind.ITF);
		e.handleJPackageDep(ui,JDepKind.ITF);
		
		ui.handleJPackageDep(a,JDepKind.ITF);
		ui.handleJPackageDep(a,JDepKind.ITF);
		ui.handleJPackageDep(a,JDepKind.ITF);
		ui.handleJPackageDep(a,JDepKind.ITF);
		
		a.handleJPackageDep(ui,JDepKind.ITF);
		
		a.handleJPackageDep(b,JDepKind.ITF);
		a.handleJPackageDep(b,JDepKind.ITF);
		a.handleJPackageDep(b,JDepKind.ITF);
		a.handleJPackageDep(b,JDepKind.ITF);
		a.handleJPackageDep(b,JDepKind.ITF);
		a.handleJPackageDep(b,JDepKind.ITF);
		
		a.handleJPackageDep(k,JDepKind.ITF);
		a.handleJPackageDep(k,JDepKind.ITF);
		a.handleJPackageDep(k,JDepKind.ITF);
		a.handleJPackageDep(k,JDepKind.ITF);
		a.handleJPackageDep(k,JDepKind.ITF);
		
		b.handleJPackageDep(k,JDepKind.ITF);
		b.handleJPackageDep(k,JDepKind.ITF);
		b.handleJPackageDep(k,JDepKind.ITF);
		b.handleJPackageDep(k,JDepKind.ITF);
		b.handleJPackageDep(k,JDepKind.ITF);
		b.handleJPackageDep(k,JDepKind.ITF);
		
		k.handleJPackageDep(ui,JDepKind.ITF);
		
		k.handleJPackageDep(d,JDepKind.ITF);
		k.handleJPackageDep(d,JDepKind.ITF);
		k.handleJPackageDep(d,JDepKind.ITF);
		k.handleJPackageDep(d,JDepKind.ITF);
		k.handleJPackageDep(d,JDepKind.ITF);
		k.handleJPackageDep(d,JDepKind.ITF);
		k.handleJPackageDep(d,JDepKind.ITF);
		k.handleJPackageDep(d,JDepKind.ITF);
		
		JPackageGroup  pkgGrp = new JPackageGroup();
		pkgGrp.getJPackages().add(e);
		pkgGrp.getJPackages().add(ui);
		pkgGrp.getJPackages().add(a);
		pkgGrp.getJPackages().add(b);
		pkgGrp.getJPackages().add(k);
		pkgGrp.getJPackages().add(d);
		
		return pkgGrp;
	}

}
