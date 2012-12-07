package ui.wizard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public abstract class WizardDialog {
	protected final Shell shell;
	protected final WizardPage[] pages;
	
	protected volatile int currentPage = 0;
	
	private Button buttonPrevious;
	private Button buttonNext;
	private Composite compositeHeader;
	private Composite compositeBody;
	private Composite compositeFooterWrapper;
	private Composite compositeFooter;
	private Label labelPageTitle;
	private GridLayout gridLayout;
	private RowLayout rowLayoutHeader;
	private RowLayout rowLayoutFooter;
	private StackLayout stackLayout;
	
	public WizardDialog(Shell parentShell) {
		this(parentShell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	}
	public WizardDialog(Shell parentShell, int style) {
		shell = new Shell(parentShell, style);
		init();
		pages = createPages();
		for (int i=0; i<pages.length; i++)
			if (pages[i].getTitle() == null)
				pages[i].setTitle("Page " + (i + 1));
		setPage(0);
	}
	
	protected void init() {
		setSize(500, 400);
		
		gridLayout = new GridLayout(1, true);
		gridLayout.horizontalSpacing = gridLayout.marginLeft = gridLayout.marginRight = gridLayout.marginWidth = gridLayout.marginHeight = gridLayout.marginTop = 0;
		gridLayout.verticalSpacing = gridLayout.marginBottom = 10;
		
		shell.setLayout(gridLayout);
		
		compositeHeader = new Composite(shell, SWT.NONE);
		compositeHeader.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL));
		rowLayoutHeader = new RowLayout(SWT.VERTICAL);
		rowLayoutHeader.marginBottom = 10;
		rowLayoutHeader.marginTop = 20;
		compositeHeader.setLayout(rowLayoutHeader);
		labelPageTitle = new Label(compositeHeader, SWT.NONE);
		
		compositeBody = new Composite(shell, SWT.BORDER);
		compositeBody.setLayoutData(new GridData(GridData.CENTER | GridData.GRAB_VERTICAL | GridData.FILL_BOTH));
		compositeBody.setLayout(stackLayout = new StackLayout());
		
		compositeFooterWrapper = new Composite(shell, SWT.NONE);
		compositeFooterWrapper.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL));
		rowLayoutFooter = new RowLayout();
		rowLayoutFooter.marginRight = 10;
		compositeFooterWrapper.setLayout(rowLayoutFooter);
			compositeFooter = new Composite(compositeFooterWrapper, SWT.NONE);
			compositeFooter.setLayout(new FillLayout());
				buttonPrevious = new Button(compositeFooter, SWT.PUSH);
				buttonPrevious.setText("< Vorige");
				buttonPrevious.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent arg0) {
						setPage(currentPage - 1);
					}
				});
			
				buttonNext = new Button(compositeFooter, SWT.PUSH);
				buttonNext.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent arg0) {
						if (buttonNext.getText().equals("Voltooien")) {
							completed();
							shell.dispose();
						} else
							setPage(currentPage + 1);
					}
				});
	}
	
	public void addDisposeListener(DisposeListener listener) {
		shell.addDisposeListener(listener);
	}
	public void removeDisposeListener(DisposeListener listener) {
		shell.addDisposeListener(listener);
	}
	
	public void addShellListener(ShellListener listener) {
		shell.addShellListener(listener);
	}
	public void removeShellListener(ShellListener listener) {
		shell.removeShellListener(listener);
	}
	
	public Composite getBody() {
		return compositeBody;
	}
	protected abstract WizardPage[] createPages();
	
	public void pageStateChanged() {
		buttonNext.setEnabled(pages[currentPage].isPageReady());
	}
	
	protected void cancelled() {}
	protected void completed() {}
	
	public void setPage(int index) {
		if (index < 0 || index >= pages.length)
			throw new IllegalArgumentException();
		
		currentPage = index;
		labelPageTitle.setText(pages[index].getTitle());
		compositeHeader.layout();
		pages[index].pageSelected();
		
		buttonPrevious.setEnabled(index > 0);
		buttonNext.setText(index < pages.length-1 ? "Volgende >" : "Voltooien");
		
		stackLayout.topControl = pages[index].getControl();
		compositeBody.layout();
		pageStateChanged();
		pages[index].getControl().setFocus();
	}
	
	public void setSize(int width, int height) {
		shell.setSize(500, 400);
	}
	public void setTitle(String title) {
		shell.setText(title);
	}
	
	public void open() {
		shell.open();
	}
}
