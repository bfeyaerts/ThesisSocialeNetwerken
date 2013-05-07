package util.form;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;

import util.Parameter;

public class BooleanField extends Field<Boolean> {
	protected final Button button;

	public BooleanField(Form form, Parameter parameter) {
		super(form, parameter);
		
		button = new Button(form.getBody(), SWT.CHECK);
		button.setText(parameter.getLabel());
	}

	@Override
	public Boolean getValue() {
		return button.getSelection();
	}
	@Override
	public void setValue(Boolean value) {
		button.setSelection(value);
	}

	@Override
	public Control getControl() {
		return button;
	}

	@Override
	public void layout(Control previousControl) {
		FormData data = new FormData();
		data.top = previousControl != null ? new FormAttachment(previousControl, 5) : new FormAttachment(0, 5);
		data.left = new FormAttachment(0, 5);
		data.right = new FormAttachment(100, -5);
		button.setLayoutData(data);
	}

	public boolean setFocus() {
		return button.setFocus();
	}
}
