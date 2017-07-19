package logic.variables.actuators;

import org.jdom2.Element;
import org.omg.Messaging.SyncScopeHelper;

import input.configuration.XMLKeywords;
import input.events.eventTypes.LAnimaRPEvent;
import input.events.publishers.LAnimaRPEventPublisher;
import logic.data.string.UpdatableWithString;
import logic.variables.variableTypes.BoundedIntegerVariable;
import logic.variables.variableTypes.Variable;

public interface VariableActuator<VT extends Variable<V>, V> {

	public static VariableActuator NO_ACTION = new VariableActuator() {

		@Override
		public void setVariable(Variable res) {
		}
	};

	public void setVariable(VT res);

}
