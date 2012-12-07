package util;

public class ParameterParser {
	public static Object getObject(String type, String value) {
		try {
			return getObject(Class.forName(type), value);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
	public static Object getObject(Class<?> type, String value) {
		if (String.class.equals(type))
			return value;
		if (Boolean.class.equals(type))
			return Boolean.parseBoolean(value);
		if (Byte.class.equals(type))
			return Byte.parseByte(value);
		if (Double.class.equals(type))
			return Double.parseDouble(value);
		if (Float.class.equals(type))
			return Float.parseFloat(value);
		if (Integer.class.equals(type))
			return Integer.parseInt(value);
		if (Long.class.equals(type))
			return Long.parseLong(value);
		if (Short.class.equals(type))
			return Short.parseShort(value);
		return null;
	}
}
