package DWR.CSDP;

import java.awt.event.MouseWheelEvent;

import org.jzy3d.chart.controllers.mouse.camera.AWTCameraMouseController;

/**
 * Overrides default MouseController for jzy3d graphs, which would only zoom in the z direction.
 * This was a problem when displaying cross-sections, because when the x and y axes have difference scales, the
 * cross-sections appear to be not perpendicular to the channel.
 * This also allows user to zoom way in.
 * 
 * MouseWheel zooms z axis
 * Ctrl-MouseWheel zooms x axis
 * Alt-MouseWheel zooms y axis 
 * 
 * See AbstractCameraController and AWTCameraMouseController for more interactive possibilities
 * https://groups.google.com/forum/#!topic/jzy3d/6nkwF8C01Do
 * @author btom
 *
 */
public class Bathymetry3dAWTCameraMouseController extends AWTCameraMouseController{
	/** Compute zoom */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		stopThreadController();
		float factor = 1 + (e.getWheelRotation() / 10.0f);
		if(e.isControlDown()) {
			zoomX(factor);
		}else if(e.isAltDown()) {
			zoomY(factor);
		}else {
			zoomZ(factor);
		}
	}//mouseWheelMoved

	
	//this was an attempt at enabling panning in the x and y directions...this may be the wrong approach.
//	/** Compute shift or rotate */
//	@Override
//	public void mouseDragged(MouseEvent e) {
//		Coord2d mouse = xy(e);
//
//		// Rotate
//		if (AWTMouseUtilities.isLeftDown(e)) {
//			Coord2d move = mouse.sub(prevMouse).div(100);
//			rotate(move);
//		}
//		// Shift
//		else if (AWTMouseUtilities.isRightDown(e)) {
//			Coord2d move = mouse.sub(prevMouse);
//			if(move.x==0 && move.y!=0) {
//				if (move.y != 0)
//					shift(move.y / 500);
//			}else if(move.x!=0 && move.y!=0) {
//				pan((float) Math.sqrt((move.x/500)*(move.x/500) + (move.y/500)*(move.y/500)), true);
//			}else {
////				if (move.y != 0)
////					shift(move.y / 500);
//			}
//		}
//		prevMouse = mouse;
//	}
//	
//    
//	protected void pan(final float factor, boolean updateView){
//		for(Chart c: targets)
//			c.getView().shift(factor, updateView);
//		fireControllerEvent(ControllerType.PAN, factor);
//	}
	
	
}//class Bathymetry3dAWTCameraMouseController
