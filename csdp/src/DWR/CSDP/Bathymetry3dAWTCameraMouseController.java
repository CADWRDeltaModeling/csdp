package DWR.CSDP;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.ControllerType;
import org.jzy3d.chart.controllers.mouse.AWTMouseUtilities;
import org.jzy3d.chart.controllers.mouse.camera.AWTCameraMouseController;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Scale;
import org.jzy3d.plot3d.rendering.view.View;

/**
 * Overrides default MouseController for jzy3d graphs, which would only zoom in the z direction.
 * This was a problem when displaying cross-sections, because when the x and y axes have difference scales, the
 * cross-sections appear to be not perpendicular to the channel.
 * This also allows user to zoom way in.
 * 
 * MouseWheel zooms z axis
 * Ctrl-MouseWheel zooms x axis
 * Alt-MouseWheel zooms y axis 
 * Ctrl-Alt-MouseWheel zooms x and y axes simultaneously by the same amount
 * 
 * See AbstractCameraController and AWTCameraMouseController for more interactive possibilities
 * https://groups.google.com/forum/#!topic/jzy3d/6nkwF8C01Do
 * @author btom
 *
 */
public class Bathymetry3dAWTCameraMouseController extends AWTCameraMouseController{
	private static final int X_DIRECTION = 10;
	private static final int Y_DIRECTION = 20;
	private static final int Z_DIRECTION = 30;
	
	/** Compute zoom */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		stopThreadController();
		float factor = 1 + (e.getWheelRotation() / 10.0f);
		if(e.isControlDown() && e.isAltDown()) {
			zoomX(factor);
			zoomY(factor);
		}else if(e.isControlDown()) {
			zoomX(factor);
		}else if(e.isAltDown()) {
			zoomY(factor);
		}else {
			zoomZ(factor);
		}
	}//mouseWheelMoved

	//this was an attempt at enabling panning in the x and y directions...it doesn't work yet.
	/** Compute shift or rotate */
	@Override
	public void mouseDragged(MouseEvent e) {
		Coord2d mouse = xy(e);

		// Rotate
		if (AWTMouseUtilities.isLeftDown(e)) {
			Coord2d move = mouse.sub(prevMouse).div(100);
			rotate(move);
		}
		// Shift
		else if (AWTMouseUtilities.isRightDown(e)) {
			Coord2d move = mouse.sub(prevMouse);
			if(e.isControlDown() && e.isAltDown()) {
				pan(-move.x/500, true, X_DIRECTION);
				pan(move.y/500, true, Y_DIRECTION);
			}else if(e.isControlDown()) {
				pan(-move.x/500, true, X_DIRECTION);
			}else if(e.isAltDown()) {
				pan(move.y/500, true, Y_DIRECTION);
			}else {
				if (move.y != 0)
					shift(move.y / 500);
			}
		}
		prevMouse = mouse;
	}
	
	protected void pan(final float factor, boolean updateView, int direction){
		for(Chart c: targets) {
			View view = c.getView();
			if(direction == X_DIRECTION) {
				Scale current = new Scale(view.getBounds().getXmin(), view.getBounds().getXmax());
				Scale newScale = current.add(factor * current.getRange());
				view.setScaleX(newScale, true);
			}else if(direction == Y_DIRECTION) {
				Scale current = new Scale(view.getBounds().getYmin(), view.getBounds().getYmax());
				Scale newScale = current.add(factor * current.getRange());
				view.setScaleY(newScale, true);
			}
		}
		fireControllerEvent(ControllerType.SHIFT, factor);
	}
	
	
}//class Bathymetry3dAWTCameraMouseController
