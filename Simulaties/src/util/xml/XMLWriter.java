package util.xml;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class XMLWriter {
	public interface XMLElementWriter {
		public void writeElementBody(XMLEventWriter eventWriter) throws XMLStreamException;
	}
	
	protected final static XMLEventFactory eventFactory = XMLEventFactory.newInstance();
	
	protected final static XMLEvent end = eventFactory.createDTD("\n");
	protected final static XMLEvent tab = eventFactory.createDTD("\t");
	
	public static void saveConfig(String filename, XMLElementWriter body) throws IOException {
	    // Create a XMLOutputFactory
	    XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
	    // Create XMLEventWriter
	    FileOutputStream fos = null;
	    try {
		    fos = new FileOutputStream(filename);
		    XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(fos);
		    // Create a EventFactory
		    XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		    // Create and write Start Tag
		    StartDocument startDocument = eventFactory.createStartDocument();
		    eventWriter.add(startDocument);
	
		    // Create config open tag
		    StartElement startElement = eventFactory.createStartElement("", "", "config");
		    eventWriter.add(startElement);
		    body.writeElementBody(eventWriter);
		    eventWriter.add(eventFactory.createEndElement("", "", "config"));
		    eventWriter.add(end);
		    eventWriter.add(eventFactory.createEndDocument());
		    eventWriter.close();
	    } catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} finally {
			if (fos != null)
				fos.close();
		}
	  }
	
	public static void createElement(XMLEventWriter eventWriter, String name, XMLElementWriter body) throws XMLStreamException {
		// Create Start node
	    StartElement sElement = eventFactory.createStartElement("", "", name);
	    eventWriter.add(tab);
	    eventWriter.add(sElement);
	    // Create Content
	    body.writeElementBody(eventWriter);
	    // Create End node
	    EndElement eElement = eventFactory.createEndElement("", "", name);
	    eventWriter.add(eElement);
	    eventWriter.add(end);
	}
	
	public static void createAttribute(XMLEventWriter eventWriter, String name, String value) throws XMLStreamException {
		eventWriter.add(eventFactory.createAttribute(name, value));
	}
	
	public static void createNode(XMLEventWriter eventWriter, String name, String value) throws XMLStreamException {
	    // Create Start node
	    StartElement sElement = eventFactory.createStartElement("", "", name);
	    eventWriter.add(tab);
	    eventWriter.add(sElement);
	    // Create Content
	    Characters characters = eventFactory.createCharacters(value);
	    eventWriter.add(characters);
	    // Create End node
	    EndElement eElement = eventFactory.createEndElement("", "", name);
	    eventWriter.add(eElement);
	    eventWriter.add(end);
	}
}
