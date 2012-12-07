package core.experiment.grafiek;

import java.io.IOException;
import java.io.PrintWriter;

import org.jfree.chart.JFreeChart;

import util.Parameter;

import core.Model;
import core.experiment.Experiment;

public abstract class Grafiek extends Model {
	protected final Experiment experiment;
	
	public Grafiek(Experiment experiment) {
		this.experiment = experiment;
	}
	
	@Override
	public Parameter[] getParameters() {
		return null;
	}
	
	public abstract void reset();
	
	public abstract void update();
	
	public abstract JFreeChart getChart();
	
	public abstract void writeData(PrintWriter out) throws IOException;
}
