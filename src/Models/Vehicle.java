package Models;

import Utilities.Parameters;
import org.javatuples.Triplet;

import java.io.Serializable;
import java.util.ArrayList;

public class Vehicle implements Serializable {

    private final double maxLoad;
    private double currentLoad;
    private ArrayList<Customer> customers;
    private final MDVRP problem;
    private final Depot depot;
    private double routeCost;
    private boolean updated;

    public Vehicle(double maxLoad, MDVRP problem, Depot depot) {
        this.maxLoad = maxLoad;
        this.problem = problem;
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

    public Triplet<Integer, Double, Boolean> bestInsertion(Customer c) {
        Boolean feasible = this.testDemandIncrement(c.getDemand());
        if (this.customers.size() == 0) {
            double routeCost = 2 * this.problem.getD2CDistance(this.depot.getId(), c.getId());

            return Triplet.with(0, routeCost, feasible);

        } else {
            int bestIndex = 0;
            double bestDeltaCost = this.problem.getD2CDistance(this.depot.getId(), c.getId());
            bestDeltaCost += this.problem.getC2CDistance(c.getId(), this.customers.get(0).getId());
            bestDeltaCost -= this.problem.getD2CDistance(this.depot.getId(), this.customers.get(0).getId());

            if (this.customers.size() > 1) {
                for (int i = 1; i < this.customers.size(); i++) {
                    double currDeltaCost = this.problem.getC2CDistance(this.customers.get(i - 1).getId(), c.getId());
                    currDeltaCost += this.problem.getC2CDistance(c.getId(), this.customers.get(i).getId());
                    currDeltaCost -= this.problem.getC2CDistance(this.customers.get(i - 1).getId(), this.customers.get(i).getId());

                    if (currDeltaCost < bestDeltaCost) {
                        bestDeltaCost = currDeltaCost;
                        bestIndex = i;
                    }
                }
                double lastDeltaCost = this.problem.getC2CDistance(this.customers.get(this.customers.size() - 1).getId(), c.getId());
                lastDeltaCost += this.problem.getD2CDistance(this.depot.getId(), c.getId());
                lastDeltaCost -= this.problem.getD2CDistance(this.depot.getId(), this.customers.get(this.customers.size() - 1).getId());

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

    private void insertCustomer(Customer customer) {
        this.insertCustomer(this.customers.size(), customer);
    }

    private void insertCustomer(int i, Customer customer) {
        for (Customer cc : this.customers) {
            if (cc.getId() == customer.getId()) {
                System.out.println("HELLO");
            }
        }
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

    public void clearRoute() {
        this.customers.clear();
        this.currentLoad = 0.0;
        this.setUpdated();
    }

    public double getFitness() {
        return this.getRouteCost() + this.getPenalty();
    }

    public double getRouteCost() {
        if (this.updated) {
            updateRouteCost();
            this.updated = false;
        }
        return this.routeCost;
    }

    public double getPenalty() {
        return Math.max(Parameters.PENALTY_DEMAND * (this.currentLoad - this.maxLoad), 0);
    }

    private void updateRouteCost() {
        this.routeCost = 0;

        if (customers.size() == 0) {
            return;
        }

        double depotDistance = this.problem.getD2CDistance(this.depot.getId(), this.customers.get(0).getId());

        if (customers.size() == 1) {
            this.routeCost += 2 * depotDistance;
            return;
        } else {
            this.routeCost += depotDistance;
        }

        for (int i = 0; i < customers.size() - 1; i++) {
            this.routeCost += this.problem.getC2CDistance(this.customers.get(i).getId(), this.customers.get(i + 1).getId());
        }

        this.routeCost += this.problem.getD2CDistance(this.depot.getId(), this.customers.get(this.customers.size() - 1).getId());
    }

    public void setUpdated() {
        this.updated = true;
    }
}
