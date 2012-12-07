package util.validator;


public class Message {
	final protected MessageType type;
	final protected String message;
	
	public Message(String message) {
		this(MessageType.INFO, message);
	}
	public Message(MessageType type, String message) {
		this.type = type;
		this.message = message;
	}
	
	public MessageType getType() {
		return type;
	}
	public String getMessage() {
		return message;
	}
	
	@Override
	public String toString() {
		return "[" + type.name() + "] " + message;
	}
}
