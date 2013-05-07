package core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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

public class Configuratie<M extends Model> {
	public M model = null;
	public Object[] setup = new Object[0];
	
	public Configuratie(M model) {
		this.model = model;
	}
	public Configuratie(M model, Object[] setup) {
		this.model = model;
		this.setup = setup;
	}
	
	public static Configuratie readElement(Element parentElement, XPath xpath) throws XPathExpressionException {
		Element element = (Element) ((NodeList) xpath.evaluate("configuratie", parentElement, XPathConstants.NODESET)).item(0);
		if (element == null)
			return null;
		
		Model model = null;
		try {
			String classname = (String) xpath.evaluate("model/@class", element, XPathConstants.STRING);
			Class<?> cls = Class.forName(classname);
			if (Model.class.isAssignableFrom(cls))
				model = Model.class.cast(cls.newInstance());
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
			if (value != null)
				setup[i] = ParameterParser.getObject(type, value);
			else try {
				Class<?> cls = Class.forName(type);
				Method method = cls.getMethod("readElement", Element.class, XPath.class);
				setup[i] = method.invoke(null, parameter, xpath);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return new Configuratie(model, setup);
	}
	
	public void createElement(XMLEventWriter eventWriter) throws XMLStreamException {
		XMLWriter.createElement(eventWriter, "configuratie", new XMLElementWriter() {
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
									if (parameter instanceof Configuratie)
										((Configuratie<?>) parameter).createElement(eventWriter);
									else
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
