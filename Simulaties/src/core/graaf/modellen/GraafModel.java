package core.graaf.modellen;

import java.util.Collection;
import java.util.HashMap;

import core.Model;
import core.graaf.Knoop;
import core.graaf.Tak;
import edu.uci.ics.jung.graph.AbstractGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import util.Parameter;

public abstract class GraafModel extends Model {
	public Parameter[] getParameters() {
		return new Parameter[]{new Parameter("gericht", "Gerichte graaf", Boolean.class)};
	}
	
	public abstract AbstractGraph<Knoop, Tak> genereer(Object[] setup);
	public AbstractGraph<Knoop, Tak> genereer(Object[] setup, AbstractGraph<Knoop, Tak> graph) {
		return genereer(setup, graph.getVertices(), graph.getEdges());
	}
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
