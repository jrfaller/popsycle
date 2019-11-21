package fr.labri.popsycle.algo.deps;

import java.util.HashSet;
import java.util.Set;

import fr.labri.popsycle.algo.DecomposeSCCs;
import fr.labri.popsycle.model.JPackage;
import fr.labri.popsycle.model.JPackageDep;
import fr.labri.popsycle.model.JPackageGroup;
import fr.labri.popsycle.model.rank.DistanceUtils;
import fr.labri.popsycle.model.utils.JavaUtils;

public class TaiFinder extends JPackageDepsFinder {

	public TaiFinder(JPackageGroup pkgGrp) {
		super(pkgGrp);
	}

	@Override
	public Set<JPackageDep> extractJPackageDeps() {
		Set<JPackageDep> tai = new HashSet<JPackageDep>();
		
		while( hasCycle() ) {
			JPackageDep dep = nextDep();
			tai.add(dep);
			pkgGrp.getJPackage(dep.getSource().getName()).removeJPackageDep(dep.getTarget());
		}
		
		return tai;
	}
	
	private JPackageDep nextDep() {
		DecomposeSCCs f = new DecomposeSCCs(pkgGrp);
		Set<JPackageGroup> sccs = f.extractSCCs();
		for(JPackageGroup scc : sccs ) {
			if ( scc.getJPackages().size() > 1 ) {
				//test if children
				for(JPackageDep sccDep: scc.getAllJPackageDeps() ) {
					if (DistanceUtils.isParent(sccDep.getSource(), sccDep.getTarget())) {
						return sccDep;
					}
				}
				//test if children of a node in the same layer
				for(JPackageDep sccDep: scc.getAllJPackageDeps() ) {
					//TODO
					if (getpackageLevel(sccDep.getSource()) < getpackageLevel(sccDep.getTarget()))
						return sccDep;
				}
				//test if dep on a leaf
				for(JPackageDep sccDep: scc.getAllJPackageDeps() ) {
					if (sccDep.getTarget().getJPackageDeps().size() == 0) {
						return sccDep;
					}
				}
				//test the weight and remove the highest
				int max = 0;
				JPackageDep maxDep = null;
				for( JPackageDep dep: scc.getAllJPackageDeps() ) {
					int cur = this.weight(dep);
					if ( cur > max ) {
						max = cur;
						maxDep = dep;
					}
				}
				return maxDep;
			}
		}
		return null;
	}
	
	private static int getpackageLevel(JPackage p1) {
		String[] p1tok = JavaUtils.split(p1.getName());
		
		return p1tok.length;
	}
	
	private boolean hasCycle() {
		DecomposeSCCs f = new DecomposeSCCs(pkgGrp);
		for( JPackageGroup g : f.extractSCCs() )
			if ( g.getJPackages().size() > 1 )
				return true;
		
		return false;
	}
	
	private Integer weight(JPackageDep dep) {
		
		return incomingSize(dep.getSource())+outgoingSize(dep.getTarget());
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
