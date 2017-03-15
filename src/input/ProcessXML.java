package input;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import draw.displayItems.BackgroundImage;
import draw.displayItems.BarDisplayer;
import draw.displayItems.ImageDisplayer;
import draw.displayItems.TextPrinter;
import draw.displayItems.VariablePrinter;
import logic.BlinkingShape;
import logic.variables.VariableModifierFunction;

public class ProcessXML {

	public static void loadXML(){
		//On crée une instance de SAXBuilder
		Document document=null;
		SAXBuilder sxb = new SAXBuilder();
		File configFile = null;

		configFile = FileManagerUtils.getLocalFileFor("configuration.xml");

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

		Element racine = document.getRootElement();

		loadVariables(racine);
		loadAll(racine);
		/*

		final BoundedIntegerVariable biv = new BoundedIntegerVariable(0,20,300);
		KeyMonitorer.addListener(new IncreaseVariableFunction(biv,10),'a');
		KeyMonitorer.addListener(new DecreaseVariableFunction(biv,10),'q');

		DisplayableItems.itemsToDisplay.add(new BarDisplayer(biv,new Rectangle(20, 40, 50, 100)));
		DisplayableItems.itemsToDisplay.add(new VariableDisplayer(new Point(35,20),biv, VariableDisplayer.PERCENT));

		Callable<Boolean>threshold = ()-> biv.getValue()>100;
		ThresholdVariable tv = new ThresholdVariable(threshold, "","overload");
		DisplayableItems.itemsToDisplay.add(new VariableDisplayer(new Point(35,30),tv, VariableDisplayer.NAKED));
		 */
	}

	private static void loadAll(Element racine) {
		for(Element e:racine.getChild("displayed_objects").getChildren())
			switch(e.getName())
			{
			case "bar": DisplayedItemsManager.add(BarDisplayer.generate(e.getChildren()));break;
			case "background":DisplayedItemsManager.add(BackgroundImage.generate(e.getChild("filename").getAttribute("name").getValue()));break;
			case "blinking_shape": DisplayedItemsManager.add(BlinkingShape.generate(e));break;
			case "heartbeat_monitor": DisplayedItemsManager.add(HeartBeatMonitor.generate(e));break;
			case "variable_printer":DisplayedItemsManager.add(VariablePrinter.generate(e)); break;
		//	case "hacking_frame": DisplayedItemsManager.add(TextPrinter.newInstance(e));break;
			case "image": DisplayedItemsManager.add(ImageDisplayer.newInstance(e));break;
			default: throw new Error("Unknown object:"+e.getName());
			}
	}

	private static void loadVariables(Element racine) {
		for(Element e: racine.getChildren())
			if(e.getName().equals("variables"))
				for(Element variable : e.getChildren())
				{
					String variableType=variable.getName();
					Map<String,String>parameters = new HashMap<>();
					for(Attribute a: variable.getAttributes())
						parameters.put(a.getName(), a.getValue());
					
					Variable currentVariable = Variable.generate(variableType,parameters);
					VariableManager.add(currentVariable);

					
					Element connectors = variable.getChild("keyboard_actuators");
					for(Element modifyier: connectors.getChildren())
					{
						
						VariableModifierFunction vmf = 
								VariableModifierFunction.generate(currentVariable,modifyier);
						
						KeyMonitorer.addListener(vmf, modifyier.getAttribute("key").getValue().charAt(0));
					}
					
					
				}
				
				//throw new Error();
	}
}
