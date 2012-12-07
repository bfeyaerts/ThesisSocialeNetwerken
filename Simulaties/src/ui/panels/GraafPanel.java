package ui.panels;

import java.awt.Dimension;
import java.awt.Paint;
import java.lang.reflect.Constructor;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.GraphMouseListener;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.swt.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.swt.VisualizationComposite;

import org.apache.commons.collections15.Transformer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import core.experiment.Experiment;
import core.graaf.Graaf;
import core.graaf.Knoop;

import ui.Programma;
import ui.wizard.NewExperimentWizard;
import util.xml.XMLWriter.XMLElementWriter;

public class GraafPanel extends AbstractPanel {
	protected final static String[] layouts = {"KKLayout", "FRLayout", "FRLayout2", "CircleLayout"};
	protected final static int defaultLayout = 0;
	
	protected final Graaf graaf;
	protected final boolean topLevel;
	
	private Button btnSaveConfig;
	private Button btnSaveGraph;
	private Combo cboLayout;
	private Button btnExperiment;
	private Button btnClose;
	
	private Composite graphWrapper;
	private GraphZoomScrollPane<Knoop, Long> graphPane;
	private Layout<Knoop, Long> graphLayout;
	private VisualizationComposite<Knoop, Long> visualizationComposite;
	private RenderContext<Knoop, Long> renderContext;

	public GraafPanel(Graaf graaf, Composite parent, int style) {
		super(parent, style);
		this.graaf = graaf;
		topLevel = parent instanceof CTabFolder;
		initComponent(composite);
	}
	
	private Dimension getGraphDimension() {
		Rectangle clientArea = graphWrapper.getClientArea();
		return new Dimension((int) (clientArea.width * .9), (int) (clientArea.height * .9));
	}
	
	@SuppressWarnings("unchecked")
	private Layout<Knoop, Long> getLayoutFromName(String layoutName) {
		try {
			Class<?> aClass = Class.forName("edu.uci.ics.jung.algorithms.layout." + layoutName);
			Constructor<?> constructor = aClass.getConstructor(new Class[]{Graph.class});
			return (Layout<Knoop, Long>) constructor.newInstance(graaf.getGraph());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	protected void setLayout(String layoutName) {
		Layout<Knoop, Long> graphLayout = getLayoutFromName(layoutName);
		if (graphLayout != null) {
			graphLayout.setSize(getGraphDimension());
			this.graphLayout = graphLayout;
			graphPane.vv.setGraphLayout(graphLayout);
		}
	}
	
	public void refresh() {
		visualizationComposite.repaint();
	}
	
	public void setVertexFillPaintTransformer(Transformer<Knoop, Paint> transformer) {
		renderContext.setVertexFillPaintTransformer(transformer);
	}
	public void addGraphMouseListener(GraphMouseListener<Knoop> listener) {
		visualizationComposite.addGraphMouseListener(listener);
	}
	
	@Override
	protected void initComponent(final Composite composite) {
		composite.setLayout(new FormLayout());
		
		btnSaveConfig = new Button(composite, SWT.PUSH);
		btnSaveConfig.setText("Graafconfiguratie opslaan");
		btnSaveConfig.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				Programma.saveFile(composite.getShell(), "graafconfig", "config", new XMLElementWriter() {
					@Override
					public void writeElementBody(XMLEventWriter eventWriter) throws XMLStreamException {
						graaf.createElement(eventWriter, "graaf", false);
					}
				});
			}
		});
		btnSaveGraph = new Button(composite, SWT.PUSH);
		btnSaveGraph.setText("Graaf opslaan");
		btnSaveGraph.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				Programma.saveFile(composite.getShell(), "graaf", "config", new XMLElementWriter() {
					@Override
					public void writeElementBody(XMLEventWriter eventWriter) throws XMLStreamException {
						graaf.createElement(eventWriter, "graaf", true);
					}
				});
			}
		});
		
		cboLayout = new Combo(composite, SWT.READ_ONLY | SWT.DROP_DOWN);
		for (int i=0; i<layouts.length; i++)
			cboLayout.add(layouts[i]);
		cboLayout.select(defaultLayout);
		cboLayout.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				setLayout(cboLayout.getItem(cboLayout.getSelectionIndex()));
			}
		});
		
		btnExperiment = new Button(composite, SWT.PUSH);
		btnExperiment.setText("Nieuw experiment");
		btnExperiment.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				final NewExperimentWizard wizard = new NewExperimentWizard(composite.getShell(), graaf);
				wizard.addDisposeListener(new DisposeListener() {
					@Override
					public void widgetDisposed(DisposeEvent arg0) {
						Experiment experiment = wizard.getExperiment();
						if (experiment != null) {
							final CTabItem tabItem = new CTabItem(Programma.tabFolder, SWT.NONE);
							tabItem.setText("Experiment");
							ExperimentPanel experimentPanel = new ExperimentPanel(wizard.getExperiment(), Programma.tabFolder, SWT.BORDER);
							tabItem.setControl(experimentPanel.getBody());
							Programma.tabFolder.setSelection(tabItem);
							experimentPanel.addDisposeListener(new DisposeListener() {
								@Override
								public void widgetDisposed(DisposeEvent arg0) {
									tabItem.dispose();
								}
							});
						}
					}
				});
				wizard.open();
			}
		});
		
		if (topLevel) {
			btnClose = new Button(composite, SWT.PUSH);
			btnClose.setText("Sluiten");
			btnClose.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					composite.dispose();
					try {
						GraafPanel.this.finalize();
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			});
		}
		
		graphWrapper = new Composite(composite, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, true);
		gridLayout.marginBottom = gridLayout.marginTop = 0;
		gridLayout.marginLeft = gridLayout.marginRight = 0;
		graphWrapper.setLayout(gridLayout);
		
		FormData formData = new FormData();
		formData.left = new FormAttachment(0, 0);
		formData.top = new FormAttachment(0, 0);
		btnSaveConfig.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(btnSaveConfig, 5);
		formData.top = new FormAttachment(0, 0);
		btnSaveGraph.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(btnSaveGraph, 5);
		formData.top = new FormAttachment(0, 0);
		cboLayout.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(cboLayout, 5);
		formData.top = new FormAttachment(0, 0);
		btnExperiment.setLayoutData(formData);
		
		if (topLevel) {
			formData = new FormData();
			formData.right = new FormAttachment(100, 0);
			formData.top = new FormAttachment(0, 0);
			btnClose.setLayoutData(formData);
		}
		
		formData = new FormData();
		formData.left = new FormAttachment(0, 0);
		formData.bottom = new FormAttachment(100, 0);
		formData.top = new FormAttachment(btnSaveConfig, 5);
		formData.right = new FormAttachment(100, 0);
		graphWrapper.setLayoutData(formData);
		
		graphLayout = getLayoutFromName(layouts[defaultLayout]);
		graphPane = new GraphZoomScrollPane<Knoop, Long>(graphWrapper, SWT.NONE, graphLayout, getGraphDimension());
		GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        graphPane.setLayoutData(gridData);
		
        visualizationComposite = graphPane.vv;
        renderContext = visualizationComposite.getRenderContext();
	}
}
