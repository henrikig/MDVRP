package Models;

import java.io.Serializable;
import java.util.ArrayList;

public class Depot implements Serializable {

    private final int id;
    private ArrayList<Customer> customers;
    private ArrayList<Vehicle> vehicles;
    private final double maxLoad;
    private final int maxVehicles;
    private final MDVRP problem;
    public String testField = "HELLO";

    public Depot(int id, ArrayList<Customer> customers, double maxLoad, int maxVehicles, MDVRP problem) {
        this.id = id;
        this.customers = customers;
        this.vehicles = new ArrayList<>();
        this.maxLoad = maxLoad;
        this.maxVehicles = maxVehicles;
        this.problem = problem;

        initVehicles();
    }

    private void initVehicles() {
        for (int i = 0; i < maxVehicles; i++) {
            this.vehicles.add(new Vehicle(this.maxLoad, this.problem, this));
        }
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

    public double getRouteCosts() {
        return vehicles.stream().mapToDouble(Vehicle::getRouteCost).sum();
    }

    public void scheduleRoutes() {
        for (Vehicle vehicle : vehicles) {
            vehicle.clearRoute();
        }

        int vehicleNum = 0;

        for (Customer customer : customers) {

            Vehicle currentVehicle = vehicles.get(vehicleNum);

            if (!currentVehicle.insertCustomerIfFeasible(customer)) {
                if (!(vehicleNum >= vehicles.size() - 1)) {
                    vehicleNum++;
                }

                currentVehicle = vehicles.get(vehicleNum);
                currentVehicle.forceInsertCustomer(customer);
            }
        }

        for (int i = 0; i < this.vehicles.size() - 1; i++) {
            Vehicle currentVehicle = this.vehicles.get(i);

            if (currentVehicle.getNumCustomers() < 1) {
                continue;
            }

            Vehicle nextVehicle = this.vehicles.get(i + 1);

            Customer lastCustomer = currentVehicle.getLastCustomer();

            double lastDemand = lastCustomer.getDemand();

            if (nextVehicle.testDemandIncrement(lastDemand) && nextVehicle.getNumCustomers() > 0) {
                double deltaCost = 0.0;
                int secondLastCustomerId = currentVehicle.getSecondLastCustomer().getId();
                int lastCustomerId = lastCustomer.getId();
                int firstCustomerId = nextVehicle.getFirstCustomer().getId();

                // Remove second last to last customer
                deltaCost -= this.problem.getC2CDistance(secondLastCustomerId, lastCustomerId);
                // Add second last to depot
                deltaCost += this.problem.getD2CDistance(this.getId(), secondLastCustomerId);
                // Remove depot to first customer
                deltaCost -= this.problem.getD2CDistance(this.getId(), firstCustomerId);
                // Add last customer from route i to first customer route i+1
                deltaCost += this.problem.getC2CDistance(lastCustomerId, firstCustomerId);

                if (deltaCost < 0) {
                    currentVehicle.removeLastCustomer();
                    nextVehicle.insertFirstCustomer(lastCustomer);
                }
            }
        }
    }
}
