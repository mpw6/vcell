package cbit.vcell.math;
/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import cbit.vcell.parser.*;
/**
 * This class was generated by a SmartGuide.
 * 
 */
public class Function extends Variable {
	private Expression exp = null;
	private Boolean bConstant = null;

/**
 * Constant constructor comment.
 * @param name java.lang.String
 */
public Function(String name, Expression exp, Domain domain) {
	super(name,domain);
	this.exp = new Expression(exp);
	try {
		this.exp.bindExpression(null);
	}catch (ExpressionBindingException e){
		//
		// this should never happen
		//
		e.printStackTrace(System.out);
	}
}


/**
 * This method was created by a SmartGuide.
 * @param symbolTable cbit.vcell.parser.SymbolTable
 * @exception java.lang.Exception The exception description.
 */
public void bind(SymbolTable symbolTable) throws ExpressionBindingException {
	if (exp!=null){
		exp.bindExpression(symbolTable);
	}
}


/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj Matchable
 */
public boolean compareEqual(org.vcell.util.Matchable obj) {
	if (!(obj instanceof Function)){
		return false;
	}
	if (!compareEqual0(obj)){
		return false;
	}
	Function v = (Function)obj;
	if (!org.vcell.util.Compare.isEqualOrNull(exp,v.exp)){
		return false;
	}
	
	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (8/4/2006 10:45:41 AM)
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean equals(Object obj) {	
	if (obj instanceof Function){
		return compareEqual0((Function)obj);
	}
	return false;
}


/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Expression
 * @exception java.lang.Exception The exception description.
 */
public Expression getExpression() {
	return exp;
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public String getVCML() {
	return VCML.Function+"  "+getQualifiedName()+"\t "+exp.infix()+";";
}


/**
 * This method was created by a SmartGuide.
 * @return boolean
 */
public boolean isConstant() {
	if (bConstant == null) {
		try {
			exp.evaluateConstant();
			bConstant = true;
		}catch (Exception e){
			bConstant = false;
		}
	}
	return bConstant;
}


/**
 * This method was created in VisualAge.
 * @param exp cbit.vcell.parser.Expression
 */
public void setExpression(Expression exp) {
	this.exp = exp;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return VCML.Function+"("+hashCode()+") <"+getQualifiedName()+"> = '"+getExpression()+"'";
}
}