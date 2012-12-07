package util.validator;


public class RangeValidator<N extends Number> extends TypeValidator {
	protected final N start;
	protected final N end;
	
	public RangeValidator(Class<N> type, N start, N end) {
		super(type, false);
		this.start = start;
		this.end = end;
	}

	@Override
	public Message[] test(Object o) {
		Message[] test = super.test(o);
		boolean valid = true;
		if (test != null) for (Message message: test)
			valid &= !message.getType().isError();
		
		if (valid) {
			if (Byte.class.equals(type)) {
				Byte value = (Byte) o;
				valid = start.byteValue() <= value && (end == null || value <= end.byteValue());
			} else if (Double.class.equals(type)) {
				Double value = (Double) o;
				valid = start.doubleValue() <= value && (end == null || value <= end.doubleValue());
			} else if (Float.class.equals(type)) {
				Float value = (Float) o;
				valid = start.floatValue() <= value && (end == null || value <= end.floatValue());
			} else if (Integer.class.equals(type)) {
				Integer value = (Integer) o;
				valid = start.intValue() <= value && (end == null || value <= end.intValue());
			} else if (Long.class.equals(type)) {
				Long value = (Long) o;
				valid = start.longValue() <= value && (end == null || value <= end.longValue());
			} else if (Short.class.equals(type)) {
				Short value = (Short) o;
				valid = start.shortValue() <= value && (end == null || value <= end.shortValue());
			}
			if (!valid)
				test = new Message[]{new Message(MessageType.ERROR, 
						end == null ? ("Parameter must be greater than or equal to " + start + ".") : ("Parameter must be in the range " + start + " to " + end + "."))};
		}
		return test;
	}

}
