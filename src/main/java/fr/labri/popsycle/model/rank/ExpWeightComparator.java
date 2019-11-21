package fr.labri.popsycle.model.rank;

import java.util.Collection;
import java.util.Comparator;

import fr.labri.popsycle.model.JPackageCycle;

public class ExpWeightComparator implements Comparator<JPackageCycle>{

	private double maxWeight = Double.MIN_VALUE;
	
	public ExpWeightComparator(Collection<JPackageCycle> cycles) {
		for( JPackageCycle cycle : cycles ) {
			double w = WeightUtils.getExpWeight(cycle);
			if ( w > maxWeight )
				maxWeight = w;
		}
	}
	
	@Override
	public int compare(JPackageCycle c1, JPackageCycle c2) {
		int weightCmp = Double.compare(getWeight(c1),getWeight(c2));
		if ( weightCmp != 0 )
			return weightCmp;
		else 
			return (new SizeComparator()).compare(c1,c2);
	}
	
	public double getWeight(JPackageCycle cycle) {
		return WeightUtils.getExpWeight(cycle) / maxWeight;
	}

}
