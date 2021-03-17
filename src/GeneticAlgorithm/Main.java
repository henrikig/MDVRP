package GeneticAlgorithm;

import Models.MDVRP;
import Utilities.Parameters;
import Utilities.ProblemInit;
import Utilities.Utils;

import java.io.IOException;

public class Main {

    public static void runProblem(String filename) throws IOException {
        long startTime = System.nanoTime();

        MDVRP problem = ProblemInit.initializeProblem(filename);

        GeneticAlgorithm ga = new GeneticAlgorithm(problem);

        String solution = ga.main();

        Utils.writeSolution(solution, filename);

        long endTime = System.nanoTime();

        long duration = (endTime - startTime)/1000000000;

        System.out.println("TOTAL DURATION (s): " + duration);

        String[] cmd = {
                "python3",
                Parameters.PLOT_FILE,
                filename
        };

        Runtime.getRuntime().exec(cmd);
    }

    public static void main(String[] args) throws IOException {

        if (Parameters.RUN_ALL) {
            for (String filename : Parameters.ALL_PROBLEMS) {

                Main.runProblem(filename);

            }
        } else {

            Main.runProblem(Parameters.PROBLEM_FILE);

        }



    }
}
