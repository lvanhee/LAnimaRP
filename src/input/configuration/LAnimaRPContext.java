package input.configuration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jdom2.Attribute;

import input.events.eventTypes.LAnimaRPEvent;
import input.events.publishers.LAnimaRPEventPublisher;
import logic.variables.variableTypes.BooleanVariable;
import logic.variables.variableTypes.Variable;

public class LAnimaRPContext {
	
	private final Map<String, LAnimaRPEventPublisher<? extends LAnimaRPEvent>> eventPublishers= new HashMap<>();
	private final Map<String, Variable> variables;
	
	private LAnimaRPContext(Map<String, LAnimaRPEventPublisher<? extends LAnimaRPEvent>> eventProducers, Set<Variable> vars) {
		this.eventPublishers.putAll(eventProducers);
		variables = vars.stream().collect(Collectors.toMap(x->x.getName(), Function.identity()));
	}
	public Set<LAnimaRPEventPublisher<? extends LAnimaRPEvent>> getEventProducers() {
		return eventPublishers.values().stream().collect(Collectors.toSet());
	}
	public static LAnimaRPContext newInstance(Map<String, LAnimaRPEventPublisher<? extends LAnimaRPEvent>> eventProducers, Set<Variable> variables) {
		return new LAnimaRPContext(eventProducers, variables);
	}
	public LAnimaRPEventPublisher<? extends LAnimaRPEvent> getEventProducersFor(String eventProducerName) {
		return eventPublishers.get(eventProducerName);
	}
	
	public String toString()
	{
		return eventPublishers.toString();
	}
	public Variable getVariable(String attributeValue) {
		return variables.get(attributeValue);
	}

}
