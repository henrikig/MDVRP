package Models;

import org.javatuples.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
            Pair<Integer, Double> closestDepot = problem.getClosestDepot(customerId);

            int closestId = closestDepot.getValue0();

            Customer c = new Customer(customerId, customerDemand);

            if (customers.containsKey(closestId)) {

                customers.get(closestId).add(c);

            } else {

                ArrayList<Customer> newCustomerList = new ArrayList<>();

                newCustomerList.add(c);
                customers.put(closestId, newCustomerList);

            }
        }

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

    public double getFitness(MDVRP problem) {
        return depots.stream().mapToDouble(d -> d.getRouteCosts(problem)).sum();
    }

    public boolean isFeasible() {
        for (Depot depot : this.depots) {
            if (!depot.isFeasible()) {
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
