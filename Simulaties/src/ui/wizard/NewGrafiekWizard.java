package ui.wizard;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.swt.widgets.Shell;

import core.experiment.Experiment;
import core.experiment.grafiek.Grafiek;

import ui.wizard.WizardPage.SelectionWizardPage;

public class NewGrafiekWizard extends WizardDialog {
	protected final Experiment experiment;
	protected SelectionWizardPage<Grafiek> selectie;
	protected volatile Grafiek grafiek = null;
	
	public NewGrafiekWizard(Shell parentShell, Experiment experiment) {
		super(parentShell);
		this.experiment = experiment;
		setTitle("Nieuwe grafiek");
	}

	@Override
	protected WizardPage[] createPages() {
		selectie = new SelectionWizardPage<Grafiek>(Grafiek.class, this);
		selectie.setTitle("Grafiektype selecteren");
		selectie.setSelectionLabel("Grafiek");
		
		return new WizardPage[]{selectie};
	}

	public Grafiek getGrafiek() {
		return grafiek;
	}
	
	protected void completed() {
		try {
			grafiek = selectie.getModel().getClass().getConstructor(new Class<?>[]{Experiment.class}).newInstance(new Object[]{experiment});
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
