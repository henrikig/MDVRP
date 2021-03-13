package GeneticAlgorithm;

import Models.MDVRP;
import Utilities.Parameters;
import Utilities.ProblemInit;
import Utilities.Utils;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        MDVRP problem = ProblemInit.initializeProblem();
        GeneticAlgorithm ga = new GeneticAlgorithm(problem);
        String solution = ga.main();
        Utils.writeSolution(solution);

        String[] cmd = {
                "python3",
                Parameters.PLOT_FILE,
                Parameters.PROBLEM_FILE
        };

        Runtime.getRuntime().exec(cmd);

    }
}
