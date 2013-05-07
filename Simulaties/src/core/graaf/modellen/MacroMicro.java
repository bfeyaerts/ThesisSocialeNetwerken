package core.graaf.modellen;

import java.util.HashMap;
import java.util.Random;

import util.Parameter;

import core.Configuratie;
import core.graaf.Knoop;
import core.graaf.Tak;
import edu.uci.ics.jung.graph.AbstractGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class MacroMicro extends GraafModel {

	@Override
	public String getName() {
		return "Geclusterde graaf met macro- en micromodel";
	}
	
	@Override
	public Parameter[] getParameters() {
		return new Parameter[]{
			new Parameter("macro", "Macro-model", Configuratie.class),
			new Parameter("micro", "Micro-model", Configuratie.class),
			new Parameter("gericht", "Gerichte graaf", Boolean.class)
		};
	}

	@Override
	public AbstractGraph<Knoop, Tak> genereer(Object[] setup) {
		@SuppressWarnings("unchecked")
		Configuratie<GraafModel> macro = (Configuratie<GraafModel>) setup[0];
		@SuppressWarnings("unchecked")
		Configuratie<GraafModel> micro = (Configuratie<GraafModel>) setup[1];
		Boolean gericht = (Boolean) setup[2];
		
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
		AbstractGraph<Knoop, Tak> macroGraaf = macro.model.genereer(macro.setup);
		HashMap<Knoop, Knoop[]> macroKnoop2graphClusterMap = new HashMap<Knoop, Knoop[]>();
		for (Knoop macroKnoop: macroGraaf.getVertices()) {
			AbstractGraph<Knoop, Tak> cluster = micro.model.genereer(micro.setup);
			HashMap<Knoop, Knoop> clusterKnoop2graphKnoopMap = new HashMap<Knoop, Knoop>();
			// Knopen van een individuele cluster overnemen
			for (Knoop clusterKnoop: cluster.getVertices()) {
				Knoop graphKnoop = new Knoop(graph, knopen++);
				clusterKnoop2graphKnoopMap.put(clusterKnoop, graphKnoop);
				graph.addVertex(graphKnoop);
			}
			macroKnoop2graphClusterMap.put(macroKnoop, clusterKnoop2graphKnoopMap.values().toArray(new Knoop[0]));
			// Takken van een individuele cluster overnemen
			for (Tak clusterTak: cluster.getEdges()) {
				Knoop begin = clusterKnoop2graphKnoopMap.get(clusterTak.getBegin());
				Knoop einde = clusterKnoop2graphKnoopMap.get(clusterTak.getEinde());
				Tak graphTak = new Tak(graph, takken++, begin, einde);
				graph.addEdge(graphTak, graphTak.getBegin(), graphTak.getEinde(), edgeType);
			}
		}
		Random random = new Random();
		for (Tak macroTak: macroGraaf.getEdges()) {
			Knoop[] clusterBegin = macroKnoop2graphClusterMap.get(macroTak.getBegin());
			Knoop begin = clusterBegin[random.nextInt(clusterBegin.length)];
						
			Knoop[] clusterEinde = macroKnoop2graphClusterMap.get(macroTak.getEinde());
			Knoop einde = clusterEinde[random.nextInt(clusterEinde.length)];
			
			Tak graphTak = new Tak(graph, takken++, begin, einde);
			graph.addEdge(graphTak, graphTak.getBegin(), graphTak.getEinde(), edgeType);
		}
		return graph;
	}
}
