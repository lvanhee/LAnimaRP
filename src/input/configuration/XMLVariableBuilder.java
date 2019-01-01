package input.configuration;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.jdom2.Element;

import logic.data.Range;
import logic.variables.actuators.VariableActuator;
import logic.variables.variableTypes.BooleanVariable;
import logic.variables.variableTypes.BoundedIntegerVariable;
import logic.variables.variableTypes.PathVariable;
import logic.variables.variableTypes.StringVariable;
import logic.variables.variableTypes.Variable;
import logic.variables.variableTypes.VariableType;

public class XMLVariableBuilder
{
	private String variableName;
	private Range range;
	private String initialValue;
	private VariableType type;
	private Set<VariableActuator> actuators= new HashSet<VariableActuator>();

	public XMLVariableBuilder(String parseVariableName, VariableType type, String name) {
		this.variableName = parseVariableName;
		
		this.type = type;
	}

	public String getInitialValue() {
		
		if(initialValue!= null) return initialValue;
		
		if(type.equals(VariableType.BOUNDED_INT))return ""+range.getMinValue();
		
		throw new Error();
		
	}

	public Range getRange() {
		return range;
	}

	public String getVariableName() {
		return variableName;
	}

	public Variable build() {
		checkCoherence();
		Variable newVariable = null;
		switch(type)
		{
		case BOUNDED_INT: newVariable= BoundedIntegerVariable.newInstance(this);break;
		case BOOLEAN: newVariable = BooleanVariable.newInstance(this);break;
		case STRING: newVariable = StringVariable.newInstance(this);break;
		case PATH: newVariable = PathVariable.newInstance(this); break;
		case ENUM: newVariable = EnumVariable.newInstance(this);break;
		default: throw new Error("No builder for:"+type);
		}
				 
		 for(VariableActuator va: actuators)
			 va.setVariable(newVariable);
		 
		 
		return newVariable;
	}
	
	public String toString()
	{
		return variableName+","+type+","+range+","+initialValue+","+actuators;
	}

	private void checkCoherence() {
		if(variableName==null)
			throw new Error("Missing variable name");
		if(type.equals(VariableType.BOUNDED_INT))
			if(range == null)throw new Error("Missing range for variable:"+variableName);
			else return;
		
		if(type.equals(VariableType.BOOLEAN)|| type.equals(VariableType.STRING))
			return;
		
		if(type.equals(VariableType.PATH))
			if(initialValue==null)throw new Error("Path variables should have an initial value");
			else return;
		
		if(type.equals(VariableType.ENUM))
			if(initialValue==null)throw new Error("Enum variables must have an initial value");
			else return;
		
		throw new Error();
	}

	public XMLVariableBuilder set(String name, Element e) {
		if(name.equals(XMLKeywords.RANGE.getName())){ range = XMLParser.parseRange(e); return this;}
		if(name.equals(XMLKeywords.INITIAL_VALUE.getName())){initialValue = XMLParser.parseInitialValue(e); return this;}
		if(name.equals(XMLKeywords.VARIABLE_ACTUATORS.getName())){actuators=XMLParser.parseActuators(e);return this;}
		throw new Error();
	}

	public boolean isInitialized() {
		return initialValue != null;
	}

	public Set<VariableActuator> getActuators() {
		return actuators;
	}

	public XMLVariableBuilder setInitialValue(String attributeValue) {
		this.initialValue = attributeValue;
		return this;
	}

}
