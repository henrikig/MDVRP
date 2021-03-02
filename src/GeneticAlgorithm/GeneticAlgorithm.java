package GeneticAlgorithm;

import Models.Chromosome;
import Models.MDVRP;
import Utilities.Parameters;
import Utilities.ProblemInit;

import java.util.ArrayList;

public class GeneticAlgorithm {

    private MDVRP problem;
    private ArrayList<Chromosome> population;

    public GeneticAlgorithm(MDVRP problem) {
        this.problem = problem;
    }

    public void main() {
        initPopulation();
        scheduleRoutes();
    }

    public void initPopulation() {
        ArrayList<Chromosome> population = new ArrayList<>();
        for (int i = 0; i < Parameters.POPULATION_SIZE; i++) {
            population.add(new Chromosome(problem));
        }
        this.population = population;
    }

    public void scheduleRoutes() {
        for (Chromosome chromosome : population) {
            chromosome.scheduleRoutes();
        }
    }

    public void getFitness() {
        for (Chromosome c : population) {
            System.out.println(c.getFitness());
        }
    }

    public static void main(String[] args) {
        MDVRP problem = ProblemInit.initializeProblem();
        GeneticAlgorithm ga = new GeneticAlgorithm(problem);
        ga.main();
        ga.getFitness();
    }
}
