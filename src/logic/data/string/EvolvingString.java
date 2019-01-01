package logic.data.string;

import java.util.EventListener;
import java.util.List;

import input.events.eventTypes.StringEvolvedEvent;
import input.events.publishers.LAnimaRPEventPublisher;

public interface EvolvingString extends LAnimaRPEventPublisher<StringEvolvedEvent>, TextSource {

	String getString();


}
