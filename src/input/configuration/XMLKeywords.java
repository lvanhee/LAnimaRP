package input.configuration;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.jdom2.Element;

import logic.variables.variableTypes.VariableType;

public enum XMLKeywords {
	//Variable keywords
	VARIABLE_ACTUATORS("variable_actuators"),
	VARIABLE("variable"),
	VARIABLE_TYPE("variable_type"), 
	TYPE("type"),
	BOUNDED_INT_VARIABLE_TYPE("bounded_int"), 
	RANGE("range"), 
	INITIAL_VALUE("initial_value"),
	VARIABLE_NAME("variable_name"),
	RANGE_MIN_VALUE("min_value"), 
	RANGE_MAX_VALUE("max_value"),
	VALUE("value"),
	//Actuator keywords
	KEY_INCREASE_ACTUATOR("key_increase_actuator"),
	VARIABLE_BASED_SWITCHEABLE_ANIMATION("variable_based_switcheable_animation"),
	KEY("key"), 
	INCREASE_BY("increase_by"),
	BOOLEAN_VARIABLE_TYPE("boolean"),
	SWITCH_ACTUATOR("key_switch_actuator"), 
	STRING_VARIABLE_TYPE("string"), 
	SYNCHRONIZE_FROM_FILE_ACTUATOR("synchronize_from_file_actuator"),
	PATH_VARIABLE_TYPE("path_variable"),
	HARD_PATH("hard_path"),
	FILE_LOCATION("file_location"), 
	NAME("name"), 
	DYNAMICALLY_UPDATABLE_ANIMATION("xml_textsource_animation"),
	//Animation keywords
	BOOLEAN_ANIMATION_DISPLAYER("boolean_animation_displayer"),
	BLINKING_SHAPE("blinking_shape"),
	FILE_SYSTEM("filesystem_displayer"),
	GENERIC_MEDIUM_DISPLAYER("generic_medium_displayer"),
	SLIDESHOW("slideshow"),
	VIDEO_ANIMATION("video"), 
	NEWS_TICKER("news_ticker"), 
	IMAGE_ANIMATION("image"), 
	FILE_BASED_PARAMETERS("file_based_parameters"), 
	HEARTBEAT_MONITOR("heartbeat_monitor"), 
	POPUP("popup"), 
	PERIOD_ON("period_on"), 
	PERIOD_OFF("period_off"),
	DISPLAYED_ANIMATIONS("displayed_animations"), 
	INITIAL_DISPLAY_TIME("initial_display_time"), 
	SOUND("sound"), 
	IMAGE_STRETCHING_TYPE("stretching"), 
	MODE("mode"), 
	STRETCH_AND_BANDS("stretch_and_bands"), 
	SCROLLING_SPEED ("scrolling_speed"), 
	SPEED("speed"), 
	SCALE_NO_STRECHT_NO_BAND("scale_no_stretch_no_bands"), 
	STRETCH("stretch"), 
	FPS("fps"), 
	POSITION("position"),
	TEXT_TYPER("user_text_typer"), 
	INCREASE_ACTUATOR("increase_actuator"),
	ANIMATION_SPECIFIC_VARIABLE_ACTUATOR("animation_specific_variable_actuator"), 
	ANIMATION_TRIGGER("animation_trigger"), 
	CAUSE("cause"),
	IS_VISIBLE("is_visible"), 
	PAUSE_TRIGGER("pause_trigger"), 
	VARIABLE_BASED("variable_based"),
	BACKGROUND("background"),
	DNASCA("dnasca"),
	CHROMATOGRAPHER("chromatographer"), 
	DISPLAY_PARAMETERS("display_parameters"), 
	SCREEN_SIZE("screen_size"),
	PLAYLIST("playlist"),
	VISIBILITY("visibility"), 
	ANYKEY_INCREASE_ACTUATOR("anykey_increase_actuator"),
	KEYCODE("keycode"), 
	TEXT_TYPING_SPEED("text_typing_speed"),
	TEXT_PROMPT("text_prompt"),
	ONE_WORD_PER_PRESS("one_word_per_event"), 
	SOUND_MODE("sound_mode"),
	REPEAT("repeat"),
	ONE_SHOT("one_shot"),
	FULLSCREEN("fullscreen"),
	ENUM("enum"),
	EVENT_PUBLISHERS("event_publishers"),
	KEY_PRESSED_EVENT_PUBLISHER("key_pressed_event_publisher"),
	SWITCHEABLE_ANIMATION("switcheable_animation"), 
	TRIGGER("trigger"), 
	EVENT_PRODUCER("event_producer"), 
	REFRESH_PERIOD("refresh_period"),
	DISPLAY_AREA("display_area"), 
	X("x"), 
	Y("y"), 
	WIDTH("width"), 
	HEIGHT("height"), 
	IMAGE_WHEN_TYPING("image_when_typing_filename"),
	FILENAME_QUERY_AREA("filename_query_area"), 
	FAILURE_TEXT("failure_text_parameters"), 
	COLOR("color"), 
	TEXT_DISPLAYER_CONFIGURATION("text_displayer_configuration"), 
	SOUND_ON_TYPE("sound_on_type"),
	TEMPORARILY_DISPLAYED_ANIMATION("temporarily_displayed_animation"),
	DURATION("duration"), 
	BLINKING("blinking"), 
	SOLID("solid"), TEXT_SOURCE("text_source"),
	USER_TEXT_TYPER("user_text_typer"), ACTION_TRIGGER("action_trigger"), ACTION("action"), VALUES("values"),
	FAST_FORWARD_TO_LAST_PAGE("fast_forward_to_last_page"),
	NOTIFICATION("notification"), UPDATED_VALUE("updated_value"), RAW_TEXT("raw_text"), SHUFFLE("shuffle"),
	ENUM_ANIMATION("enum_animation"),
	UNFOLDING_MODE("unfolding_mode"), DEFAULT("default"), WORD("word");
	;
	
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
		case ENUM: return Arrays.asList(VARIABLE_TYPE, VARIABLE_NAME, VARIABLE_ACTUATORS, INITIAL_VALUE, VALUES);
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
