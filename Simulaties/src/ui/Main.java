package ui;

import infecties.Toestand;
import infecties.evolutie.Evolutie;
import infecties.evolutie.Terminaal;
import infecties.propagatie.BernoulliPropagatie;
import infecties.propagatie.Propagatie;

import org.apache.commons.collections15.Factory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.uci.ics.jung.algorithms.generators.random.ErdosRenyiGenerator;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;


public class Main extends Composite {
	private Button gerichtButton, genereerButton, propageerButton;
	private Combo layoutCombo;
	private Composite cGraaf, cLayout, cPropagate;
	private CTabFolder folder;
	private ExpandBar expandBar;
	private ExpandItem iGraaf, iLayout, iPropagate;
	private Label knopenLabel, kansLabel, layoutLabel, infectLabel, verspreidingsLabel, iteratieLabel;
	private Text knopenText, kansText, verspreidingsText, iteratieText;
	
	public Main(Composite parent, int style) {
		super(parent, style);
		initComponent();
	}
	
	protected void initComponent() {
		setLayout(new GridLayout(2, false));
		
		expandBar = new ExpandBar(this, SWT.NONE);
		GridData gridData = new GridData();
		gridData.widthHint = 200;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        expandBar.setLayoutData(gridData);
        
        // 1. Graaf
        cGraaf = new Composite(expandBar, SWT.NONE);
        FormLayout formLayout = new FormLayout();
        formLayout.marginWidth = 3;
        formLayout.marginHeight = 3;
        
        knopenLabel = new Label(cGraaf, SWT.NONE);
        knopenLabel.setText("Aantal knopen");
        knopenText = new Text(cGraaf, SWT.SINGLE | SWT.BORDER);
        FormData data = new FormData();
		data.top = new FormAttachment(knopenText, 0, SWT.CENTER);
		knopenLabel.setLayoutData(data);
		data = new FormData();
		data.left = new FormAttachment(knopenLabel, 5);
		data.right = new FormAttachment(100, 0);
		knopenText.setLayoutData(data);
        
        kansLabel = new Label(cGraaf, SWT.NONE);
        kansLabel.setText("Verbindingskans");
        kansText = new Text(cGraaf, SWT.SINGLE | SWT.BORDER);
        data = new FormData();
		data.top = new FormAttachment(kansText, 0, SWT.CENTER);
		kansLabel.setLayoutData(data);
		data = new FormData();
		data.top = new FormAttachment(knopenText, 5);
		data.left = new FormAttachment(kansLabel, 5);
		data.right = new FormAttachment(100, 0);
		kansText.setLayoutData(data);
        
        gerichtButton = new Button(cGraaf, SWT.CHECK);
        gerichtButton.setText("Gerichte graaf");
        data = new FormData();
        data.top = new FormAttachment(kansText, 5);
		data.left = new FormAttachment(0);
		data.right = new FormAttachment(100, 0);
		gerichtButton.setLayoutData(data);
        
		genereerButton = new Button(cGraaf, SWT.PUSH);
		genereerButton.setText("Graaf genereren");
		genereerButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				Graph<String, Integer> graph = generateGraph(Integer.parseInt(knopenText.getText()), Double.parseDouble(kansText.getText()));
				String layoutName = layoutCombo.getItem(layoutCombo.getSelectionIndex());
				GraafVenster venster = new GraafVenster(folder, graph, layoutName);
				folder.setSelection(venster);
			}
		});
        data = new FormData();
        data.top = new FormAttachment(gerichtButton, 5);
		data.left = new FormAttachment(0);
		data.right = new FormAttachment(100, 0);
		genereerButton.setLayoutData(data);
		
        cGraaf.setLayout(formLayout);
    	iGraaf = new ExpandItem(expandBar, SWT.NONE);
    	iGraaf.setText("Graaf");
    	iGraaf.setHeight(105);
    	iGraaf.setControl(cGraaf);
    	iGraaf.setExpanded(true);
        
    	// 2. Layout
        cLayout = new Composite(expandBar, SWT.NONE);
        formLayout = new FormLayout();
        formLayout.marginWidth = 3;
        formLayout.marginHeight = 3;
    	
    	layoutLabel = new Label(cLayout, SWT.NONE);
    	layoutLabel.setText("Layout");
        layoutCombo = new Combo(cLayout, SWT.SINGLE | SWT.BORDER);
        layoutCombo.setItems(new String[]{"KKLayout", "FRLayout", "FRLayout2", "SpringLayout", "SpringLayout2", "ISOMLayout", "CircleLayout"});
        layoutCombo.select(0);
        layoutCombo.addSelectionListener(new SelectionAdapter() {
        	@Override
			public void widgetSelected(SelectionEvent arg0) {
        		GraafVenster venster = (GraafVenster) folder.getSelection();
        		venster.setLayout(layoutCombo.getItem(layoutCombo.getSelectionIndex()));
			}
        });
        data = new FormData();
		data.top = new FormAttachment(layoutCombo, 0, SWT.CENTER);
		layoutLabel.setLayoutData(data);
		data = new FormData();
		data.left = new FormAttachment(layoutLabel, 5);
		data.right = new FormAttachment(100, 0);
		layoutCombo.setLayoutData(data);
    	
		cLayout.setLayout(formLayout);
		iLayout = new ExpandItem(expandBar, SWT.NONE);
		iLayout.setText("Layout");
		iLayout.setHeight(cLayout.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		iLayout.setControl(cLayout);
		iLayout.setExpanded(true);
    	
    	// 3. Propageren
        cPropagate = new Composite(expandBar, SWT.NONE);
        formLayout = new FormLayout();
        formLayout.marginWidth = 3;
        formLayout.marginHeight = 3;
    	
    	infectLabel = new Label(cPropagate, SWT.NONE);
    	infectLabel.setText("Klik op nodes om ze te infecteren.");
    	data = new FormData();
		data.left = new FormAttachment(0);
		data.right = new FormAttachment(100, 0);
		infectLabel.setLayoutData(data);
    	
		verspreidingsLabel = new Label(cPropagate, SWT.NONE);
		verspreidingsLabel.setText("Verspreidingskans");
        verspreidingsText = new Text(cPropagate, SWT.SINGLE | SWT.BORDER);
        data = new FormData();
		data.top = new FormAttachment(verspreidingsText, 0, SWT.CENTER);
		verspreidingsLabel.setLayoutData(data);
		data = new FormData();
		data.top = new FormAttachment(infectLabel, 5);
		data.left = new FormAttachment(verspreidingsLabel, 5);
		data.right = new FormAttachment(100, 0);
		verspreidingsText.setLayoutData(data);
    	
		iteratieLabel = new Label(cPropagate, SWT.NONE);
		iteratieLabel.setText("Aantal iteraties");
        iteratieText = new Text(cPropagate, SWT.SINGLE | SWT.BORDER);
        data = new FormData();
		data.top = new FormAttachment(iteratieText, 0, SWT.CENTER);
		iteratieLabel.setLayoutData(data);
		data = new FormData();
		data.top = new FormAttachment(verspreidingsText, 5);
		data.left = new FormAttachment(iteratieLabel, 5);
		data.right = new FormAttachment(100, 0);
		iteratieText.setLayoutData(data);
    	
		propageerButton = new Button(cPropagate, SWT.PUSH);
		propageerButton.setText("Propageren");
		propageerButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				GraafVenster venster = (GraafVenster) folder.getSelection();
				Graph<String, Integer> graph = venster.getGraph();
				Toestand<String, Integer> toestand = venster.getToestand();
				
				//Evolutie evolutie = new Terminaal(graph, toestand);
				Propagatie propagatie = new BernoulliPropagatie(graph, toestand);
				
				propagatie.propageer(Integer.parseInt(iteratieText.getText()), Double.parseDouble(verspreidingsText.getText()));
				venster.refresh();
			}
		});
        data = new FormData();
        data.top = new FormAttachment(iteratieText, 5);
		data.left = new FormAttachment(0);
		data.right = new FormAttachment(100, 0);
		propageerButton.setLayoutData(data);
		
		cPropagate.setLayout(formLayout);
		iPropagate = new ExpandItem(expandBar, SWT.NONE);
		iPropagate.setText("Propageren");
		iPropagate.setHeight(105);
		iPropagate.setControl(cPropagate);
		iPropagate.setExpanded(true);
        
    	folder = new CTabFolder(this, SWT.BORDER);
    	gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        folder.setLayoutData(gridData);
	}
	
	protected static Graph<String, Integer> generateGraph(int nNodes, double p) {
		Factory<UndirectedGraph<String,Integer>> graphFactory = new Factory<UndirectedGraph<String,Integer>>() {
			public UndirectedGraph<String, Integer> create() {
				return new UndirectedSparseGraph<String,Integer>();
			}
		};
		
		Factory<String> vertexFactory = new Factory<String>() {
			int i=0;
			public String create() {
				return "V" + i++;
			}
		};
		
		Factory<Integer> edgeFactory = new Factory<Integer>() {
			int i=0;
			public Integer create() {
				return i++;
			}
		};
		
		ErdosRenyiGenerator<String, Integer> generator = new ErdosRenyiGenerator<String, Integer>(
			graphFactory
			, vertexFactory
			, edgeFactory
			, nNodes // Aantal knopen
			, p // Probabiliteit dat 2 willekeurige nodes met elkaar verbonden zijn
		);
		
		Graph<String, Integer> graph = generator.create();
		return graph;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("Thesis sociale netwerken");
		shell.setLayout(new FillLayout());

		new Main(shell, SWT.NONE);

		shell.open ();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}

}
