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

public class ScaleFree extends GraafModel {
	
	@Override
	public String getName() {
		return "Scale Free";
	}
	
	@Override
	public Parameter[] getParameters() {
		return new Parameter[]{
			new Parameter("N", "Aantal knopen", Integer.class, new RangeValidator<Integer>(Integer.class, 2, null)),
			new Parameter("exp", "Graadexponent", Double.class, new RangeValidator<Double>(Double.class, 2., 3.)),
			new Parameter("gericht", "Gerichte graaf", Boolean.class)
		};
	}
	
	@Override
	public AbstractGraph<Knoop, Tak> genereer(Object[] setup) {
		int N = (Integer) setup[0];
		double exp = (Double) setup[1];
		boolean gericht = (Boolean) setup[2];
		
		AbstractGraph<Knoop, Tak> graph;
		EdgeType edgeType;
		if (gericht) {
			graph = new DirectedSparseGraph<Knoop, Tak>();
			edgeType = EdgeType.DIRECTED;
		} else {
			graph = new UndirectedSparseGraph<Knoop, Tak>();
			edgeType = EdgeType.UNDIRECTED;
		}
		for (int i=0; i<N; i++){
			graph.addVertex(new Knoop(graph, i));
		}
		
		Random rng = new Random();
	
		// genereer de takken
		// graad ^(-gamma)
		Knoop[] knopen = graph.getVertices().toArray(new Knoop[]{});
		long edges = 0;
		for (int i=0; i<knopen.length; i++) {
			for (int j = gericht ? 0 : i+1; j<knopen.length; j++) {
				if (i == j)
					continue;
				int graad = graph.degree(knopen[i]);
				double prob = Math.pow(graad, (-exp));
				if (rng.nextDouble() <= prob) {
					Tak tak = new Tak(graph, edges++, knopen[i], knopen[j]);
					graph.addEdge(tak, tak.getBegin(), tak.getEinde(), edgeType);
				}
			}
		}
		return graph; 
	}
}
