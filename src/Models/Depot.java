package Models;

import java.util.ArrayList;

public class Depot {

    private final int id;
    private ArrayList<Customer> customers;
    private ArrayList<Vehicle> vehicles;
    private final double maxLoad;
    private final int maxVehicles;
    private final MDVRP problem;

    public Depot(int id, ArrayList<Customer> customers, double maxLoad, int maxVehicles, MDVRP problem) {
        this.id = id;
        this.customers = customers;
        this.vehicles = new ArrayList<>();
        this.maxLoad = maxLoad;
        this.maxVehicles = maxVehicles;
        this.problem = problem;

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
            this.vehicles.add(new Vehicle(this.maxLoad, this.problem, this));
        }
    }

    public void scheduleRoutes() {
        for (Vehicle vehicle : vehicles) {
            vehicle.clearRoute();
        }

        int vehicleNum = 0;

        for (Customer customer : customers) {

            Vehicle currentVehicle = vehicles.get(vehicleNum);

            if (!currentVehicle.insertCustomer(customer)) {
                if (!(vehicleNum >= vehicles.size() - 1)) {
                    vehicleNum++;
                }

                currentVehicle = vehicles.get(vehicleNum);
                currentVehicle.forceInsertCustomer(customer);
            }
        }

        for (int i = 0; i < this.vehicles.size(); i++) {
            double deltaCost = 0.0;

            Customer lastCustomer = this.vehicles.get(i).getLastCustomer();
            
        }
    }
}
