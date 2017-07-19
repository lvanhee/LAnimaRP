package input.configuration;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.jdom2.Element;

import draw.displayItems.DisplayableItem;
import draw.displayItems.advanced.Popup;
import draw.displayItems.advanced.chromatograph.Chromatographer;
import draw.displayItems.advanced.dnasca.GenericDisplayer;
import draw.displayItems.advanced.dnasca.HeartBeatMonitor;
import draw.displayItems.images.EventAdaptiveImageDisplayer;
import draw.displayItems.images.SlideShow;
import draw.displayItems.sound.SoundPlayer;
import draw.displayItems.text.FPSDisplayer;
import draw.displayItems.text.NewsTicker;
import draw.displayItems.text.TextTyper;
import draw.displayItems.videos.VariableBasedPauseTrigger;
import draw.displayItems.videos.VideoDisplayer;
import input.events.eventTypes.LAnimaRPKeyEvent;
import input.events.publishers.KeyMonitorer;
import input.events.publishers.LAnimaRPEventPublisher;
import input.events.triggers.PauseTrigger;
import logic.data.PeriodicRefreshInfo;
import logic.data.Range;
import logic.data.drawing.StretchingType;
import logic.data.fileLocators.FileLocator;
import logic.data.fileLocators.StaticFileLocator;
import logic.data.fileLocators.VariableBasedFileLocator;
import logic.data.string.TextSource;
import logic.variables.actuators.AnimationSpecificVariableActuator;
import logic.variables.actuators.AnimationSpecificVariableActuator.AnimationSpecificVariableActuatorCause;
import logic.variables.actuators.IncreaseVariableActuator;
import logic.variables.actuators.SwitchVariableActuator;
import logic.variables.actuators.SynchronizeFromFileVariableActuator;
import logic.variables.actuators.VariableActuator;
import logic.variables.management.VariableManager;
import logic.variables.variableTypes.BooleanVariable;
import logic.variables.variableTypes.BoundedIntegerVariable;
import logic.variables.variableTypes.PathVariable;
import logic.variables.variableTypes.Variable;
import logic.variables.variableTypes.VariableType;
import main.DisplayWindow;

public class XMLParser {

	private static final String PRE_TEXT_NAME = "pre_text";
	private static final String VALUE_NAME = "value";

	public static Shape processShape(Element child) {
		GeneralPath gp = null;
		for(Element e: child.getChildren())
		{
			if(gp==null)
				{gp = new GeneralPath();
				gp.moveTo(Double.parseDouble(e.getAttributeValue("x")), Double.parseDouble(e.getAttributeValue("y")));
				}
			else
				gp.lineTo(Double.parseDouble(e.getAttributeValue("x")), Double.parseDouble(e.getAttributeValue("y")));
		}
		gp.closePath();
		return gp;
	}

	public static Color parseColor(String s) {
		return new Color(
				Integer.valueOf( s.substring( 1, 3 ), 16 ),
				Integer.valueOf( s.substring( 3, 5 ), 16 ),
				Integer.valueOf( s.substring( 5, 7 ), 16 ) );
	}

	public static Rectangle parseRectangle(Element e) {
		if(e==null)throw new Error();
		if(e.getChild("full_screen")!=null)
			return new Rectangle(0, 0, DisplayWindow.getWindowWidth(), DisplayWindow.getWindowHeight());
		Point position = parsePosition(e); 
		Dimension dimensions = new Dimension(
				Integer.parseInt(e.getChild("dimensions").getAttributeValue("width")),
				Integer.parseInt(e.getChild("dimensions").getAttributeValue("height")));
		return new Rectangle(position,dimensions);
	}

	public static BoundedIntegerVariable getParseBIV(Element e) {
		return (BoundedIntegerVariable)parseVariable(e);
	}
	
	public static Variable parseVariable(Element e) {
		return VariableManager.get(e.getChild("variable").getAttributeValue("name"));
	}

	public static Color parseColor(Element e) {
		return parseColor(e.getChild("color").getAttributeValue("value"));
	}

	public static Point parsePosition(Element e) {
		if(e.getChild(XMLKeywords.POSITION.getName())==null)throw new Error();
		return new Point(Integer.parseInt(e.getChild(XMLKeywords.POSITION.getName()).getAttributeValue("x")), 
		Integer.parseInt(e.getChild("position").getAttributeValue("y")));
	}

	public static BooleanVariable getParseBooleanVariable(Element e,String function) {
		for(Element e2:e.getChildren())
		{
			if(e2.getName().equals("variable")&&e2.getAttributeValue("function").equals(function))
				return (BooleanVariable) VariableManager.get(e2.getAttributeValue("name"));
		}
		throw new Error("Variable has not been created.");
	}

