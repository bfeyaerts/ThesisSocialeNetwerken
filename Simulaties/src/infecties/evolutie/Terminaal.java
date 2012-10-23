package infecties.evolutie;

import infecties.Toestand;
import edu.uci.ics.jung.graph.Graph;

public class Terminaal extends Evolutie {

	public Terminaal(Graph<String, Integer> graph, Toestand<String, Integer> toestand) {
		super(graph, toestand);
	}

	@Override
	public void evolueer(String knoop) {
		// Geen evolutie mogelijk: eens besmet, altijd besmet
	}

}
