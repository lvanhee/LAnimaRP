package input;

import java.util.Map;

public class PredicateVariable implements Variable {

	public static final String XMLNAME = "predicate";

	public static Variable generate(Map<String, String> parameters) {
		return parse(parameters.get("expression"));
	}

	private static Variable parse(String string) {
		
		String[] split = string.split(" ");
		String left = split[0];
		String operator = split[1];
		String right = split[2];
		
		Variable v = VariableManager.get(left);
		throw new Error();
	}

	@Override
	public String getName() {
		throw new Error();
	}

}
