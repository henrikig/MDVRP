package Models;

import org.javatuples.Pair;

import java.io.Serializable;
import java.util.*;

public class Chromosome implements Serializable {

    private ArrayList<Depot> depots = new ArrayList<>();
    private final int numDepots;
    private final int numCustomers;
    private final double maxLoad;
    private final int maxVehicles;

    public Chromosome(MDVRP problem) {
        this.numDepots = problem.getNumDepots();
        this.numCustomers = problem.getNumCustomers();
        this.maxLoad = problem.getMaxLoad();
        this.maxVehicles = problem.getMaxVehicles();
        this.clusterCustomers(problem);
    }

    public Depot getDepot(int i) {
        return this.depots.get(i);
    }

    public Depot getDepotById(int id) {
        for (Depot depot : depots) {
            if (depot.getId() == id) {
                return depot;
            }
        }
        return null;
    }

    public ArrayList<Depot> getDepots() {
        return depots;
    }

    public void removeCustomers(ArrayList<Customer> customers) {
        for (Customer c : customers) {
            for (Depot depot : this.depots) {
                if(depot.removeCustomer(c)) {
                    break;
                }
            }
        }
    }

    private void clusterCustomers(MDVRP problem) {
        Map<Integer, ArrayList<Customer>> customers = new HashMap<>();

        for (int customerId = 0; customerId < this.numCustomers; customerId++) {

            ArrayList<Double> customer = problem.getCustomer(customerId);
            double customerDemand = customer.get(2);
            int closestDepot = problem.getClosestDepot(customerId);

            if (!problem.isCalculated()) {
                problem.getSecondClosestDepot(customerId, closestDepot);
            }

            Customer c = new Customer(customerId, customerDemand);

            if (customers.containsKey(closestDepot)) {

                customers.get(closestDepot).add(c);

            } else {

                ArrayList<Customer> newCustomerList = new ArrayList<>();

                newCustomerList.add(c);
                customers.put(closestDepot, newCustomerList);

            }
        }

        problem.setCalculated();

        for (int depotId = 0; depotId < this.numDepots; depotId++) {
            ArrayList<Customer> depotCustomers = customers.get(depotId);
            Collections.shuffle(depotCustomers);

            Depot depot = new Depot(depotId, customers.get(depotId), this.maxLoad, this.maxVehicles, problem);
            depots.add(depot);
        }
    }

    public void scheduleRoutes(MDVRP problem) {
        for (Depot depot : depots) {
            depot.scheduleRoutes(problem);
        }
    }

    public void interDepot(MDVRP problem) {
        ArrayList<Integer> swappable = problem.getRandomSwappable();

        Depot depot1 = this.getDepotById(swappable.get(1));
        Depot depot2 = this.getDepotById(swappable.get(2));

        int customerId = swappable.get(0);

        Customer customer = depot1.getCustomerById(customerId);

        if (customer != null) {
            depot1.removeCustomer(customer);
            depot2.bestCostInsertions(new ArrayList<>(Collections.singletonList(customer)), problem);
        } else {
            customer = depot2.getCustomerById(customerId);
            depot2.removeCustomer(customer);
            depot1.bestCostInsertions(new ArrayList<>(Collections.singletonList(customer)), problem);
        }

    }

    public void checkNumCustomers() {
        int custCount = depots.stream().mapToInt(Depot::getNumCustomers).sum();
        if (custCount != this.numCustomers) {
            System.out.println("HELLO");
        }
    }

    public double getFitness(MDVRP problem) {

        return depots.stream().mapToDouble(d -> d.getFitness(problem)).sum();
    }

    public boolean isFeasible(MDVRP problem) {
        for (Depot depot : this.depots) {
            if (!depot.isFeasible(problem)) {
                return false;
            }
        }
        return true;
    }

    public static int compare(Chromosome c1, Chromosome c2, MDVRP problem) {
        if (c1.getFitness(problem) == c2.getFitness(problem)) {
            return 0;
        } else if (c1.getFitness(problem) > c2.getFitness(problem)) {
            return -1;
        }
        return 1;
    }
}
