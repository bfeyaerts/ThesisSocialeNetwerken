package core.experiment;

import java.awt.Color;
import java.awt.Paint;
import java.util.HashMap;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import util.ParameterParser;
import util.xml.XMLWriter;
import util.xml.XMLWriter.XMLElementWriter;

import core.graaf.Graaf;
import core.graaf.Knoop;
import core.infecties.diagnose.Diagnose;
import core.infecties.evolutie.Evolutie;
import core.infecties.propagatie.Propagatie;

public class Experiment {
	protected final Graaf graaf;
	protected final Evolutie evolutiemodel;
	protected final Object[] evolutiesetup;
	protected final HashMap<Diagnose, Color> diagnoseColors;
	protected final Diagnose defaultDiagnose;
	protected final Propagatie propagatiemodel;
	protected final Object[] propagatiesetup;
	
	public Experiment(Graaf graaf,
				Evolutie evolutiemodel, Object[] evolutiesetup,
				HashMap<Diagnose, Color> diagnoseColors, Diagnose defaultDiagnose,
				Propagatie propagatiemodel, Object[] propagatiesetup) {
		this.graaf = graaf;
		this.evolutiemodel = evolutiemodel;
		this.evolutiesetup = evolutiesetup;
		this.diagnoseColors = diagnoseColors;
		this.defaultDiagnose = defaultDiagnose;
		this.propagatiemodel = propagatiemodel;
		this.propagatiesetup = propagatiesetup;
		
		reset();
	}

	public Paint getColor(Diagnose diagnose) {
		return diagnoseColors.containsKey(diagnose) ? diagnoseColors.get(diagnose) : diagnose.getDefaultColor();
	}
	
	public Graaf getGraaf() {
		return graaf;
	}
	
	public Evolutie getEvolutieModel() {
		return evolutiemodel;
	}
	
	public void reset() {
		for (Knoop knoop: graaf.getKnopen())
			knoop.setDiagnose(defaultDiagnose);
	}
	public void stap() {
		Knoop[] besmettingen = propagatiemodel.propageer(graaf, propagatiesetup);
		
		evolutiemodel.evolueer(graaf, evolutiesetup);
		
		for (Knoop knoop: besmettingen)
			evolutiemodel.besmet(knoop, evolutiesetup);
	}
	
	public static Experiment readElement(Graaf graaf, Element element, XPath xpath) throws XPathExpressionException {
		Evolutie evolutiemodel = null;
		try {
			String classname = (String) xpath.evaluate("evolutie/@class", element, XPathConstants.STRING);
			Class<?> cls = Class.forName(classname);
			if (Evolutie.class.isAssignableFrom(cls))
				evolutiemodel = Evolutie.class.cast(cls.newInstance());
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
		
		NodeList parameters = (NodeList) xpath.evaluate("evolutie/parameter", element, XPathConstants.NODESET);
		Object[] evolutiesetup = new Object[parameters.getLength()];
		for (int i=0; i<parameters.getLength(); i++) {
			Element parameter = (Element) parameters.item(i);
			String type = (String) xpath.evaluate("@class", parameter, XPathConstants.STRING);
			String value = (String) xpath.evaluate("@value", parameter, XPathConstants.STRING);
			evolutiesetup[i] = ParameterParser.getObject(type, value);
		}
		
		Diagnose defaultDiagnose = null;
		HashMap<Diagnose, Color> diagnoseColors = new HashMap<Diagnose, Color>();
		if (evolutiemodel != null) {
			Enum<? extends Diagnose>[] diagnoses = evolutiemodel.possibleDiagnoses().getEnumConstants();
			String defaultName = (String) xpath.evaluate("diagnoses/@default", element, XPathConstants.STRING);
			for (Enum<? extends Diagnose> diagnose: diagnoses) {
				if (defaultName.equals(diagnose.name()))
					defaultDiagnose = (Diagnose) diagnose;
				Element diagnoseElement = (Element) xpath.evaluate("diagnoses/diagnose[@name=" + diagnose.name() + "]", element, XPathConstants.NODE);
				if (diagnoseElement != null) {
					String rgb = (String) xpath.evaluate("@rgb", diagnoseElement, XPathConstants.STRING);
					diagnoseColors.put((Diagnose) diagnose, new Color(Integer.parseInt(rgb)));
				}
			}
		}
		
		Propagatie propagatiemodel = null;
		try {
			String classname = (String) xpath.evaluate("propagatie/@class", element, XPathConstants.STRING);
			Class<?> cls = Class.forName(classname);
			if (Propagatie.class.isAssignableFrom(cls))
				propagatiemodel = Propagatie.class.cast(cls.newInstance());
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
		
		parameters = (NodeList) xpath.evaluate("propagatie/parameter", element, XPathConstants.NODESET);
		Object[] propagatiesetup = new Object[parameters.getLength()];
		for (int i=0; i<parameters.getLength(); i++) {
			Element parameter = (Element) parameters.item(i);
			String type = (String) xpath.evaluate("@class", parameter, XPathConstants.STRING);
			String value = (String) xpath.evaluate("@value", parameter, XPathConstants.STRING);
			propagatiesetup[i] = ParameterParser.getObject(type, value);
		}
		
		return new Experiment(graaf, evolutiemodel, evolutiesetup, diagnoseColors, defaultDiagnose, propagatiemodel, propagatiesetup);
	}
	
	public void createElement(XMLEventWriter eventWriter, String name) throws XMLStreamException {
		XMLWriter.createElement(eventWriter, name, new XMLElementWriter() {
			@Override
			public void writeElementBody(XMLEventWriter eventWriter) throws XMLStreamException {
				XMLWriter.createElement(eventWriter, "evolutie", new XMLElementWriter() {
					@Override
					public void writeElementBody(XMLEventWriter eventWriter) throws XMLStreamException {
						XMLWriter.createAttribute(eventWriter, "class", evolutiemodel != null ? evolutiemodel.getClass().getName() : null);
						for (final Object parameter: evolutiesetup) {
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
				
				XMLWriter.createElement(eventWriter, "diagnoses", new XMLElementWriter() {
					@Override
					public void writeElementBody(XMLEventWriter eventWriter) throws XMLStreamException {
						XMLWriter.createAttribute(eventWriter, "default", defaultDiagnose.name());
						for (final Diagnose diagnose: diagnoseColors.keySet()) {
							XMLWriter.createElement(eventWriter, "diagnose", new XMLElementWriter() {
								@Override
								public void writeElementBody(XMLEventWriter eventWriter) throws XMLStreamException {
									XMLWriter.createAttribute(eventWriter, "name", diagnose.name());
									XMLWriter.createAttribute(eventWriter, "rgb", "" + diagnoseColors.get(diagnose).getRGB());
								}
							});
						}
					}
				});
				
				XMLWriter.createElement(eventWriter, "propagatie", new XMLElementWriter() {
					@Override
					public void writeElementBody(XMLEventWriter eventWriter) throws XMLStreamException {
						XMLWriter.createAttribute(eventWriter, "class", propagatiemodel != null ? propagatiemodel.getClass().getName() : null);
						for (final Object parameter: propagatiesetup) {
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
			}
		});
	}
}
