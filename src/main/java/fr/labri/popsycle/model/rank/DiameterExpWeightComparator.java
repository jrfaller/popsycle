package fr.labri.popsycle.model.rank;

import java.util.Collection;
import java.util.Comparator;

import fr.labri.popsycle.model.JPackageCycle;

public class DiameterExpWeightComparator implements Comparator<JPackageCycle> {
	
	private DiameterComparator diameter;
	
	private ExpWeightComparator weight;
	
	public DiameterExpWeightComparator(Collection<JPackageCycle> cycles) {
		diameter = new DiameterComparator(cycles);
		weight = new ExpWeightComparator(cycles);
	}

	@Override
	public int compare(JPackageCycle c1, JPackageCycle c2) {
		double diam1 = diameter.getDiameter(c1);
		double weight1 = this.weight.getWeight(c1);
		double diam2 = diameter.getDiameter(c2);
		double weight2 = this.weight.getWeight(c2);
		
		int cmp = Double.compare(0.5D * diam1 + 0.5D * weight1,0.5D * diam2 + 0.5D * weight2);
		
		if ( cmp != 0 )
			return cmp;
		else 
			return (new SizeComparator()).compare(c1,c2);
	}

}
