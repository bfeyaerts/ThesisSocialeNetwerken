package core.graaf.modellen;

import java.util.Random;

import util.Parameter;
import util.validator.RangeValidator;

import core.graaf.Knoop;
import core.graaf.Tak;
import edu.uci.ics.jung.graph.AbstractGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class SmallWorld extends GraafModel {
	@Override
	public String getName() {
		return "Small World";
	}

	@Override
	public Parameter[] getParameters() {
		return new Parameter[] {
				new Parameter("N", "Aantal knopen", Integer.class, new RangeValidator<Integer>(Integer.class, 2, null)),
				new Parameter("k", "Buren", Integer.class, new RangeValidator<Integer>(Integer.class, 2, null)),
				new Parameter("Shortcut", "Verbindingskans shortcut", Double.class, new RangeValidator<Double>(Double.class, 0.000000000001, 0.9999999999999)),
				new Parameter("gericht", "Gerichte graaf", Boolean.class) };
	}

	@Override
	public AbstractGraph<Knoop, Tak> genereer(Object[] setup) {
		int N = (Integer) setup[0];
		int buren = (Integer) setup[1];
		double verbindingskansShortcut = (Double) setup[2];
		boolean gericht = (Boolean) setup[3];

		AbstractGraph<Knoop, Tak> graph;
		EdgeType edgeType;
		if (gericht) {
			graph = new DirectedSparseGraph<Knoop, Tak>();
			edgeType = EdgeType.DIRECTED;
		} else {
			graph = new UndirectedSparseGraph<Knoop, Tak>();
			edgeType = EdgeType.UNDIRECTED;
		}
		for (int i = 0; i < N; i++) {
			graph.addVertex(new Knoop(graph, i));
		}

		Random rng = new Random();
		// neighbourhood niet groter dan N
		if (buren > N) {
			buren = N;
		}

		// genereer de takken
		// neighbourhoodknopen
		Knoop[] knopen = graph.getVertices().toArray(new Knoop[] {});
		
		System.out.println(knopen.toString());
		for(Knoop k : knopen){
			System.out.println("ID: "+k.getId() + "\n" + k.toString());
		}
		long edges = 0;
		// assumptie: als het aantal buren oneven is --> links 1 meer dan rechts
		// nemen!
		if (gericht) {
			for (int i = 0; i < knopen.length; i++) {
				for (int j = 0; j <= buren; j++) {
					// % = restdeling
					// N + dient voor negatieve waarden juist te interpreteren
					int buurknoop = (N + i - (int) Math.ceil((double) buren / 2.0) + j) % N;
					if (buurknoop != i) {
						Tak tak = new Tak(graph, edges++, knopen[i], knopen[buurknoop]);
						graph.addEdge(tak, tak.getBegin(), tak.getEinde(), edgeType);
					}
				}
			}
		} else {
			int l = (int) Math.ceil((double) buren / 2.0);
			for (int i = 0; i < knopen.length; i++) {
				int laatste = Math.min(i + l, knopen.length - 1);
				for (int buurknoop = i + 1; buurknoop <= laatste; buurknoop++) {

					// % = restdeling
					// N + dient voor negatieve waarden juist te interpreteren

					if (buurknoop != i) {
						Tak tak = new Tak(graph, edges++, knopen[i], knopen[buurknoop]);
						graph.addEdge(tak, tak.getBegin(), tak.getEinde(), edgeType);
					}
				}
			}
			// laatste knoop moet verbinding maken met eerste knoop, als het
			// aantal buren > 0, maar dat is altijd zo
			for (int i = 0; i < l; i++) {
				for (int j = knopen.length - 1; j > knopen.length - 1 - l + i; j--) {
					Tak tak = new Tak(graph, edges++, knopen[i], knopen[j]);
					graph.addEdge(tak, tak.getBegin(), tak.getEinde(), edgeType);
				}
			}
		}

		// rewirering

		// +1 want jezelf meetellen en je k buren waarmee er reeds verbinding is
		// aantal is het aantal knopen waar er nog geen verbinding mee gemaakt
		// is voor de rewiring
		int aantal = knopen.length - (buren + 1);
		for (int i = 0; i < knopen.length; i++) {
			/*
			 * startplaats bepalen --> idee is naar rechts gaan, en eerste knoop
			 * die nog geen connectie heeft als startknoop nemen
			 */

			if (gericht) {
				for (int g = 0; g < aantal; g++) {
					// g = teller voor het aantal knopen waarmee nog geen verbinding gemaakt is
					int startknoop = ((int) Math.floor((double) buren / 2.0) + 1 + i) % N;
					int j = (g + startknoop) % N;
					if (rng.nextDouble() <= verbindingskansShortcut) {
						Tak tak = new Tak(graph, edges++, knopen[i], knopen[j]);
						graph.addEdge(tak, tak.getBegin(), tak.getEinde(), edgeType);
					}
				}
			} else {
				// bepalen van de laatste knoop:
				int startknoop = ((int) Math.ceil((double) buren / 2.0) + 1 + i);
				if (startknoop < knopen.length) {
					int laatste = Math.min(startknoop + aantal - 1, knopen.length - 1);

					for (int j = startknoop; j <= laatste; j++) {
						if (rng.nextDouble() <= verbindingskansShortcut) {
							System.out.println("i: "+i + "en j: "+j);
							Tak tak = new Tak(graph, edges++, knopen[i], knopen[j]);
							graph.addEdge(tak, tak.getBegin(), tak.getEinde(), edgeType);
						}
					}
				}
			}
		}

		return graph;
	}
}
