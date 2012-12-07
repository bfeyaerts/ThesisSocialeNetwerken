package ui.panels;

import java.awt.Paint;
import java.util.ArrayList;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import ui.Programma;
import ui.wizard.NewGrafiekWizard;
import util.xml.XMLWriter.XMLElementWriter;

import core.experiment.Experiment;
import core.experiment.grafiek.Grafiek;
import core.graaf.Knoop;
import core.infecties.diagnose.Diagnose;
import edu.uci.ics.jung.visualization.GraphMouseListener;
import edu.uci.ics.jung.visualization.event.MouseEvent;

public class ExperimentPanel extends AbstractPanel {
	protected final Experiment experiment;
	
	protected final ArrayList<GrafiekPanel> grafiekPanels = new ArrayList<GrafiekPanel>();
	
	private Button btnSave;
	private Button btnPropagate;
	private Button btnReset;
	private Button btnClose;
	private GraafPanel graafPanel;
	private Sash sashV;
	private Composite grafiekWrapper;
	private ToolBar grafiekToolbar;
	private CTabFolder grafiekFolder;
	
	public ExperimentPanel(Experiment experiment, Composite parent, int style) {
		super(parent, style);
		this.experiment = experiment;
		initComponent(composite);
	}
	
	public void addGrafiek() {
		final NewGrafiekWizard wizard = new NewGrafiekWizard(getBody().getShell(), experiment);
		wizard.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				Grafiek grafiek = wizard.getGrafiek();
				if (grafiek != null) {
					final CTabItem tabItem = new CTabItem(grafiekFolder, SWT.NONE);
					tabItem.setText("Grafiek");
					GrafiekPanel grafiekPanel = new GrafiekPanel(experiment, grafiek, grafiekFolder, SWT.BORDER);
					grafiekPanels.add(grafiekPanel);
					tabItem.setControl(grafiekPanel.getBody());
					grafiekFolder.setSelection(tabItem);
					grafiekPanel.addDisposeListener(new DisposeListener() {
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
	
	@Override
	protected void initComponent(final Composite composite) {
		composite.setLayout(new FormLayout());
		
		btnSave = new Button(composite, SWT.PUSH);
		btnSave.setText("Experiment opslaan");
		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				Programma.saveFile(composite.getShell(), "experiment", "config", new XMLElementWriter() {
					@Override
					public void writeElementBody(XMLEventWriter eventWriter) throws XMLStreamException {
						experiment.getGraaf().createElement(eventWriter, "graaf", true);
						experiment.createElement(eventWriter, "experiment");
					}
				});
			}
		});
		
		btnPropagate = new Button(composite, SWT.PUSH);
		btnPropagate.setText("Propageren");
		btnPropagate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				experiment.stap();
				graafPanel.refresh();
				
				for (GrafiekPanel grafiekPanel: grafiekPanels)
					grafiekPanel.refresh();
			}
		});
		btnReset = new Button(composite, SWT.PUSH);
		btnReset.setText("Resetten");
		btnReset.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				experiment.reset();
				graafPanel.refresh();
				
				for (GrafiekPanel grafiekPanel: grafiekPanels)
					grafiekPanel.reset();
			}
		});
		
		btnClose = new Button(composite, SWT.PUSH);
		btnClose.setText("Sluiten");
		btnClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				composite.dispose();
				try {
					ExperimentPanel.this.finalize();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		});
		
		graafPanel = new GraafPanel(experiment.getGraaf(), composite, SWT.BORDER);
		graafPanel.setVertexFillPaintTransformer(new Transformer<Knoop, Paint>(){
			@Override
			public Paint transform(Knoop knoop) {
				Diagnose diagnose = (Diagnose) knoop.getDiagnose();
				return experiment.getColor(diagnose);
			}
		});
		graafPanel.addGraphMouseListener(new GraphMouseListener<Knoop>() {
			@SuppressWarnings("rawtypes")
			@Override
			public void graphClicked(Knoop knoop, MouseEvent event) {
				experiment.getEvolutieModel().next(knoop);
				graafPanel.refresh();
			}
			@SuppressWarnings("rawtypes")
			@Override
			public void graphPressed(Knoop knoop, MouseEvent event) {}
			@SuppressWarnings("rawtypes")
			@Override
			public void graphReleased(Knoop knoop, MouseEvent event) {}
        });
		
		sashV = new Sash(composite, SWT.HORIZONTAL | SWT.SMOOTH);
		
		grafiekWrapper = new Composite(composite, SWT.BORDER);
		grafiekWrapper.setLayout(new FormLayout());
		
		grafiekToolbar = new ToolBar(grafiekWrapper, SWT.NONE);
		ToolItem tiGrafiekToevoegen = new ToolItem(grafiekToolbar, SWT.PUSH);
		tiGrafiekToevoegen.setText("Grafiek toevoegen");
		tiGrafiekToevoegen.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				addGrafiek();
			}
		});
		grafiekToolbar.pack();
		grafiekFolder = new CTabFolder(grafiekWrapper, SWT.BORDER);
		
		final FormLayout formLayout = new FormLayout();
		composite.setLayout(formLayout);
		
		FormData formData = new FormData();
		formData.left = new FormAttachment(0, 0);
		formData.top = new FormAttachment(0, 0);
		btnSave.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(btnSave, 5);
		formData.top = new FormAttachment(0, 0);
		btnPropagate.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(btnPropagate, 5);
		formData.top = new FormAttachment(0, 0);
		btnReset.setLayoutData(formData);
		
		formData = new FormData();
		formData.right = new FormAttachment(100, 0);
		formData.top = new FormAttachment(0, 0);
		btnClose.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(0, 0);
		formData.bottom = new FormAttachment(sashV, 0);
		formData.top = new FormAttachment(btnSave, 5);
		formData.right = new FormAttachment(100, 0);
		graafPanel.getBody().setLayoutData(formData);

		final int limit = 20, percent = 50;
		final FormData sashData = new FormData ();
		sashData.top = new FormAttachment (percent, 0);
		sashData.left = new FormAttachment (0, 0);
		sashData.right = new FormAttachment (100, 0);
		sashV.setLayoutData (sashData);
		sashV.addSelectionListener(new SelectionAdapter () {
			public void widgetSelected(SelectionEvent e) {
				Rectangle sashRect = sashV.getBounds();
				Rectangle shellRect = composite.getClientArea();
				int right = shellRect.height - sashRect.height - limit;
				e.y = Math.max (Math.min (e.y, right), limit);
				if (e.y != sashRect.y)  {
					sashData.top = new FormAttachment (0, e.y);
					composite.layout ();
				}
			}
		});
		
		formData = new FormData ();
		formData.top = new FormAttachment (sashV, 0);
		formData.right = new FormAttachment (100, 0);
		formData.left = new FormAttachment (0, 0);
		formData.bottom = new FormAttachment (100, 0);
		grafiekWrapper.setLayoutData (formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(0, 0);
		formData.top = new FormAttachment(0, 0);
		formData.right = new FormAttachment(100, 0);
		grafiekToolbar.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(0, 0);
		formData.bottom = new FormAttachment(100, 0);
		formData.top = new FormAttachment(grafiekToolbar, 5);
		formData.right = new FormAttachment(100, 0);
		grafiekFolder.setLayoutData(formData);
	}
}
