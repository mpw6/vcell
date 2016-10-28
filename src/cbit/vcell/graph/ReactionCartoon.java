/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.graph;

import java.awt.Graphics2D;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.model.rbm.RbmNetworkGenerator.CompartmentMode;

import cbit.gui.graph.GraphContainerLayoutReactions;
import cbit.gui.graph.GraphEvent;
import cbit.gui.graph.GraphPane;
import cbit.gui.graph.Shape;
import cbit.vcell.graph.structures.StructureSuite;
import cbit.vcell.model.Catalyst;
import cbit.vcell.model.Diagram;
import cbit.vcell.model.Feature;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.NodeReference;
import cbit.vcell.model.Product;
import cbit.vcell.model.ProductPattern;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactantPattern;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.ReactionRuleParticipant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.RuleParticipantSignature;
import cbit.vcell.model.RuleParticipantSignature.Criteria;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;

public class ReactionCartoon extends ModelCartoon {

	protected StructureSuite structureSuite = null;
	private Set<RuleParticipantSignature> ruleParticipantSignatures = new HashSet<>();
	private RuleParticipantSignature.Criteria ruleParticipantGroupingCriteria = RuleParticipantSignature.Criteria.full;
	
	public ReactionCartoon() {
		containerLayout = new GraphContainerLayoutReactions();
	}

	public StructureSuite getStructureSuite() {
		return structureSuite;
	}
	
	public void setRuleParticipantGroupingCriteria(RuleParticipantSignature.Criteria newCriteria) {
		
		if(this.ruleParticipantGroupingCriteria == RuleParticipantSignature.Criteria.full && newCriteria == RuleParticipantSignature.Criteria.moleculeNumber) {
			this.ruleParticipantGroupingCriteria = newCriteria;
			refreshAll(true);
		} else if(this.ruleParticipantGroupingCriteria == RuleParticipantSignature.Criteria.moleculeNumber && newCriteria == RuleParticipantSignature.Criteria.full) {
			this.ruleParticipantGroupingCriteria = newCriteria;
			refreshAll(true);
		} else {			// switching to the same criteria, nothing to do
//			refreshAll(false);
			return;
		}
	}
	
	public RuleParticipantSignature.Criteria getRuleParticipantGroupingCriteria() {
		return ruleParticipantGroupingCriteria;
	}

	// for the RULE_PARTICIPANT_SIGNATURE_NODE nodes we initialize the node's speciesPattern field 
	// from the matching signature
	public void rebindAll(Diagram diagram) {
		String nodeStructure = diagram.getStructure().getName();
		List<NodeReference> nodeList = diagram.getNodeList();
		for (int i = 0; i < nodeList.size(); i++) {
			NodeReference node = nodeList.get(i);
			if(node.nodeType != NodeReference.RULE_PARTICIPANT_SIGNATURE_NODE) {
				continue;
			}
			if(node.speciesPattern != null) {
				continue;
			}
			for(RuleParticipantSignature signature : ruleParticipantSignatures) {
				if(!signature.getStructure().getName().equals(nodeStructure)) {
					continue;
				}
				String speciesPatternName = signature.getSpeciesPatternAsString();
				if(speciesPatternName.equals(node.name)) {
					node.speciesPattern = signature.getSpeciesPattern();
					break;
				}
			}
		}
	}
	
