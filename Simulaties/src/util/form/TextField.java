package util.form;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import util.Parameter;

public class TextField extends Field<Object> {
	protected final Label label;
	protected final Text text;
	
	protected Convertor<String> convertor = null;

	public TextField(Form form, Parameter parameter) {
		super(form, parameter);
		label = new Label(form.getBody(), SWT.NONE);
		label.setText(parameter.getLabel());
		text = new Text(form.getBody(), SWT.BORDER);
		
		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				TextField.this.valueChanged();
			}
		});
	}

	public void setConvertor(Convertor<String> convertor) {
		this.convertor = convertor;
	}
	
	@Override
	public Object getValue() {
		Object value = text.getText();
		if (convertor != null)
			try {
				value = convertor.convert((String) value);
			} catch (Exception e) {}
		return value;
	}
	@Override
	public void setValue(Object value) {
		text.setText("" + value);
	}

	@Override
	public Control getControl() {
		return text;
	}

	@Override
	public void layout(Control previousControl) {
		FormData data = new FormData();
		data.left = new FormAttachment(0, 5);
		data.top = new FormAttachment(text, 0, SWT.CENTER);
		label.setLayoutData(data);
		data = new FormData();
		data.top = previousControl != null ? new FormAttachment(previousControl, 5) : new FormAttachment(0, 5);
		data.left = new FormAttachment(label, 5);
		data.right = new FormAttachment(100, -5);
		text.setLayoutData(data);
	}

	public boolean setFocus() {
		return text.setFocus();
	}
}
