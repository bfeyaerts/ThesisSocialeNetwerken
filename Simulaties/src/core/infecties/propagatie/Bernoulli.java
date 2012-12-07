package core.infecties.propagatie;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import util.Parameter;
import util.validator.RangeValidator;

import core.graaf.Graaf;
import core.graaf.Knoop;

public class Bernoulli extends Propagatie {
	Random randomGenerator = new Random();
	
	protected ArrayList<ArrayList<Integer>> propageer(int aantalStappen, double verspreidingskans, ArrayList<ArrayList<Integer>> besmettingen) {
		if (aantalStappen <= 0)
			return besmettingen;
		return propageer(--aantalStappen, verspreidingskans, besmettingen);
	}

	@Override
	public Parameter[] getParameters() {
		return new Parameter[]{
			new Parameter("prob", "Verspreidingskans", Double.class, new RangeValidator<Double>(Double.class, 0., 1.)),
		};
	}

	@Override
	public Knoop[] propageer(Graaf graaf, Object[] settings) {
		double verspreidingskans = (Double) settings[0];
		
		HashSet<Knoop> besmettingen = new HashSet<Knoop>();
		for (Knoop knoop: graaf.getKnopen()) {
			if (! knoop.getDiagnose().isBesmettelijk())
				continue;
			
			for (Knoop opvolger: knoop.getOpvolgers()) {
				if (opvolger.getDiagnose().isBesmetbaar() && (randomGenerator.nextDouble() <= verspreidingskans))
					besmettingen.add(opvolger);
			}
		}
		return besmettingen.toArray(new Knoop[0]);
	}
	
}
