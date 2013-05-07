package ui.wizard;

import org.eclipse.swt.widgets.Shell;

import core.Configuratie;
import core.graaf.Graaf;
import core.graaf.modellen.GraafModel;

import ui.wizard.WizardPage.ModelSetupPage;
import ui.wizard.WizardPage.SelectionWizardPage;

public class NewGraphWizard extends WizardDialog {
	protected SelectionWizardPage<GraafModel> selectie;
	protected ModelSetupPage<GraafModel> setup;
	protected volatile Graaf graaf = null;
	
	protected boolean generateOnComplete = true;
	
	public NewGraphWizard(Shell parentShell) {
		super(parentShell);
		setTitle("Nieuwe graaf");
	}
	public NewGraphWizard(Shell parentShell, boolean generateOnComplete) {
		super(parentShell);
		this.generateOnComplete = generateOnComplete;
		setTitle("Nieuwe graaf");
	}

	@Override
	protected WizardPage[] createPages() {
		selectie = new SelectionWizardPage<GraafModel>(GraafModel.class, this);
		selectie.setTitle("Graafmodel selecteren");
		selectie.setSelectionLabel("Graafmodel");
		
		setup = new ModelSetupPage<GraafModel>(GraafModel.class, selectie);
		
		return new WizardPage[]{selectie, setup};
	}

	public Configuratie<GraafModel> getConfiguratie() {
		return new Configuratie<GraafModel>(selectie.getModel(), setup.getSetup());
	}
	public void setConfiguratie(Configuratie<GraafModel> configuratie) {
		selectie.setModel(configuratie.model);
		setup.setModel(configuratie.model);
		setup.setSetup(configuratie.setup);
	}
	
	public Graaf getGraaf() {
		return graaf;
	}
	
	protected void completed() {
		if (generateOnComplete)
			graaf = new Graaf(selectie.getModel(), setup.getSetup());
	}
}
