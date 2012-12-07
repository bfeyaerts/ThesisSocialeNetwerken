package core.graaf;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import core.graaf.modellen.GraafModel;

import util.ParameterParser;
import util.xml.XMLWriter;
import util.xml.XMLWriter.XMLElementWriter;
import edu.uci.ics.jung.graph.AbstractGraph;

public class Graaf {
	protected final GraafModel model;
	protected final Object[] setup;
	
	protected final AbstractGraph<Knoop, Tak> graph;
	
	public Graaf(GraafModel model, Object[] setup) throws RuntimeException {
		this.model = model;
		this.setup = setup;
		graph = model.genereer(setup);
	}
	public Graaf(GraafModel model, Object[] setup, Collection<Knoop> knopen, Collection<Tak> takken) throws RuntimeException {
		this.model = model;
		this.setup = setup;
		graph = model.genereer(setup, knopen, takken);
	}
	public Graaf(Graaf graaf) throws RuntimeException {
		this.model = graaf.model;
		this.setup = graaf.setup;
		this.graph = model.genereer(setup, graaf.graph);
	}
	
	public GraafModel getModel() {
		return model;
	}
	public Object[] getSetup() {
		return setup;
	}
	public AbstractGraph<Knoop, Tak> getGraph() {
		return graph;
	}
	
	public Collection<Knoop> getKnopen() {
		return graph.getVertices();
	}
	
	public static Graaf readElement(Element element, XPath xpath) throws XPathExpressionException {
		GraafModel model = null;
		try {
			String classname = (String) xpath.evaluate("model/@class", element, XPathConstants.STRING);
			Class<?> cls = Class.forName(classname);
			if (GraafModel.class.isAssignableFrom(cls))
				model = GraafModel.class.cast(cls.newInstance());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		NodeList parameters = (NodeList) xpath.evaluate("model/parameter", element, XPathConstants.NODESET);
		Object[] setup = new Object[parameters.getLength()];
		for (int i=0; i<parameters.getLength(); i++) {
			Element parameter = (Element) parameters.item(i);
			String type = (String) xpath.evaluate("@class", parameter, XPathConstants.STRING);
			String value = (String) xpath.evaluate("@value", parameter, XPathConstants.STRING);
			setup[i] = ParameterParser.getObject(type, value);
		}
		
		Element graph = (Element) xpath.evaluate("graph", element, XPathConstants.NODE);
		if (graph != null) {
			ArrayList<Knoop> knopen = new ArrayList<Knoop>();
			{
				NodeList knoopList = (NodeList) xpath.evaluate("knopen/knoop", graph, XPathConstants.NODESET);
				for (int i=0; i<knoopList.getLength(); i++) {
					double id = (Double) xpath.evaluate("@id", knoopList.item(i), XPathConstants.NUMBER);
					knopen.add(new Knoop(null, (long) id));
				}
			}
			
			ArrayList<Tak> takken = new ArrayList<Tak>();
			{
				NodeList takList = (NodeList) xpath.evaluate("takken/tak", graph, XPathConstants.NODESET);
				for (int i=0; i<takList.getLength(); i++) {
					Element tak = (Element) takList.item(i);
					double id = (Double) xpath.evaluate("@id", tak, XPathConstants.NUMBER);
					double begin = (Double) xpath.evaluate("@begin", tak, XPathConstants.NUMBER);
					double einde = (Double) xpath.evaluate("@einde", tak, XPathConstants.NUMBER);
					takken.add(new Tak(null, (long) id, new Knoop(null, (long) begin), new Knoop(null, (long) einde)));
				}
			}
			return new Graaf(model, setup, knopen, takken);
		} else
			return new Graaf(model, setup);
	}
	
	public void createElement(XMLEventWriter eventWriter, String name, final boolean includeGraph) throws XMLStreamException {
		XMLWriter.createElement(eventWriter, name, new XMLElementWriter() {
			@Override
			public void writeElementBody(XMLEventWriter eventWriter) throws XMLStreamException {
				XMLWriter.createElement(eventWriter, "model", new XMLElementWriter() {
					@Override
					public void writeElementBody(XMLEventWriter eventWriter) throws XMLStreamException {
						XMLWriter.createAttribute(eventWriter, "class", model != null ? model.getClass().getName() : null);
						for (final Object parameter: setup) {
							XMLWriter.createElement(eventWriter, "parameter", new XMLElementWriter() {
								@Override
								public void writeElementBody(XMLEventWriter eventWriter) throws XMLStreamException {
									XMLWriter.createAttribute(eventWriter, "class", parameter != null ? parameter.getClass().getName() : null);
									XMLWriter.createAttribute(eventWriter, "value", parameter != null ? parameter.toString() : null);
								}
							});
						}
					}
				});
				if (includeGraph) {
					XMLWriter.createElement(eventWriter, "graph", new XMLElementWriter() {
						@Override
						public void writeElementBody(XMLEventWriter eventWriter) throws XMLStreamException {
							XMLWriter.createElement(eventWriter, "knopen", new XMLElementWriter() {
								@Override
								public void writeElementBody(XMLEventWriter eventWriter) throws XMLStreamException {
									for (final Knoop knoop: graph.getVertices())
										XMLWriter.createElement(eventWriter, "knoop", new XMLElementWriter() {
											@Override
											public void writeElementBody(XMLEventWriter eventWriter) throws XMLStreamException {
												XMLWriter.createAttribute(eventWriter, "id", "" + knoop.getId());
											}
										});
								}
							});
							XMLWriter.createElement(eventWriter, "takken", new XMLElementWriter() {
								@Override
								public void writeElementBody(XMLEventWriter eventWriter) throws XMLStreamException {
									for (final Tak tak: graph.getEdges())
										XMLWriter.createElement(eventWriter, "tak", new XMLElementWriter() {
											@Override
											public void writeElementBody(XMLEventWriter eventWriter) throws XMLStreamException {
												XMLWriter.createAttribute(eventWriter, "id", "" + tak.getId());
												XMLWriter.createAttribute(eventWriter, "begin", "" + tak.getBegin().getId());
												XMLWriter.createAttribute(eventWriter, "einde", "" + tak.getEinde().getId());
											}
										});
								}
							});
						}
					});
				}
			}
		});
	}
}
