package core.graaf.modellen;

import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

import core.graaf.Knoop;
import core.graaf.Tak;

import edu.uci.ics.jung.graph.AbstractGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import util.Parameter;
import util.validator.RangeValidator;

public class ErdosRenyi extends GraafModel {
	@Override
	public String getName() {
		return "Erdos-Renyi";
	}
	
	@Override
	public Parameter[] getParameters() {
		return new Parameter[]{
			new Parameter("N", "Aantal knopen", Integer.class, new RangeValidator<Integer>(Integer.class, 2, null)),
			new Parameter("prob", "Verbindingskans", Double.class, new RangeValidator<Double>(Double.class, 0., 1.)),
			new Parameter("gericht", "Gerichte graaf", Boolean.class)
		};
	}

	@Override
	//public AbstractGraph<Knoop, Long> genereer(Integer N, Double prob, Boolean gericht) {
	public AbstractGraph<Knoop, Tak> genereer(Object[] setup) {
		int N = (Integer) setup[0];
		double prob = (Double) setup[1];
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
		for (int i=0; i<N; i++)
			graph.addVertex(new Knoop(graph, i));
		
		Random rng = new Random();
		long edges = 0;
		Knoop[] knopen = graph.getVertices().toArray(new Knoop[]{});
		for (int i=0; i<knopen.length; i++) {
			for (int j = gericht ? 0 : i+1; j<knopen.length; j++) {
				if (i == j)
					continue;
				if (rng.nextDouble() <= prob) {
					Tak tak = new Tak(graph, edges++, knopen[i], knopen[j]);
					graph.addEdge(tak, tak.getBegin(), tak.getEinde(), edgeType);
				}
			}
		}
		return graph;
	}

	@Override
	public AbstractGraph<Knoop, Tak> genereer(Object[] setup, AbstractGraph<Knoop, Tak> graph) {
		return genereer(setup, graph.getVertices(), graph.getEdges());
	}
	@Override
	public AbstractGraph<Knoop, Tak> genereer(Object[] setup, Collection<Knoop> knopen, Collection<Tak> takken) {
		boolean gericht = (Boolean) setup[2];
		
		AbstractGraph<Knoop, Tak> newGraph;
		EdgeType edgeType;
		if (gericht) {
			newGraph = new DirectedSparseGraph<Knoop, Tak>();
			edgeType = EdgeType.DIRECTED;
		} else {
			newGraph = new UndirectedSparseGraph<Knoop, Tak>();
			edgeType = EdgeType.UNDIRECTED;
		}
		HashMap<Long, Knoop> map = new HashMap<Long, Knoop>();
		for (Knoop knoopOrig: knopen) {
			Knoop knoopNew = new Knoop(newGraph, knoopOrig.getId());
			map.put(knoopOrig.getId(), knoopNew);
			newGraph.addVertex(knoopNew);
		}
		for (Tak tak: takken) {
			Tak takNew = new Tak(newGraph, tak.getId(), map.get(tak.getBegin().getId()), map.get(tak.getEinde().getId()));
			newGraph.addEdge(takNew, takNew.getBegin(), takNew.getEinde(), edgeType);
		}
		return newGraph;
	}

}
