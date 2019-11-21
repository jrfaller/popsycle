package fr.labri.popsycle.algo.deps;

import java.util.HashSet;
import java.util.Set;

import fr.labri.popsycle.algo.DecomposeSCCs;
import fr.labri.popsycle.model.JPackage;
import fr.labri.popsycle.model.JPackageDep;
import fr.labri.popsycle.model.JPackageGroup;

public class BriandFinder extends JPackageDepsFinder {

	public BriandFinder(JPackageGroup pkgGrp) {
		super(pkgGrp);
	}

	@Override
	public Set<JPackageDep> extractJPackageDeps() {
		Set<JPackageDep> briand = new HashSet<>();
		
		while( hasCycle() ) {
			JPackageDep dep = nextDep();
			briand.add(dep);
			pkgGrp.getJPackage(dep.getSource().getName()).removeJPackageDep(dep.getTarget());
		}
		
		return briand;
	}
	
	private JPackageDep nextDep() {
		DecomposeSCCs f = new DecomposeSCCs(pkgGrp);
		int max = 0;
		JPackageDep maxDep = null;
		for( JPackageGroup g : f.extractSCCs() ) {
			if ( g.getJPackages().size() > 1 ) {
				for( JPackageDep dep: g.getAllJPackageDeps() ) {
					int cur = this.weight(dep);
					if ( cur > max ) {
						max = cur;
						maxDep = dep;
					}
				}
				return maxDep;
			}
		}
		return maxDep;
	}
	
	private boolean hasCycle() {
		DecomposeSCCs f = new DecomposeSCCs(pkgGrp);
		for( JPackageGroup g : f.extractSCCs() )
			if ( g.getJPackages().size() > 1 )
				return true;
		
		return false;
	}
	
	private Integer weight(JPackageDep dep) {
		
		return incomingSize(dep.getSource())*outgoingSize(dep.getTarget());
	}
	
	private Integer incomingSize(JPackage pack) {
		int size = 0;
		for( JPackageDep dep: pkgGrp.getAllJPackageDeps() ) {
			if ( dep.getTarget() == pack ) {
				size = size + dep.occurences();
			}
		}
		return size;
	}
	
	private Integer outgoingSize(JPackage pack) {
		
		return pack.getJPackageDeps().size();
	}
}
