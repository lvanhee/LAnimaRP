package input.events.eventTypes;

import java.util.List;

public final class StringEvolvedEventImpl implements StringEvolvedEvent {

	private final String newVal;
	private StringEvolvedEventImpl(String newVal) {
		this.newVal = newVal; 
	}

	public static StringEvolvedEventImpl newInstance(String newVal) {
		return new StringEvolvedEventImpl(newVal);
	}
	
	public String getStrings()
	{
		return newVal;
	}
	
	public String toString()
	{
		return newVal.toString();
	}

}
