package util.validator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public enum MessageType {
	INFO(SWT.COLOR_LIST_BACKGROUND), WARNING(SWT.COLOR_DARK_YELLOW), ERROR(SWT.COLOR_RED);
	
	final private int id;
	
	private MessageType(int id) {
		this.id = id;
	}
	
	public Color getColor(Display display) {
		return display.getSystemColor(id);
	}
	
	public boolean isError() {
		return this == MessageType.ERROR;
	}
}