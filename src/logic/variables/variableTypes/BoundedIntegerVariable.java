package logic.variables.variableTypes;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jdom2.Element;

import input.configuration.XMLKeywords;
import input.configuration.XMLParser;
import input.configuration.XMLVariableBuilder;
import input.events.eventTypes.VariableEvolvedEvent;
import input.events.listeners.LAnimaRPEventListener;
import input.events.publishers.GenericPublisher;
import logic.data.Range;

public class BoundedIntegerVariable implements DisplayableVariable<Integer> {

	private int currentValue;
	private String name;
	private Range r;

	public BoundedIntegerVariable(String variableName,Range r, int initialValue) {
		this.name = variableName;
		this.r = r;
		this.currentValue = initialValue;
	}

	public BoundedIntegerVariable(XMLVariableBuilder builder) {
		this(builder.getVariableName(), builder.getRange(),Integer.parseInt(builder.getInitialValue()));
	}

	public double getRatio() {
		return (double)currentValue/r.getMaxValue();
	}

	public void increaseValue(int increaseFactor) {
		currentValue+=increaseFactor;
		if(currentValue>=r.getMaxValue())currentValue=r.getMaxValue();
		if(currentValue<=r.getMaxValue())currentValue=r.getMinValue();
	}

	public int getValue() {
		return currentValue;
	}

	@Override
	public String getDisplayValue() {
		return ""+getValue();
	}

	public String toString()
	{
		return "BoundedIntegerVariable("+r+","+currentValue+")";
	}

		@Override
	public String getName() {
		return name;
	}

		public static Variable newInstance(XMLVariableBuilder xmlVariableBuilder) {
			return new BoundedIntegerVariable(xmlVariableBuilder);
		}

		@Override
		public void setValue(Integer value) {
			if(currentValue>r.getMaxValue())currentValue = r.getMaxValue();
			else if(currentValue<r.getMinValue())currentValue = r.getMinValue();
			else currentValue = value;
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
}

