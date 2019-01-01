package input.configuration;

import java.util.HashMap;
import java.util.Map;

import org.jdom2.Attribute;

public class TriggerManager {
	
	private Map<String, GenericEventPublisher> eventPublisherPerID = new HashMap<>();

	public GenericEventPublisher getEventProducerFor(String attribute) {
		return eventPublisherPerID.get(attribute);
	}

}
