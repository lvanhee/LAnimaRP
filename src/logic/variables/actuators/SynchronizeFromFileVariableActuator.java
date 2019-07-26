package logic.variables.actuators;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.jdom2.Element;

import input.configuration.LAnimaRPContext;
import input.configuration.XMLParser;
import input.events.eventTypes.LAnimaRPEvent;
import input.events.eventTypes.StringEvolvedEvent;
import input.events.listeners.LAnimaRPEventListener;
import input.events.publishers.FileBasedEvolvingString;
import logic.data.PeriodicRefreshInfo;
import logic.data.fileLocators.URLLocator;
import logic.data.string.UpdatableWithString;
import logic.variables.variableTypes.StringUpdatableVariable;
import logic.variables.variableTypes.StringVariable;
import logic.variables.variableTypes.Variable;

public class SynchronizeFromFileVariableActuator<T>
implements VariableActuator<StringUpdatableVariable<Variable<T>,T>, T>, LAnimaRPEventListener<StringEvolvedEvent> {
	
	private final FileBasedEvolvingString stringToObserve;
	
	private UpdatableWithString v;

	private SynchronizeFromFileVariableActuator(URLLocator fileToObserve, PeriodicRefreshInfo pr) {
		stringToObserve = FileBasedEvolvingString.newInstance(fileToObserve, pr);
		stringToObserve.subscribe(this);
		
	}

	public static SynchronizeFromFileVariableActuator newInstance(Element modifier) {
		URLLocator fileToObserve= XMLParser.parseFileLocator(modifier, null );
		PeriodicRefreshInfo pr = XMLParser.parsePeriodicRefresh(modifier);
		return new SynchronizeFromFileVariableActuator(fileToObserve, pr);
	}
	
	public String toString()
	{
		return "Updating:"+v+" with:"+stringToObserve.getFile();
	}

	@Override
	public void setVariable(StringUpdatableVariable res) {
		this.v= res;
		v.updateFrom(stringToObserve.getString());
	}

	@Override
	public void handleEvent(StringEvolvedEvent event) {
		if(v!= null)
			v.updateFrom(event.getStrings());
	}

}
