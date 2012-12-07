package ui.wizard;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import util.Parameter;
import util.form.Form;
import util.form.FormStateListener;

import core.Model;

public abstract class WizardPage {
	public static class SelectionWizardPage<M extends Model> extends WizardPage {
		protected final Class<M> type;
		
		private Label label;
		private Combo combo;
		
		protected ArrayList<M> modellen;
		protected volatile M selectedModel = null;
		protected volatile String labelText = "Model";
		
		public SelectionWizardPage(Class<M> type, WizardDialog dialog) {
			super(dialog);
			this.type = type;
		}
		public SelectionWizardPage(Class<M> type, WizardDialog dialog, int style) {
			super(dialog, style);
			this.type = type;
		}
		
		@Override
		public boolean isPageReady() {
			boolean ready = true;
			int index = combo.getSelectionIndex();
			ready &= (index >= 0) && (index < modellen.size());
			if (ready)
				selectedModel = modellen.get(index);
			return ready;
		}
		
		public void setSelectionLabel(String text) {
			labelText = text;
		}
		
		@Override
		protected void initComponent() {
			if (label != null)
				return;
			composite.setLayout(new FormLayout());
			
			label = new Label(composite, SWT.NONE);
			label.setText(labelText + ":");
			combo = new Combo(composite, SWT.READ_ONLY);
			modellen = new ArrayList<M>();
			
			final String pkg = type.getPackage().getName();
			new File("bin/" + pkg.replace('.', '/')).listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File file, String filename) {
					if (filename.endsWith(".class")) {
						String classname = pkg + "." + filename.substring(0, filename.length() - 6);
						try {
							Class<?> cls = Class.forName(classname);
							if (type.isAssignableFrom(cls)) {
								M model = type.cast(cls.newInstance());
								combo.add(model.getName());
								modellen.add(model);
							}
						} catch (Exception e) {}
					}
					return false;
				}
			});
			if (combo.getItemCount() == 1)
				combo.select(0);
			combo.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					wizard.pageStateChanged();
				}
			});
			
			FormData data = new FormData();
			data.left = new FormAttachment(0, 5);
			data.top = new FormAttachment(combo, 5, SWT.CENTER);
			label.setLayoutData(data);
			data = new FormData();
			data.left = new FormAttachment(label, 5);
			data.right = new FormAttachment(100, -5);
			data.top = new FormAttachment(0, 5);
			combo.setLayoutData(data);
		}
		
		@Override
		protected void pageSelected() {
			initComponent();
			composite.layout();
			combo.setFocus();
		}
		
		public M getModel() {
			return selectedModel;
		}
	}
	
	public static class ModelSetupPage<M extends Model> extends WizardPage {
		protected final Class<M> type;
		protected final SelectionWizardPage<M> selectie;
		
		protected volatile M model = null;
		protected volatile Form form = null;
		
		public ModelSetupPage(Class<M> type, SelectionWizardPage<M> selectie) {
			super(selectie.wizard);
			this.type = type;
			this.selectie = selectie;
			setTitle("Instellingen");
		}
		public ModelSetupPage(Class<M> type, SelectionWizardPage<M> selectie, int style) {
			super(selectie.wizard, style);
			this.type = type;
			this.selectie = selectie;
			setTitle("Instellingen");
		}
		
		@Override
		public boolean isPageReady() {
			return form != null && form.isComplete();
		}
		@Override
		protected void initComponent() {
			Control[] children = composite.getChildren();
			for (Control child: children)
				child.dispose();
			
			composite.setLayout(new FormLayout());
			
			if (model != null) {
				Parameter[] parameters = model.getParameters();
				form = new Form(composite, parameters);
				form.addFormStateListener(new FormStateListener() {
					@Override
					public void stateChanged(Form form) {
						wizard.pageStateChanged();
					}
				});
			}
		}
		@Override
		protected void pageSelected() {
			M model = selectie.getModel();
			if (model.equals(this.model))
				return;
			setModel(model);
			initComponent();
			composite.layout();
			form.setFocus();
		}
		protected void setModel(M model) {
			this.model = model;
		}
		
		public Object[] getSetup() {
			if (model != null) {
				Parameter[] parameters = model.getParameters();
				Object [] values = new Object[parameters.length];
				for (int i=0; i<parameters.length; i++)
					values[i] = form.getValue(parameters[i]);
				return values;
			} else
				return null;
		}
	}
	
	protected final WizardDialog wizard;
	protected final Composite composite;
	
	protected String title = null;
	
	public WizardPage(WizardDialog wizard) {
		this(wizard, SWT.NONE);
	}
	public WizardPage(WizardDialog wizard, int style) {
		this.wizard = wizard;
		this.composite = new Composite(wizard.getBody(), style);
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public abstract boolean isPageReady();
	
	protected abstract void initComponent();
	
	protected void pageSelected() {
		initComponent();
	}
	
	public Control getControl() {
		return composite;
	}
}
