package input.configuration;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import draw.displayItems.DisplayableItem;
import draw.displayItems.advanced.Popup;
import draw.displayItems.advanced.dnasca.DNASCA;
import draw.displayItems.advanced.dnasca.DynamicallyUpdatableDisplayer;
import draw.displayItems.advanced.dnasca.HeartBeatMonitor;
import draw.displayItems.images.ImageDisplayer;
import draw.displayItems.images.SlideShow;
import draw.displayItems.shapes.BlinkingShape;
import draw.displayItems.shapes.bars.BarDisplayer;
import draw.displayItems.sound.SoundPlayerDisplayableItem;
import draw.displayItems.text.FPSDisplayer;
import draw.displayItems.text.NewsTicker;
import draw.displayItems.text.UserTextTyper;
import draw.displayItems.text.VariablePrinter;
import draw.displayItems.videos.PassiveVideoDisplayer;
import draw.displayItems.videos.VideoDisplayer;
import input.events.eventTypes.LAnimaRPEvent;
import input.events.publishers.KeyMonitorer;
import input.events.publishers.LAnimaRPEventPublisher;
import logic.variables.actuators.VariableActuator;
import logic.variables.variableTypes.BoundedIntegerVariable;
import logic.variables.variableTypes.Variable;
import logic.variables.variableTypes.VariableType;

public class ProcessXML {

	public static DisplaySetupParameters loadXML(URL inputFile){
		Document document=null;
		SAXBuilder sxb = new SAXBuilder();
		
		try
		{
			document = sxb.build(inputFile);
		}
		catch(Exception e){
			e.printStackTrace();
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			throw new Error(sw.toString()+"\n"+"Erreur au chargement de "+inputFile);

		}

		Element root = document.getRootElement();
		
		Map<String, LAnimaRPEventPublisher<? extends LAnimaRPEvent>> eventProducers = loadEventPublishers(root);
		
		LAnimaRPContext context = LAnimaRPContext.newInstance(eventProducers, loadVariables(root));
		return DisplaySetupParameters.newInstance(getDisplayParameters(root),XMLParser.parseDisplayableItem(root, context));
	}



	private static Map<String,LAnimaRPEventPublisher<? extends LAnimaRPEvent>> loadEventPublishers(Element root) {
		if(root.getChild(XMLKeywords.EVENT_PUBLISHERS.getName())!= null)
		{			
			Map<String, LAnimaRPEventPublisher<? extends LAnimaRPEvent>>res = new HashMap<>();
			for(Element e: root.getChild(XMLKeywords.EVENT_PUBLISHERS.getName()).getChildren())
				res.put(e.getAttributeValue(XMLKeywords.NAME.getName()),
						XMLParser.parseXMLEventProducer(e));
			return res;
		}
		else return new HashMap<>();
	}



	private static DisplayParameters getDisplayParameters(Element root) {
		DisplayParameters res = DisplayParameters.newInstance();
		if(root.getChild(XMLKeywords.DISPLAY_PARAMETERS.getName())== null) return res;
		
		Element head = root.getChild(XMLKeywords.DISPLAY_PARAMETERS.getName());
		Element screenSize = head.getChild(XMLKeywords.DISPLAY_AREA.getName());

		res.setFullScreen(XMLParser.parseFullScreen(head));
		if(screenSize==null)
			throw new Error("Missing the definition of the size of the screen in display parameters");
		res.setScreenSize(XMLParser.parseRectangle(screenSize));
		
		return res;
		
	}



	private static Set<Variable> loadVariables(Element racine) {
		Set<Variable>res = new HashSet<>();
		for(Element e: racine.getChildren())
			if(e.getName().equals("variables"))
				for(Element variable : e.getChildren())
				{
					if(!variable.getName().equals(XMLKeywords.VARIABLE.getName()))
						throw new Error("Expected \"variable\" and got instead:"+variable.getName());
					res.add(loadVariable(variable));
				}
		return res;
				//throw new Error();
	}

	private static Variable loadVariable(Element variable) {
		VariableType variableType=XMLParser.getVariableType(variable);
		return generateVariable(variableType, variable);		
	}

	private static Variable generateVariable(VariableType variableType, Element variable) {
		XMLVariableBuilder b = new XMLVariableBuilder(XMLParser.parseVariableName(variable),
				VariableType.parseFromXML(variable.getAttributeValue(XMLKeywords.TYPE.getName())),
				variable.getAttributeValue(XMLKeywords.NAME.getName())
				);
		
		b=b.setInitialValue(variable.getAttributeValue(XMLKeywords.INITIAL_VALUE.getName()));

		for(Element e:variable.getChildren())
			b = b.set(e.getName(),e);
		
		XMLParser.checkAllowedChildKeywords(variable, XMLKeywords.getKeywordCheckerFor(variableType, variable));
		
		return b.build();
		
	}

	public static DisplayableItem getDisplayableItemFromString(String string, LAnimaRPContext context) {
		if(string.isEmpty())return NoAnimation.INSTANCE;
		String exampleXML = string;
		try {
			InputStream stream = new ByteArrayInputStream(exampleXML.getBytes("UTF-8"));
		  SAXBuilder builder = new SAXBuilder();
		
			Document anotherDocument = builder.build(stream);
			return XMLParser.parseDisplayableItem(anotherDocument.getRootElement(), context);
			
		} catch (JDOMException e) {
			e.printStackTrace();
			throw new Error();
		} catch (IOException e) {
			e.printStackTrace();
			throw new Error();
		}
	}
	
	static DisplayableItem getDisplayableItemFrom(Element e, LAnimaRPContext context) {
		return XMLParser.getAnimationBuilder(XMLKeywords.fromString(e.getName())).apply(e,context);
		
	}

}
