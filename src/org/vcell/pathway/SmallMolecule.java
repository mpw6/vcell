package org.vcell.pathway;

public class SmallMolecule extends PhysicalEntity {
	private EntityReference entityReference;
	
	public EntityReference getEntityReference() {
		return entityReference;
	}

	public void setEntityReference(EntityReference entityReference) {
		this.entityReference = entityReference;
	}
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printObject(sb, "entityReference",entityReference,level);
	}
	
	public String getTypeLabel(){
		return "small molecule";
	}
}
