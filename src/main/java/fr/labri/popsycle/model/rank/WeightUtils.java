package fr.labri.popsycle.model.rank;

import fr.labri.popsycle.model.JDepKind;
import fr.labri.popsycle.model.JPackageCycle;
import fr.labri.popsycle.model.JPackageDep;

public class WeightUtils {
	
	public static double getEqWeight(JPackageCycle c) {
		double minw = Double.MAX_VALUE;
		
		for(JPackageDep d : c.getJPackageDeps()) {
			double w = 0F;
			for(JDepKind kind : d.getKindMap().keySet())
				w += d.getKindMap().get(kind);
			
			if ( w < minw )
				minw = w;
		}
		
		return minw;
	}
	
	public static double getExpWeight(JPackageCycle c) {
		double minw = Double.MAX_VALUE;
		
		for(JPackageDep d : c.getJPackageDeps()) {
			double w = 0F;
			
			if ( d.getKindMap().containsKey(JDepKind.REF) )
				w += d.getKindMap().get(JDepKind.REF);
			
			if ( d.getKindMap().containsKey(JDepKind.ITF) )
				w += d.getKindMap().get(JDepKind.ITF) * 10;
			
			if ( d.getKindMap().containsKey(JDepKind.INH) )
				w += d.getKindMap().get(JDepKind.INH) * 10;
			
			if ( w < minw )
				minw = w;
		}
		
		return minw;
	}


}
