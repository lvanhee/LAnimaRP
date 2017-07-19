package logic.variables.variableTypes;

import java.io.File;
import java.util.Map;

import input.configuration.XMLParser;
import input.configuration.XMLVariableBuilder;
import input.events.eventTypes.VariableEvolvedEvent;
import input.events.listeners.LAnimaRPEventListener;
import input.events.publishers.GenericPublisher;
import logic.data.fileLocators.FileLocator;
import logic.data.fileLocators.FileManagerUtils;
import logic.data.fileLocators.StaticFileLocator;
import logic.data.string.UpdatableWithString;

public class PathVariable implements Variable<FileLocator>, UpdatableWithString{
	private final String name;
	private FileLocator value = null;
	private PathVariable(String string, FileLocator def) {
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
		value = StaticFileLocator.newInstance(s);
	}

	public FileLocator getPath() {
		if(value==null)
			throw new Error("Requesting value of an uninitialized variable");
		return value;
	}

	public static PathVariable newInstance(XMLVariableBuilder xmlVariableBuilder) {
		return new PathVariable(xmlVariableBuilder.getVariableName(), 
				StaticFileLocator.newInstance(xmlVariableBuilder.getInitialValue()));
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
	public void setValue(FileLocator value) {
		this.value = value;
	}
	
	

}
