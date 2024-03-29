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
    private final double maxLength;
    private final int maxVehicles;
    private final Random random = new Random();
    private boolean updated;
    private double fitness;

    public Depot(int id, ArrayList<Customer> customers, double maxLoad, int maxVehicles, MDVRP problem) {
        this.id = id;
        this.customers = customers;
        this.vehicles = new ArrayList<>();
        this.maxLoad = maxLoad;
        this.maxLength = problem.getMaxLength();
        this.maxVehicles = maxVehicles;
        this.updated = true;

        initVehicles(problem);
    }

    private void initVehicles(MDVRP problem) {
        for (int i = 0; i < maxVehicles; i++) {
            this.vehicles.add(new Vehicle(this.maxLoad, this.maxLength, this));
        }
    }

    public int getId() {
        return id;
    }

    public int getNumCustomers() {
        return this.vehicles.stream().mapToInt(Vehicle::getNumCustomers).sum();
    }

    public double getFitness(MDVRP problem) {
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

    public Customer getCustomerById(int id) {
        for (Vehicle vehicle : this.vehicles) {
            Customer customer = vehicle.getCustomerById(id);
            if (customer != null) {
                return customer;
            }
        }
        return null;
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

        int customerIndex = random.nextInt(vehicle.getNumCustomers());
        Customer c = vehicle.getCustomer(customerIndex);
        vehicle.removeCustomer(c);

        int bestVehicle = 0;
        Triplet<Integer, Double, Boolean> bestInsert = this.bestInsertCustomer(0, c, problem);

        for (int i = 1; i < this.vehicles.size(); i++) {
            // Triplet<index, costReduction, feasible>
            Triplet<Integer, Double, Boolean> currInsert = this.bestInsertCustomer(i, c, problem);

            if (currInsert.getValue2()) {
                if (bestInsert.getValue2() && currInsert.getValue1() < bestInsert.getValue1()) {
                    bestInsert = currInsert;
                    bestVehicle = i;
                } else if (!bestInsert.getValue2()) {
                    bestInsert = currInsert;
                    bestVehicle = i;
                }
            } else {
                if (!bestInsert.getValue2()) {
                    if (currInsert.getValue1() < bestInsert.getValue1()) {
                        bestInsert = currInsert;
                        bestVehicle = i;
                    }
                }
            }
        }

        this.vehicles.get(bestVehicle).insertCustomerByIndex(bestInsert.getValue0(), c);
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

    public boolean isFeasible(MDVRP problem) {
        for (Vehicle vehicle : this.vehicles) {
            if (!vehicle.getFeasibility(problem)) {
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

            if (!currentVehicle.insertCustomerIfFeasible(customer, problem)) {
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
                if (nextVehicle.testLengthIncrement(lastCustomer, 0, problem)) {
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
}
