package util.validator;


public class TypeValidator implements Validator {
	protected final Class<?> type;
	protected final boolean acceptNull;
	
	public TypeValidator(Class<?> type) {
		this(type, false);
	}
	public TypeValidator(Class<?> type, boolean acceptNull) {
		this.type = type;
		this.acceptNull = acceptNull;
	}
	
	@Override
	public Message[] test(Object o) {
		if (o != null) {
			if (! type.isAssignableFrom(o.getClass()))
				return new Message[]{new Message(MessageType.ERROR, "Incompatible class.")};
		} else {
			if (! acceptNull)
				return new Message[]{new Message(MessageType.ERROR, "Parameter must not be null.")};
		}
		return null;
	}

}