	public static boolean hasPreText(Element e) {
		for(Element e2:e.getChildren())
			if(e2.getName().equals(PRE_TEXT_NAME))
				return true;
		return false;
	}

	public static String getPreText(Element e) {
		return e.getChild(PRE_TEXT_NAME).getAttributeValue(VALUE_NAME);
	}

	public static FileLocator parseFileLocator(Element e) {
		Element fileLoc = e.getChild("file_location");
		if(fileLoc==null)
			throw new Error("Expecting a \"file_location\" field");
		
		check(fileLoc, XMLKeywords.FILE_LOCATION);

		
		if(fileLoc.getChild(XMLKeywords.VARIABLE_NAME.getName())!= null)
		{
			String variableName = fileLoc.getChild(XMLKeywords.VARIABLE_NAME.getName())
					.getAttributeValue(XMLKeywords.NAME.getName());
			if(variableName == null) 
				throw new Error("Must define a \"name\" attribute for"+e+"/"+fileLoc);
			return VariableBasedFileLocator.newInstance((PathVariable)VariableManager.get(variableName));					
		}
			
		else if(fileLoc.getChild(XMLKeywords.HARD_PATH.getName())!= null)
			return StaticFileLocator.newInstance(fileLoc.getChild(XMLKeywords.HARD_PATH.getName()).getAttributeValue("path"));
		else throw new Error("Missing adequate file location for: "+e);
	}

	private static void check(Element e, XMLKeywords fileLocation) {
		if(!e.getChildren().stream().map(x->x.getName()).allMatch(
				x->
				XMLKeywords.getAllowedChildrenFor(fileLocation).contains(XMLKeywords.fromString(x))))
			throw new Error(XMLKeywords.FILE_LOCATION.getName()+" can only have the following children:"+
		XMLKeywords.getAllowedChildrenFor(fileLocation).stream().map(XMLKeywords::getName).collect(Collectors.toList())+" and has for children:"+
					e.getChildren().stream().map(x->x.getName()).collect(Collectors.toList()));
	}

	private static File parseSoundFile(Element e) {
		return new File(e.getChild("sound").getChild("file_location").getAttributeValue("value"));
	}

	private static boolean parseFadeAway(Element e) {
		return Boolean.parseBoolean(e.getChild("fading_away").getAttributeValue("value"));
	}

	private static int parseDuration(Element e) {
		return Integer.parseInt(e.getChild("duration").getAttributeValue("value"));
	}

	public static LAnimaRPEventPublisher<LAnimaRPKeyEvent> parseKeyboardUpdateMechanism(Element e) {
		if(e.getChild("update_mechanism").getAttributeValue("name").equals("key_pressed"))
			return KeyMonitorer.INSTANCE;
		throw new Error();
	}

	public static PeriodicRefreshInfo parsePeriodicRefresh(Element modifier) {
		Element periodicRefresh = modifier.getChild("periodic_refresh");
		if(periodicRefresh == null)
			throw new Error("Please indicate a \"periodic_refresh\" field for:"+ modifier);
		else {
			if(periodicRefresh.getAttributeValue("period")==null)
				throw new Error("Please give a \"period\" attribute for:"+modifier+" "+periodicRefresh);
			return PeriodicRefreshInfo.newInstance(Long.parseLong(periodicRefresh.getAttributeValue("period")));
		}
	}

	public static int getHeight(Element e) {
		return Integer.parseInt(e.getChild("height").getAttributeValue("y"));
	}

	public static TextSource getTextSource(Element e) {
		Element child= e.getChild("text_source");
		Element variableBasedTextSource = child.getChild("variable_based_text_source");
		if(variableBasedTextSource != null)
		{
			Element variable = variableBasedTextSource.getChild("variable");
			return (TextSource) VariableManager.get(variable.getAttributeValue("name"));
		}
		throw new Error();
	}

	public static int getScrollingSpeed(Element e) 
	{
		return Integer.parseInt(e.getChild(XMLKeywords.SCROLLING_SPEED.getName())
				.getChild(XMLKeywords.SPEED.getName()).getAttributeValue(XMLKeywords.VALUE.getName()));
	}

	public static FileLocator getFolder(Element e) {
		Element fileLoc = e.getChild("folder_location");
		if(fileLoc==null)throw new Error("Expecting a \"folder_location\" field");
		
		if(fileLoc.getChild("variable")!= null)
		{
			String variableName = fileLoc.getChild("variable")
					.getAttributeValue("name");
			if(variableName == null) 
				throw new Error("Must define a \"name\" attribute for"+e+"/"+fileLoc);
			return VariableBasedFileLocator.newInstance((PathVariable)VariableManager.get(variableName));					
		}
			
		else if(fileLoc.getChild("hard_link")!= null)
			return StaticFileLocator.newInstance(fileLoc.getChild("hard_link").getAttributeValue("path"));
		else throw new Error();
	}

