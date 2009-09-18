package cbit.vcell.solver.ode;
/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import cbit.vcell.parser.*;

import java.io.PrintWriter;
import java.util.*;

import cbit.vcell.mapping.FastSystemAnalyzer;
import cbit.vcell.math.*;
import cbit.vcell.matrix.MatrixException;
import cbit.vcell.matrix.RationalExp;
import cbit.vcell.matrix.RationalExpMatrix;
import cbit.vcell.solver.*;
/**
 * Insert the type's description here.
 * Creation date: (3/8/00 10:29:24 PM)
 * @author: John Wagner
 */
public class IDAFileWriter extends OdeFileWriter {
/**
 * OdeFileCoder constructor comment.
 */
public IDAFileWriter(PrintWriter pw, Simulation simulation) {
	this(pw, simulation, 0, false);
}


public IDAFileWriter(PrintWriter pw, Simulation simulation, int ji, boolean bUseMessaging) {
	super(pw, simulation, ji, bUseMessaging);
}
/**
 * Insert the method's description here.
 * Creation date: (3/8/00 10:31:52 PM)
 */
protected void writeEquations() throws MathException, ExpressionException {
	VariableSymbolTable varsSymbolTable = createSymbolTable();

	HashMap<Discontinuity, String> discontinuityNameMap = new HashMap<Discontinuity, String>();
		
	StringBuffer sb = new StringBuffer();
	sb.append("NUM_EQUATIONS " + getStateVariableCount() + "\n");
	
	if (getSimulation().getMathDescription().hasFastSystems()){
		//
		// define vector of original variables
		//
		CompartmentSubDomain subDomain = (CompartmentSubDomain)getSimulation().getMathDescription().getSubDomains().nextElement();
		FastSystem fastSystem = subDomain.getFastSystem();
		FastSystemAnalyzer fs_Analyzer = new FastSystemAnalyzer(fastSystem, getSimulation());
		int numIndependent = fs_Analyzer.getNumIndependentVariables();
		int systemDim = getSimulation().getMathDescription().getStateVariableNames().size();
		int numDependent = systemDim - numIndependent;
		
		//
		// get all variables from fast system (dependent and independent)
		//
		HashSet<String> fastSystemVarHash = new HashSet<String>();
		Enumeration<Variable> dependentfastSystemVarEnum = fs_Analyzer.getDependentVariables();
		while (dependentfastSystemVarEnum.hasMoreElements()){
			fastSystemVarHash.add(dependentfastSystemVarEnum.nextElement().getName());
		}
		Enumeration<Variable> independentfastSystemVarEnum = fs_Analyzer.getIndependentVariables();
		while (independentfastSystemVarEnum.hasMoreElements()){
			fastSystemVarHash.add(independentfastSystemVarEnum.nextElement().getName());
		}
		//
		// get all equations including for variables that are not in the fastSystem (ode equations for "slow system")
		//
		RationalExpMatrix origInitVector = new RationalExpMatrix(systemDim,1);
		RationalExpMatrix origSlowRateVector = new RationalExpMatrix(systemDim,1);
		RationalExpMatrix origVarColumnVector = new RationalExpMatrix(systemDim,1);
		Enumeration<Equation> enumEquations = subDomain.getEquations();
		int varIndex=0;
		while (enumEquations.hasMoreElements()){
			Equation equation = enumEquations.nextElement();
			origVarColumnVector.set_elem(varIndex, 0, new RationalExp(equation.getVariable().getName()));
			
			Expression rateExpr = equation.getRateExpression();
			rateExpr.bindExpression(varsSymbolTable);
			rateExpr = MathUtilities.substituteFunctions(rateExpr, varsSymbolTable);
			origSlowRateVector.set_elem(varIndex, 0, new RationalExp("("+rateExpr.flatten().infix()+")"));

			Expression initExpr = new Expression(equation.getInitialExpression());
			initExpr.substituteInPlace(new Expression("t"), new Expression(0.0));
			initExpr = MathUtilities.substituteFunctions(initExpr, varsSymbolTable).flatten();
			origInitVector.set_elem(varIndex, 0, new RationalExp("("+initExpr.flatten().infix()+")"));

			varIndex++;
		}
		//
		// make symbolic matrix for fast invariants (from FastSystem's fastInvariants as well as a new fast invariant for each variable not included in the fast system.
		//
		RationalExpMatrix fastInvarianceMatrix = new RationalExpMatrix(numDependent,systemDim);
		int row=0;
		for (int i = 0; i < origVarColumnVector.getNumRows(); i++) {
			//
			// if orig_var[i] not in fast system, add trivial fast invariant for it.
			//
			if (!fastSystemVarHash.contains(origVarColumnVector.get(i,0).infixString())){
				fastInvarianceMatrix.set_elem(row, i, RationalExp.ONE);
				row++;
			}
		}
		Enumeration<FastInvariant> enumFastInvariants = fastSystem.getFastInvariants();
		while (enumFastInvariants.hasMoreElements()){
			FastInvariant fastInvariant = enumFastInvariants.nextElement();
			Expression fastInvariantExpression = fastInvariant.getFunction();
			for (int col = 0; col < systemDim; col++) {
				Expression coeff = fastInvariantExpression.differentiate(origVarColumnVector.get(col, 0).infixString()).flatten();
				fastInvarianceMatrix.set_elem(row, col, RationalExpUtils.getRationalExp(coeff));
			}
			row++;
		}
		for (int i = 0; i < systemDim; i++) {
			sb.append("VAR "+origVarColumnVector.get(i,0).infixString()+" INIT "+origInitVector.get(i,0).infixString()+";\n");
		}

		RationalExpMatrix fullMatrix = null;
		RationalExpMatrix inverseFullMatrix = null;
		RationalExpMatrix newSlowRateVector = null;
		try {
			RationalExpMatrix fastMat = ((RationalExpMatrix)fastInvarianceMatrix.transpose().findNullSpace());
			fullMatrix = new RationalExpMatrix(systemDim,systemDim);
			row = 0;
			for (int i= 0; i < fastInvarianceMatrix.getNumRows(); i++) {
				for (int col = 0; col < systemDim; col++) {
					fullMatrix.set_elem(row, col, fastInvarianceMatrix.get(i, col));
				}
				row++;
			}
			for (int i=0; i < fastMat.getNumRows(); i++) {
				for (int col = 0; col < systemDim; col++) {
					fullMatrix.set_elem(row, col, fastMat.get(i, col));
				}
				row++;
			}
			inverseFullMatrix = new RationalExpMatrix(systemDim,systemDim);
			inverseFullMatrix.identity();
			RationalExpMatrix copyOfFullMatrix = new RationalExpMatrix(fullMatrix);
			copyOfFullMatrix.gaussianElimination(inverseFullMatrix);
			newSlowRateVector = new RationalExpMatrix(numDependent,1);
			newSlowRateVector.matmul(fastInvarianceMatrix,origSlowRateVector);
		} catch (MatrixException ex) {
			throw new MathException(ex.getMessage());
		}

		sb.append("TRANSFORM\n");
		for (row = 0; row < systemDim; row++) {
			for (int col = 0; col < systemDim; col++) {
				sb.append(fullMatrix.get(row, col).getConstant().doubleValue() + " ");
			}
			sb.append("\n");
		}
		sb.append("INVERSETRANSFORM\n");
		for (row = 0; row < systemDim; row++) {
			for (int col = 0; col < systemDim; col++) {
				sb.append(inverseFullMatrix.get(row, col).getConstant().doubleValue() + " ");
			}
			sb.append("\n");
		}
		int numDifferential = numDependent;
		int numAlgebraic = numIndependent;
		sb.append("RHS DIFFERENTIAL "+numDifferential+" ALGEBRAIC "+numAlgebraic + "\n");
		int equationIndex = 0;
		while (equationIndex < numDependent){
			// print row of mass matrix followed by slow rate corresponding to fast invariant
			Expression slowRateExp = new Expression(newSlowRateVector.get(equationIndex,0).infixString()).flatten();
			slowRateExp.bindExpression(getSimulation());	
			slowRateExp = MathUtilities.substituteFunctions(slowRateExp, varsSymbolTable).flatten();
			
			Vector<Discontinuity> v = slowRateExp.getDiscontinuities();
			for (Discontinuity od : v) {
				od = getSubsitutedAndFlattened(od,varsSymbolTable);
				String dname = discontinuityNameMap.get(od);
				if (dname == null) {
					dname = ROOT_VARIABLE_PREFIX + discontinuityNameMap.size();
					discontinuityNameMap.put(od, dname);				
				}
				slowRateExp.substituteInPlace(od.getDiscontinuityExp(), new Expression("(" + dname + "==1)"));
			}
			
			sb.append(slowRateExp.infix()+";\n");
			equationIndex++;
		}
		Enumeration<FastRate> enumFastRates = fastSystem.getFastRates();
		while (enumFastRates.hasMoreElements()){
			// print the fastRate for this row
			Expression fastRateExp = new Expression(enumFastRates.nextElement().getFunction());
			fastRateExp = MathUtilities.substituteFunctions(fastRateExp, varsSymbolTable).flatten();

			Vector<Discontinuity> v = fastRateExp.getDiscontinuities();
			for (Discontinuity od : v) {
				od = getSubsitutedAndFlattened(od,varsSymbolTable);
				String dname = discontinuityNameMap.get(od);
				if (dname == null) {
					dname = ROOT_VARIABLE_PREFIX + discontinuityNameMap.size();
					discontinuityNameMap.put(od, dname);
				}
				fastRateExp.substituteInPlace(od.getDiscontinuityExp(), new Expression("(" + dname + "==1)"));
			}
			
			sb.append(fastRateExp.flatten().infix()+";\n");
			equationIndex++;
		}
	} else {
		for (int i = 0; i < getStateVariableCount(); i++) {
			StateVariable stateVar = (StateVariable)getStateVariable(i);
			Expression initExpr = null;
			if (stateVar instanceof ODEStateVariable) {
				initExpr = new Expression(((ODEStateVariable)stateVar).getInitialRateExpression());
			} else if (stateVar instanceof SensStateVariable) {
				initExpr = new Expression(((SensStateVariable)stateVar).getInitialRateExpression());
			}		
			
			initExpr = MathUtilities.substituteFunctions(initExpr, varsSymbolTable);
			initExpr.substituteInPlace(new Expression("t"), new Expression(0.0));
			sb.append("VAR " + stateVar.getVariable().getName() + " INIT " + initExpr.flatten().infix() + ";\n");
		}
		
		sb.append("TRANSFORM\n");
		for (int row = 0; row < getStateVariableCount(); row++) {
			for (int col = 0; col < getStateVariableCount(); col++) {
				sb.append((row == col ? 1 : 0) + " ");
			}
			sb.append("\n");
		}
		sb.append("INVERSETRANSFORM\n");
		for (int row = 0; row < getStateVariableCount(); row++) {
			for (int col = 0; col < getStateVariableCount(); col++) {
				sb.append((row == col ? 1 : 0) + " ");
			}
			sb.append("\n");
		}
		sb.append("RHS DIFFERENTIAL " + getStateVariableCount() + " ALGEBRAIC 0\n");
		for (int i = 0; i < getStateVariableCount(); i++) {
			StateVariable stateVar = (StateVariable)getStateVariable(i);
			Expression rateExpr = null;
			if (stateVar instanceof ODEStateVariable) {
				rateExpr = new Expression(((ODEStateVariable)stateVar).getRateExpression());
			} else if (stateVar instanceof SensStateVariable) {
				rateExpr = new Expression(((SensStateVariable)stateVar).getRateExpression());
			}	
			
			rateExpr = MathUtilities.substituteFunctions(rateExpr, varsSymbolTable).flatten();
			
			Vector<Discontinuity> v = rateExpr.getDiscontinuities();			
			for (Discontinuity od : v) {
				od = getSubsitutedAndFlattened(od,varsSymbolTable);
				String dname = discontinuityNameMap.get(od);
				if (dname == null) {
					dname = ROOT_VARIABLE_PREFIX + discontinuityNameMap.size();
					discontinuityNameMap.put(od, dname);				
				}
				rateExpr.substituteInPlace(od.getDiscontinuityExp(), new Expression("(" + dname + "==1)"));
			}
			
			sb.append(rateExpr.infix() + ";\n");
		}
	}
	
	if (discontinuityNameMap.size() > 0) {
		printWriter.println("DISCONTINUITIES " + discontinuityNameMap.size());
		for (Discontinuity od : discontinuityNameMap.keySet()) {
			printWriter.println(discontinuityNameMap.get(od) + " " + od.getDiscontinuityExp().flatten().infix() + "; " + od.getRootFindingExp().flatten().infix() + ";");
		}
	}
	printWriter.print(sb);
}


@Override
String getSolverName() {
	return "IDA";
}
}