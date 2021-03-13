package GeneticAlgorithm;

import Models.Chromosome;
import Models.Customer;
import Models.Depot;
import Models.MDVRP;
import Utilities.ProblemInit;
import Utilities.Utils;

import java.io.IOException;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws IOException {
        MDVRP problem = ProblemInit.initializeProblem();
        GeneticAlgorithm ga = new GeneticAlgorithm(problem);
        String solution = ga.main();
        Utils.writeSolution(solution);
    }
}
