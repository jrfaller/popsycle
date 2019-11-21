package fr.labri.popsycle.tests;

import fr.labri.popsycle.algo.deps.TaiFinder;
import fr.labri.popsycle.model.JDepKind;
import fr.labri.popsycle.model.JPackage;
import fr.labri.popsycle.model.JPackageDep;
import fr.labri.popsycle.model.JPackageGroup;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertTrue;

public class TestSpoonReader {
	@Test
	public void testTaiFinder() {
		JPackageGroup pkgGrp = getTestJPackageGroup();
		
		TaiFinder f1 = new TaiFinder(pkgGrp);
		Set<JPackageDep> deps = f1.extractJPackageDeps();

		System.out.println("Should be: [ii -> s (ITF 1), s.cs -> s.cs.ms.c (ITF 1), e -> s.cs.w (ITF 1), bl -> bl.b (ITF 1)]");
		System.out.println(deps);
		
		assertTrue(deps.size() == 4);
	}
	
	public static JPackageGroup getTestJPackageGroup() {
		JPackage ii = new JPackage("ii");
		JPackage s = new JPackage("s");
		JPackage bl = new JPackage("bl");
		JPackage ss = new JPackage("ss");
		JPackage e = new JPackage("e");
		JPackage cr = new JPackage("cr");
		JPackage cv = new JPackage("cv");
		
		JPackage bs = new JPackage("s.bs");
		JPackage cs = new JPackage("s.cs");
		JPackage b = new JPackage("bl.b");
		JPackage sc = new JPackage("cv.sc");
		
		JPackage tb = new JPackage("bl.b.tb");
		JPackage ms = new JPackage("s.cs.ms");
		JPackage w = new JPackage("s.cs.w");
		
		JPackage c = new JPackage("s.cs.ms.c");
		
		
		ii.handleJPackageDep(s,JDepKind.ITF);
		s.handleJPackageDep(ii,JDepKind.ITF);
		bl.handleJPackageDep(b,JDepKind.ITF);
		ss.handleJPackageDep(e,JDepKind.ITF);
		cv.handleJPackageDep(cr,JDepKind.ITF);
		e.handleJPackageDep(w,JDepKind.ITF);
		
		bs.handleJPackageDep(s,JDepKind.ITF);
		cs.handleJPackageDep(s,JDepKind.ITF);
		cs.handleJPackageDep(c,JDepKind.ITF);
		b.handleJPackageDep(bl,JDepKind.ITF);
		b.handleJPackageDep(bs,JDepKind.ITF);
		b.handleJPackageDep(ss,JDepKind.ITF);
		b.handleJPackageDep(e,JDepKind.ITF);
		sc.handleJPackageDep(ss,JDepKind.ITF);
		sc.handleJPackageDep(cv,JDepKind.ITF);
		
		tb.handleJPackageDep(bs,JDepKind.ITF);
		tb.handleJPackageDep(b,JDepKind.ITF);
		ms.handleJPackageDep(ss,JDepKind.ITF);
		ms.handleJPackageDep(sc,JDepKind.ITF);
		w.handleJPackageDep(sc,JDepKind.ITF);
		w.handleJPackageDep(cv,JDepKind.ITF);
		
		c.handleJPackageDep(cs,JDepKind.ITF);
		c.handleJPackageDep(ms,JDepKind.ITF);
		c.handleJPackageDep(ss,JDepKind.ITF);
		c.handleJPackageDep(e,JDepKind.ITF);
		
		JPackageGroup  pkgGrp = new JPackageGroup();
		pkgGrp.getJPackages().add(e);
		
		
		pkgGrp.getJPackages().add( ii);
		pkgGrp.getJPackages().add( s);
		pkgGrp.getJPackages().add( bl);
		pkgGrp.getJPackages().add( ss );
		pkgGrp.getJPackages().add( e);
		pkgGrp.getJPackages().add( cr);
		pkgGrp.getJPackages().add( cv );
		
		pkgGrp.getJPackages().add( bs);
		pkgGrp.getJPackages().add( cs);
		pkgGrp.getJPackages().add( b );
		pkgGrp.getJPackages().add( sc);
		
		pkgGrp.getJPackages().add( tb);
		pkgGrp.getJPackages().add( ms);
		pkgGrp.getJPackages().add( w );
		
		pkgGrp.getJPackages().add( c);
		
		return pkgGrp;
	}
}
