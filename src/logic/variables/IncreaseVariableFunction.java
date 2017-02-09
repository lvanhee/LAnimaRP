package logic.variables;

import java.util.List;

import org.jdom2.Attribute;

public class IncreaseVariableFunction implements VariableModifierFunction {
	
	BoundedIntegerVariable biv;
	int increaseFactor;
	public IncreaseVariableFunction(BoundedIntegerVariable biv, int i) {
		this.biv = biv;
		this.increaseFactor = i;
	}
	@Override
	public void activate() {
		biv.increaseValue(increaseFactor);
	}

}
