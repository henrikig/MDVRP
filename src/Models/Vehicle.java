package Models;

import java.util.ArrayList;

public class Vehicle {

    private final double maxLoad;
    private double currentLoad;
    private ArrayList<Customer> customers;
    private MDVRP problem;
    private Depot depot;
    private double routeCost;
    private boolean updated;

    public Vehicle(double maxLoad, MDVRP problem, Depot depot) {
        this.maxLoad = maxLoad;
        this.problem = problem;
        this.depot = depot;
        this.updated = true;
        this.customers = new ArrayList<>();
    }

    public double getMaxLoad() {
        return maxLoad;
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

    public boolean insertCustomer(Customer customer) {
        double demand = customer.getDemand();

        if (this.currentLoad + demand < this.maxLoad) {

            this.currentLoad += demand;
            this.customers.add(customer);

            return true;
        }
        return false;
    }

    public void forceInsertCustomer(Customer customer) {
        this.currentLoad += customer.getDemand();
        this.customers.add(customer);
    }

    public void insertCustomerByIndex(int i, Customer customer) {
        this.currentLoad += customer.getDemand();
        this.customers.add(i, customer);
    }

    public void insertFirstCustomer(Customer customer) {
        this.currentLoad += customer.getDemand();
        this.customers.add(0, customer);
    }

    public void removeCustomerByIndex(int i) {
        Customer c = this.customers.remove(i);
        this.currentLoad -= c.getDemand();
    }

    public void removeLastCustomer() {
        Customer c = this.customers.remove(this.customers.size() - 1);
        this.currentLoad -= c.getDemand();
    }

    public boolean testDemandIncrement(double demand) {
        return this.currentLoad + demand <= this.maxLoad;
    }

    public void clearRoute() {
        this.customers.clear();
        this.setUpdated();
    }

    public double getRouteCost() {
        if (this.updated) {
            updateRouteCost();
            this.updated = false;
        }
        return this.routeCost;
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
