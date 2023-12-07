package DWR.CSDP.CURVEFIT;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.common.os.MachineDataInfo.StaticConfig;

import DWR.CSDP.StationTimeSeriesData;

/**
 * Ramer-Douglas-Peucker algorithm, a well-known method for reducing the number of points in a curve approximated by a series of points
 * @author btom
 *
 */
public class LineSimplification {
	private static final double DEFAULT_EPSILON=1.0; 
	public static final double MIN_EPSILON=0.1;
	public static final double MAX_EPSILON=10.0;
	
    private static double perpendicularDistance(double[] point, double[] lineStart, double[] lineEnd) {
        double dx = lineEnd[0] - lineStart[0];
        double dy = lineEnd[1] - lineStart[1];

        double mag = Math.sqrt(dx * dx + dy * dy);
        if (mag > 0.0) {
            dx /= mag;
            dy /= mag;
        }

        double pvx = point[0] - lineStart[0];
        double pvy = point[1] - lineStart[1];

        double pvdot = dx * pvx + dy * pvy;

        double ax = pvx - pvdot * dx;
        double ay = pvy - pvdot * dy;

        return Math.sqrt(ax * ax + ay * ay);
    }

    private static void rdp(double[][] points, double epsilon, int startIndex, int endIndex, List<Integer> pointIndexList) {
        if (startIndex >= endIndex) {
            return;
        }
        double maxDistance = 0.0;
        int indexFarthest = 0;

        for (int i = startIndex + 1; i < endIndex; i++) {
            double distance = perpendicularDistance(points[i], points[startIndex], points[endIndex]);
            if (distance > maxDistance) {
                maxDistance = distance;
                indexFarthest = i;
            }
        }

        if (maxDistance > epsilon) {
            rdp(points, epsilon, startIndex, indexFarthest, pointIndexList);
            rdp(points, epsilon, indexFarthest, endIndex, pointIndexList);
        } else {
            pointIndexList.add(endIndex);
        }
    }

    public static double[][] simplifyLine(double[][] points, double epsilon) {
    	epsilon = Math.max(epsilon, MIN_EPSILON);
    	epsilon = Math.min(epsilon, MAX_EPSILON);
    	if (points == null || points.length < 3) {
            return points;
        }

        List<Integer> pointIndexList = new ArrayList();
        pointIndexList.add(0);
        rdp(points, epsilon, 0, points.length - 1, pointIndexList);

        double[][] simplifiedPoints = new double[pointIndexList.size()][2];
        for (int i = 0; i < pointIndexList.size(); i++) {
            simplifiedPoints[i] = points[pointIndexList.get(i)];
        }

        return simplifiedPoints;
    }

    public static double[][] simplifyLine(double[][] points) {
    	double epsilon=DEFAULT_EPSILON;
    	if (points == null || points.length < 3) {
            return points;
        }

        List<Integer> pointIndexList = new ArrayList();
        pointIndexList.add(0);
        rdp(points, epsilon, 0, points.length - 1, pointIndexList);

        double[][] simplifiedPoints = new double[pointIndexList.size()][2];
        for (int i = 0; i < pointIndexList.size(); i++) {
            simplifiedPoints[i] = points[pointIndexList.get(i)];
        }

        return simplifiedPoints;
    }

    // Example usage
    public static void main(String[] args) {
        double[][] points = {{0, 0}, {1, 0.1}, {2, -0.1}, {3, 5}, {4, 6}, {5, 7}, {6, 8.1}, {7, 8}, {8, 9}};
        double epsilon = 1.0;
        double[][] simplifiedPoints = simplifyLine(points, epsilon);

        for (double[] point : simplifiedPoints) {
            System.out.println("[" + point[0] + ", " + point[1] + "]");
        }
    }
}