	public void applyDefaults(Diagram diagram) {
		List<NodeReference> nodeList = diagram.getNodeList();
		for (int i = 0; i < nodeList.size(); i++) {
			NodeReference node = nodeList.get(i);
			Object obj = null;
			switch (node.nodeType) {
			case NodeReference.SIMPLE_REACTION_NODE: {
				obj = getModel().getReactionStep(node.name);
				if (!(obj instanceof SimpleReaction)) {
					System.out
							.println("ReactionCartoon.applyDefaults(), diagram reaction "
									+ node.name
									+ " type mismatch in model, using location anyway");
				}
				break;
			}
			case NodeReference.FLUX_REACTION_NODE: {
				obj = getModel().getReactionStep(node.name);
				if (!(obj instanceof FluxReaction)) {
					System.out
							.println("ReactionCartoon.applyDefaults(), diagram flux "
									+ node.name
									+ " type mismatch in model, using location anyway");
				}
				break;
			}
			case NodeReference.SPECIES_CONTEXT_NODE: {
				obj = getModel().getSpeciesContext(node.name);
				break;
			}
			case NodeReference.REACTION_RULE_NODE: {
				obj = getModel().getRbmModelContainer().getReactionRule(node.name);
				break;
			}
			case NodeReference.RULE_PARTICIPANT_SIGNATURE_NODE: {		// obj is a RuleParticipantSignature
				Structure struct = diagram.getStructure();
				for(RuleParticipantSignature signature : ruleParticipantSignatures) {
					if (signature.getStructure() == struct && signature.compareByCriteria(node.getName(), Criteria.full)){
						obj = signature;
						break;
					}
				}
				break;
			}
			}
			Shape shape = getShapeFromModelObject(obj);
			if (shape != null) {
				Point relPosOld = shape.getRelPos();
				Point relPosNew = node.location;
				// In old models, the same node can appear in multiple diagrams.
				// Now, we have only one diagram, so if a node has multiple positions,
				// some would overwrite others.
				// This attempts to prevent overwriting a position with a worse one.
//				if(relPosOld.x + relPosOld.y < relPosNew.x + relPosNew.y) {
					shape.setRelPos(relPosNew);					
//				}
			}
		}
	}

	public void cleanupAll() {
		if (getStructureSuite() != null) {
			for(Structure structure : getStructureSuite().getStructures()) {
				structure.removePropertyChangeListener(this);
				if (structure instanceof Membrane) {
					Membrane membrane = (Membrane) structure;
					if (membrane.getMembraneVoltage() != null) {
						membrane.getMembraneVoltage().removePropertyChangeListener(this);
					}
				}
			}
		}
		if (getModel() != null) {
			getModel().removePropertyChangeListener(this);
			SpeciesContext structSpeciesContext[] = getModel().getSpeciesContexts();
			if (structSpeciesContext != null) {
				for (int i = 0; i < structSpeciesContext.length; i++) {
					structSpeciesContext[i].removePropertyChangeListener(this);
					structSpeciesContext[i].getSpecies().removePropertyChangeListener(this);
				}
			}
			ReactionStep reactionSteps[] = getModel().getReactionSteps();
			if (reactionSteps != null) {
				for (int i = 0; i < reactionSteps.length; i++) {
					reactionSteps[i].removePropertyChangeListener(this);
				}
			}
		}
	}

	@Override
	public void paint(Graphics2D g, GraphPane canvas) {
		super.paint(g, canvas);
	}
	
	private void relistenToMolecule(PropertyChangeEvent event) {
		System.out.println(event.getSource().getClass() + ": " + event.getPropertyName());
		if(event.getPropertyName().equals(Model.RbmModelContainer.PROPERTY_NAME_MOLECULAR_TYPE_LIST)) {
			for(MolecularType mt : getModel().getRbmModelContainer().getMolecularTypeList()) {
				mt.removePropertyChangeListener(this);
				mt.addPropertyChangeListener(this);
			}
		} else if(event.getSource() instanceof MolecularType) {
//			MolecularType mt = (MolecularType)event.getSource();
//			System.out.println(mt + ", " + event.getPropertyName() + ": " + event.getOldValue() + " -> " + event.getNewValue());
		}
	}

	public void propertyChange(PropertyChangeEvent event) {
		relistenToMolecule(event);
		refreshAll();
	}

//	private static Integer getStructureLevel(Structure s) {
//		Structure s0 = s;
//		int level = 0;
//		while (s0 != null) {
//			level += 1;
//			s0 = s0.getParentStructure();
//		}
//		return level;
//	}

