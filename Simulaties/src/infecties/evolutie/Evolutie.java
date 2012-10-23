package infecties.evolutie;

import infecties.Toestand;
import edu.uci.ics.jung.graph.Graph;

public abstract class Evolutie {
	final protected Graph<String, Integer> graph;
	final protected Toestand<String, Integer> toestand;
	
	public Evolutie(Graph<String, Integer> graph, Toestand<String, Integer> toestand) {
		this.graph = graph;
		this.toestand = toestand;
	}
	
	public abstract void evolueer(String knoop);
}
