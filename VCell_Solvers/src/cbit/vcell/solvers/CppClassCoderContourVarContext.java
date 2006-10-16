package cbit.vcell.solvers;
/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import org.vcell.expression.ExpressionFactory;

import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.Constant;
import cbit.vcell.math.Equation;
import cbit.vcell.math.FilamentSubDomain;
import cbit.vcell.math.FilamentVariable;
import cbit.vcell.math.Function;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.math.Variable;
import cbit.vcell.math.VolVariable;
import cbit.vcell.simulation.Simulation;
/**
 * This class was generated by a SmartGuide.
 * 
 */
public class CppClassCoderContourVarContext extends CppClassCoderAbstractVarContext {
	protected CompartmentSubDomain compartmentSubDomain = null;

/**
 * VarContextCppCoder constructor comment.
 * @param name java.lang.String
 */
protected CppClassCoderContourVarContext(CppCoderVCell argCppCoderVCell,
												Equation argEquation,
												FilamentSubDomain argFilamentSubDomain,
												Simulation argSimulation, 
												String argParentClass) throws Exception
{
	super(argCppCoderVCell,argEquation,argFilamentSubDomain,argSimulation,argParentClass);
	this.compartmentSubDomain = argFilamentSubDomain.getOutsideCompartment();
}


/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.model.Feature
 */
public CompartmentSubDomain getCompartmentSubDomain() {
	return compartmentSubDomain;
}


/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.model.Feature
 */
public FilamentSubDomain getFilamentSubDomain() {
	return (FilamentSubDomain)getSubDomain();
}


/**
 * This method was created by a SmartGuide.
 * @param out java.io.PrintWriter
 */
protected void writeConstructor(java.io.PrintWriter out) throws Exception {
	out.println(getClassName()+"::"+getClassName()+"(Contour *Acontour,Feature *Afeature,CString AspeciesName)");
	out.println(": "+getParentClassName()+"(Afeature,AspeciesName)");
	out.println("{");
	try {
		double value = getEquation().getInitialExpression().evaluateConstant();
		out.println("   initialValue = new double;");
		out.println("   *initialValue = "+value+";");
	}catch (Exception e){
		out.println("   initialValue = NULL;");
	}	
	out.println("");
	Variable requiredVariables[] = getRequiredVariables();
	for (int i = 0; i < requiredVariables.length; i++){
		Variable var = requiredVariables[i];
		if (var instanceof VolVariable){
			out.println("   var_"+var.getName()+" = NULL;");
		}
		if (var instanceof FilamentVariable){
			out.println("   var_"+var.getName()+" = NULL;");
		}
	}		  	
	out.println("}");
}


/**
 * This method was created by a SmartGuide.
 * @param printWriter java.io.PrintWriter
 */
public void writeDeclaration(java.io.PrintWriter out) throws Exception {
	out.println("//---------------------------------------------");
	out.println("//  class " + getClassName());
	out.println("//---------------------------------------------");

	out.println("class " + getClassName() + " : public " + getParentClassName());
	out.println("{");
	out.println(" public:");
	out.println("    "+getClassName() + "(Contour *contour, Feature *feature, CString speciesName);");
	out.println("    virtual boolean resolveReferences(Simulation *sim);");

	try {
		double value = getEquation().getInitialExpression().evaluateConstant();
	}catch (Exception e){
		out.println("    virtual double getInitialValue(ContourElement *contourElement);");
	}
	out.println(" protected:");
	out.println("    virtual double getContourReactionRate(ContourElement *memElement);");
	out.println("    virtual double getContourDiffusionRate(ContourElement *memElement);");
	out.println(" private:");
	Variable requiredVariables[] = getRequiredVariables();
	for (int i = 0; i < requiredVariables.length; i++){
		Variable var = requiredVariables[i];
		if (var instanceof FilamentVariable){
			out.println("    ContourVariable    *var_"+var.getName()+";");
		}else if (var instanceof VolVariable){
			out.println("    VolumeVariable      *var_"+var.getName()+";");
		}else if (var instanceof ReservedVariable){
		}else if (var instanceof Constant){
		}else if (var instanceof Function){
		}else{
			throw new Exception("unknown identifier type '"+var.getClass().getName()+"' for identifier: "+var.getName());
		}	
	}		  	
	out.println("};");
}


/**
 * This method was created by a SmartGuide.
 * @param printWriter java.io.PrintWriter
 */
public void writeImplementation(java.io.PrintWriter out) throws Exception {
	out.println("//---------------------------------------------");
	out.println("//  class " + getClassName());
	out.println("//---------------------------------------------");
	writeConstructor(out);
	out.println("");
	writeResolveReferences(out);
	out.println("");
	writeContourFunction(out,"getContourReactionRate", getEquation().getRateExpression());
	out.println("");
	writeContourFunction(out,"getContourDiffusionRate", ExpressionFactory.createExpression(0.0));
	out.println("");
	try {
		double value = getEquation().getInitialExpression().evaluateConstant();
	}catch (Exception e){
		writeContourFunction(out,"getInitialValue", getEquation().getInitialExpression());
	}
	out.println("");
}
}