package core.graaf.modellen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import util.Parameter;
import util.validator.RangeValidator;

import core.Configuratie;
import core.graaf.Knoop;
import core.graaf.Tak;
import edu.uci.ics.jung.graph.AbstractGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class InterClusterNode extends GraafModel {

	@Override
	public String getName() {
		return "Geclusterde graaf met vaste kans tussen knopen van verschillende clusters";
	}
	
	@Override
	public Parameter[] getParameters() {
		return new Parameter[]{
			new Parameter("N", "Aantal clusters", Integer.class, new RangeValidator<Integer>(Integer.class, 2, null)),
			new Parameter("micro", "Micro-model", Configuratie.class),
			new Parameter("prob", "Intercluster verbindingskans", Double.class, new RangeValidator<Double>(Double.class, 0., 1.)),
			new Parameter("gericht", "Gerichte graaf", Boolean.class)
		};
	}

	@Override
	public AbstractGraph<Knoop, Tak> genereer(Object[] setup) {
		int N = (Integer) setup[0];
		@SuppressWarnings("unchecked")
		Configuratie<GraafModel> micro = (Configuratie<GraafModel>) setup[1];
		double prob = (Double) setup[2];
		Boolean gericht = (Boolean) setup[3];
		
		// De supergraaf klaarzetten
		AbstractGraph<Knoop, Tak> graph;
		EdgeType edgeType;
		if (gericht) {
			graph = new DirectedSparseGraph<Knoop, Tak>();
			edgeType = EdgeType.DIRECTED;
		} else {
			graph = new UndirectedSparseGraph<Knoop, Tak>();
			edgeType = EdgeType.UNDIRECTED;
		}
		int knopen = 0;
		int takken = 0;
		
		// De macrograaf genereren
		ArrayList<Knoop[]> clusters = new ArrayList<Knoop[]>();
		for (int i=0; i<N; i++) {
			AbstractGraph<Knoop, Tak> cluster = micro.model.genereer(micro.setup);
			HashMap<Knoop, Knoop> clusterKnoop2graphKnoopMap = new HashMap<Knoop, Knoop>();
			// Knopen van een individuele cluster overnemen
			for (Knoop clusterKnoop: cluster.getVertices()) {
				Knoop graphKnoop = new Knoop(graph, knopen++);
				clusterKnoop2graphKnoopMap.put(clusterKnoop, graphKnoop);
				graph.addVertex(graphKnoop);
			}
			clusters.add(clusterKnoop2graphKnoopMap.values().toArray(new Knoop[0]));
			// Takken van een individuele cluster overnemen
			for (Tak clusterTak: cluster.getEdges()) {
				Knoop begin = clusterKnoop2graphKnoopMap.get(clusterTak.getBegin());
				Knoop einde = clusterKnoop2graphKnoopMap.get(clusterTak.getEinde());
				Tak graphTak = new Tak(graph, takken++, begin, einde);
				graph.addEdge(graphTak, graphTak.getBegin(), graphTak.getEinde(), edgeType);
			}
		}
		Random random = new Random();
		
		for (int i=0; i<N; i++) {
			Knoop[] cluster_begin = clusters.get(i);
			for (int j=gericht?0:(i+1); j<N; j++) {
				if (i==j)
					continue;
				
				Knoop[] cluster_einde = clusters.get(j);
				
				for (Knoop begin: cluster_begin)
					for (Knoop einde: cluster_einde)
						if (random.nextDouble() < prob) {
							Tak graphTak = new Tak(graph, takken++, begin, einde);
							graph.addEdge(graphTak, graphTak.getBegin(), graphTak.getEinde(), edgeType);
						}
			}
		}
		
		return graph;
	}
}
