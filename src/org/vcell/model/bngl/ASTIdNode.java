/* Generated By:JJTree: Do not edit this line. ASTIdNode.java */

package org.vcell.model.bngl;

public class ASTIdNode extends SimpleNode {
  public String name;


public ASTIdNode(int id) {
    super(id);
  }

  public ASTIdNode(BNGLParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(BNGLParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }

@Override
public String toBNGL() {
	return name;
}
}
