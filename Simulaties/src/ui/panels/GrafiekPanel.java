package ui.panels;

import java.io.File;
import java.io.PrintWriter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.jfree.chart.JFreeChart;
import org.jfree.experimental.chart.swt.ChartComposite;

import core.experiment.Experiment;
import core.experiment.grafiek.Grafiek;

public class GrafiekPanel extends AbstractPanel {
	protected enum View {GRAFIEK, DATA}
	
	protected final Experiment experiment;
	protected final Grafiek grafiek;
	
	private Button btnExport;
	private Button btnReset;
	private Button buttonClose;
	
	private Composite grafiekWrapper;
	private JFreeChart jFreeChart;
	private ChartComposite chartComposite;
	
	public GrafiekPanel(Experiment experiment, Grafiek grafiek, Composite parent, int style) {
		super(parent, style);
		this.experiment = experiment;
		this.grafiek = grafiek;
		initComponent(composite);
	}
	
	public void reset() {
		grafiek.reset();
		refresh();
	}
	
	public void refresh() {
		grafiek.update();
		
		jFreeChart.fireChartChanged();
		chartComposite.layout();
	}
	
	@Override
	protected void initComponent(final Composite composite) {
		composite.setLayout(new FormLayout());
		
		btnExport = new Button(composite, SWT.PUSH);
		btnExport.setText("Data exporteren");
		btnExport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				FileDialog dialog = new FileDialog(composite.getShell(), SWT.SAVE);
				dialog.setFilterNames(new String [] {"CSV-bestand"});
				dialog.setFilterExtensions(new String [] {"*.csv"});
				dialog.setFileName("data");
				
				final String filename = dialog.open();
				if (filename != null) {
					File file = new File(filename);
					if (file.exists()) {
						MessageBox messageBox = new MessageBox(composite.getShell(), SWT.APPLICATION_MODAL | SWT.YES | SWT.NO);
						messageBox.setMessage("Het bestand '" + filename + "' bestaat reeds.\n\rBent u zeker dat u het wilt overschrijven?");
						messageBox.setText("Bestand overschrijven?");
						if (messageBox.open() != SWT.YES)
							return;
					}
					try {
						PrintWriter out = new PrintWriter(filename);
						grafiek.writeData(out);
						out.close();
				    } catch (Exception ex) {
				      ex.printStackTrace();
				    }
				}
			}
		});
		btnReset = new Button(composite, SWT.PUSH);
		btnReset.setText("Reset");
		btnReset.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				reset();
			}
		});
		
		buttonClose = new Button(composite, SWT.PUSH);
		buttonClose.setText("Sluiten");
		buttonClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
			}
		});
		
		grafiekWrapper = new Composite(composite, SWT.NONE);
		grafiekWrapper.setLayout(new GridLayout());
		jFreeChart = grafiek.getChart();
		chartComposite = new ChartComposite(grafiekWrapper, SWT.NONE, jFreeChart, true);
		chartComposite.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
		
		FormData formData = new FormData();
		formData.left = new FormAttachment(0, 0);
		formData.top = new FormAttachment(0, 0);
		btnExport.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(btnExport, 0);
		formData.top = new FormAttachment(0, 0);
		btnReset.setLayoutData(formData);
		
		formData = new FormData();
		formData.top = new FormAttachment(0, 0);
		formData.right = new FormAttachment(100, 0);
		buttonClose.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(0, 0);
		formData.bottom = new FormAttachment(100, 0);
		formData.top = new FormAttachment(btnExport, 5);
		formData.right = new FormAttachment(100, 0);
		grafiekWrapper.setLayoutData(formData);
	}
}
