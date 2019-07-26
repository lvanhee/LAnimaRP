package logic.variables.variableTypes;

import java.io.File;
import java.util.Map;

import input.configuration.XMLParser;
import input.configuration.XMLVariableBuilder;
import input.events.eventTypes.VariableEvolvedEvent;
import input.events.listeners.LAnimaRPEventListener;
import input.events.publishers.GenericPublisher;
import logic.data.fileLocators.URLLocator;
import logic.data.fileLocators.URLManagerUtils;
import logic.data.fileLocators.StaticURLPathLocator;
import logic.data.string.UpdatableWithString;

public class PathVariable implements Variable<URLLocator>, UpdatableWithString{
	private final String name;
	private URLLocator value = null;
	private PathVariable(String string, URLLocator def) {
		name = string;
		value = def;
	}

	@Override
	public String getName() {
		return name;
	}
	
	public String toString()
	{
		return name+":"+value;
	}

	@Override
	public void updateFrom(String s) {
		value = StaticURLPathLocator.newInstance(s);
	}

	public URLLocator getPath() {
		if(value==null)
			throw new Error("Requesting value of an uninitialized variable");
		return value;
	}

	public static PathVariable newInstance(XMLVariableBuilder xmlVariableBuilder) {
		return new PathVariable(xmlVariableBuilder.getVariableName(), 
				StaticURLPathLocator.newInstance(xmlVariableBuilder.getInitialValue()));
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
	public void setValue(URLLocator value) {
		this.value = value;
	}
	
	

}
