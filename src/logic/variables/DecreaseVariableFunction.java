package logic.variables;

public class DecreaseVariableFunction implements VariableModifierFunction
{

	BoundedIntegerVariable biv;
	int decreaseFactor;
	public DecreaseVariableFunction(BoundedIntegerVariable biv, int i) {
		this.biv = biv;
		this.decreaseFactor = i;
	}
	@Override
	public void activate() {
		biv.decreaseValue(decreaseFactor);
	}


}
