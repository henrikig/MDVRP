package Models;

import Utilities.Parameters;
import org.javatuples.Triplet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class Vehicle implements Serializable {

    private final double maxLoad;
    private double currentLoad;
    private ArrayList<Customer> customers;
    private final Depot depot;
    private final Random random = new Random();
    private double routeCost;
    private boolean updated;

    public Vehicle(double maxLoad, Depot depot) {
        this.maxLoad = maxLoad;
        this.depot = depot;
        this.updated = true;
        this.customers = new ArrayList<>();
        this.currentLoad = 0.0;
    }

    public double getCurrentLoad() {
        return currentLoad;
    }

    public ArrayList<Customer> getCustomers() {
        return customers;
    }

    public int getNumCustomers() {
        return this.customers.size();
    }

    public Customer getCustomer(int i) {
        return this.customers.get(i);
    }

    public Customer getFirstCustomer() {
        return this.customers.get(0);
    }

    public Customer getLastCustomer() {
        return this.customers.get(this.customers.size() - 1);
    }

    public Customer getSecondLastCustomer() {
        return this.customers.get(this.customers.size() - 2);
    }

    public boolean getFeasibility() {
        return this.currentLoad <= this.maxLoad;
    }

    public boolean insertCustomerIfFeasible(Customer customer) {
        double demand = customer.getDemand();

        if (this.currentLoad + demand < this.maxLoad) {

            this.insertCustomer(customer);

            return true;
        }
        return false;
    }

    public Triplet<Integer, Double, Boolean> bestInsertion(Customer c, MDVRP problem) {
        Boolean feasible = this.testDemandIncrement(c.getDemand());
        if (this.customers.size() == 0) {
            double routeCost = 2 * problem.getD2CDistance(this.depot.getId(), c.getId());

            return Triplet.with(0, routeCost, feasible);

        } else {
            int bestIndex = 0;
            double bestDeltaCost = problem.getD2CDistance(this.depot.getId(), c.getId());
            bestDeltaCost += problem.getC2CDistance(c.getId(), this.customers.get(0).getId());
            bestDeltaCost -= problem.getD2CDistance(this.depot.getId(), this.customers.get(0).getId());

            if (this.customers.size() > 1) {
                for (int i = 1; i < this.customers.size(); i++) {
                    double currDeltaCost = problem.getC2CDistance(this.customers.get(i - 1).getId(), c.getId());
                    currDeltaCost += problem.getC2CDistance(c.getId(), this.customers.get(i).getId());
                    currDeltaCost -= problem.getC2CDistance(this.customers.get(i - 1).getId(), this.customers.get(i).getId());

                    if (currDeltaCost < bestDeltaCost) {
                        bestDeltaCost = currDeltaCost;
                        bestIndex = i;
                    }
                }
                double lastDeltaCost = problem.getC2CDistance(this.customers.get(this.customers.size() - 1).getId(), c.getId());
                lastDeltaCost += problem.getD2CDistance(this.depot.getId(), c.getId());
                lastDeltaCost -= problem.getD2CDistance(this.depot.getId(), this.customers.get(this.customers.size() - 1).getId());

                if (lastDeltaCost < bestDeltaCost) {
                    bestDeltaCost = lastDeltaCost;
                    bestIndex = this.customers.size();
                }
            }
            return Triplet.with(bestIndex, bestDeltaCost, feasible);
        }
    }

    public void forceInsertCustomer(Customer customer) {
        this.insertCustomer(customer);
    }

    public void insertCustomerByIndex(int i, Customer customer) {
        this.insertCustomer(i, customer);
    }

    public void insertFirstCustomer(Customer customer) {
        this.insertCustomer(0, customer);
    }

    public void removeLastCustomer() {
        this.removeCustomer(this.customers.size() - 1);
    }

    public boolean removeCustomer(Customer c) {
        for (int i = 0; i < this.customers.size(); i++) {
            if (this.customers.get(i).getId() == c.getId()) {
                this.removeCustomer(i);
                return true;
            }
        }
        return false;
    }

    public void setCustomer(Customer newCustomer, Customer oldCustomer) {
        int index = this.customers.indexOf(oldCustomer);
        this.removeCustomer(oldCustomer);
        this.insertCustomer(index, newCustomer);
    }

    private void insertCustomer(Customer customer) {
        this.insertCustomer(this.customers.size(), customer);
    }

    private void insertCustomer(int i, Customer customer) {
        this.currentLoad += customer.getDemand();
        this.setUpdated();
        this.customers.add(i, customer);
    }

    private void removeCustomer(int i) {
        Customer c = this.customers.remove(i);
        this.currentLoad -= c.getDemand();
        this.setUpdated();
    }

    public boolean testDemandIncrement(double demand) {
        return this.currentLoad + demand <= this.maxLoad;
    }

    public void reverse() {
        int k1 = random.nextInt(getNumCustomers() + 1);
        int k2 = random.nextInt(getNumCustomers() + 1);

        int low = Math.min(k1, k2);
        int hi = Math.max(k1, k2);

        for (int i = low; i < low + (hi - low)/2; i++) {
            Customer c1 = this.customers.get(i);
            Customer c2 = this.customers.get(hi - i - 1);

            this.setCustomer(c1, c2);
            this.setCustomer(c2, c1);
        }
    }

    public void clearRoute() {
        this.customers.clear();
        this.currentLoad = 0.0;
        this.setUpdated();
    }

    public double getFitness(MDVRP problem) {
        return this.getRouteCost(problem) + this.getPenalty();
    }

    public double getRouteCost(MDVRP problem) {
        if (this.updated) {
            updateRouteCost(problem);
            this.updated = false;
        }
        return this.routeCost;
    }

    public double getPenalty() {
        return Math.max(Parameters.PENALTY_DEMAND * (this.currentLoad - this.maxLoad), 0);
    }

    private void updateRouteCost(MDVRP problem) {
        this.routeCost = 0;

        if (customers.size() == 0) {
            return;
        }

        double depotDistance = problem.getD2CDistance(this.depot.getId(), this.customers.get(0).getId());

        if (customers.size() == 1) {
            this.routeCost += 2 * depotDistance;
            return;
        } else {
            this.routeCost += depotDistance;
        }

        for (int i = 0; i < customers.size() - 1; i++) {
            this.routeCost += problem.getC2CDistance(this.customers.get(i).getId(), this.customers.get(i + 1).getId());
        }

        this.routeCost += problem.getD2CDistance(this.depot.getId(), this.customers.get(this.customers.size() - 1).getId());
    }

    public void setUpdated() {
        this.updated = true;
        this.depot.setUpdated();
    }
}
