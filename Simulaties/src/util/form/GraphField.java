package util.form;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import core.Configuratie;
import core.graaf.modellen.GraafModel;

import ui.wizard.NewGraphWizard;
import util.Parameter;

public class GraphField extends Field<Configuratie<GraafModel>> {
	protected final Label label;
	protected final Composite composite;
	protected final Button button;
	protected final Label text;
	
	protected volatile Configuratie<GraafModel> configuratie = null;
	
	public GraphField(final Form form, final Parameter parameter) {
		super(form, parameter);
		label = new Label(form.getBody(), SWT.NONE);
		label.setText(parameter.getLabel());
		
		composite = new Composite(form.getBody(), SWT.NONE);
		
		FormLayout layout = new FormLayout();
		composite.setLayout(layout);
		
		text = new Label(composite, SWT.NONE);
		text.setText("<Geen graafmodel ingesteld>");
		text.setEnabled(false);
		button = new Button(composite, SWT.PUSH);
		button.setText("Instellen...");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				final NewGraphWizard wizard = new NewGraphWizard(form.getBody().getShell(), false);
				wizard.setTitle(parameter.getLabel());
				wizard.addDisposeListener(new DisposeListener() {
					@Override
					public void widgetDisposed(DisposeEvent arg0) {
						setValue(wizard.getConfiguratie());
					}
				});
				if (configuratie != null)
					wizard.setConfiguratie(configuratie);
				wizard.open();
			}
		});
		
		FormData data1 = new FormData();
        data1.left = new FormAttachment(0, 0);
        data1.top = new FormAttachment(button, 0, SWT.CENTER);
        data1.right = new FormAttachment(button);
        text.setLayoutData(data1);
        FormData data2 = new FormData();
        data2.top = new FormAttachment(0, 0);
        data2.right = new FormAttachment(100, 0);
        button.setLayoutData(data2);
	}

	@Override
	public Configuratie<GraafModel> getValue() {
		return configuratie;
	}
	@Override
	public void setValue(Configuratie<GraafModel> configuratie) {
		this.configuratie = configuratie;
		if (configuratie.model != null) {
			text.setEnabled(true);
			text.setText(configuratie.model.getName());
		}
	}

	@Override
	public Control getControl() {
		return composite;
	}

	@Override
	public void layout(Control previousControl) {
		FormData data = new FormData();
		data.left = new FormAttachment(0, 5);
		data.top = new FormAttachment(composite, 0, SWT.CENTER);
		label.setLayoutData(data);
		data = new FormData();
		data.top = previousControl != null ? new FormAttachment(previousControl, 5) : new FormAttachment(0, 5);
		data.left = new FormAttachment(label, 5);
		data.right = new FormAttachment(100, -5);
		composite.setLayoutData(data);
	}

	public boolean setFocus() {
		return composite.setFocus();
	}
}
