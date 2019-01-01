package logic.variables.variableTypes;

import input.configuration.XMLKeywords;

public enum VariableType {
	BOUNDED_INT, BOOLEAN, STRING, PATH, ENUM
	;

	public static VariableType parseFromXML(final String attributeValue) {
		if(attributeValue.equals(XMLKeywords.BOUNDED_INT_VARIABLE_TYPE.getName()))
			return BOUNDED_INT;
		if(attributeValue.equals(XMLKeywords.BOOLEAN_VARIABLE_TYPE.getName()))
			return BOOLEAN;
		if(attributeValue.equals(XMLKeywords.STRING_VARIABLE_TYPE.getName()))
			return STRING;
		if(attributeValue.equals(XMLKeywords.PATH_VARIABLE_TYPE.getName()))
			return PATH;
		if(attributeValue.equals(XMLKeywords.ENUM.getName()))
			return ENUM;
		
		throw new Error("Unknown variable type:"+attributeValue);
	}

}
