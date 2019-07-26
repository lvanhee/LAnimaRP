package logic.data.fileLocators;

import java.io.File;

import logic.variables.variableTypes.PathVariable;
import logic.variables.variableTypes.Variable;

public class VariableBasedFileLocator implements URLLocator {

	private final PathVariable pv;
	public VariableBasedFileLocator(PathVariable variable) {
		pv = variable;
	}

	public static URLLocator newInstance(PathVariable variable) {
		return new VariableBasedFileLocator(variable);
	}

	@Override
	public File getURL() {
		return pv.getPath().getURL();
	}
}