	public static FileLocator parsePathLocator(Element e) {
		if(e.getChild("folder_location")!= null)return getFolder(e);
		else if(e.getChild("file_location")!= null)return parseFileLocator(e);
		else throw new Error();
	}

	public static VariableType getVariableType(Element variable) {
		if(variable.getName().equals(XMLKeywords.VARIABLE.getName()))
			variable = variable.getChild(XMLKeywords.VARIABLE_TYPE.getName());
		
		return VariableType.parseFromXML(
				variable.getAttributeValue(XMLKeywords.TYPE.getName()));
	}

	public static void checkAllowedChildKeywords(Element variable, Predicate<String> keywordCheckerFor) {
		for(Element e: variable.getChildren())
			if(!keywordCheckerFor.test(e.getName()))
				throw new Error("Forbidden keyword:"+e.getName()+" at "+variable);
	}

	public static String parseVariableName(Element variable) {
	/*	Element res = variable.getChild(XMLKeywords.VARIABLE_NAME.getName());
		if(res==null)
			throw new Error("Missing name for a variable");*/
		return variable.getAttributeValue(XMLKeywords.NAME.getName());
	}

	public static Range parseRange(Element e) {
		return Range.newInstance(e.getAttributeValue(XMLKeywords.RANGE_MIN_VALUE.getName()),
				e.getAttributeValue(XMLKeywords.RANGE_MAX_VALUE.getName()));
	}

	public static String parseInitialValue(Element e) {
		return e.getAttributeValue(XMLKeywords.VALUE.getName());
	}

	public static Collection<VariableActuator> parseActuators(Element e) {
		List<VariableActuator>res =  new LinkedList<VariableActuator>();
		for(Element e2:e.getChildren())
			res.add(parseActuator(e2));
		return res;
	}

	private static VariableActuator parseActuator(Element e2) {
		if(e2.getName().equals(XMLKeywords.KEY_INCREASE_ACTUATOR.getName()))
			return IncreaseVariableActuator.newInstance(
					e2.getAttributeValue(XMLKeywords.KEY.getName()).charAt(0),
					Integer.parseInt(e2.getAttributeValue(XMLKeywords.INCREASE_BY.getName()))
					);
		
		if(e2.getName().equals(XMLKeywords.SWITCH_ACTUATOR.getName()))
			return SwitchVariableActuator.newInstance(e2.getAttributeValue(XMLKeywords.KEY.getName()).charAt(0));
		
		if(e2.getName().equals(XMLKeywords.SYNCHRONIZE_FROM_FILE_ACTUATOR.getName()))
			return SynchronizeFromFileVariableActuator.newInstance(e2);
			
			throw new Error();
	}

	public static boolean hasPeriodicRefreshInfos(Element e) {
		return e.getChild("periodic_refresh")!= null;
	}

	public static boolean isVariableBasedParameter() {
		throw new Error();
	}

	public static boolean isFileBasedParameter(Element e) {
		assert(
				e.getChild(XMLKeywords.FILE_BASED_PARAMETERS.getName())== null ||
				e.getChildren().size()==1);
		
		return e.getChild(XMLKeywords.FILE_BASED_PARAMETERS.getName())!= null;
	}

	public static boolean hasFileLocation(Element e) {
		return e.getChild(XMLKeywords.FILE_LOCATION.getName())!= null;
	}

	public static DisplayableItem parse(Element child) {
		throw new Error();
	}

	public static Collection<DisplayableItem> parseAnimations(Element child) {
		return ProcessXML.getDisplayableItemsFrom(child);
	}

	public static long parseInitialDisplayTime(Element e) {
		return Long.parseLong(e.getChild(XMLKeywords.INITIAL_DISPLAY_TIME.getName()).getAttributeValue(XMLKeywords.VALUE.getName()));
	}

