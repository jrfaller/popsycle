package fr.labri.popsycle.algo.deps;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import fr.labri.popsycle.algo.DecomposeSCCs;
import fr.labri.popsycle.algo.cycles.AllShortestCycleFinder;
import fr.labri.popsycle.algo.cycles.DirectCycleFinder;
import fr.labri.popsycle.model.JPackageCycle;
import fr.labri.popsycle.model.JPackageDep;
import fr.labri.popsycle.model.JPackageGroup;

public class OzoneFinder extends JPackageDepsFinder {

	AllShortestCycleFinder shortest = null;
	DecomposeSCCs f = null;
	Set<JPackageCycle> smallCycles = null;
	
	public OzoneFinder(JPackageGroup pkgGrp) {
		super(pkgGrp);
	}

	@Override
	public Set<JPackageDep> extractJPackageDeps() {
		Set<JPackageDep> ozone = new HashSet<JPackageDep>();
		
		ozone.addAll(this.removeDirectCycles());
		while( hasCycle() ) {
			ozone.addAll(this.removeOtherCycles());
		}
		return ozone;
	}
	
	private Integer weight(JPackageDep dep) {
		return dep.occurences();
	}

	private Set<JPackageDep> removeDirectCycles() {
		Set<JPackageDep> ozone = new HashSet<JPackageDep>();
		DirectCycleFinder f = new DirectCycleFinder(pkgGrp);
		shortest = new AllShortestCycleFinder(pkgGrp);
		for( JPackageCycle g : f.extractDirectJPackageCycle()) {
			smallCycles = shortest.extractJPackageCycles();
			Iterator<JPackageDep> it = g.getJPackageDeps().iterator();
			JPackageDep depA = it.next();
			JPackageDep depB = it.next();
			
			if(this.weight(depA) >= this.weight(depB)*3){
				ozone.add(depB);
				pkgGrp.getJPackage(depB.getSource().getName()).removeJPackageDep(depB.getTarget());
			}
			else if(this.weight(depB) >= this.weight(depA)*3){
				ozone.add(depA);
				pkgGrp.getJPackage(depA.getSource().getName()).removeJPackageDep(depA.getTarget());
			}
			else if(this.weightShared(depA, smallCycles) > this.weightShared(depB, smallCycles)){
				ozone.add(depA);
				pkgGrp.getJPackage(depA.getSource().getName()).removeJPackageDep(depA.getTarget());
			}	
			else if(this.weightShared(depB, smallCycles) > this.weightShared(depA, smallCycles)){
				ozone.add(depB);
				pkgGrp.getJPackage(depB.getSource().getName()).removeJPackageDep(depB.getTarget());
			}	
			else if(this.weight(depB) > this.weight(depA)){
				ozone.add(depA);
				pkgGrp.getJPackage(depA.getSource().getName()).removeJPackageDep(depA.getTarget());
			}	
			else {
				ozone.add(depB);
				pkgGrp.getJPackage(depB.getSource().getName()).removeJPackageDep(depB.getTarget());
			}
		}	
		return ozone;
	}
	
	public Integer weightShared(JPackageDep dep, Set<JPackageCycle> sC) {
		int value = 0;
		for( JPackageCycle g : sC) {
			if ( g.size() > 1 ) {
				if (g.getJPackageDeps().contains(dep))
					value = value + 1;
			}
		}
		return value;
	}
	
	private Set<JPackageDep> removeOtherCycles() {
		Set<JPackageDep> ozone = new HashSet<JPackageDep>();
		f = new DecomposeSCCs(pkgGrp);
		shortest = new AllShortestCycleFinder(pkgGrp);
		smallCycles = shortest.extractJPackageCycles();
		int max = -1;
		JPackageDep maxDep = null;
		for( JPackageGroup g : f.extractSCCs() ) {
			if ( g.getJPackages().size() > 1 ) {
				for( JPackageDep dep: g.getAllJPackageDeps() ) {
					int cur = this.weightShared(dep, smallCycles);
					if ( cur >= max ) {
							max = cur;
							maxDep = dep;
					}
					if ( cur == max ) {
						if (this.weight(maxDep) > this.weight(dep)) {
							maxDep = dep;
						}
					}
				}
				ozone.add(maxDep);
				pkgGrp.getJPackage(maxDep.getSource().getName()).removeJPackageDep(maxDep.getTarget());
				return ozone;
			}
		}
		return null;
	}
	

	private boolean hasCycle() {
		f = new DecomposeSCCs(pkgGrp);
		for( JPackageGroup g : f.extractSCCs() )
			if ( g.getJPackages().size() > 1 )
				return true;
		
		return false;
	}
	

}
