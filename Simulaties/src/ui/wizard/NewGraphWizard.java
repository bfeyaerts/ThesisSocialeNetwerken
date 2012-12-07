package ui.wizard;

import org.eclipse.swt.widgets.Shell;

import core.graaf.Graaf;
import core.graaf.modellen.GraafModel;

import ui.wizard.WizardPage.ModelSetupPage;
import ui.wizard.WizardPage.SelectionWizardPage;

public class NewGraphWizard extends WizardDialog {
	protected SelectionWizardPage<GraafModel> selectie;
	protected ModelSetupPage<GraafModel> setup;
	protected volatile Graaf graaf = null;
	
	public NewGraphWizard(Shell parentShell) {
		super(parentShell);
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

	public Graaf getGraaf() {
		return graaf;
	}
	
	protected void completed() {
		graaf = new Graaf(selectie.getModel(), setup.getSetup());
	}
}
