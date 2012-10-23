package infecties.propagatie;

import java.util.ArrayList;

import infecties.Toestand;
import edu.uci.ics.jung.graph.Graph;

public abstract class Propagatie {
	final protected Graph<String, Integer> graph;
	final protected Toestand<String, Integer> toestand;
	
	public Propagatie(Graph<String, Integer> graph, Toestand<String, Integer> toestand) {
		this.graph = graph;
		this.toestand = toestand;
	}
	
	public ArrayList<ArrayList<Integer>> propageer(int aantalStappen, double verspreidingskans) {
		return propageer(aantalStappen, verspreidingskans, new ArrayList<ArrayList<Integer>>());
	}
	protected abstract ArrayList<ArrayList<Integer>> propageer(int aantalStappen, double verspreidingskans, ArrayList<ArrayList<Integer>> infecties);
}
