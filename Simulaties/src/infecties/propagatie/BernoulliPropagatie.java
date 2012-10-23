package infecties.propagatie;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import infecties.InfectieGraad;
import infecties.Toestand;
import edu.uci.ics.jung.graph.Graph;

public class BernoulliPropagatie extends Propagatie {
	Random randomGenerator = new Random();
	
	public BernoulliPropagatie(Graph<String, Integer> graph, Toestand<String, Integer> toestand) {
		super(graph, toestand);
	}
	
	protected ArrayList<ArrayList<Integer>> propageer(int aantalStappen, double verspreidingskans, ArrayList<ArrayList<Integer>> besmettingen) {
		if (aantalStappen <= 0)
			return besmettingen;
		
		Object[] besmetteKnopen = toestand.getKnopen(InfectieGraad.BESMET).toArray();
		ArrayList<Integer> nieuweBesmettingen = new ArrayList<Integer>();
		for (Object o: besmetteKnopen) {
			String besmetteKnoop = (String) o;
			Collection<String> opvolgers = graph.getSuccessors(besmetteKnoop);
			for (String opvolger: opvolgers) {
				if (!toestand.getKnopen(InfectieGraad.BESMET).contains(opvolger) && (randomGenerator.nextDouble() <= verspreidingskans)) {
					toestand.setInfectieGraad(opvolger, InfectieGraad.BESMET);
					nieuweBesmettingen.add(graph.findEdge(besmetteKnoop, opvolger));
				}
			}
		}
		besmettingen.add(nieuweBesmettingen);
		return propageer(--aantalStappen, verspreidingskans, besmettingen);
	}
}
