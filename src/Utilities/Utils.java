package Utilities;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import static java.lang.Math.sqrt;
import static java.lang.Math.pow;

public class Utils {

    public static double EuclideanDist(double x1, double y1, double x2, double y2) {

        double xDist = x2 - x1;
        double yDist = y2 - y1;

        return sqrt(pow(xDist, 2) + pow(yDist, 2));

    }

    public static void writeSolution(String solution) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(Parameters.SOLUTIONS_FOLDER + Parameters.PROBLEM_FILE + ".res"));

        writer.write(solution);

        writer.close();
    }
}
