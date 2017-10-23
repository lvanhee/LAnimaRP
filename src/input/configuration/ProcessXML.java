package input.configuration;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import draw.displayItems.DisplayableItem;
import draw.displayItems.advanced.Popup;
import draw.displayItems.advanced.dnasca.DNASCA;
import draw.displayItems.advanced.dnasca.GenericDisplayer;
import draw.displayItems.advanced.dnasca.HeartBeatMonitor;
import draw.displayItems.images.EventAdaptiveImageDisplayer;
import draw.displayItems.images.SlideShow;
import draw.displayItems.shapes.BlinkingShape;
import draw.displayItems.shapes.bars.BarDisplayer;
import draw.displayItems.sound.SoundPlayer;
import draw.displayItems.text.FPSDisplayer;
import draw.displayItems.text.NewsTicker;
import draw.displayItems.text.TextTyper;
import draw.displayItems.text.VariablePrinter;
import draw.displayItems.videos.PassiveVideoDisplayer;
import draw.displayItems.videos.VideoDisplayer;
import input.events.publishers.KeyMonitorer;
import logic.variables.actuators.VariableActuator;
import logic.variables.variableTypes.BoundedIntegerVariable;
import logic.variables.variableTypes.Variable;
import logic.variables.variableTypes.VariableType;

public class ProcessXML {

	public static DisplaySetupParameters loadXML(File configFile){
		Document document=null;
		SAXBuilder sxb = new SAXBuilder();
		
		try
		{
			document = sxb.build(configFile);
		}
		catch(Exception e){
			e.printStackTrace();
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			throw new Error(sw.toString()+"\n"+"Erreur au chargement de "+configFile);

		}

		Element root = document.getRootElement();

		loadVariables(root);
		return DisplaySetupParameters.newInstance(getDisplayParameters(root),getDisplayableItemsFrom(root));
	}



	private static DisplayParameters getDisplayParameters(Element root) {
		DisplayParameters res = DisplayParameters.newInstance();
		if(root.getChild(XMLKeywords.DISPLAY_PARAMETERS.getName())== null) return res;
		
		Element head = root.getChild(XMLKeywords.DISPLAY_PARAMETERS.getName());
		Element screenSize = head.getChild(XMLKeywords.SCREEN_SIZE.getName());
		res.setScreenSize(XMLParser.parseRectangle(screenSize));
		
		return res;
		
	}



	private static void loadVariables(Element racine) {
		for(Element e: racine.getChildren())
			if(e.getName().equals("variables"))
				for(Element variable : e.getChildren())
				{
					if(!variable.getName().equals(XMLKeywords.VARIABLE.getName()))
						throw new Error("Expected \"variable\" and got instead:"+variable.getName());
					loadVariable(variable);
				}
				
				//throw new Error();
	}

	private static void loadVariable(Element variable) {
		VariableType variableType=XMLParser.getVariableType(variable);
		Variable res = generateVariable(variableType, variable);
		
		
	}

	private static Variable generateVariable(VariableType variableType, Element variable) {
		XMLVariableBuilder b = new XMLVariableBuilder(XMLParser.parseVariableName(variable));

		for(Element e:variable.getChildren())
			b = b.set(e.getName(),e);
		
		XMLParser.checkAllowedChildKeywords(variable, XMLKeywords.getKeywordCheckerFor(variableType, variable));
		
		return b.build();
		
	}

	public static Collection<DisplayableItem> getDisplayableItemFromString(String string) {
		if(string.isEmpty())return new LinkedList<>();
		String exampleXML = string;
		try {
			InputStream stream = new ByteArrayInputStream(exampleXML.getBytes("UTF-8"));
		  SAXBuilder builder = new SAXBuilder();
		
			Document anotherDocument = builder.build(stream);
			return getDisplayableItemsFrom(anotherDocument.getRootElement());
			
		} catch (JDOMException e) {
			e.printStackTrace();
			throw new Error();
		} catch (IOException e) {
			e.printStackTrace();
			throw new Error();
		}
	}
	
	private static DisplayableItem getDisplayableItemFrom(Element e) {
		return XMLParser.getAnimationBuilder(XMLKeywords.fromString(e.getName())).apply(e);
		
	}

	public static Collection<DisplayableItem> getDisplayableItemsFrom(Element rootElement) {
		//System.out.println("bing"+rootElement);
		if(rootElement.getChild(XMLKeywords.DISPLAYED_ANIMATIONS.getName())==null)
			throw new Error(rootElement+" must have a child:"+XMLKeywords.DISPLAYED_ANIMATIONS.getName());
		return 
				rootElement.getChild(XMLKeywords.DISPLAYED_ANIMATIONS.getName()).getChildren()
				.stream().map(ProcessXML::getDisplayableItemFrom)
				.collect(Collectors.toList());

	}

}
