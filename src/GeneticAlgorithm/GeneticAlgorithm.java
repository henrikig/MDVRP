package GeneticAlgorithm;

import Models.Chromosome;
import Models.MDVRP;
import Utilities.Parameters;
import Utilities.ProblemInit;
import com.google.gson.Gson;
import org.apache.commons.lang.SerializationUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GeneticAlgorithm {

    private MDVRP problem;
    private ArrayList<Chromosome> population;
    private ArrayList<Chromosome> parents;
    private ArrayList<Chromosome> matingPool;
    private final Random random = new Random();
    private final Gson gson = new Gson();

    public GeneticAlgorithm(MDVRP problem) {
        this.problem = problem;
        this.population = new ArrayList<>(Parameters.POPULATION_SIZE);
        this.parents = new ArrayList<>(Parameters.POPULATION_SIZE);
    }

    public void main() {
        initPopulation();

        for (int i = 0; i < Parameters.GENERATIONS; i++) {
            scheduleRoutes();
            resetPopulation();
            elitism();
            tournamentSelection();
            getFitness();
        }
    }

    public void initPopulation() {
        for (int i = 0; i < Parameters.POPULATION_SIZE; i++) {
            this.population.add(new Chromosome(problem));
        }
    }

    public void scheduleRoutes() {
        for (Chromosome chromosome : population) {
            chromosome.scheduleRoutes();
        }
    }

    public void resetPopulation() {
        this.parents.clear();
        this.parents.addAll(this.population);
        this.population.clear();
    }

    public void elitism() {
        Collections.sort(this.parents);
        for (int i = this.parents.size() - 1; i >= Parameters.POPULATION_SIZE - Parameters.ELITISM; i--) {
            this.population.add(parents.get(i));
        }
    }

    public void tournamentSelection() {
        for (int i = 0; i < Parameters.POPULATION_SIZE - Parameters.ELITISM; i++) {
            Chromosome p1 = parents.get(random.nextInt(parents.size()));
            Chromosome p2 = parents.get(random.nextInt(parents.size()));

            Chromosome clone;

            if (Math.random() < Parameters.KEEP_BEST) {
                if (p1.compareTo(p2) > 0) {
                    clone = (Chromosome) SerializationUtils.clone(p1);
                } else {
                    clone = (Chromosome) SerializationUtils.clone(p2);
                }
            } else {
                clone = (Chromosome) SerializationUtils.clone(p1);
            }
            population.add(clone);
        }
    }

    public void getFitness() {
        double totalFitness = 0.0;
        double counter = 0;
        for (Chromosome c : population) {
            totalFitness += c.getFitness();
            counter++;
        }
        System.out.println("AVG: " + totalFitness/counter);
        System.out.println("BEST: " + this.parents.get(this.parents.size()-1).getFitness());
    }

    public static void main(String[] args) {
        MDVRP problem = ProblemInit.initializeProblem();
        GeneticAlgorithm ga = new GeneticAlgorithm(problem);
        ga.main();
    }
}
