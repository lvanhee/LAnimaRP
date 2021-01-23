package logic.data.string;

import input.configuration.GenericEventPublisher;
import input.events.eventTypes.LAnimaRPKeyEventImpl;
import input.events.eventTypes.StringEvolvedEvent;
import input.events.eventTypes.StringEvolvedEventImpl;
import input.events.listeners.LAnimaRPEventListener;
import input.events.publishers.LAnimaRPEventPublisher;

public class MutableString implements EvolvingString {
	
	private final GenericEventPublisher publisher = GenericEventPublisher.newInstance();
	private String currentString="";

	@Override
	public void subscribe(LAnimaRPEventListener<StringEvolvedEvent> el) {
		publisher.subscribe((LAnimaRPEventListener)el);
	}

	@Override
	public void publish(StringEvolvedEvent newInstance) {
		publisher.publish(newInstance);
	}

	@Override
	public void unsubscribe(LAnimaRPEventListener<StringEvolvedEvent> el) {
		publisher.unsubscribe((LAnimaRPEventListener)el);
	}

	@Override
	public String getString() {
		return currentString;
	}

	public static MutableString newInstance() {
		return new MutableString();
	}

	public void setString(String string) {
		currentString = string;
		publisher.publish(StringEvolvedEventImpl.newInstance(currentString));
	}
	
	public String toString() {return currentString;}

}
