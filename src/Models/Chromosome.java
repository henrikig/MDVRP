package Models;

import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Chromosome {

    private MDVRP problem;
    private ArrayList<Depot> depots = new ArrayList<>();
    private final int numDepots;
    private final int numCustomers;
    private final double maxLoad;
    private final int maxVehicles;
    private double fitness;

    public Chromosome(MDVRP problem) {
        this.problem = problem;
        this.numDepots = problem.getNumDepots();
        this.numCustomers = problem.getNumCustomers();
        this.maxLoad = problem.getMaxLoad();
        this.maxVehicles = problem.getMaxVehicles();
        this.clusterCustomers();

    }

    private void clusterCustomers() {
        Map<Integer, ArrayList<Customer>> customers = new HashMap<>();

        for (int customerId = 0; customerId < this.numCustomers; customerId++) {

            ArrayList<Double> customer = this.problem.getCustomer(customerId);
            double customerDemand = customer.get(2);
            Pair<Integer, Double> closestDepot = this.problem.getClosestDepot(customerId);

            int closestId = closestDepot.getValue0();
            double closestDistance = closestDepot.getValue1();

            Customer c = new Customer(customerId, customerDemand, closestId, closestDistance);

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

            Depot depot = new Depot(depotId, customers.get(depotId), this.maxLoad, this.maxVehicles);
            depots.add(depot);
        }
    }

    public void scheduleRoutes() {
        for (Depot depot : depots) {
            depot.scheduleRoutes();
        }
    }
}
