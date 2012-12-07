package core.graaf;

import core.infecties.diagnose.Diagnose;

import edu.uci.ics.jung.graph.Graph;

public class Knoop {
	protected final Graph<Knoop, Tak> graph;
	protected final long id;
	
	protected Diagnose diagnose = null;
	
	public Knoop(Graph<Knoop, Tak> graph, long id) {
		this.graph = graph;
		this.id = id;
	}
	
	public Diagnose getDiagnose() {
		return diagnose;
	}
	public void setDiagnose(Diagnose diagnose) {
		this.diagnose = diagnose;
	}
	
	public long getId() {
		return id;
	}
	
	public Knoop[] getOpvolgers() {
		return graph.getSuccessors(this).toArray(new Knoop[0]);
	}
	
	public String toString() {
		return "" + id;
	}
}
