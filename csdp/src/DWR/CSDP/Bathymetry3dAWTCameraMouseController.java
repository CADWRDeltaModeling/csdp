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
}//class Bathymetry3dAWTCameraMouseController
