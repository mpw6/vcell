package cbit.vcell.graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.util.Displayable;

public class MolecularTypeSmallShape {
	
	private static final int baseWidth = 11;
	private static final int baseHeight = 9;
//	private static final int baseWidth = 15;
//	private static final int baseHeight = 10;
	private static final int cornerArc = 10;

	private int xPos = 0;
	private int yPos = 0;
	private int width = baseWidth;
	private int height = baseHeight;
	
	final Graphics graphicsContext;
	
	private final MolecularType mt;
	private final MolecularTypePattern mtp;
	private final Displayable owner;
	
	List <MolecularComponentSmallShape> componentShapes = new ArrayList<MolecularComponentSmallShape>();

	public MolecularTypeSmallShape(int xPos, int yPos, Graphics graphicsContext, Displayable owner) {
		this.owner = owner;
		this.mt = null;
		this.mtp = null;
		this.xPos = xPos;
		this.yPos = yPos;
		this.graphicsContext = graphicsContext;
		
		width = baseWidth;		// plain species, we want it look closest to a circle (so width smaller than baseWidth)
		height = baseHeight + MolecularComponentSmallShape.componentDiameter / 2;
		// no species pattern - this is a plain species context
	}
	public MolecularTypeSmallShape(int xPos, int yPos, MolecularTypePattern mtp, Graphics graphicsContext, Displayable owner) {
		this.owner = owner;
		this.mt = mtp.getMolecularType();
		this.mtp = mtp;
		this.xPos = xPos;
		this.yPos = yPos;
		this.graphicsContext = graphicsContext;
		int numComponents = mt.getComponentList().size();
		int offsetFromRight = 0;		// total width of all components, based on the length of their names
		for(int i=numComponents-1; i >=0; i--) {
//			MolecularComponentPattern mcp = mtp.getComponentPatternList().get(i);
			MolecularComponent mc = mt.getComponentList().get(i);
			MolecularComponentPattern mcp = mtp.getMolecularComponentPattern(mc);
			MolecularComponentSmallShape mlcls = new MolecularComponentSmallShape(100, 50, mcp, graphicsContext, owner);
			offsetFromRight += mlcls.getWidth() + MolecularComponentSmallShape.componentSeparation;
		}
		
		width = baseWidth + offsetFromRight;	// adjusted for # of components
		height = baseHeight + MolecularComponentSmallShape.componentDiameter / 2;
		int fixedPart = xPos + width;
		offsetFromRight = 4;
		for(int i=numComponents-1; i >=0; i--) {
			int rightPos = fixedPart - offsetFromRight;		// we compute distance from right end
			int y = yPos + height - MolecularComponentSmallShape.componentDiameter;
			// now that we know the dimensions of the molecular type shape we create the component shapes
//			MolecularComponentPattern mcp = mtp.getComponentPatternList().get(i);
			MolecularComponent mc = mt.getComponentList().get(i);
			MolecularComponentPattern mcp = mtp.getMolecularComponentPattern(mc);
			MolecularComponentSmallShape mcss = new MolecularComponentSmallShape(rightPos, y-2, mcp, graphicsContext, owner);
			offsetFromRight += mcss.getWidth() + MolecularComponentSmallShape.componentSeparation;
			componentShapes.add(0, mcss);
		}
	}
	public MolecularTypeSmallShape(int xPos, int yPos, MolecularType mt, Graphics graphicsContext, Displayable owner) {
		this.owner = owner;
		this.mt = mt;
		this.mtp = null;
		this.xPos = xPos;
		this.yPos = yPos;
		this.graphicsContext = graphicsContext;
		int numComponents = mt.getComponentList().size();
		int offsetFromRight = 0;		// total width of all components, based on the length of their names
		for(int i=numComponents-1; i >=0; i--) {
			MolecularComponent mc = getMolecularType().getComponentList().get(i);
			MolecularComponentSmallShape mlcls = new MolecularComponentSmallShape(100, 50, mc, graphicsContext, owner);
			offsetFromRight += mlcls.getWidth() + MolecularComponentSmallShape.componentSeparation;
		}
		
		width = baseWidth + offsetFromRight;	// adjusted for # of components
		height = baseHeight + MolecularComponentSmallShape.componentDiameter / 2;
		int fixedPart = xPos + width;
		offsetFromRight = 4;
		for(int i=numComponents-1; i >=0; i--) {
			int rightPos = fixedPart - offsetFromRight;		// we compute distance from right end
			int y = yPos + height - MolecularComponentSmallShape.componentDiameter;
			// now that we know the dimensions of the molecular type shape we create the component shapes
			MolecularComponent mc = mt.getComponentList().get(i);
			MolecularComponentSmallShape mcss = new MolecularComponentSmallShape(rightPos, y-2, mc, graphicsContext, owner);
			offsetFromRight += mcss.getWidth() + MolecularComponentSmallShape.componentSeparation;
			componentShapes.add(0, mcss);
		}
	}
	
