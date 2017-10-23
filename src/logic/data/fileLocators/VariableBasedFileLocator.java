package logic.data.fileLocators;

import java.io.File;

import logic.variables.variableTypes.PathVariable;
import logic.variables.variableTypes.Variable;

public class VariableBasedFileLocator implements FileLocator {

	private final PathVariable pv;
	public VariableBasedFileLocator(PathVariable variable) {
		pv = variable;
	}

	public static FileLocator newInstance(PathVariable variable) {
		return new VariableBasedFileLocator(variable);
	}

	@Override
	public File getFile() {
		return pv.getPath().getFile();
	}
}
