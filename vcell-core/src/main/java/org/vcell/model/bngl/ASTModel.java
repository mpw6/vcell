/* Generated By:JJTree: Do not edit this line. ASTModel.java */

package org.vcell.model.bngl;

public class ASTModel extends SimpleNode {
	private String prolog = null;

	public ASTModel(int id) {
		super(id);
	}

	public ASTModel(BNGLParser p, int id) {
		super(p, id);
	}
	
	public boolean hasMolecularDefinitions() {
		for (int i = 0; i < jjtGetNumChildren(); i++) {
			if (jjtGetChild(i) instanceof ASTMolecularDefinitionBlock) {
				return true;
			}
		}
		return false;
	}

	/**
	 * ParameterBlock() | MolecularDefinitionBlock() | SeedSpeciesBlock() |
	 * ReactionRuleBlock() | ObservablesBlock()
	 */
	@Override
	public String toBNGL() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\nbegin model\n");

		//
		// compartments block
		//
		for (int i = 0; i < jjtGetNumChildren(); i++) {
			if (jjtGetChild(i) instanceof ASTCompartmentsBlock) {
				buffer.append(jjtGetChild(i).toBNGL());
			}
		}

		//
		// parameter block
		//
		for (int i = 0; i < jjtGetNumChildren(); i++) {
			if (jjtGetChild(i) instanceof ASTParameterBlock) {
				buffer.append(jjtGetChild(i).toBNGL());
			}
		}

		//
		// Molecules block
		//
		for (int i = 0; i < jjtGetNumChildren(); i++) {
			if (jjtGetChild(i) instanceof ASTMolecularDefinitionBlock) {
				buffer.append(jjtGetChild(i).toBNGL());
			}
		}

		//
		// Seed species block
		//
		for (int i = 0; i < jjtGetNumChildren(); i++) {
			if (jjtGetChild(i) instanceof ASTSeedSpeciesBlock) {
				buffer.append(jjtGetChild(i).toBNGL());
			}
		}

		//
		// Reaction Rules block
		//
		for (int i = 0; i < jjtGetNumChildren(); i++) {
			if (jjtGetChild(i) instanceof ASTReactionRulesBlock) {
				buffer.append(jjtGetChild(i).toBNGL());
			}
		}

		//
		// Observables block
		//
		for (int i = 0; i < jjtGetNumChildren(); i++) {
			if (jjtGetChild(i) instanceof ASTObservablesBlock) {
				buffer.append(jjtGetChild(i).toBNGL());
			}
		}

		//
		// Actions
		//
		for (int i = 0; i < jjtGetNumChildren(); i++) {
			if (jjtGetChild(i) instanceof ASTAction) {
				buffer.append(jjtGetChild(i).toBNGL());
			}
		}

		buffer.append("end model\n");
		return buffer.toString();
	}

	/** Accept the visitor. **/
	public Object jjtAccept(BNGLParserVisitor visitor, Object data) {
		return visitor.visit(this, data);
	}

	public boolean hasCompartments() {
		for (int i = 0; i < jjtGetNumChildren(); i++) {
			if (jjtGetChild(i) instanceof ASTCompartmentsBlock) {
				return true;
			}
		}
		return false;
	}
	public boolean hasUnitSystem() {
		return false;
	}
	public BngUnitSystem getUnitSystem() {
		BngUnitSystem bngUnitSystem = new BngUnitSystem(BngUnitSystem.BngUnitOrigin.PARSER);
		return bngUnitSystem;
	}

	public void setProlog(String prolog) {
		this.prolog = prolog;
	}
	public String getProlog() {
		if(prolog != null) {
			return prolog;
		} else {
			return new String("");
		}
	}
}