	public static StretchingType parseStrechtingType(Element e) {
		Element displayTypeElem = e.getChild(XMLKeywords.IMAGE_STRETCHING_TYPE.getName());
		if(displayTypeElem == null)return StretchingType.getDefaultValue();
		Element mode =displayTypeElem.getChild(XMLKeywords.MODE.getName());
		String value = mode.getAttributeValue(XMLKeywords.VALUE.getName());
		if(value.equals(XMLKeywords.STRETCH_AND_BANDS.getName()))
			return StretchingType.STRETCH_AND_BANDS;
		if(value.equals(XMLKeywords.STRETCH.getName()))
			return StretchingType.STRETCH;
		if(value.equals(XMLKeywords.SCALE_NO_STRECHT_NO_BAND.getName()))
			return StretchingType.SCALE_NO_STRETCH_NO_BAND;
		throw new Error();
	}
	
	
	private static final Map<XMLKeywords, Function<Element, DisplayableItem>> toDisplayableItems = 
			new HashMap<XMLKeywords, Function<Element, DisplayableItem>>();
	static
	{
		toDisplayableItems.put(XMLKeywords.GENERIC_DISPLAYER, GenericDisplayer::newInstance);
		toDisplayableItems.put(XMLKeywords.IMAGE_ANIMATION, EventAdaptiveImageDisplayer::newInstance);
		toDisplayableItems.put(XMLKeywords.SLIDESHOW, SlideShow::newInstance);
		toDisplayableItems.put(XMLKeywords.NEWS_TICKER, NewsTicker::newInstance);
		toDisplayableItems.put(XMLKeywords.VIDEO_ANIMATION, VideoDisplayer::newInstance);
		toDisplayableItems.put(XMLKeywords.FPS, FPSDisplayer::newInstance);
		toDisplayableItems.put(XMLKeywords.TEXT_TYPER, TextTyper::newInstance);
		toDisplayableItems.put(XMLKeywords.POPUP, Popup::newInstance);
		toDisplayableItems.put(XMLKeywords.SOUND, SoundPlayer::newInstance);
		toDisplayableItems.put(XMLKeywords.HEARTBEAT_MONITOR, HeartBeatMonitor::newInstance);
		toDisplayableItems.put(XMLKeywords.CHROMATOGRAPHER, Chromatographer::newInstance);
		
		
		/*
	
	if(e.getName().equals(XMLKeywords.HEARTBEAT_MONITOR.getName()))
		return HeartBeatMonitor.generate(e);
	if(e.getName().equals(XMLKeywords.IMAGE_ANIMATION.getName()))
		return EventAdaptiveImageDisplayer.newInstance(e);
	if(e.getName().equals(XMLKeywords.FPS.getName()))
		return FPSDisplayer.newInstance(e);
	if(e.getName().equals(XMLKeywords.POPUP.getName()))
		return Popup.newInstance(e);
	
	if(e.getName().equals(XMLKeywords.SOUND.getName()))
		return SoundPlayer.newInstance(e);
	if(e.getName().equals(XMLKeywords.))
		
	switch(e.getName())
	{
	case "bar": return BarDisplayer.generate(e.getChildren());
	case "blinking_shape": DisplayedItemsManager.add(BlinkingShape.generate(e));break;
	
	case "variable_printer":DisplayedItemsManager.add(VariablePrinter.newInstance(e)); break;
	//case "hacking_frame": DisplayedItemsManager.add(TextPrinter.newInstance(e));break;
	
	
	case "dnasca": DisplayedItemsManager.add(DNAMatcher.newInstance(e));break;
	case "text_typer":DisplayedItemsManager.add(TextTyper.newInstance(e));break;
	case "news_ticker": DisplayedItemsManager.add(NewsTicker.newInstance(e));break;*/
	}

	public static Function<Element, DisplayableItem> getAnimationBuilder(XMLKeywords fromString) {
		if(!toDisplayableItems.containsKey(fromString))throw new Error("No builder for:"+fromString);
		return toDisplayableItems.get(fromString);
	}

	public static <T extends Variable<V>, V> AnimationSpecificVariableActuator parseAnimationSpecificActuator(Element e) {
		Element actuator = e.getChild(XMLKeywords.ANIMATION_SPECIFIC_VARIABLE_ACTUATOR.getName());
		Variable<V> v = XMLParser.parseVariable(actuator);
		AnimationSpecificVariableActuatorCause cause = XMLParser.parseAnimationTriggerCause(actuator);
		return AnimationSpecificVariableActuator.newInstance(v, cause);
	}

	public static AnimationSpecificVariableActuatorCause parseAnimationTriggerCause(Element e) {
		String causeName=e.getChild(XMLKeywords.ANIMATION_TRIGGER.getName()).
				getAttribute(XMLKeywords.CAUSE.getName()).getValue();
		if(causeName.equals(XMLKeywords.IS_VISIBLE.getName()))
			return AnimationSpecificVariableActuatorCause.IS_VISIBLE;
		throw new Error();
	}

	public static PauseTrigger parsePauseTrigger(Element e) {
		Element e2 = e.getChild(XMLKeywords.PAUSE_TRIGGER.getName());
		return VariableBasedPauseTrigger.newInstance((BooleanVariable)parseVariable(e2.getChild(XMLKeywords.VARIABLE_BASED.getName())));
	}


}