	@Override
	public void refreshAll() {
		refreshAll(false);
	}
	private void refreshAll(boolean transitioning) {
		try {
			if (getModel() == null || getStructureSuite() == null) {
				return;
			}
			for(Structure structure : structureSuite.getStructures()) {
				Diagram diagram = getModel().getDiagram(structure);
				if (diagram != null) {
					// Maintain consistency between rule participant nodes, signatures and 
					// species pattern when a molecule is being modified.
					rebindAll(diagram);
				}				
			}
			Set<Shape> unwantedShapes = new HashSet<Shape>();
			Set<RuleParticipantSignature> unwantedSignatures = new HashSet<RuleParticipantSignature>();
			unwantedShapes.addAll(getShapes());
			unwantedSignatures.addAll(ruleParticipantSignatures);
			ContainerContainerShape containerShape = (ContainerContainerShape) getShapeFromModelObject(getModel());
			List<ReactionContainerShape> reactionContainerShapeList = 
				new ArrayList<ReactionContainerShape>();
			List<Structure> structureList = new ArrayList<Structure>(getStructureSuite().getStructures());
//			Collections.sort(structureList, new Comparator<Structure>() {
//				public int compare(Structure o1, Structure o2) {
//					 return getStructureLevel(o1).compareTo(getStructureLevel(o2));
//				}
//			});
			// create all ReactionContainerShapes (one for each Structure)
			for(Structure structure : structureList) {
				if (structure instanceof Membrane) {
					Membrane membrane = (Membrane) structure;
					ReactionContainerShape membraneShape = 
						(ReactionContainerShape) getShapeFromModelObject(membrane);
					if (membraneShape == null) {
						membraneShape = new ReactionContainerShape(membrane, structureSuite, this);
						addShape(membraneShape);
						membrane.getMembraneVoltage().removePropertyChangeListener(this);
						membrane.getMembraneVoltage().addPropertyChangeListener(this);
					} else {
						membraneShape.setStructureSuite(structureSuite);
					}
					membrane.removePropertyChangeListener(this);
					membrane.addPropertyChangeListener(this);
					membraneShape.refreshLabel();
					unwantedShapes.remove(membraneShape);
					reactionContainerShapeList.add(membraneShape);
				}else if (structure instanceof Feature){
					Feature feature = (Feature) structure;
					ReactionContainerShape featureShape = (ReactionContainerShape) getShapeFromModelObject(feature);
					if (featureShape == null) {
						featureShape = new ReactionContainerShape(feature, structureSuite, this);
						addShape(featureShape);
					} else {
						featureShape.setStructureSuite(structureSuite);
					}
					feature.removePropertyChangeListener(this);
					feature.addPropertyChangeListener(this);
					featureShape.refreshLabel();
					unwantedShapes.remove(featureShape);
					reactionContainerShapeList.add(featureShape);
				}
			}
			if (containerShape == null) {
				containerShape = new ContainerContainerShape(this, getModel(), reactionContainerShapeList);
				addShape(containerShape);
			} else {
				containerShape.setReactionContainerShapeList(reactionContainerShapeList);
			}
			containerShape.refreshLabel();
			unwantedShapes.remove(containerShape);
			// add all species context shapes within the structures
			for(Structure structure : getStructureSuite().getStructures()) {
				ReactionContainerShape reactionContainerShape =	(ReactionContainerShape) getShapeFromModelObject(structure);
				structure.removePropertyChangeListener(this);
				structure.addPropertyChangeListener(this);
				for(SpeciesContext structSpeciesContext : getModel().getSpeciesContexts(structure)) {
					SpeciesContextShape ss = (SpeciesContextShape) getShapeFromModelObject(structSpeciesContext);
					if (ss == null) {
						ss = new SpeciesContextShape(structSpeciesContext, this);
						ss.truncateLabelName(false);
						structSpeciesContext.getSpecies().removePropertyChangeListener(this);
						structSpeciesContext.getSpecies().addPropertyChangeListener(this);
						reactionContainerShape.addChildShape(ss);
						addShape(ss);
						ss.getSpaceManager().setRelPos(reactionContainerShape.getRandomPosition());
					}
					structSpeciesContext.removePropertyChangeListener(this);
					structSpeciesContext.addPropertyChangeListener(this);
					ss.refreshLabel();
					unwantedShapes.remove(ss);
				}
			}
			// add all reactionSteps that are in this structure (ReactionContainerShape), and draw the lines
			getModel().removePropertyChangeListener(this);
			getModel().addPropertyChangeListener(this);
			//
			// =================================== Rules ================================================
			//
			// we go through all the participants; depending on the transition, we make their shapes visible or invisible
			for(ReactionRule rr : getModel().getRbmModelContainer().getReactionRuleList()) {
				Structure structure = rr.getStructure();
				if(getStructureSuite().areReactionsShownFor(structure)) {
					List<ReactionRuleParticipant> participants = rr.getReactionRuleParticipants();
					for(ReactionRuleParticipant participant : participants) {
						participant.getSpeciesPattern().removePropertyChangeListener(this);
						participant.getSpeciesPattern().addPropertyChangeListener(this);
						Structure speciesStructure = participant.getStructure();
						Structure reactionStructure = rr.getStructure();
						if(getStructureSuite().getStructures().contains(speciesStructure) && getStructureSuite().areReactionsShownFor(reactionStructure)) {
							for (RuleParticipantSignature signature : ruleParticipantSignatures){
								if (signature.getStructure() == participant.getStructure() && signature.compareByCriteria(participant.getSpeciesPattern(), Criteria.full)){
									RuleParticipantSignatureDiagramShape signatureShape = (RuleParticipantSignatureDiagramShape) getShapeFromModelObject(signature);
									if(ruleParticipantGroupingCriteria == Criteria.full && transitioning) {
										signatureShape.setVisible(true);
									} else if(ruleParticipantGroupingCriteria == Criteria.moleculeNumber && transitioning) {
										signatureShape.setVisible(false);
									}
									unwantedShapes.remove(signatureShape);		// we already know we want to keep them all, regardless of transition or visibility
									unwantedSignatures.remove(signature);
									break;
								}
							}
						}
					}
				}
			}
			
			// 
			for(ReactionRule rr : getModel().getRbmModelContainer().getReactionRuleList()) {
				rr.removePropertyChangeListener(this);
				rr.addPropertyChangeListener(this);
				Structure structure = rr.getStructure();
				if(getStructureSuite().areReactionsShownFor(structure)) {
					ReactionContainerShape reactionContainerShape =	(ReactionContainerShape) getShapeFromModelObject(structure);
					ReactionRuleDiagramShape rrShape = (ReactionRuleDiagramShape) getShapeFromModelObject(rr);
					if (rrShape == null) {
						rrShape = new ReactionRuleDiagramShape(rr, this);
						addShape(rrShape);
						rrShape.getSpaceManager().setRelPos(reactionContainerShape.getRandomPosition());
						reactionContainerShape.addChildShape(rrShape);
						rrShape.getSpaceManager().setRelPos(reactionContainerShape.getRandomPosition());
					}
					rrShape.refreshLabel();
					unwantedShapes.remove(rrShape);
					
					//
					// add reaction participants as edges and SignatureShapes as needed
					//
					List<ReactionRuleParticipant> participants = rr.getReactionRuleParticipants();
					List<RuleParticipantEdgeDiagramShape> ruleEdges = new ArrayList<> ();
					for(ReactionRuleParticipant participant : participants) {
						participant.getSpeciesPattern().removePropertyChangeListener(this);
						participant.getSpeciesPattern().addPropertyChangeListener(this);
						Structure speciesStructure = participant.getStructure();
						Structure reactionStructure = rr.getStructure();
						
						if(getStructureSuite().getStructures().contains(speciesStructure) && getStructureSuite().areReactionsShownFor(reactionStructure)) {
							//
							// find existing RuleParticipantSignatureShape in cartoon
							//
							RuleParticipantSignature ruleParticipantSignature = null;
							for (RuleParticipantSignature signature : ruleParticipantSignatures){
								if (signature.getStructure() == participant.getStructure() && signature.compareByCriteria(participant.getSpeciesPattern(), ruleParticipantGroupingCriteria)){
									ruleParticipantSignature = signature;
									break;
								}
							}
							//
							// if didn't find signature in cartoons list of signatures, then create one (and create a shape for it).
							//
							RuleParticipantSignatureDiagramShape signatureShape = null;
							if (ruleParticipantSignature == null){
								ruleParticipantSignature = RuleParticipantSignature.fromReactionRuleParticipant(participant, this);
								ruleParticipantSignatures.add(ruleParticipantSignature);
								signatureShape = new RuleParticipantSignatureDiagramShape(ruleParticipantSignature, this);
								addShape(signatureShape);
								ReactionContainerShape participantContainerShape =	(ReactionContainerShape) getShapeFromModelObject(participant.getStructure());
								signatureShape.getSpaceManager().setRelPos(participantContainerShape.getRandomPosition());
								participantContainerShape.addChildShape(signatureShape);
								signatureShape.getSpaceManager().setRelPos(participantContainerShape.getRandomPosition());
							} else {
								signatureShape = (RuleParticipantSignatureDiagramShape) getShapeFromModelObject(ruleParticipantSignature);
								signatureShape.setVisible(true);
							}
							unwantedShapes.remove(signatureShape);
							unwantedSignatures.remove(ruleParticipantSignature);
							if(signatureShape != null) {
								signatureShape.refreshLabel();
							}

							//
							// add edge for ReactionRuleParticipant if not already present.
							//
							RuleParticipantEdgeDiagramShape ruleParticipantShape = (RuleParticipantEdgeDiagramShape) getShapeFromModelObject(participant);
							if (ruleParticipantShape == null || ruleParticipantShape.getRuleParticipantSignatureShape() != signatureShape) {
								if (participant instanceof ReactantPattern && signatureShape.isVisible()) {
									ruleParticipantShape = new ReactantPatternEdgeDiagramShape((ReactantPattern) participant, rrShape, signatureShape, this);
								} else if (participant instanceof ProductPattern && signatureShape.isVisible()) {
									ruleParticipantShape = new ProductPatternEdgeDiagramShape((ProductPattern) participant, rrShape, signatureShape, this);
//								} else if (participant instanceof Catalyst) {
//									ruleParticipantShape = new CatalystShape((Catalyst) participant, reactionStepShape, speciesContextShape, this);
								} else {
									throw new RuntimeException("unsupported ReactionRuleParticipant " + participant.getClass());
								}
								addShape(ruleParticipantShape);
							}
							if(!containerShape.getChildren().contains(ruleParticipantShape)) {
								containerShape.addChildShape(ruleParticipantShape);								
							}
							unwantedShapes.remove(ruleParticipantShape);
							ruleParticipantShape.refreshLabel();
							ruleEdges.add(ruleParticipantShape);	// all the edges for this rule
						}
					}
					// now let's see if any reactant and product pair have the same signature - means we need to draw a reactant and 
					// a product edge (a closed loop) between the rule diagram shape and and the signature diagram shape
					for(RuleParticipantEdgeDiagramShape ours : ruleEdges) {
						ours.setSibling(false);		// reset them all
					}
					for(RuleParticipantEdgeDiagramShape ours : ruleEdges) {
						for(RuleParticipantEdgeDiagramShape theirs : ruleEdges) {
							if(ours == theirs) {
								continue;			// don't compare with self
							}
							if(ours.getRuleParticipantSignatureShape() == theirs.getRuleParticipantSignatureShape()) {
								ours.setSibling(true);
								theirs.setSibling(true);
							}
						}
					}
				}
			}
			ruleParticipantSignatures.removeAll(unwantedSignatures);
			
			for(ReactionStep reactionStep : getModel().getReactionSteps()) {
				reactionStep.removePropertyChangeListener(this);
				reactionStep.addPropertyChangeListener(this);
				Structure structure = reactionStep.getStructure();
				if(getStructureSuite().areReactionsShownFor(structure)) {
					ReactionContainerShape reactionContainerShape =	(ReactionContainerShape) getShapeFromModelObject(structure);
					if(reactionContainerShape == null) {
						System.out.println("Reaction container shape is null for structure " + structure +
								" for reaction step " + reactionStep);
					}
					ReactionStepShape reactionStepShape = (ReactionStepShape) getShapeFromModelObject(reactionStep);
					if (reactionStepShape == null) {
						if (reactionStep instanceof SimpleReaction) {
							reactionStepShape = new SimpleReactionShape((SimpleReaction) reactionStep, this);
						} else if (reactionStep instanceof FluxReaction) {
							reactionStepShape = new FluxReactionShape((FluxReaction) reactionStep, this);
						} else {
							throw new RuntimeException("unknown type of ReactionStep '"	+ reactionStep.getClass().toString());
						}
						addShape(reactionStepShape);
						reactionStepShape.getSpaceManager().setRelPos(reactionContainerShape.getRandomPosition());
						reactionContainerShape.addChildShape(reactionStepShape);
						reactionStepShape.getSpaceManager().setRelPos(reactionContainerShape.getRandomPosition());
					}
					reactionStepShape.refreshLabel();
					unwantedShapes.remove(reactionStepShape);
					// add reaction participants as edges
					for(ReactionParticipant participant : reactionStep.getReactionParticipants()) {
						participant.removePropertyChangeListener(this);
						participant.addPropertyChangeListener(this);
						Structure speciesStructure = participant.getStructure();
						Structure reactionStructure = reactionStep.getStructure();
						if(getStructureSuite().getStructures().contains(speciesStructure) &&
								getStructureSuite().areReactionsShownFor(reactionStructure)) {
							SpeciesContext speciesContext = getModel().getSpeciesContext(participant.getSpecies(), speciesStructure);
							// add speciesContextShapes that are not in this structure, but are referenced from the reactionParticipants
							// these are only when reactionParticipants are from features that are outside of the membrane being displayed
							SpeciesContextShape speciesContextShape = 
								(SpeciesContextShape) getShapeFromModelObject(speciesContext);
							if (speciesContextShape == null) {
								speciesContextShape = new SpeciesContextShape(speciesContext, this);
								speciesContextShape.truncateLabelName(false);
								reactionContainerShape.addChildShape(speciesContextShape);
								addShape(speciesContextShape);
								speciesContextShape.getSpaceManager().setRelPos(reactionContainerShape.getRandomPosition());
							}
							speciesContextShape.refreshLabel();
							unwantedShapes.remove(speciesContextShape);
							ReactionParticipantShape reactionParticipantShape = (ReactionParticipantShape) getShapeFromModelObject(participant);
							if (reactionParticipantShape == null) {
								if (participant instanceof Reactant) {
									reactionParticipantShape = new ReactantShape((Reactant) participant, reactionStepShape, speciesContextShape, this);
								} else if (participant instanceof Product) {
									reactionParticipantShape = new ProductShape((Product) participant, reactionStepShape, speciesContextShape, this);
								} else if (participant instanceof Catalyst) {
									reactionParticipantShape = new CatalystShape((Catalyst) participant, reactionStepShape, speciesContextShape, this);
								} else {
									throw new RuntimeException("unsupported ReactionParticipant " + participant.getClass());
								}
								addShape(reactionParticipantShape);
							}
							if(!containerShape.getChildren().contains(reactionParticipantShape)) {
								containerShape.addChildShape(reactionParticipantShape);								
							}
							unwantedShapes.remove(reactionParticipantShape);
							reactionParticipantShape.refreshLabel();
						}
					}
				}
			}
			for(Shape unwantedShape : unwantedShapes) { removeShape(unwantedShape); }
			// update diagrams
			for(Structure structure : structureSuite.getStructures()) {
				Diagram diagram = getModel().getDiagram(structure);
				if (diagram != null) {
					applyDefaults(diagram);
				}				
			}
			fireGraphChanged(new GraphEvent(this));
		} catch (Throwable e) {
			handleException(e);
		}
	}

