package util.form;

import util.Parameter;

public class NumberField extends TextField {
	
	public NumberField(Form form, Parameter parameter) {
		super(form, parameter);
		
		Class<?> type = parameter.getValueType();
		
		if (Byte.class.equals(type)) {
			setConvertor(new Convertor<String>() {
				public Object convert(String text) throws Exception {
					return Byte.parseByte(text);
				}
			});
		} else if (Double.class.equals(type)) {
			setConvertor(new Convertor<String>() {
				public Object convert(String text) throws Exception {
					return Double.parseDouble(text);
				}
			});
		} else if (Float.class.equals(type)) {
			setConvertor(new Convertor<String>() {
				public Object convert(String text) throws Exception {
					return Float.parseFloat(text);
				}
			});
		} else if (Integer.class.equals(type)) {
			setConvertor(new Convertor<String>() {
				public Object convert(String text) throws Exception {
					return Integer.parseInt(text);
				}
			});
		} else if (Long.class.equals(type)) {
			setConvertor(new Convertor<String>() {
				public Object convert(String text) throws Exception {
					return Long.parseLong(text);
				}
			});
		} else if (Short.class.equals(type)) {
			setConvertor(new Convertor<String>() {
				public Object convert(String text) throws Exception {
					return Short.parseShort(text);
				}
			});
		}
	}

}
