package Models;

import Utilities.Parameters;
import org.javatuples.Triplet;

import java.io.Serializable;
import java.util.*;


public class Depot implements Serializable {

    private final int id;
    private ArrayList<Customer> customers;
    private ArrayList<Vehicle> vehicles;
    private final double maxLoad;
    private final int maxVehicles;
    private final Random random = new Random();
    private boolean updated;
    private double fitness;

    public Depot(int id, ArrayList<Customer> customers, double maxLoad, int maxVehicles, MDVRP problem) {
        this.id = id;
        this.customers = customers;
        this.vehicles = new ArrayList<>();
        this.maxLoad = maxLoad;
        this.maxVehicles = maxVehicles;
        this.updated = true;

        initVehicles(problem);
    }

    private void initVehicles(MDVRP problem) {
        for (int i = 0; i < maxVehicles; i++) {
            this.vehicles.add(new Vehicle(this.maxLoad, this));
        }
    }

    public int getId() {
        return id;
    }

    public double getRouteCosts(MDVRP problem) {
        if (this.updated) {
            this.fitness = vehicles.stream().mapToDouble(v -> v.getFitness(problem)).sum();
            this.updated = false;
        }
        return this.fitness;
    }

    public void setUpdated() {
        this.updated = true;
    }

    public Vehicle getVehicle(int i) {
        return this.vehicles.get(i);
    }

    public ArrayList<Vehicle> getVehicles() {
        return this.vehicles;
    }

    public boolean removeCustomer(Customer c) {
        for (Vehicle vehicle : this.vehicles) {
            if (vehicle.removeCustomer(c)) {
                return true;
            }
        }
        return false;
    }

    public void bestCostInsertions(ArrayList<Customer> customers, MDVRP problem) {
        for (Customer c : customers) {
            ArrayList<Triplet<Integer, Integer, Double>> feasibleInsertion = new ArrayList<>();
            ArrayList<Triplet<Integer, Integer, Double>> allInsertions = new ArrayList<>();

            for (int i = 0; i < this.vehicles.size(); i++) {
                Triplet<Integer, Double, Boolean> currCost = this.bestInsertCustomer(i, c, problem);
                if (currCost.getValue2()) {
                    feasibleInsertion.add(Triplet.with(currCost.getValue0(), i, currCost.getValue1()));
                }
                allInsertions.add(Triplet.with(currCost.getValue0(), i, currCost.getValue1()));
            }

            Triplet<Integer, Integer, Double> insertion;
            if (Math.random() <= Parameters.INSERT_BEST) {
                if (feasibleInsertion.size() == 0) {
                    allInsertions.sort(Comparator.comparing(Triplet::getValue2));
                    insertion = allInsertions.get(0);
                    this.vehicles.get(insertion.getValue1()).insertCustomerByIndex(insertion.getValue0(), c);
                } else {
                    feasibleInsertion.sort(Comparator.comparing(Triplet::getValue2));
                    insertion = feasibleInsertion.get(0);
                    this.vehicles.get(insertion.getValue1()).insertCustomerByIndex(insertion.getValue0(), c);
                }

            } else {
                insertion = allInsertions.get(random.nextInt(allInsertions.size()));
                this.vehicles.get(insertion.getValue1()).insertCustomerByIndex(insertion.getValue0(), c);
            }
        }
    }

    public Triplet<Integer, Double, Boolean> bestInsertCustomer(int i, Customer c, MDVRP problem) {

        return this.vehicles.get(i).bestInsertion(c, problem);

    }

    public void customerReroute(MDVRP problem) {
        Vehicle vehicle = this.vehicles.get(random.nextInt(this.vehicles.size()));

        if (vehicle.getNumCustomers() == 0) {
            return;
        }

        Customer c = vehicle.getCustomer(random.nextInt(vehicle.getNumCustomers()));

        vehicle.removeCustomer(c);

        int bestIndex = -1;
        int bestVehicle = -1;
        double bestCost = Double.POSITIVE_INFINITY;

        for (int i = 0; i < this.vehicles.size(); i++) {
            Triplet<Integer, Double, Boolean> currCost = this.bestInsertCustomer(i, c, problem);

            if (currCost.getValue2() && currCost.getValue1() < bestCost) {
                bestIndex = currCost.getValue0();
                bestVehicle = i;
                bestCost = currCost.getValue1();
            }
        }

        if (bestVehicle != -1) {
            this.vehicles.get(bestVehicle).insertCustomerByIndex(bestIndex, c);
        }
    }

    public void swap() {
        Vehicle v1 = this.vehicles.get(random.nextInt(this.vehicles.size()));
        Vehicle v2 = this.vehicles.get(random.nextInt(this.vehicles.size()));

        if (v1.getNumCustomers() > 0 && v2.getNumCustomers() > 0) {
            Customer c1 = v1.getCustomer(random.nextInt(v1.getNumCustomers()));
            Customer c2 = v2.getCustomer(random.nextInt(v2.getNumCustomers()));

            v1.setCustomer(c2, c1);
            v2.setCustomer(c1, c2);
        }

    }

    public void reverse() {
        Vehicle vehicle = this.vehicles.get(random.nextInt(this.vehicles.size()));

        if (vehicle.getNumCustomers() > 0) {
            vehicle.reverse();
        }
    }

    public boolean isFeasible() {
        for (Vehicle vehicle : this.vehicles) {
            if (!vehicle.getFeasibility()) {
                return false;
            }
        }
        return true;
    }

    public void scheduleRoutes(MDVRP problem) {
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
                deltaCost -= problem.getC2CDistance(secondLastCustomerId, lastCustomerId);
                // Add second last to depot
                deltaCost += problem.getD2CDistance(this.getId(), secondLastCustomerId);
                // Remove depot to first customer
                deltaCost -= problem.getD2CDistance(this.getId(), firstCustomerId);
                // Add last customer from route i to first customer route i+1
                deltaCost += problem.getC2CDistance(lastCustomerId, firstCustomerId);

                if (deltaCost < 0) {
                    currentVehicle.removeLastCustomer();
                    nextVehicle.insertFirstCustomer(lastCustomer);
                }
            }
        }
    }
}
