package GeneticAlgorithm;

import Models.*;
import Utilities.Parameters;
import org.apache.commons.lang.SerializationUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GeneticAlgorithm {

    private MDVRP problem;
    private ArrayList<Chromosome> population;
    private ArrayList<Chromosome> parents;
    private final Random random = new Random();
    private Chromosome bestSolution;

    public GeneticAlgorithm(MDVRP problem) {
        this.problem = problem;
        this.population = new ArrayList<>(Parameters.POPULATION_SIZE);
        this.parents = new ArrayList<>(Parameters.POPULATION_SIZE);
    }

    public String main() {
        initPopulation();
        scheduleRoutes(this.problem);

        bestSolution = this.population.get(0);

        long start = System.nanoTime();

        for (int i = 0; i < Parameters.GENERATIONS; i++) {
            long elapsedTime = (System.nanoTime() - start)/1000000000;

            if (elapsedTime < 300 && bestSolution.getFitness(this.problem) > Parameters.FITNESS_TARGET) {
                resetPopulation();

                elitism();

                tournamentSelection();

                nextPopulation();

                getFitness();

                bestFeasible();


            } else {
                break;
            }

        }

        return createSolution();
    }

    public void initPopulation() {
        for (int i = 0; i < Parameters.POPULATION_SIZE; i++) {
            this.population.add(new Chromosome(problem));
        }
    }

    public void scheduleRoutes(MDVRP problem) {
        for (Chromosome chromosome : population) {
            chromosome.scheduleRoutes(problem);
        }
    }

    public void resetPopulation() {
        this.parents.clear();
        this.parents.addAll(this.population);
        this.population.clear();
    }

    public void elitism() {
        this.parents.sort((c1, c2) -> Chromosome.compare(c1, c2, this.problem));

        for (int i = this.parents.size() - 1; i >= Parameters.POPULATION_SIZE - Parameters.ELITISM; i--) {
            this.population.add(parents.get(i));
        }
    }

    public void tournamentSelection() {
        for (int i = 0; i < Parameters.POPULATION_SIZE - Parameters.ELITISM; i++) {
            Chromosome p1 = parents.get(random.nextInt(parents.size()));
            Chromosome p2 = parents.get(random.nextInt(parents.size()));

            Chromosome clone;

            if (Math.random() <= Parameters.KEEP_BEST) {
                if (Chromosome.compare(p1, p2, this.problem) > 0) {
                    clone = (Chromosome) SerializationUtils.clone(p1);
                } else {
                    clone = (Chromosome) SerializationUtils.clone(p2);
                }
            } else {
                if (random.nextInt(2) == 1) {
                    clone = (Chromosome) SerializationUtils.clone(p1);
                } else {
                    clone = (Chromosome) SerializationUtils.clone(p2);
                }
            }
            population.add(clone);
        }
    }

    public void nextPopulation() {
        for (int i = Parameters.ELITISM; i < Parameters.POPULATION_SIZE; i+=2) {
            if(Math.random() <= Parameters.XOVER_PROB) {
                Chromosome p1 = this.population.get(i);
                Chromosome p2 = this.population.get(i + 1);

                crossover(p1, p2);


                if (Math.random() <= Parameters.MUTATION_PROB) {
                    this.mutation(p1);
                }
                if (Math.random() <= Parameters.MUTATION_PROB) {
                    this.mutation(p2);
                }

            }
        }

        Collections.shuffle(this.population);
    }

    public void crossover(Chromosome c1, Chromosome c2) {
        int randomDepot = random.nextInt(this.problem.getNumDepots());

        Depot depot1 = c1.getDepot(randomDepot);
        Depot depot2 = c2.getDepot(randomDepot);

        Vehicle v1 = depot1.getVehicle(random.nextInt(this.problem.getMaxVehicles()));
        Vehicle v2 = depot2.getVehicle(random.nextInt(this.problem.getMaxVehicles()));

        ArrayList<Customer> removeCustomers1 = (ArrayList<Customer>) SerializationUtils.clone(v1.getCustomers());
        ArrayList<Customer> removeCustomers2 = (ArrayList<Customer>) SerializationUtils.clone(v2.getCustomers());

        c1.removeCustomers(removeCustomers2);
        c2.removeCustomers(removeCustomers1);

        depot1.bestCostInsertions(removeCustomers2, this.problem);
        depot2.bestCostInsertions(removeCustomers1, this.problem);

    }

    public void mutation(Chromosome chromosome) {
        double mutationType = Math.random();

        if (mutationType < 0.4) {

            this.reverse(chromosome);

        } else if (mutationType < 0.8) {

            this.reverse(chromosome);

        } else {

            this.swap(chromosome);

        }
    }

    public void customerReroute(Chromosome chromosome) {
        int randomDepot = random.nextInt(this.problem.getNumDepots());

        Depot depot = chromosome.getDepot(randomDepot);

        depot.customerReroute(this.problem);

    }

    public void swap(Chromosome chromosome) {
        int depotNum = random.nextInt(this.problem.getNumDepots());

        Depot depot = chromosome.getDepot(depotNum);

        depot.swap();

    }

    public void reverse(Chromosome chromosome) {
        int depotNum = random.nextInt(this.problem.getNumDepots());

        Depot depot = chromosome.getDepot(depotNum);

        depot.reverse();
    }

    public void getFitness() {
        double totalFitness = 0.0;
        double counter = 0;
        for (Chromosome c : population) {
            totalFitness += c.getFitness(this.problem);
            counter++;
        }
        System.out.println("AVG: " + totalFitness/counter);
        System.out.println("BEST: " + this.parents.get(this.parents.size()-1).getFitness(this.problem));
    }

    public void bestFeasible() {
        this.parents.sort((c1, c2) -> Chromosome.compare(c1, c2, this.problem));

        for (int i = this.parents.size() - 1; i >= 0; i--) {
            Chromosome c = this.parents.get(i);

            if (c.isFeasible(this.problem)) {
                System.out.println("BEST FEASIBLE: " + c.getFitness(this.problem));

                if (c.getFitness(this.problem) < bestSolution.getFitness(this.problem)) {
                    bestSolution = c;
                }

                return;
            }
        }
    }

    public String createSolution() {
        if (bestSolution.getFitness(this.problem) > 15000) {
            bestSolution = this.parents.get(this.parents.size()-1);
        }

        StringBuilder solution = new StringBuilder(Math.round(bestSolution.getFitness(this.problem) * 100.0) / 100.0 + "\n");

        for (Depot depot : bestSolution.getDepots()) {
            int vehicleNum = 1;

            for (Vehicle vehicle : depot.getVehicles()) {
                if (vehicle.getCustomers().size() > 0) {
                    solution.append(depot.getId() + 1).append("\t");

                    solution.append(vehicleNum).append("\t");

                    solution.append(Math.round(vehicle.getRouteCost(this.problem) * 100.0) / 100.0).append("\t");

                    solution.append(vehicle.getCurrentLoad()).append("\t");

                    solution.append(0).append(" ");

                    for (Customer customer : vehicle.getCustomers()) {
                        solution.append(customer.getId() + 1).append(" ");
                    }

                    solution.append(0).append("\n");
                }
                vehicleNum++;
            }
        }
        return solution.toString();
    }
}
