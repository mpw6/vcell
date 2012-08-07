/* Generated By:JJTree: Do not edit this line. ASTUnitSymbol.java */

package cbit.vcell.units.parser;

import cbit.vcell.matrix.RationalNumber;

public class ASTUnitSymbol extends SimpleNode {
  public ASTUnitSymbol(int id) {
    super(id);
  }

  public ASTUnitSymbol(UnitSymbolParser p, int id) {
    super(p, id);
  }

  public String toInfix(RationalNumber power) {
		if (jjtGetNumChildren()==1){
			return jjtGetChild(0).toInfix(power);
		}else {
			return jjtGetChild(0).toInfix(power)+"*"+jjtGetChild(1).toInfix(power);
		}
	}

  public String toInfixWithoutNumericScale() {
	  if (jjtGetNumChildren()==1){
		  if (jjtGetChild(0) instanceof ASTNumericScale){
			return "1";
		  }else{
			return jjtGetChild(0).toInfix(RationalNumber.ONE);
		  }
	  }else{
		return jjtGetChild(1).toInfix(RationalNumber.ONE);
	  }
	}

  public double getNumericScale() {
	if (jjtGetChild(0) instanceof ASTNumericScale){
		return ((ASTNumericScale)jjtGetChild(0)).value.doubleValue();
	}else{
		return 1.0;
	}
  }

public String toSymbol(RationalNumber power, UnitTextFormat format) {
	if (jjtGetNumChildren()==1){
		return jjtGetChild(0).toSymbol(power,format);
	}else {
		return jjtGetChild(0).toSymbol(power,format)+" "+jjtGetChild(1).toSymbol(power,format);
	}
}

}