	public void setPositionsFromReactionCartoon(Diagram diagram) {
		List<NodeReference> nodeList = new ArrayList<NodeReference>();
		for(Shape shape : getShapes()) {
			if (shape instanceof FluxReactionShape) {
				nodeList.add(new NodeReference(
						NodeReference.FLUX_REACTION_NODE, ((FluxReaction) shape.getModelObject()).getName(), 
						shape.getSpaceManager().getRelPos()));
			} else if (shape instanceof SimpleReactionShape) {
				nodeList.add(new NodeReference(
						NodeReference.SIMPLE_REACTION_NODE,
						((ReactionStep) shape.getModelObject()).getName(),
						shape.getSpaceManager().getRelPos()));
			} else if (shape instanceof ReactionRuleDiagramShape) {
				nodeList.add(new NodeReference(
						NodeReference.REACTION_RULE_NODE,
						((ReactionRule) shape.getModelObject()).getName(),
						shape.getSpaceManager().getRelPos()));
			} else if (shape instanceof SpeciesContextShape) {
				nodeList.add(new NodeReference(
						NodeReference.SPECIES_CONTEXT_NODE,
						((SpeciesContext) shape.getModelObject()).getName(),
						shape.getSpaceManager().getRelPos()));
			} else if (shape instanceof RuleParticipantSignatureDiagramShape) {
				RuleParticipantSignature ruleParticipantSignature = (RuleParticipantSignature) shape.getModelObject();
				if (ruleParticipantSignature.getStructure() == diagram.getStructure()){
					String spAsString = ruleParticipantSignature.getSpeciesPatternAsString();
					NodeReference nr = new NodeReference(NodeReference.RULE_PARTICIPANT_SIGNATURE_NODE, spAsString, shape.getSpaceManager().getRelPos());
					nr.speciesPattern = ruleParticipantSignature.getSpeciesPattern();
					nodeList.add(nr);
				}
			}
		}
		diagram.setNodeReferences(nodeList.toArray(new NodeReference[0]));
	}

	public void setStructureSuite(StructureSuite structureSuite) {
		if(this.structureSuite != null){
			cleanupAll();
		}
		this.structureSuite = structureSuite;
		refreshAll();
	}

}
