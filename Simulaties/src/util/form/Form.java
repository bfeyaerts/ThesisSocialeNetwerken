package util.form;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import util.Parameter;

public class Form {
	final protected Composite parent;
	final protected Parameter[] parameters;
	
	final protected HashMap<String, Field> fields = new HashMap<String, Field>();
	final protected ArrayList<FormStateListener> formStateListeners = new ArrayList<FormStateListener>();
	
	protected Control finalControl = null;
	
	public Form(Composite parent, Parameter[] parameters) {
		this.parent = parent;
		this.parameters = parameters;
		
		Control previousControl = null;
		for (final Parameter parameter: parameters) {
			Class<?> valueType = parameter.getValueType();
			Field field;
			
			if (valueType.equals(Boolean.class))
				field = new BooleanField(this, parameter);
			else if (Number.class.isAssignableFrom(valueType))
				field = new NumberField(this, parameter);
			else
				field = new TextField(this, parameter);
			fields.put(parameter.getName(), field);
			field.layout(previousControl);
			previousControl = field.getControl();
		}
		finalControl = previousControl;
	}
	
	public Composite getBody() {
		return parent;
	}
	public Control getFinalControl() {
		return finalControl;
	}
	
	public boolean isComplete() {
		boolean complete = true;
		Field[] fields = this.fields.values().toArray(new Field[0]);
		for (Field field: fields)
			complete &= !field.isError();
		return complete;
	}
	
	public Object getValue(Parameter parameter) {
		return fields.get(parameter.getName()).getValue();
	}
	
	public void stateChanged() {
		FormStateListener[] listeners = formStateListeners.toArray(new FormStateListener[0]);
		for (FormStateListener listener: listeners)
			listener.stateChanged(this);
	}
	public void addFormStateListener(FormStateListener listener) {
		formStateListeners.add(listener);
	}
	public void removeFormStateListener(FormStateListener listener) {
		formStateListeners.remove(listener);
	}
	
	public boolean setFocus() {
		if (fields.isEmpty())
			return false;
		return fields.get(parameters[0].getName()).setFocus();
	}
}
