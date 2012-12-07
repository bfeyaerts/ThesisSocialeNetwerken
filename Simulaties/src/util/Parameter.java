package util;

import java.util.ArrayList;
import java.util.Arrays;

import util.validator.Message;
import util.validator.Validator;

public class Parameter {
	protected final String name;
	protected final String label;
	protected final Class<?> cls;
	protected final ArrayList<Validator> validators = new ArrayList<Validator>();
	
	public Parameter(String name, String label, Class<?> cls, Validator... validators) {
		this.name = name;
		this.label = label;
		this.cls = cls;
		for (Validator validator: validators)
			this.validators.add(validator);
	}
	
	public String getName() {
		return name;
	}
	public String getLabel() {
		return label;
	}
	public Class<?> getValueType() {
		return cls;
	}
	public Message[] test(Object value) {
		Message[] test = new Message[0];
		Validator[] validators = this.validators.toArray(new Validator[0]);
		for (Validator validator: validators) {
			Message[] newTest = validator.test(value);
			if (newTest == null)
				continue;
			int origLength = test.length;
			test = Arrays.copyOf(test, test.length + newTest.length);
			for (int i=0; i<newTest.length; i++)
				test[origLength + i] = newTest[i];
		}
		return test;
	}
}
