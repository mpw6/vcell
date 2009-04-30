package cbit.vcell.math;
/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import java.util.*;

import cbit.util.CommentStringTokenizer;
import cbit.vcell.parser.*;
/**
 * This class was generated by a SmartGuide.
 * 
 */
public class VolumeRegionEquation extends Equation {
	private Expression uniformRateExpression = new Expression(0.0);
	private Expression volumeRateExpression = new Expression(0.0);
	private Expression membraneRateExpression = new Expression(0.0);

/**
 * OdeEquation constructor comment.
 * @param subDomain cbit.vcell.math.SubDomain
 * @param var cbit.cell.math.Variable
 * @param initialExp cbit.vcell.parser.Expression
 * @param rateExp cbit.vcell.parser.Expression
 */
public VolumeRegionEquation(VolumeRegionVariable var, Expression initialExp) {
	super(var, initialExp, null);
}


/**
 * Insert the method's description here.
 * Creation date: (9/4/2003 12:32:19 PM)
 * @return boolean
 * @param object cbit.util.Matchable
 */
public boolean compareEqual(org.vcell.util.Matchable object) {
	VolumeRegionEquation equ = null;
	if (!(object instanceof VolumeRegionEquation)){
		return false;
	}else{
		equ = (VolumeRegionEquation)object;
	}
	if (!compareEqual0(equ)){
		return false;
	}
	if (!org.vcell.util.Compare.isEqualOrNull(volumeRateExpression,equ.volumeRateExpression)){
		return false;
	}
	if (!org.vcell.util.Compare.isEqualOrNull(membraneRateExpression,equ.membraneRateExpression)){
		return false;
	}
	if (!org.vcell.util.Compare.isEqualOrNull(uniformRateExpression,equ.uniformRateExpression)){
		return false;
	}
	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (10/10/2002 10:41:10 AM)
 * @param sim cbit.vcell.solver.Simulation
 */
void flatten(cbit.vcell.solver.Simulation sim, boolean bRoundCoefficients) throws cbit.vcell.parser.ExpressionException, MathException {
	super.flatten0(sim,bRoundCoefficients);
	
	volumeRateExpression = getFlattenedExpression(sim,volumeRateExpression,bRoundCoefficients);
	membraneRateExpression = getFlattenedExpression(sim,membraneRateExpression,bRoundCoefficients);
	uniformRateExpression = getFlattenedExpression(sim,uniformRateExpression,bRoundCoefficients);
}


/**
 * This method was created by a SmartGuide.
 * @return java.util.Vector
 */
protected Vector<Expression> getExpressions(MathDescription mathDesc){
	Vector<Expression> list = new Vector<Expression>();
	list.addElement(getVolumeRateExpression());
	list.addElement(getMembraneRateExpression());
	list.addElement(getUniformRateExpression());
	
	if (getRateExpression()!=null)		list.addElement(getRateExpression());
	if (getInitialExpression()!=null)	list.addElement(getInitialExpression());
	if (getExactSolution()!=null)		list.addElement(getExactSolution());
	return list;
}


/**
 * Insert the method's description here.
 * Creation date: (7/9/01 2:05:09 PM)
 * @return cbit.vcell.parser.Expression
 */
public cbit.vcell.parser.Expression getMembraneRateExpression() {
	return membraneRateExpression;
}


/**
 * This method was created by a SmartGuide.
 * @return java.util.Enumeration
 */
public Enumeration<Expression> getTotalExpressions() throws ExpressionException {
	Vector<Expression> vector = new Vector<Expression>();
	Expression lvalueExp = new Expression("VolumeRate_"+getVariable().getName());
	Expression rvalueExp = new Expression(getVolumeRateExpression());
	Expression totalExp = Expression.assign(lvalueExp,rvalueExp);
	totalExp.bindExpression(null);
	totalExp.flatten();
	vector.addElement(totalExp);
	lvalueExp = new Expression("MembraneRate_"+getVariable().getName());
	rvalueExp = new Expression(getMembraneRateExpression());
	totalExp = Expression.assign(lvalueExp,rvalueExp);
	totalExp.bindExpression(null);
	totalExp.flatten();
	vector.addElement(totalExp);
	vector.addElement(getTotalInitialExpression());
	Expression solutionExp = getTotalSolutionExpression();
	if (solutionExp!=null){
		vector.addElement(solutionExp);
	}	
	return vector.elements();
}


/**
 * Insert the method's description here.
 * Creation date: (7/9/01 2:05:09 PM)
 * @return cbit.vcell.parser.Expression
 */
public cbit.vcell.parser.Expression getUniformRateExpression() {
	return uniformRateExpression;
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public String getVCML() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("\t"+VCML.VolumeRegionEquation+" "+getVariable().getName()+" {\n");
	if (getUniformRateExpression() != null){
		buffer.append("\t\t"+VCML.UniformRate+" "+getUniformRateExpression().infix()+";\n");
	}else{
		buffer.append("\t\t"+VCML.UniformRate+" "+"0.0;\n");
	}
	if (getVolumeRateExpression() != null){
		buffer.append("\t\t"+VCML.VolumeRate+" "+getVolumeRateExpression().infix()+";\n");
	}else{
		buffer.append("\t\t"+VCML.VolumeRate+" "+"0.0;\n");
	}
	if (getMembraneRateExpression() != null){
		buffer.append("\t\t"+VCML.MembraneRate+" "+getMembraneRateExpression().infix()+";\n");
	}else{
		buffer.append("\t\t"+VCML.MembraneRate+" "+"0.0;\n");
	}
	if (initialExp != null){
		buffer.append("\t\t"+VCML.Initial+"\t "+initialExp.infix()+";\n");
	}
	switch (solutionType){
		case UNKNOWN_SOLUTION:{
			if (initialExp == null){
				buffer.append("\t\t"+VCML.Initial+"\t "+"0.0;\n");
			}
			break;
		}
		case EXACT_SOLUTION:{
			buffer.append("\t\t"+VCML.Exact+" "+exactExp.infix()+";\n");
			break;
		}
	}				
		
	buffer.append("\t}\n");
	return buffer.toString();		
}


/**
 * Insert the method's description here.
 * Creation date: (7/9/01 2:05:09 PM)
 * @return cbit.vcell.parser.Expression
 */
public cbit.vcell.parser.Expression getVolumeRateExpression() {
	return volumeRateExpression;
}


/**
 * This method was created by a SmartGuide.
 * @param tokens java.util.StringTokenizer
 * @exception java.lang.Exception The exception description.
 */
public void read(CommentStringTokenizer tokens) throws MathFormatException, ExpressionException {
	String token = null;
	token = tokens.nextToken();
	if (!token.equalsIgnoreCase(VCML.BeginBlock)){
		throw new MathFormatException("unexpected token "+token+" expecting "+VCML.BeginBlock);
	}			
	while (tokens.hasMoreTokens()){
		token = tokens.nextToken();
		if (token.equalsIgnoreCase(VCML.EndBlock)){
			break;
		}			
		if (token.equalsIgnoreCase(VCML.Initial)){
			initialExp = new Expression(tokens);
			continue;
		}
		if (token.equalsIgnoreCase(VCML.VolumeRate)){
			Expression exp = new Expression(tokens);
			setVolumeRateExpression(exp);
			continue;
		}
		if (token.equalsIgnoreCase(VCML.UniformRate)){
			Expression exp = new Expression(tokens);
			setUniformRateExpression(exp);
			continue;
		}
		if (token.equalsIgnoreCase(VCML.MembraneRate)){
			Expression exp = new Expression(tokens);
			setMembraneRateExpression(exp);
			continue;
		}
		if (token.equalsIgnoreCase(VCML.Exact)){
			exactExp = new Expression(tokens);
			solutionType = EXACT_SOLUTION;
			continue;
		}
		throw new MathFormatException("unexpected identifier "+token);
	}	
		
}


/**
 * Insert the method's description here.
 * Creation date: (7/9/01 2:05:09 PM)
 * @param newMembraneRateExpression cbit.vcell.parser.Expression
 */
public void setMembraneRateExpression(cbit.vcell.parser.Expression newMembraneRateExpression) {
	membraneRateExpression = newMembraneRateExpression;
}


/**
 * Insert the method's description here.
 * Creation date: (7/9/01 2:05:09 PM)
 * @param newVolumeRateExpression cbit.vcell.parser.Expression
 */
public void setUniformRateExpression(cbit.vcell.parser.Expression newUniformRateExpression) {
	uniformRateExpression = newUniformRateExpression;
}


/**
 * Insert the method's description here.
 * Creation date: (7/9/01 2:05:09 PM)
 * @param newVolumeRateExpression cbit.vcell.parser.Expression
 */
public void setVolumeRateExpression(cbit.vcell.parser.Expression newVolumeRateExpression) {
	volumeRateExpression = newVolumeRateExpression;
}
}