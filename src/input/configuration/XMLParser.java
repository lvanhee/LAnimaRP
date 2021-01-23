package input.configuration;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.geom.GeneralPath;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.swing.KeyStroke;

import org.jdom2.Element;

import draw.displayItems.DisplayableItem;
import draw.displayItems.advanced.Popup;
import draw.displayItems.advanced.chromatograph.Chromatographer;
import draw.displayItems.advanced.dnasca.DNASCA;
import draw.displayItems.advanced.dnasca.DynamicallyUpdatableDisplayer;
import draw.displayItems.advanced.dnasca.HeartBeatMonitor;
import draw.displayItems.images.ImageDisplayer;
import draw.displayItems.images.GenericParameters;
import draw.displayItems.images.SlideShow;
import draw.displayItems.shapes.BlinkingShape;
import draw.displayItems.sound.SoundPlayerDisplayableItem;
import draw.displayItems.sound.SoundPlayerDisplayableItem.Mode;
import draw.displayItems.text.FPSDisplayer;
import draw.displayItems.text.NewsTicker;
import draw.displayItems.text.TextPrompt;
import draw.displayItems.text.UserTextTyper;
import draw.displayItems.text.textprinter.PreSetPassiveAppendTextAreaDrawer.AppendMethods;
import draw.displayItems.videos.VariableBasedPauseTrigger;
import draw.displayItems.videos.VideoDisplayer;
import input.events.eventTypes.LAnimaRPEvent;
import input.events.eventTypes.LAnimaRPKeyEvent;
import input.events.publishers.FileBasedEvolvingString;
import input.events.publishers.KeyMonitorer;
import input.events.publishers.LAnimaRPEventPublisher;
import input.events.triggers.PauseTrigger;
import input.online.google.GoogleDocsUtils;
import logic.data.PeriodicRefreshInfo;
import logic.data.Range;
import logic.data.drawing.StretchingType;
import logic.data.fileLocators.URLLocator;
import logic.data.fileLocators.StaticURLPathLocator;
import logic.data.fileLocators.VariableBasedFileLocator;
import logic.data.string.TextSource;
import logic.variables.actuators.AnimationSpecificVariableActuator;
import logic.variables.actuators.AnimationSpecificVariableActuator.AnimationSpecificVariableActuatorCause;
import logic.variables.actuators.IncreaseVariableActuator;
import logic.variables.actuators.SwitchVariableActuator;
import logic.variables.actuators.SynchronizeFromFileVariableActuator;
import logic.variables.actuators.VariableActuator;
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
		
		//hex
		if(!s.contains(","))
			return new Color(
					Integer.valueOf( s.substring( 1, 3 ), 16 ),
					Integer.valueOf( s.substring( 3, 5 ), 16 ),
					Integer.valueOf( s.substring( 5, 7 ), 16 ) );
		
		String[] split = s.split(",");
		int r = Integer.valueOf( split[0] );
		int g = Integer.valueOf( split[1] );
		int b = Integer.valueOf( split[2] );
		return new Color(
				r,
				g,
				b);
	}

	public static Rectangle parseRectangle(Element e) {
		if(e==null)
			throw new Error();
		if(e.getChild("full_screen")!=null)
			return new Rectangle(0, 0, DisplayWindow.getWindowWidth(), DisplayWindow.getWindowHeight());
		if(!e.getName().equals(XMLKeywords.DISPLAY_AREA.getName()))
			e = e.getChild(XMLKeywords.DISPLAY_AREA.getName());
		if(e==null)
			throw new Error("Missing a "+XMLKeywords.DISPLAY_AREA.getName()+" field");
		int x = Integer.parseInt(e.getAttributeValue(XMLKeywords.X.getName()));
		int y = Integer.parseInt(e.getAttributeValue(XMLKeywords.Y.getName()));
		int w = Integer.parseInt(e.getAttributeValue(XMLKeywords.WIDTH.getName()));
		int h = Integer.parseInt(e.getAttributeValue(XMLKeywords.HEIGHT.getName()));
		
		return new Rectangle(x,y,w,h);
	}

	
	public static Variable parseVariable(Element e, LAnimaRPContext context) {
		if(e.getAttributeValue(XMLKeywords.VARIABLE_NAME.getName())!= null)
			return context.getVariable(e.getAttributeValue(XMLKeywords.VARIABLE_NAME.getName()));
		//return VariableManager.get(e.getChild("variable").getAttributeValue("name"));
		throw new Error("No variable name given");
	}

	public static Color parseColor(Element e) {
		return parseColor(e.getAttributeValue(XMLKeywords.COLOR.getName()));
	}

	public static Point parsePosition(Element e) {
		if(e.getChild(XMLKeywords.POSITION.getName())==null)
			throw new Error();
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

	public static URLLocator parseFileLocator(Element e, LAnimaRPContext context) {
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
			return VariableBasedFileLocator.newInstance((PathVariable)context.getVariable(variableName));					
		}
			
		else if(fileLoc.getChild(XMLKeywords.HARD_PATH.getName())!= null)
			return StaticURLPathLocator.newInstance(fileLoc.getChild(XMLKeywords.HARD_PATH.getName()).getAttributeValue("path"));
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
		if(modifier.getAttributeValue(XMLKeywords.REFRESH_PERIOD.getName())==null)
			modifier = modifier.getChild(XMLKeywords.REFRESH_PERIOD.getName());
		String refreshPeriod = modifier.getAttributeValue(XMLKeywords.REFRESH_PERIOD.getName());
		if(refreshPeriod == null)
			throw new Error("Please indicate a \""+XMLKeywords.REFRESH_PERIOD+"\" field for:"+ modifier);
		else {
			return PeriodicRefreshInfo.newInstance(Long.parseLong(refreshPeriod));
		}
	}

	public static int getHeight(Element e) {
		return Integer.parseInt(e.getChild("height").getAttributeValue("y"));
	}

	public static TextSource getTextSource(Element e, LAnimaRPContext c) {
		
		Element child= e;
		if(!e.getName().equals(XMLKeywords.TEXT_SOURCE.getName())) child = e.getChild("text_source");
		
		
			
		if(child.getChild(XMLKeywords.FILE_LOCATION.getName())!= null)
		{
			URLLocator loc = parsePathLocator(child, c);
			
			if(isUpdateable(child))
				return FileBasedEvolvingString.newInstance(child, c);
			else throw new Error();
		}
		Element variableBasedTextSource = child.getChild("variable_based_text_source");
		if(variableBasedTextSource != null)
		{
			
			Element variable = variableBasedTextSource.getChild(XMLKeywords.VARIABLE.getName());
			return (TextSource) c.getVariable(variable.getAttributeValue(XMLKeywords.NAME.getName()));
		}
		throw new Error();
	}

	private static boolean isUpdateable(Element child) {
		return child.getChild(XMLKeywords.REFRESH_PERIOD.getName())!= null;
	}

	public static int getScrollingSpeed(Element e) 
	{
		return Integer.parseInt(e.getChild(XMLKeywords.SCROLLING_SPEED.getName())
				.getChild(XMLKeywords.SPEED.getName()).getAttributeValue(XMLKeywords.VALUE.getName()));
	}

	public static URLLocator getFolder(Element e, LAnimaRPContext context) {
		Element fileLoc = e.getChild("folder_location");
		if(fileLoc==null)throw new Error("Expecting a \"folder_location\" field");
		
		if(fileLoc.getChild("variable")!= null)
		{
			String variableName = fileLoc.getChild("variable")
					.getAttributeValue("name");
			if(variableName == null) 
				throw new Error("Must define a \"name\" attribute for"+e+"/"+fileLoc);
			return VariableBasedFileLocator.newInstance((PathVariable)context.getVariable(variableName));					
		}
			
		else if(fileLoc.getChild(XMLKeywords.HARD_PATH.getName())!= null)
			return StaticURLPathLocator.newInstance(fileLoc.getChild(XMLKeywords.HARD_PATH.getName()).getAttributeValue("path"));
		else 
			throw new Error();
	}

	public static URLLocator parsePathLocator(Element e, LAnimaRPContext c) {
		if(e.getChild("folder_location")!= null)return getFolder(e,c);
		else if(e.getChild("file_location")!= null)return parseFileLocator(e,c);
		else throw new Error();
	}

	public static VariableType getVariableType(Element variable) {
			return VariableType.parseFromXML(variable.getAttributeValue(XMLKeywords.TYPE.getName()));		
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

	public static Set<VariableActuator> parseActuators(Element e) {
		Set<VariableActuator>res =  new HashSet<VariableActuator>();
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
			return SwitchVariableActuator.newInstance(XMLParser.parseKeyOnKeyboardIntoKeycode(e2));
		
		if(e2.getName().equals(XMLKeywords.SYNCHRONIZE_FROM_FILE_ACTUATOR.getName()))
			return SynchronizeFromFileVariableActuator.newInstance(e2);
		
		/*if(e2.getName().equals(XMLKeywords.ANYKEY_INCREASE_ACTUATOR.getName()))
			return IncreaseVariableActuator.newInstance(
					KeyTypes.SpecialKeyTypes.WILDCARD,
					Integer.parseInt(e2.getAttributeValue(XMLKeywords.INCREASE_BY.getName()))
					);*/
			
			throw new Error();
	}

	private static int parseKeyOnKeyboardIntoKeycode(Element e2) {
		if(e2.getAttributeValue(XMLKeywords.KEY.getName())!= null)
		{
			String s = e2.getAttributeValue(XMLKeywords.KEY.getName());
			if(s.equals("control"))return KeyEvent.VK_CONTROL;
			return java.awt.event.KeyEvent.getExtendedKeyCodeForChar(e2.getAttributeValue(XMLKeywords.KEY.getName()).charAt(0));
			}
		
		if(e2.getAttributeValue(XMLKeywords.KEYCODE.getName())!= null)
			return Integer.parseInt(e2.getAttributeValue(XMLKeywords.KEYCODE.getName()));
		
		throw new Error();
				
		
	}

	public static boolean hasPeriodicRefreshInfos(Element e) {
		return e.getChild(XMLKeywords.REFRESH_PERIOD.getName())!= null;
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
	
	
	private static final Map<XMLKeywords, BiFunction<Element, LAnimaRPContext, DisplayableItem>> displayableItemFromKeywordGenerator = 
			new HashMap<XMLKeywords, BiFunction<Element,LAnimaRPContext, DisplayableItem>>();
	
	static
	{
		displayableItemFromKeywordGenerator.put(XMLKeywords.DYNAMICALLY_UPDATABLE_ANIMATION, DynamicallyUpdatableDisplayer::newInstance);
		displayableItemFromKeywordGenerator.put(XMLKeywords.IMAGE_ANIMATION, ImageDisplayer::newInstance);
		displayableItemFromKeywordGenerator.put(XMLKeywords.SLIDESHOW, SlideShow::newInstance);
		displayableItemFromKeywordGenerator.put(XMLKeywords.NEWS_TICKER, NewsTicker::newInstance);
		displayableItemFromKeywordGenerator.put(XMLKeywords.VIDEO_ANIMATION, VideoDisplayer::newInstance);
		displayableItemFromKeywordGenerator.put(XMLKeywords.FPS, FPSDisplayer::newInstance);
		displayableItemFromKeywordGenerator.put(XMLKeywords.TEXT_TYPER, UserTextTyper::newInstance);
		displayableItemFromKeywordGenerator.put(XMLKeywords.POPUP, Popup::newInstance);
		displayableItemFromKeywordGenerator.put(XMLKeywords.SOUND, SoundPlayerDisplayableItem::newInstance);
		displayableItemFromKeywordGenerator.put(XMLKeywords.HEARTBEAT_MONITOR, HeartBeatMonitor::newInstance);
		displayableItemFromKeywordGenerator.put(XMLKeywords.CHROMATOGRAPHER, Chromatographer::newInstance);
		displayableItemFromKeywordGenerator.put(XMLKeywords.BACKGROUND, FullScreenFiller::newInstance);
		displayableItemFromKeywordGenerator.put(XMLKeywords.DNASCA, DNASCA::newInstance);
		displayableItemFromKeywordGenerator.put(XMLKeywords.SWITCHEABLE_ANIMATION, SwitcheableAnimation::newInstance);
		displayableItemFromKeywordGenerator.put(XMLKeywords.BLINKING_SHAPE, BlinkingShape::newInstance);
		displayableItemFromKeywordGenerator.put(XMLKeywords.GENERIC_MEDIUM_DISPLAYER,
				GenericMediumDisplayer::newInstance);
		displayableItemFromKeywordGenerator.put(XMLKeywords.FILE_SYSTEM,
				FileSystem::newInstance);
		displayableItemFromKeywordGenerator.put(XMLKeywords.BOOLEAN_ANIMATION_DISPLAYER,
				BooleanAnimationDisplayer::newInstance);
		displayableItemFromKeywordGenerator.put(XMLKeywords.TEMPORARILY_DISPLAYED_ANIMATION,
				TemporarilyDisplayedAnimation::newInstance);
		
		displayableItemFromKeywordGenerator.put(XMLKeywords.VARIABLE_BASED_SWITCHEABLE_ANIMATION,
				VariableBasedSwitcheableAnimation::newInstance);
		displayableItemFromKeywordGenerator.put(XMLKeywords.TEXT_PROMPT,
				TextPrompt::newInstance);
		displayableItemFromKeywordGenerator.put(XMLKeywords.USER_TEXT_TYPER,
				UserTextTyper::newInstance);
		
		
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

	public static BiFunction<Element, LAnimaRPContext, DisplayableItem> getAnimationBuilder(XMLKeywords fromString) {
		if(!displayableItemFromKeywordGenerator.containsKey(fromString))
			throw new Error("No builder for:"+fromString);
		return displayableItemFromKeywordGenerator.get(fromString);
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

	public static GenericParameters parseGenericParameters(Element e, LAnimaRPContext context) {
		if(e.getChild(XMLKeywords.VISIBILITY.getName())!=null)
		{
			Element visibilityElement = e.getChild(XMLKeywords.VISIBILITY.getName());
			return GenericParameters.newInstance(
					parseVariableName(visibilityElement.getChild(XMLKeywords.VARIABLE.getName())), context);
		}
		return GenericParameters.newInstance(context);
	}

	public static AppendMethods parseTextTypingSpeed(Element e) {
		if(e.getChild(XMLKeywords.TEXT_TYPING_SPEED.getName())!=null)
		{
			Element tts = e.getChild(XMLKeywords.TEXT_TYPING_SPEED.getName());
			
			if(tts.getAttribute(XMLKeywords.VALUE.getName()).getValue()
					.equals(XMLKeywords.ONE_WORD_PER_PRESS.getName()))
				return AppendMethods.ONE_WORD_PER_PRESS;
			else throw new Error();
		}
		return AppendMethods.ONE_CHAR_PER_ACTION;
		
	}

	public static Mode parseSoundMode(Element e) {
			Element tts = e.getChild(XMLKeywords.SOUND_MODE.getName());
			if(tts.getAttribute(XMLKeywords.VALUE.getName())==null)throw new Error("Missing sound mode for "+e);
			
			if(tts.getAttribute(XMLKeywords.VALUE.getName()).getValue()
					.equals(XMLKeywords.REPEAT_FORVER_WHEN_VISIBLE.getName()))
				return Mode.FOREVER_WHEN_DRAWN;
			else if(tts.getAttribute(XMLKeywords.VALUE.getName()).getValue()
					.equals(XMLKeywords.ONE_SHOT.getName()))
				return Mode.ONE_SHOT;
			else throw new Error();		
	}

	public static boolean parseFullScreen(Element head) {
		if(head.getChild(XMLKeywords.FULLSCREEN.getName())==null)
			throw new Error("Missing a description for determining whether the display should be made full screen or not\n"
					+ " Add a <fullscreen value=\"true\"> or <fullscreen value=\"false\"> in the field display_parameters");
		if(head.getChild(XMLKeywords.FULLSCREEN.getName()).getAttributeValue(XMLKeywords.VALUE.getName())==null)
		{
			throw new Error("Missing a value for the \"fullscreen\" element"
					+ " Set <fullscreen value=\"true\"> or <fullscreen value=\"false\"> in the field fullscreen");
		}
		return Boolean.parseBoolean(head.getChild(XMLKeywords.FULLSCREEN.getName()).getAttributeValue(XMLKeywords.VALUE.getName()));
	}
	
	public static LAnimaRPEventPublisher<? extends LAnimaRPEvent> parseXMLEventProducer(Element e)
	{
		if(e.getName().equals(XMLKeywords.KEY_PRESSED_EVENT_PUBLISHER.getName()))
			return KeyPressedEventPublisher.newInstance(e.getAttributeValue("value"));
		throw new Error();
	}

	public static DisplayableItem parseDisplayableItem(Element rootElement, LAnimaRPContext context) {
		//System.out.println("bing"+rootElement);
		
		Element localElement = rootElement;
		if(!localElement.getName().equals(XMLKeywords.DISPLAYED_ANIMATIONS.getName()))
			localElement = localElement.getChild(XMLKeywords.DISPLAYED_ANIMATIONS.getName());
		if(localElement==null)
			throw new Error(rootElement+" must have a child:"+XMLKeywords.DISPLAYED_ANIMATIONS.getName());
		return 
				ComboAnimation.newInstance(localElement.getChildren()
				.stream().map(x->ProcessXML.getDisplayableItemFrom(x,context))
				.collect(Collectors.toList()));
	
	}

	public static TextParameters parseTextParameters(Element child) {
		Color c = parseColor(child);
		return TextParameters.newInstance(c);
	}

	public static String getName(Element x) {
		return x.getAttributeValue(XMLKeywords.NAME.getName());
	}

	public static Consumer<String> parseActionTrigger(Element e, LAnimaRPContext context) {
		Element child = e.getChild(XMLKeywords.ACTION_TRIGGER.getName());
		if(child.getAttribute(XMLKeywords.ACTION.getName())!=null)
		{
			if(child.getChild(XMLKeywords.FILE_LOCATION.getName())!=null)
			{
				URLLocator loc = parseFileLocator(child, context);
				if(URLLocator.isGoogleDocsURL(loc))
				{
					return x->{
						try {
							GoogleDocsUtils.printNewParagraphAtStartOfDocumentWithTimestamp(loc, x);
						} catch (GeneralSecurityException | IOException e1) {
							e1.printStackTrace();
						}
					};
				}
			}
		}
		throw new Error();
	}

	public static List<String> parseEnum(Element e) {
		List<String>res = new ArrayList<>();
		for(Element e2:e.getChildren())
			res.add(e2.getAttributeValue(XMLKeywords.VALUE.getName()));
		return res;
	}


}
