package GeneticAlgorithm;

import Models.Chromosome;
import Models.Customer;
import Models.Depot;
import Models.MDVRP;
import Utilities.ProblemInit;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        MDVRP mdvrp = ProblemInit.initializeProblem();
        Chromosome c = new Chromosome(mdvrp);
    }
}
