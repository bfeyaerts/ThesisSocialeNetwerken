package core.graaf;

import edu.uci.ics.jung.graph.Graph;

public class Tak {
	protected final Graph<Knoop, Tak> graph;
	protected final long id;
	
	protected final Knoop begin;
	protected final Knoop einde;
	
	public Tak(Graph<Knoop, Tak> graph, long id, Knoop begin, Knoop einde) {
		this.graph = graph;
		this.id = id;
		this.begin = begin;
		this.einde = einde;
	}
	
	public long getId() {
		return id;
	}
	
	public Knoop getBegin() {
		return begin;
	}
	
	public Knoop getEinde() {
		return einde;
	}
	
	
	public String toString() {
		return "" + id + "[" + begin + ", " + einde + "]";
	}
}
