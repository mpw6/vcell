/* Generated By:JJTree: Do not edit this line. ASTFunctionsBlock.java */

package org.vcell.model.bngl;

public class ASTFunctionsBlock extends SimpleNode {
  public ASTFunctionsBlock(int id) {
    super(id);
  }

  public ASTFunctionsBlock(BNGLParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(BNGLParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }

@Override
public String toBNGL() {
	// TODO Auto-generated method stub
	return null;
}
}
