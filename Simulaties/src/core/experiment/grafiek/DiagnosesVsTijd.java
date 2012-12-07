package core.experiment.grafiek;

import java.io.IOException;
import java.io.PrintWriter;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import core.experiment.Experiment;
import core.graaf.Knoop;
import core.infecties.diagnose.Diagnose;

public class DiagnosesVsTijd extends Grafiek {
	protected final Enum<? extends Diagnose>[] diagnoses;
	protected final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	
	public DiagnosesVsTijd() {
		super(null);
		diagnoses = null;
	}
	public DiagnosesVsTijd(Experiment experiment) {
		super(experiment);
		diagnoses = experiment.getEvolutieModel().possibleDiagnoses().getEnumConstants();
		update();
	}
	
	public String getName() {
		return "Diagnoses vs Tijd";
	}
	
	@Override
	public void reset() {
		dataset.clear();
	}

	@Override
	public void update() {
		int columnKey = dataset.getColumnCount();
		
		for (int i=0; i<diagnoses.length; i++) {
			int knopen = 0;
			for (Knoop knoop: experiment.getGraaf().getKnopen())
				if (knoop.getDiagnose() == diagnoses[i])
					knopen++;
			dataset.addValue((Number) knopen, diagnoses[i].toString(), columnKey);
		}
	}
	
	@Override
	public JFreeChart getChart() {
		JFreeChart chart = ChartFactory.createStackedBarChart("Diagnoses vs Tijd", "Tijd", "Aantal knopen", dataset, PlotOrientation.VERTICAL, true, true, false);
		CategoryPlot plot = chart.getCategoryPlot();
		StackedBarRenderer renderer = new StackedBarRenderer();
		renderer.setShadowVisible(false);
		for (int i=0; i<diagnoses.length; i++)
			renderer.setSeriesPaint(diagnoses[i].ordinal(), experiment.getColor((Diagnose) diagnoses[i]));
		plot.setRenderer(renderer);
		return chart;
	}
	
	@Override
	public void writeData(PrintWriter out) throws IOException {
		out.print("\"Tijdstip\"");
		for (int j=0; j<diagnoses.length; j++) {
			out.print(";\"" + diagnoses[j].toString() + "\"");
		}
	    out.println();
	    
		for (int i=0; i<dataset.getColumnCount(); i++) {
		    out.print("\"" + i + "\"");
		    for (int j=0; j<diagnoses.length; j++) {
		    	out.print(";\"" + dataset.getValue(j, i) + "\"");
			}
		    out.println();
		}
	}
}
