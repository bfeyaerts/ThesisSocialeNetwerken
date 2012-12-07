package ui;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import core.experiment.Experiment;
import core.graaf.Graaf;

import ui.panels.ExperimentPanel;
import ui.panels.GraafPanel;
import ui.wizard.NewGraphWizard;
import util.xml.XMLWriter;
import util.xml.XMLWriter.XMLElementWriter;

public class Programma {
	public static CTabFolder tabFolder;
	
	public static void main(String[] args) {
		Display display = new Display();
		final Shell shell = new Shell(display, SWT.SHELL_TRIM);
		
		shell.setText("Thesis sociale netwerken");
		
		GridLayout gridLayout = new GridLayout(1, true);
		gridLayout.marginBottom = gridLayout.marginTop = 0;
		gridLayout.marginLeft = gridLayout.marginRight = 0;
		shell.setLayout(gridLayout);
		
		tabFolder = new CTabFolder(shell, SWT.BORDER);
		GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        tabFolder.setLayoutData(gridData);
		
		Menu menubar = new Menu(shell, SWT.BAR);
		MenuItem miBestand = new MenuItem(menubar, SWT.CASCADE);
		miBestand.setText("Bestand");
		
		Menu menuBestand = new Menu(miBestand);
		MenuItem miNieuweGraaf = new MenuItem(menuBestand, SWT.CASCADE);
		miNieuweGraaf.setText("Nieuwe graaf");
		miNieuweGraaf.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				final NewGraphWizard wizard = new NewGraphWizard(shell);
				wizard.addDisposeListener(new DisposeListener() {
					@Override
					public void widgetDisposed(DisposeEvent arg0) {
						Graaf graaf = wizard.getGraaf();
						if (graaf != null) {
							final CTabItem tabItem = new CTabItem(tabFolder, SWT.NONE);
							tabItem.setText("Graaf");
							GraafPanel graafPanel = new GraafPanel(graaf, tabFolder, SWT.BORDER);
							tabItem.setControl(graafPanel.getBody());
							tabFolder.setSelection(tabItem);
							graafPanel.addDisposeListener(new DisposeListener() {
								@Override
								public void widgetDisposed(DisposeEvent arg0) {
									tabItem.dispose();
								}
							});
						}
					}
				});
				wizard.open();
			}
		});
		MenuItem miGraafOpenen = new MenuItem(menuBestand, SWT.CASCADE);
		miGraafOpenen.setText("Openen");
		miGraafOpenen.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				openFile(shell);
			}
		});
		
		miBestand.setMenu(menuBestand);
		shell.setMenuBar(menubar);
		
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
	
	public static void saveFile(Shell shell, String defaultName, String documentElement, XMLElementWriter eventWriter) {
		FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		dialog.setFilterNames(new String [] {"XML-bestand"});
		dialog.setFilterExtensions(new String [] {"*.xml"});
		dialog.setFileName(defaultName);
		
		final String filename = dialog.open();
		if (filename != null) {
			File file = new File(filename);
			if (file.exists()) {
				MessageBox messageBox = new MessageBox(shell, SWT.APPLICATION_MODAL | SWT.YES | SWT.NO);
				messageBox.setMessage("Het bestand '" + filename + "' bestaat reeds.\n\rBent u zeker dat u het wilt overschrijven?");
				messageBox.setText("Bestand overschrijven?");
				if (messageBox.open() != SWT.YES)
					return;
			}
			try {
				XMLWriter.saveConfig(filename, eventWriter);
		    } catch (Exception ex) {
		      ex.printStackTrace();
		    }
		}
	}
	
	public static void openFile(Shell shell) {
		FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		dialog.setFilterNames(new String [] {"XML-bestand"});
		dialog.setFilterExtensions(new String [] {"*.xml"});
		
		final String filename = dialog.open();
		if (filename != null && new File(filename).exists()) {
			try {
				
				DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
				documentBuilderFactory.setNamespaceAware(true); // never forget this!
				DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
				Document document = builder.parse(filename);
				
				XPathFactory xPathFactory = XPathFactory.newInstance();
				XPath xpath = xPathFactory.newXPath();
				
				Element graafElement = (Element) xpath.evaluate("graaf", document.getDocumentElement(), XPathConstants.NODE);
				Graaf graaf = Graaf.readElement(graafElement, xpath);
				
				Element experimentElement = (Element) xpath.evaluate("experiment", document.getDocumentElement(), XPathConstants.NODE);
				if (experimentElement == null) {
					final CTabItem tabItem = new CTabItem(tabFolder, SWT.NONE);
					tabItem.setText("Graaf");
					GraafPanel graafPanel = new GraafPanel(graaf, tabFolder, SWT.BORDER);
					tabItem.setControl(graafPanel.getBody());
					tabFolder.setSelection(tabItem);
					graafPanel.addDisposeListener(new DisposeListener() {
						@Override
						public void widgetDisposed(DisposeEvent arg0) {
							tabItem.dispose();
						}
					});
				} else {
					Experiment experiment = Experiment.readElement(graaf, experimentElement, xpath);
					final CTabItem tabItem = new CTabItem(tabFolder, SWT.NONE);
					tabItem.setText("Graaf");
					ExperimentPanel experimentPanel = new ExperimentPanel(experiment, tabFolder, SWT.BORDER);
					tabItem.setControl(experimentPanel.getBody());
					tabFolder.setSelection(tabItem);
					experimentPanel.addDisposeListener(new DisposeListener() {
						@Override
						public void widgetDisposed(DisposeEvent arg0) {
							tabItem.dispose();
						}
					});
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XPathExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
