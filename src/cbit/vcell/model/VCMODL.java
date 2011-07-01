package cbit.vcell.model;
/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
/**
 * This class was generated by a SmartGuide.
 * 
 */
public class VCMODL {
	public final static String SimulationContext			   = "SimulationContext";
	public final static String BioModel						   = "BioModel";
	public final static String MathModel					   = "MathModel";
	public final static String GeometryContext				   = "GeometryContext";
	public final static String FeatureMapping				   = "FeatureMapping";
	public final static String MembraneMapping				   = "MembraneMapping";
	public final static String ReactionContext				   = "ReactionContext";
	public final static String Resolved						   = "Resolved";
	public final static String BoundaryCondition			   = "BoundaryCondition";
	public final static String 	BoundaryConditionXm			   = "BoundaryConditionXm";
	public final static String 	BoundaryConditionXp			   = "BoundaryConditionXp";
	public final static String 	BoundaryConditionYm			   = "BoundaryConditionYm";
	public final static String 	BoundaryConditionYp			   = "BoundaryConditionYp";
	public final static String 	BoundaryConditionZm			   = "BoundaryConditionZm";
	public final static String 	BoundaryConditionZp			   = "BoundaryConditionZp";
	public final static String SpeciesContextSpec			   = "SpeciesContextSpec";
	public final static String ForceConstant				   = "ForceConstant";
	public final static String ForceIndependent				   = "ForceIndependent";
	public final static String EnableDiffusion				   = "EnableDiffusion";
	public final static String VelocityX				  	   = "VelocityX";
	public final static String VelocityY				  	   = "VelocityY";
	public final static String VelocityZ				   	   = "VelocityZ";
	
	public final static String Diagram 						   = "Diagram";
	public final static String Model 						   = "Model";
	public final static String ModelReference 				   = "ModelRef";
	public final static String Name 						   = "Name";
	public final static String Fast 						   = "Fast";
	public final static String EnclosingFeature 			   = "EnclosingFeature";
	public final static String VolumeFraction 				   = "VolumeFraction";
	public final static String SurfaceToVolume 				   = "SurfaceToVolume";
	public final static String Reaction		 				   = "Reaction";
	public final static String SimpleReaction	 			   = "SimpleReaction";
	public final static String FluxStep		 				   = "FluxStep";
	public final static String InFlux						   = "InFlux";
	public final static String Species	 					   = "Species";
	public final static String BeginBlock	 				   = "{";
	public final static String EndBlock	 					   = "}";
	public final static String Product						   = "Product";
	public final static String Parameter					   = "Parameter";
	public final static String Reactant						   = "Reactant";
	public final static String Catalyst						   = "Catalyst";
	public final static String Kinetics						   = "Kinetics";
	public final static String MassActionKinetics			   = "MassActionKinetics";
	public final static String GeneralKinetics				   = "GeneralKinetics";
	public final static String GeneralCurrentKinetics   	   = "GenCurrKinetics";
	public final static String GHKKinetics					   = "GHKKinetics";
	public final static String NernstKinetics			       = "NernstKinetics";
	public final static String HMM_IrreversibleKinetics 	   = "HenriMichaelasMentenKineticsIrr";
	public final static String HMM_ReversibleKinetics		   = "HenriMichaelasMentenKineticsRev";
	public final static String GeneralLumpedKinetics   	       = "GeneralLumpedKinetics";
	public final static String GeneralCurrentLumpedKinetics	   = "GeneralCurrentLumpedKinetics";
	public static final String GeneralPermeabilityKinetics 	   = "GeneralPermeabilityKinetics";
	public static final String Macroscopic_IRRKinetics 	   	   = "Macroscopic_IRRKinetics";
	public static final String Microscopic_IRRKinetics 	   	   = "Microscopic_IRRKinetics";
	public final static String CurrentDensity				   = "CurrentDensity";
	public final static String LumpedCurrent				   = "LumpedCurrent";
	public final static String ReactionRate					   = "Rate";
	public final static String Valence						   = "Valence";
	public final static String PhysicsOptions				   = "PhysicsOptions";
	public final static String MembraneVoltageName			   = "MembraneVoltageName";
	public final static String CalculateVoltage				   = "CalculateVoltage";
	public final static String InitialVoltage				   = "InitialVoltage";
	public final static String StructureSize				   = "StructureSize";
	public final static String VolumePerUnitVolume			   = "VolumePerUnitVolume";
	public final static String VolumePerUnitArea			   = "VolumePerUnitArea";
	public final static String AreaPerUnitVolume			   = "AreaPerUnitVolume";
	public final static String AreaPerUnitArea				   = "AreaPerUnitArea";
	public final static String SpecificCapacitance			   = "SpecificCapacitance";	
	public final static String Permeability					   = "Permeability";
	public final static String Conductivity					   = "Conductivity";
	public final static String ForwardRate					   = "ForwardRate";
	public final static String ReverseRate					   = "ReverseRate";
	public final static String DissociationConstant			   = "DissociationConstant";
	public final static String Vmax							   = "Vmax";
	public final static String Km							   = "Km";
	public final static String KmFwd						   = "KmFwd";
	public final static String VmaxFwd						   = "VmaxFwd";
	public final static String KmRev						   = "KmRev";
	public final static String VmaxRev						   = "VmaxRev";
	public final static String Binding_Radius				   = "Binding_Radius";
	public final static String Kon							   = "Kon";
	public final static String Diffusion_Reactant1			   = "Diffusion_Reactant1";
	public final static String Diffusion_Reactant2			   = "Diffusion_Reactant2";
	public final static String Concentration_Reactant1		   = "Concentration_Reactant1";
	public final static String Concentration_Reactant2		   = "Concentration_Reactant2";
	public final static String LumpedReactionRate			   = "LumpedReactionRate";
	public final static String TotalRate_oldname			   = "TotalRate"; // "TotalRate" is the old name for "LumpedReactionRate"
	public final static String AssumedCompartmentSize_oldname  = "AssumedCompartmentSize";
	public final static String Feature						   = "Compartment";
	public final static String Membrane						   = "Membrane";
	public final static String Context						   = "Context";
	public final static String InitialConcentration			   = "InitialConcentration";
	public final static String InitialCount	   				   = "InitialCount";
	public final static String DiffusionRate				   = "DiffusionRate";
	public final static String Reversible					   = "Reversible";
	public final static String Irreversible					   = "Irreversible";
	public final static String ElectricalStimulus			   = "ElectricalStimulus";
	public final static String ES_Role_Voltage			   	   = "Voltage";
	public final static String ES_Role_Current_deprecated   = "Current";
	public final static String ES_Role_CurrentDensity	  	   = "CurrentDensity";
	public final static String ES_Role_TotalCurrent		  	   = "TotalCurrent";
	public final static String ES_Role_UserDefined			   = "UserDefined";
}