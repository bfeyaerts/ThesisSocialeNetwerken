package ui.wizard;

import java.util.Arrays;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import core.experiment.Experiment;
import core.graaf.Graaf;
import core.infecties.diagnose.Diagnose;
import core.infecties.evolutie.Evolutie;
import core.infecties.propagatie.Propagatie;

import ui.wizard.WizardPage.ModelSetupPage;
import ui.wizard.WizardPage.SelectionWizardPage;
import util.Parameter;
import util.form.Form;
import util.form.FormStateListener;

public class NewExperimentWizard extends WizardDialog {
	protected final Graaf graaf;
	protected SelectionWizardPage<Evolutie> evoSelectie;
	protected ModelSetupPage<Evolutie> evoSetup;
	protected SelectionWizardPage<Propagatie> propSelectie;
	protected ModelSetupPage<Propagatie> propSetup;
	
	protected volatile Experiment experiment = null;
	
	public NewExperimentWizard(Shell parentShell, Graaf graaf) {
		super(parentShell);
		this.graaf = new Graaf(graaf);
		setTitle("Nieuw experiment");
	}

	@Override
	protected WizardPage[] createPages() {
		evoSelectie = new SelectionWizardPage<Evolutie>(Evolutie.class, this);
		evoSelectie.setTitle("Evolutiemodel selecteren");
		evoSelectie.setSelectionLabel("Evolutiemodel");
		evoSetup = new ModelSetupPage<Evolutie>(Evolutie.class, evoSelectie) {
			Enum<? extends Diagnose>[] diagnoses = null;
			
			HashMap<Diagnose, java.awt.Color> diagnoseColors = null;
			Enum<? extends Diagnose> defaultDiagnose = null;
			
			@Override
			protected void setModel(Evolutie model) {
				super.setModel(model);
				
				diagnoses = model.possibleDiagnoses().getEnumConstants();
				defaultDiagnose = diagnoses[0];
				diagnoseColors = new HashMap<Diagnose, java.awt.Color>();
			}
			
			protected void setLabelColor(Label label, java.awt.Color awt) {
				org.eclipse.swt.graphics.Color swt = new org.eclipse.swt.graphics.Color(label.getDisplay(), awt.getRed(), awt.getGreen(), awt.getBlue());
				label.setBackground(swt);
			}
			
			@Override
			protected void initComponent() {
				Control[] children = composite.getChildren();
				for (Control child: children)
					child.dispose();
				
				composite.setLayout(new FormLayout());
				
				if (model != null) {
					Parameter[] parameters = model.getParameters();
					form = new Form(composite, parameters);
					form.addFormStateListener(new FormStateListener() {
						@Override
						public void stateChanged(Form form) {
							wizard.pageStateChanged();
						}
					});
					
					Label lColors = new Label(composite, SWT.NONE);
					lColors.setText("Kleuren diagnoses");
					Composite colors = new Composite(composite, SWT.NONE);
					colors.setLayout(new FormLayout());
					Control previousControl = null;
					
					for (int i=0; i<diagnoses.length; i++) {
						final int index = i;
						final Label label = new Label(colors, SWT.BORDER);
						label.setText(diagnoses[index].toString());
						setLabelColor(label, ((Diagnose) diagnoses[index]).getDefaultColor());
						
						Button bChange = new Button(colors, SWT.PUSH);
						bChange.setText("Change");
						bChange.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent arg0) {
								ColorDialog dialog = new ColorDialog(wizard.shell);
								RGB rgb = label.getBackground().getRGB();
								dialog.setRGB(rgb);
								if ((rgb = dialog.open()) == null)
									return;
								java.awt.Color awt = new java.awt.Color(rgb.red, rgb.green, rgb.blue);
								diagnoseColors.put((Diagnose) diagnoses[index], awt);
								setLabelColor(label, awt);
							}
						});
						
						Button bReset = new Button(colors, SWT.PUSH);
						bReset.setText("Reset");
						bReset.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent arg0) {
								diagnoseColors.remove((Diagnose) diagnoses[index]);
								setLabelColor(label, ((Diagnose) diagnoses[index]).getDefaultColor());
							}
						});
						
						FormData data = new FormData();
						data.left = new FormAttachment(0, 5);
						data.right = new FormAttachment(bChange, -5);
						data.top = new FormAttachment(bChange, 0, SWT.CENTER);
						label.setLayoutData(data);
						
						data = new FormData();
						data.top = previousControl != null ? new FormAttachment(previousControl, 2) : new FormAttachment(0, 0);
						data.right = new FormAttachment(bReset, -5);
						bChange.setLayoutData(data);
						
						data = new FormData();
						data.top = previousControl != null ? new FormAttachment(previousControl, 2) : new FormAttachment(0, 0);
						data.right = new FormAttachment(100, 0);
						bReset.setLayoutData(data);
						
						previousControl = bChange;
					}
					
					Label lCombo = new Label(composite, SWT.NONE);
					lCombo.setText("Standaarddiagnose");
					final Combo combo = new Combo(composite, SWT.SINGLE | SWT.BORDER);
					for (Enum<? extends Diagnose> diagnose: diagnoses)
						combo.add(diagnose.toString());
					combo.select(0);
					combo.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent arg0) {
							defaultDiagnose = diagnoses[combo.getSelectionIndex()];
						}
					});
							
					FormData data = new FormData();
					data.left = new FormAttachment(0, 5);
					data.top = new FormAttachment(colors, 5, SWT.TOP);
					lColors.setLayoutData(data);
					data = new FormData();
					data.top = form.getFinalControl() != null ? new FormAttachment(form.getFinalControl(), 5) : new FormAttachment(0, 5);
					data.left = new FormAttachment(lColors, 5);
					data.right = new FormAttachment(100, -5);
					colors.setLayoutData(data);
					
					data = new FormData();
					data.left = new FormAttachment(0, 5);
					data.top = new FormAttachment(combo, 0, SWT.CENTER);
					lCombo.setLayoutData(data);
					data = new FormData();
					data.top = new FormAttachment(colors, 5);
					data.left = new FormAttachment(lCombo, 5);
					data.right = new FormAttachment(100, -5);
					combo.setLayoutData(data);
				}
			}
			
			@Override
			public Object[] getSetup() {
				if (model != null) {
					Parameter[] parameters = model.getParameters();
					Object [] values = new Object[parameters.length + 2];
					for (int i=0; i<parameters.length; i++)
						values[i] = form.getValue(parameters[i]);
					
					values[parameters.length] = diagnoseColors;
					values[parameters.length + 1] = defaultDiagnose;
					
					return values;
				} else
					return null;
			}
		};
		propSelectie = new SelectionWizardPage<Propagatie>(Propagatie.class, this);
		propSelectie.setTitle("Propagatiemodel selecteren");
		propSelectie.setSelectionLabel("Propagatiemodel");
		propSetup = new ModelSetupPage<Propagatie>(Propagatie.class, propSelectie);
		
		return new WizardPage[]{evoSelectie, evoSetup, propSelectie, propSetup};
	}
	
	public Experiment getExperiment() {
		return experiment;
	}
	
	@Override
	protected void completed() {
		Object[] setupEvolutie = evoSetup.getSetup();
		
		@SuppressWarnings("unchecked")
		HashMap<Diagnose, java.awt.Color> diagnoseColors = (HashMap<Diagnose, java.awt.Color>) setupEvolutie[setupEvolutie.length - 2];
		Diagnose defaultDiagnose = (Diagnose) setupEvolutie[setupEvolutie.length - 1];
		
		setupEvolutie = Arrays.copyOf(setupEvolutie, setupEvolutie.length - 2);
				
		experiment = new Experiment(graaf, evoSelectie.getModel(), setupEvolutie, diagnoseColors, defaultDiagnose, propSelectie.getModel(), propSetup.getSetup());
	}
}