	public void setX(int xPos){ 
		this.xPos = xPos;
	}
	public int getX(){
		return xPos;
	}
	public void setY(int yPos){
		this.yPos = yPos;
	}
	public int getY(){
		return yPos;
	}
	public int getWidth(){
		return width;
	} 
	public int getHeight(){
		return height;
	}
	public MolecularType getMolecularType() {
		return mt;
	}
	public MolecularTypePattern getMolecularTypePattern() {
		return mtp;
	}
	public MolecularComponentSmallShape getComponentShape(int index) {
		return componentShapes.get(index);
	}
	public MolecularComponentSmallShape getShape(MolecularComponentPattern mcpTo) {
		for(MolecularComponentSmallShape mcss : componentShapes) {
			MolecularComponentPattern mcpThis = mcss.getMolecularComponentPattern();
			if(mcpThis == mcpTo) {
				return mcss;
			}
		}
		return null;
	}
	
	public void paintSelf(Graphics g) {
		paintSpecies(g);
	}
	// --------------------------------------------------------------------------------------
	// paintComponent is being overridden in the renderer
	//
	private void paintSpecies(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		Color colorOld = g2.getColor();
		Color primaryColor = null;
		int finalHeight = baseHeight;
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if(mt == null && mtp == null) {		// plain species context
			 primaryColor = Color.green.darker().darker();
			 finalHeight = baseHeight+3;
			Color exterior = Color.green.darker().darker();
			Point2D center = new Point2D.Float(xPos+finalHeight/3, yPos+finalHeight/3);
			float radius = finalHeight*0.5f;
			Point2D focus = new Point2D.Float(xPos+finalHeight/3-1, yPos+finalHeight/3-1);
			float[] dist = {0.1f, 1.0f};
			Color[] colors = {Color.white, exterior};
			RadialGradientPaint p = new RadialGradientPaint(center, radius, focus, dist, colors, CycleMethod.NO_CYCLE);
			g2.setPaint(p);
			Ellipse2D circle = new Ellipse2D.Double(xPos, yPos, finalHeight, finalHeight);
			g2.fill(circle);
			Ellipse2D circle2 = new Ellipse2D.Double(xPos-1, yPos-1, finalHeight, finalHeight);
			g2.setPaint(Color.DARK_GRAY);
			g2.draw(circle2);
				
			g.setColor(colorOld);
			return;
		} else {							// molecular type, species pattern, observable
			primaryColor = Color.blue.darker().darker();
		}
		
		
		
		GradientPaint p = new GradientPaint(xPos, yPos, primaryColor, xPos, yPos + finalHeight/2, Color.WHITE, true);
		g2.setPaint(p);

		RoundRectangle2D rect = new RoundRectangle2D.Float(xPos, yPos, width, finalHeight, cornerArc, cornerArc);
		g2.fill(rect);

		RoundRectangle2D inner = new RoundRectangle2D.Float(xPos+1, yPos+1, width-2, finalHeight-2, cornerArc-3, cornerArc-3);

		g2.setPaint(Color.black);
		g2.draw(rect);
		
		g.setColor(colorOld);
		for(MolecularComponentSmallShape mcss : componentShapes) {
			mcss.paintSelf(g);
		}
		g.setColor(colorOld);
	}
}