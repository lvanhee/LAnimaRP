package logic.data.string;

import java.util.EventListener;

import input.events.eventTypes.StringEvolvedEvent;
import input.events.publishers.LAnimaRPEventPublisher;

public interface EvolvingString extends LAnimaRPEventPublisher<StringEvolvedEvent>, TextSource {

	String getString();


}
