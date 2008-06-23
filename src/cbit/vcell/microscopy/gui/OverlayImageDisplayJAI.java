package cbit.vcell.microscopy.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.operator.CompositeDescriptor;
import javax.media.jai.operator.ExtremaDescriptor;
import javax.media.jai.operator.RescaleDescriptor;
import javax.media.jai.operator.ScaleDescriptor;

import com.sun.media.jai.widget.DisplayJAI;

/**
 */
public class OverlayImageDisplayJAI extends DisplayJAI {
	private BufferedImage underlyingImage = null;
	private BufferedImage highlightImage = null;
	private RenderedImage alphaImageUnderlying = null;
	private RenderedImage alphaImageHightlight = null;
	private short[] highlightImageWritebackBuffer = null;
	private float zoom = 1.0f;
	private PlanarImage source = null;
	
	private int contrastFactor = 0;
	private static final int CONTRAST_BOUND = 4;
	private Rectangle cropRect = null;
	
	private static final double SCALE_MAX = Math.pow(2, 8)-1;

	public OverlayImageDisplayJAI(){
		super();
	}
	
	/**
	 * Method setUnderlyingImage.
	 * @param argUnderlyingImage BufferedImage
	 */
	public void setUnderlyingImage(BufferedImage argUnderlyingImage,boolean bNew){
		this.underlyingImage = argUnderlyingImage;
		if(bNew){
			resetGUI();
		}
		refreshImage();
	}
	private void resetGUI(){
		zoom = 1.0f;
		contrastFactor = 0;
		cropRect = null;
	}
	/**
	 * Method setHighlightImageAndWritebackBuffer.
	 * @param argHighlightImage BufferedImage
	 * @param argHighlightImageWritebackBuffer short[]
	 */
	public void setHighlightImageAndWritebackBuffer(BufferedImage argHighlightImage, short[] argHighlightImageWritebackBuffer){
		this.highlightImage = argHighlightImage;
		this.highlightImageWritebackBuffer = argHighlightImageWritebackBuffer;
		refreshImage();
	}
	
	/**
	 * Method getHighlightImageWritebackImageBuffer.
	 * @return short[]
	 */
	public short[] getHighlightImageWritebackImageBuffer(){
		return highlightImageWritebackBuffer;
	}
	
	/**
	 * Method getHighlightImage.
	 * @return BufferedImage
	 */
	public BufferedImage getHighlightImage(){
		return highlightImage;
	}
	
	/**
	 * Method getUnderlyingImage.
	 * @return BufferedImage
	 */
	public BufferedImage getUnderlyingImage(){
		return underlyingImage;
	}
	
	/**
	 * Method setZoom.
	 * @param argZoom float
	 */
	public void setZoom(float argZoom){
		zoom = argZoom;
		refreshImage();
	}
//	public void setDirty(){
//	}
	/**
	 * Method getZoom.
	 * @return float
	 */
	public float getZoom(){
		return zoom;
	}
	
	/**
	 * Method makeAlpha.
	 * @param src RenderedImage
	 * @param b float
	 * @return RenderedImage
	 */
	private RenderedImage makeAlpha(RenderedImage src, float b) {
	      // get color band
	      int band = src.getColorModel().getNumColorComponents();

	      // ignore alpha channel
	      band = band > 3 ? 3 : band;

	      // make alpha channel paramenter
	      Byte[] bandValues = new Byte[band];
	      for ( int i = 0 ; i < band ; i++ ) {
	             bandValues[i] = new Byte((byte)(b*SCALE_MAX));
	      }

	        // make alpha channel paramenter
	        ParameterBlock pb = new ParameterBlock();
	        pb.add((float)src.getWidth());
	        pb.add((float)src.getHeight());
	        pb.add(bandValues);

	        // make alpha channel
	        return JAI.create("constant", pb, null);
	 }



