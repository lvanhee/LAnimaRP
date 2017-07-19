package input.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.jdom2.Element;

import draw.displayItems.advanced.Popup;
import draw.displayItems.advanced.dnasca.DNASCA;
import draw.displayItems.advanced.dnasca.GenericDisplayer;
import draw.displayItems.advanced.dnasca.HeartBeatMonitor;
import draw.displayItems.images.EventAdaptiveImageDisplayer;
import draw.displayItems.images.SlideShow;
import draw.displayItems.shapes.BarDisplayer;
import draw.displayItems.shapes.BlinkingShape;
import draw.displayItems.sound.SoundPlayer;
import draw.displayItems.text.FPSDisplayer;
import draw.displayItems.text.NewsTicker;
import draw.displayItems.text.TextTyper;
import draw.displayItems.text.VariablePrinter;
import draw.displayItems.videos.VideoDisplayer;
import logic.variables.variableTypes.VariableType;

public enum XMLKeywords {
	VARIABLE_ACTUATORS("variable_actuators"),
	VARIABLE("variable"),
	VARIABLE_TYPE("variable_type"), 
	TYPE("type"),
	BOUNDED_INT_VARIABLE_TYPE("bounded_int"), 
	RANGE("range"), INITIAL_VALUE("initial_value"),
	VARIABLE_NAME("variable_name"), RANGE_MIN_VALUE("min_value"), 
	RANGE_MAX_VALUE("max_value"),
	VALUE("value"),
	KEY_INCREASE_ACTUATOR("key_increase_actuator"), 
	KEY("key"), INCREASE_BY("increase_by"), BOOLEAN_VARIABLE_TYPE("boolean"), SWITCH_ACTUATOR("key_switch_actuator"), STRING_VARIABLE_TYPE("string"), 
	SYNCHRONIZE_FROM_FILE_ACTUATOR("synchronize_from_file_actuator"), PATH_VARIABLE_TYPE("path_variable"), HARD_PATH("hard_path"), 
	FILE_LOCATION("file_location"), NAME("name"), GENERIC_DISPLAYER("generic_displayer"), SLIDESHOW("slideshow"),
	VIDEO_ANIMATION("video"), NEWS_TICKER("news_ticker"), IMAGE_ANIMATION("image"), 
	FILE_BASED_PARAMETERS("file_based_parameters"), HEARTBEAT_MONITOR("heartbeat_monitor"), POPUP("popup"), PERIOD_ON("period_on"), 
	PERIOD_OFF("period_off"), DISPLAYED_ANIMATIONS("displayed_animations"), INITIAL_DISPLAY_TIME("initial_display_time"), SOUND("sound"), 
	IMAGE_STRETCHING_TYPE("stretching"), MODE("mode"), STRETCH_AND_BANDS("stretch_and_bands"), SCROLLING_SPEED ("scrolling_speed"), 
	SPEED("speed"), SCALE_NO_STRECHT_NO_BAND("scale_no_stretch_no_bands"), STRETCH("stretch"), FPS("fps"), POSITION("position"),
	TEXT_TYPER("text_typer"), INCREASE_ACTUATOR("increase_actuator"),
	ANIMATION_SPECIFIC_VARIABLE_ACTUATOR("animation_specific_variable_actuator"), ANIMATION_TRIGGER("animation_trigger"), 
	CAUSE("cause"),
	IS_VISIBLE("is_visible"), 
	PAUSE_TRIGGER("pause_trigger"), 
	VARIABLE_BASED("variable_based"),
	CHROMATOGRAPHER("chromatographer");
	
	private final String keyName;
	private XMLKeywords(String s)
	{
		keyName = s;
	}
	public String getName() {
		return keyName;
	}
	public static Predicate<String> getKeywordCheckerFor(VariableType variableType, Element variable2) {
		return x->getAllowedKeywordsFor(variableType).stream().map(y-> y.getName()).collect(Collectors.toList()).contains(x);		
	}
	
	private static Collection<XMLKeywords> getAllowedKeywordsFor(VariableType variableType) {
		switch(variableType)
		{
		case BOUNDED_INT: return  Arrays.asList(VARIABLE_TYPE,VARIABLE_NAME,VARIABLE_ACTUATORS, RANGE, INITIAL_VALUE);
		case BOOLEAN: return Arrays.asList(VARIABLE_TYPE, VARIABLE_NAME,VARIABLE_ACTUATORS,INITIAL_VALUE);
		case STRING: return Arrays.asList(VARIABLE_TYPE, VARIABLE_NAME,VARIABLE_ACTUATORS, INITIAL_VALUE);
		case PATH: return Arrays.asList(VARIABLE_TYPE, VARIABLE_NAME,VARIABLE_ACTUATORS, INITIAL_VALUE);
		default: throw new Error();
		}
	}
	public static Collection<XMLKeywords> getAllowedChildrenFor(XMLKeywords kw) {
		switch(kw)
		{
		case FILE_LOCATION:return Arrays.asList(HARD_PATH, VARIABLE_NAME);
		default:throw new Error();
		}
	}
	public static XMLKeywords fromString(String x) {
		for(XMLKeywords xkw:XMLKeywords.values())
			if(xkw.keyName.equals(x))return xkw;
		throw new Error("Unknown keyword:"+x);
	}
}
