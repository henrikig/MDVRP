package Models;

import java.util.ArrayList;

public class Vehicle {

    private final double maxLoad;
    private double currentLoad;
    private ArrayList<Customer> customers;

    public Vehicle(double maxLoad) {
        this.maxLoad = maxLoad;
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

    public boolean insertCustomer(Customer customer) {
        double demand = customer.getDemand();

        if (this.currentLoad + demand < this.maxLoad) {

            this.currentLoad = this.currentLoad + demand;
            this.customers.add(customer);

            return true;

        }

        return false;
    }
}
