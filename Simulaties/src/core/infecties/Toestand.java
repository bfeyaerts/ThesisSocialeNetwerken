package core.infecties;

import java.util.ArrayList;
import java.util.HashMap;

import edu.uci.ics.jung.algorithms.scoring.VertexScorer;
import edu.uci.ics.jung.graph.Graph;

public class Toestand<K, E> {
	final protected Graph<K, E> graph;
	final protected VertexScorer<K, InfectieGraad> scorer;
	final protected HashMap<InfectieGraad, ArrayList<K>> scores = new HashMap<InfectieGraad, ArrayList<K>>();

	public Toestand(Graph<K, E> graph) {
		this.graph = graph;
		this.scorer = new VertexScorer<K, InfectieGraad>() {
			@Override
			public InfectieGraad getVertexScore(K knoop) {
				for (InfectieGraad score: InfectieGraad.values())
					if (scores.get(score).contains(knoop))
						return score;
				return null;
			}
		};
		
		scores.put(InfectieGraad.GEZOND, new ArrayList<K>(graph.getVertices()));
		for (InfectieGraad infectieGraad: InfectieGraad.values()) {
			if (infectieGraad == InfectieGraad.GEZOND)
				continue;
			scores.put(infectieGraad, new ArrayList<K>());
		}
	}
	
	public ArrayList<K> getKnopen(InfectieGraad infectieGraad) {
		return scores.get(infectieGraad);
	}
	
	public InfectieGraad setInfectieGraad(K knoop, InfectieGraad infectieGraad) {
		InfectieGraad oldScore = getInfectieGraad(knoop);
		if (! oldScore.equals(infectieGraad)) {
			scores.get(oldScore).remove(knoop);
			scores.get(infectieGraad).add(knoop);
		}
		return oldScore;
	}
	public InfectieGraad getInfectieGraad(K knoop) {
		return scorer.getVertexScore(knoop);
	}
	
	public VertexScorer<K, InfectieGraad> getVertexScorer() {
		return scorer;
	}
}
