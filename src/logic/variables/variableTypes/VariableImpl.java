package logic.variables.variableTypes;

import input.events.eventTypes.VariableEvolvedEvent;
import input.events.listeners.LAnimaRPEventListener;
import input.events.publishers.GenericPublisher;

public class VariableImpl<T> implements Variable<T>{
	
	private String variableName;
	
	private T value;
	
	protected VariableImpl(String variableName2) {
		variableName = variableName2;
	}

	@Override
	public String getName() {
		return variableName;
	}

	private final GenericPublisher<VariableEvolvedEvent>publisher = GenericPublisher.newInstance();

	@Override
	public void subscribe(LAnimaRPEventListener<VariableEvolvedEvent> el) {
		publisher.subscribe(el);
	}

	@Override
	public void publish(VariableEvolvedEvent newInstance) {
		publisher.publish(newInstance);
	}

	@Override
	public void unsubscribe(LAnimaRPEventListener<VariableEvolvedEvent> el) {
		publisher.unsubscribe(el);
	}

	@Override
	public void setValue(T value) {
		this.value = value;
	}
	
	

}
