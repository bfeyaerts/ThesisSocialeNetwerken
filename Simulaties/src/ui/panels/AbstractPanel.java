package ui.panels;

import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;

public abstract class AbstractPanel {
	protected final Composite composite;
	
	public AbstractPanel(Composite parent, int style) {
		composite = new Composite(parent, style);
	}
	
	public Composite getBody() {
		return composite;
	}
	
	public void addDisposeListener(DisposeListener listener) {
		composite.addDisposeListener(listener);
	}
	
	protected abstract void initComponent(final Composite composite);
}
