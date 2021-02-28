package Utilities;

import static java.lang.Math.sqrt;
import static java.lang.Math.pow;

public class Utils {

    public static double EuclideanDist(double x1, double y1, double x2, double y2) {

        double xDist = x2 - x1;
        double yDist = y2 - y1;

        return sqrt(pow(xDist, 2) + pow(yDist, 2));

    }
}
