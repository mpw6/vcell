package cbit.vcell.modelapp;

import org.vcell.expression.ExpressionBindingException;
import org.vcell.expression.ExpressionFactory;
import org.vcell.expression.IExpression;
/**
 * Insert the type's description here.
 * Creation date: (4/8/2002 1:39:52 PM)
 * @author: Anuradha Lakshminarayana
 */
public class CurrentClampStimulus extends ElectricalStimulus {
/**
 * CurrentClampStimulus constructor comment.
 * @param argElectrode cbit.vcell.mapping.Electrode
 * @param argName java.lang.String
 * @param argVoltName java.lang.String
 * @param argCurrName java.lang.String
 * @param argAnnotation java.lang.String
 */
public CurrentClampStimulus(Electrode argElectrode, String argName, IExpression argCurrExpr, SimulationContext argSimulationContext) {
	super(argElectrode, argName, argSimulationContext);

	try {
		getCurrentParameter().setExpression(argCurrExpr);
		getCurrentParameter().setDescription("applied current density");
		getVoltageParameter().setExpression(ExpressionFactory.createExpression(0.0));
		getVoltageParameter().setDescription("measured voltage");
		argCurrExpr.bindExpression(this);
	}catch (ExpressionBindingException e){
		e.printStackTrace(System.out);
		//throw new RuntimeException(e.getMessage());
	}catch (java.beans.PropertyVetoException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}


/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(org.vcell.util.Matchable obj) {
	if (obj instanceof CurrentClampStimulus){
		CurrentClampStimulus ccs = (CurrentClampStimulus)obj;

		if (!super.compareEqual0(obj)){
			return false;
		}
		
		return true;
	}else{
		return false;
	}

}
}