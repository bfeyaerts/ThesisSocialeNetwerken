package util.form;

import org.eclipse.swt.widgets.Control;

import util.Parameter;
import util.validator.Message;
import util.validator.MessageType;

public abstract class Field {
	final protected Form form;
	final protected Parameter parameter;
	
	protected volatile Message[] messages = null;
	protected volatile MessageType level = MessageType.INFO;
	
	public Field(Form form, Parameter parameter) {
		this.form = form;
		this.parameter = parameter;
	}
	
	protected void valueChanged() {
		messages = parameter.test(getValue());
		
		level = MessageType.INFO;
		String tooltip = null;
		for (Message message: messages) {
			MessageType type = message.getType();
			int comparison = type.compareTo(level);
			if (comparison != 0)
				comparison = comparison / Math.abs(comparison);
			switch (comparison) {
			case 1:
				level = type;
				tooltip = message.getMessage();
				break;
			case 0:
				tooltip += "\r\n" + message.getMessage();
			}
		}
		Control control = getControl();
		control.setToolTipText(tooltip);
		control.setBackground(level.getColor(control.getDisplay()));
		
		form.stateChanged();
	}
	
	public boolean isError() {
		return level.isError();
	}
	
	public String getParameterName() {
		return parameter.getName();
	}
	
	public abstract Object getValue();
	public abstract Control getControl();
	
	public abstract void layout(Control previousControl);
	
	public boolean setFocus() {
		return false;
	}
}
