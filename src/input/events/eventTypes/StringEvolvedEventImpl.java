package input.events.eventTypes;

public final class StringEvolvedEventImpl implements StringEvolvedEvent {

	private final String newVal;
	public StringEvolvedEventImpl(String newVal) {
		this.newVal = newVal; 
	}

	public static StringEvolvedEventImpl newInstance(String newVal) {
		return new StringEvolvedEventImpl(newVal);
	}
	
	public String getString()
	{
		return newVal;
	}
	
	public String toString()
	{
		return newVal;
	}

}
