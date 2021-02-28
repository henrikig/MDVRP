package Models;

import java.util.ArrayList;

public class Depot {

    private final int id;
    private ArrayList<Customer> customers;
    private ArrayList<Vehicle> vehicles;
    private final double maxLoad;
    private final int maxVehicles;

    public Depot(int id, ArrayList<Customer> customers, double maxLoad, int maxVehicles) {
        this.id = id;
        this.customers = customers;
        this.maxLoad = maxLoad;
        this.maxVehicles = maxVehicles;

        initVehicles();
    }

    public int getId() {
        return id;
    }

    public ArrayList<Customer> getCustomers() {
        return customers;
    }

    public double getMaxLoad() {
        return maxLoad;
    }

    private void initVehicles() {
        for (int i = 0; i < maxVehicles; i++) {
            this.vehicles.add(new Vehicle(this.maxLoad));
        }
    }

    public void scheduleRoutes() {
        int vehicleCount = 0;

        for (Customer customer : customers) {

            Vehicle currentVehicle = vehicles.get(vehicleCount);

            if (!currentVehicle.insertCustomer(customer)) {
                vehicleCount++;

                currentVehicle = vehicles.get(vehicleCount);
                currentVehicle.insertCustomer(customer);
            }
        }
    }
}
