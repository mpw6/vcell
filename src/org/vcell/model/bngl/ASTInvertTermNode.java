/* Generated By:JJTree: Do not edit this line. ASTInvertTermNode.java */

package org.vcell.model.bngl;

public class ASTInvertTermNode extends SimpleNode {
  public ASTInvertTermNode(int id) {
    super(id);
  }

  public ASTInvertTermNode(BNGLParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(BNGLParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }

@Override
public String toBNGL() {
	return jjtGetChild(0).toBNGL();
}
}