	public void refreshImage(){
		if (underlyingImage!=null && highlightImage!=null){
			alphaImageUnderlying = makeAlpha(underlyingImage, 0.6f);
			alphaImageHightlight = makeAlpha(highlightImage, 1.0f);
			RenderedImage contrastEnhancedUnderlyingImage = underlyingImage;
			if(contrastFactor != 0){
				double[][] minmaxArr = (double[][])ExtremaDescriptor.create(underlyingImage, null, 1, 1, false, 1, null).getProperty("extrema");
				double offset = 0;
				if((contrastFactor >= 1) && (minmaxArr[0][0]-minmaxArr[1][0]) != 0){
					offset = (SCALE_MAX*minmaxArr[0][0])/(minmaxArr[0][0]-minmaxArr[1][0]);
				}
				double scale =
					(contrastFactor<0
						?(double)(CONTRAST_BOUND+contrastFactor)/(double)CONTRAST_BOUND
						:(double)(CONTRAST_BOUND+contrastFactor)/(double)CONTRAST_BOUND);
				if((minmaxArr[1][0]-minmaxArr[0][0]) != 0){
					if(contrastFactor > 1){
						//AutoContrast and Brighten
						scale*= (SCALE_MAX)/(minmaxArr[1][0]-minmaxArr[0][0]);
					}else if(contrastFactor == 1){
						//AutoContrast
						scale = (SCALE_MAX)/(minmaxArr[1][0]-minmaxArr[0][0]);
					}
				}
				//double offset = .1*minmaxArr[1][0]-minmaxArr[0][0];
				//double offset = (255.0-minmaxArr[0][0])/(minmaxArr[0][0]-minmaxArr[1][0]);
				contrastEnhancedUnderlyingImage =
					RescaleDescriptor.create(underlyingImage,new double[]{scale},new double[]{offset},null);
			}
//			ParameterBlock params1 = new ParameterBlock();
//			params1.addSource(underlyingImage);
//			params1.addSource(highlightImage);
//			params1.add(alphaImageUnderlying);
//			params1.add(alphaImageHightlight);
//			params1.add(false);
//			params1.add(CompositeDescriptor.NO_DESTINATION_ALPHA);
//			//Interpolation interp = Interpolation.getInstance(Interpolation.INTERP_NEAREST);
			source =
				CompositeDescriptor.create(
						contrastEnhancedUnderlyingImage, highlightImage,
					alphaImageUnderlying, alphaImageHightlight,
					false, CompositeDescriptor.NO_DESTINATION_ALPHA, null);
//			source = JAI.create("composite",params1,null);
//			ParameterBlock params2 = new ParameterBlock();
//			params2.addSource(source);
//			params2.add(zoom);
//			params2.add(zoom);
//			params2.add(0f);
//			params2.add(0f);
//			//Interpolation interp = Interpolation.getInstance(Interpolation.INTERP_NEAREST);
			source =
				ScaleDescriptor.create(
					source, (float)zoom, (float)zoom, 0f, 0f,
					Interpolation.getInstance(Interpolation.INTERP_NEAREST),null);
//			source = JAI.create("scale",params2);
			set(source, 0, 0);
		}else{
			set(new BufferedImage(10,10,BufferedImage.TYPE_INT_ARGB),0,0);
		}
	}
		
	public void drawHighlight(int x, int y, int radius, boolean erase,Color highlightColor,Point lastHighlightPoint){
		if(lastHighlightPoint == null){
			lastHighlightPoint = new Point(x,y);
		}
		Color drawingColor = highlightColor;
		if (erase){
			drawingColor = Color.black;
		}
		float zoom = getZoom();
		if (getHighlightImage()!=null){
			Graphics graphicsUnscaled = getHighlightImage().getGraphics();
			graphicsUnscaled.setColor(drawingColor);
			int size = (int)(radius/zoom/*+radius/zoom*/+1);
			//-----Interpolate between paint points for continuous lines
			double currentX = lastHighlightPoint.x;
			double currentY = lastHighlightPoint.y;
			int dx = x-lastHighlightPoint.x;
			int dy = y-lastHighlightPoint.y;
			double delta = 1.0/(dx==0 && dy==0?1.0:Math.max(Math.abs(dx), Math.abs(dy)));
			int lastX = (int)currentX;
			int lastY = (int)currentY;
			for(int i=0;i<=Math.max(Math.abs(dx), Math.abs(dy));i++){
				if(i== 0 || (int)currentX != lastX || (int)currentY != lastY){
					lastX = (int)currentX;
					lastY = (int)currentY;
					graphicsUnscaled.fillOval((int)((lastX/zoom-(size/2))),(int)((lastY/zoom-(size/2))), size, size);
//					graphicsScaled.fillOval(lastX-size, lastY-size, size, size);
				}
				currentX = lastHighlightPoint.x+(i+1)*delta*dx;
				currentY = lastHighlightPoint.y+(i+1)*delta*dy;
			}
			//------
			graphicsUnscaled.dispose();
//			graphicsScaled.dispose();
			refreshImage();
		}
	}
	
	public void increaseContrast(){
		contrastFactor++;
		refreshImage();
	}
	public void decreaseContrast(){
		contrastFactor--;
		if(contrastFactor < -CONTRAST_BOUND){
			contrastFactor = -CONTRAST_BOUND;
		}
		refreshImage();
	}
	public String getContrastDescription(){
		return
		(contrastFactor == 0?"none":(contrastFactor < 0?"dark"+contrastFactor:"bright+"+contrastFactor));
	}
	
	public void setCrop(Point cropBegin,Point cropEnd){
		if(cropBegin == null || cropEnd == null){
			cropRect = null;
		}else{
			cropRect = new Rectangle(
				(cropBegin.x < cropEnd.x?cropBegin.x:cropEnd.x),
				(cropBegin.y < cropEnd.y?cropBegin.y:cropEnd.y),
				Math.abs(cropBegin.x-cropEnd.x), Math.abs(cropBegin.y-cropEnd.y)
			);
		}
		repaint();
	}
	public Rectangle getCrop(){
		return cropRect;
	}
	public void paint(Graphics g){
		super.paint(g);
		if(getCrop() != null){
			g.setColor(Color.green);
			g.drawRect(getCrop().x,getCrop().y,getCrop().width,getCrop().height);
		}
	}

}
