package ui;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.lang.reflect.Constructor;

import org.apache.commons.collections15.Transformer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import core.infecties.InfectieGraad;
import core.infecties.Toestand;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.GraphMouseListener;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.event.MouseEvent;
import edu.uci.ics.jung.visualization.swt.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.swt.VisualizationComposite;

public class GraafVenster extends CTabItem {
	static int graafCounter = 0;
	
	final protected Graph<String, Integer> graph;
	final protected Toestand<String, Integer> toestand;
	final protected int id;
	
	private Layout<String, Integer> layout;
	private Composite composite;
	private GraphZoomScrollPane<String,Integer> panel;
	
	public GraafVenster(CTabFolder parent, Graph<String, Integer> graph, String layoutName) {
		super(parent, SWT.NONE);
		this.graph = graph;
		toestand = new Toestand<String, Integer>(graph);
		this.id = ++graafCounter;
		this.layout = getLayoutFromName(layoutName);
		
		initComponent();
	}

	public Graph<String, Integer> getGraph() {
		return graph;
	}
	public Toestand<String, Integer> getToestand() {
		return toestand;
	}
	
	private Layout<String, Integer> getLayoutFromName(String layoutName) {
		try {
			Class<?> aClass = Class.forName("edu.uci.ics.jung.algorithms.layout." + layoutName);
			Constructor<?> constructor = aClass.getConstructor(new Class[]{Graph.class});
			Layout<String, Integer> layout = (Layout<String, Integer>) constructor.newInstance(graph);
			return layout;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	protected void setLayout(String layoutName) {
		Layout<String, Integer> layout = getLayoutFromName(layoutName);
		if (layout != null) {
			layout.setSize(new Dimension(600, 600));
			this.layout = layout;
			panel.vv.setGraphLayout(layout);
		}
	}
	
	public void refresh() {
		panel.vv.repaint();
	}
	
	protected void initComponent() {
		setText("Graaf " + id);
		composite = new Composite(getParent(), SWT.NONE);
		composite.setLayout(new GridLayout());
		layout.setSize(new Dimension(600, 600));
		panel = new GraphZoomScrollPane<String,Integer>(composite, SWT.NONE, layout, new Dimension(600,600));
		GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        panel.setLayoutData(gridData);
        
        final VisualizationComposite<String, Integer> vv = panel.vv;
        RenderContext<String, Integer> renderContext = vv.getRenderContext();
        renderContext.setEdgeLabelTransformer(new Transformer<Integer, String>() {
			@Override
			public String transform(Integer e) {
				return "" + e;
			}
        });
        renderContext.setVertexLabelTransformer(new Transformer<String, String>() {
			@Override
			public String transform(String v) {
				return v;
			}
        });
        
        
        vv.addGraphMouseListener(new GraphMouseListener<String>() {
			@Override
			public void graphClicked(String knoop, MouseEvent arg1) {
				InfectieGraad infectieGraad = toestand.getInfectieGraad(knoop);
				switch (infectieGraad) {
				case GEZOND:
					toestand.setInfectieGraad(knoop, InfectieGraad.BESMET);
					break;
				case BESMET:
					toestand.setInfectieGraad(knoop, InfectieGraad.GEZOND);
					break;
				}
				vv.repaint();
			}
			@Override
			public void graphPressed(String knoop, MouseEvent arg1) {}
			@Override
			public void graphReleased(String knoop, MouseEvent arg1) {}
        });
        
        
        renderContext.setVertexFillPaintTransformer(new Transformer<String, Paint>() {
			@Override
			public Paint transform(String knoop) {
				InfectieGraad score = toestand.getVertexScorer().getVertexScore(knoop);
				switch (score) {
				case GEZOND:
					return Color.GREEN;
				case BESMET:
					return Color.RED;
				}
				return null;
			}
		});
		
        setControl(composite);
	}
}
