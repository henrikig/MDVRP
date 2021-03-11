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
    private final MDVRP problem;
    private final Random random = new Random();

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

    public Vehicle getVehicle(int i) {
        return this.vehicles.get(i);
    }

    public ArrayList<Vehicle> getVehicles() {
        return this.vehicles;
    }

    public void flattenCustomers() {
        ArrayList<ArrayList<Customer>> currentCustomers = new ArrayList<>();

        for (Vehicle vehicle : this.getVehicles()) {
            currentCustomers.add(vehicle.getCustomers());
        }
        this.customers.clear();
        currentCustomers.forEach(this.customers::addAll);
    }

    public boolean removeCustomer(Customer c) {
        for (Vehicle vehicle : this.vehicles) {
            if (vehicle.removeCustomer(c)) {
                return true;
            }
        }
        return false;
    }

    public void bestCostInsertions(ArrayList<Customer> customers) {
        for (Customer c : customers) {
            Triplet<Integer, Double, Boolean> bestCost = this.bestInsertCustomer(0, c);
            Triplet<Integer, Double, Boolean> bestFeasible = Triplet.with(-1, Double.POSITIVE_INFINITY, false);

            int bestVehicle = 0;
            int bestFeasibleVehicle = 0;
            for (int i = 1; i < this.vehicles.size(); i++) {
                Triplet<Integer, Double, Boolean> currCost = this.bestInsertCustomer(i, c);
                if (currCost.getValue1() < bestCost.getValue1() && currCost.getValue2()) {
                    bestCost = currCost;
                    bestVehicle = i;
                } else if (currCost.getValue1() < bestFeasible.getValue1() && currCost.getValue2()) {
                    bestFeasible = currCost;
                    bestFeasibleVehicle = i;
                }
            }
            if (bestFeasible.getValue0() >= 0) {
                this.vehicles.get(bestFeasibleVehicle).insertCustomerByIndex(bestFeasible.getValue0(), c);

            } else {
                this.vehicles.get(bestVehicle).insertCustomerByIndex(bestCost.getValue0(), c);
                boolean isFeasible = false;
            }


        }
    }

    public void bestCostInsertions2(ArrayList<Customer> customers) {
        for (Customer c : customers) {
            ArrayList<Triplet<Integer, Integer, Double>> feasibleInsertion = new ArrayList<>();
            ArrayList<Triplet<Integer, Integer, Double>> allInsertions = new ArrayList<>();

            for (int i = 0; i < this.vehicles.size(); i++) {
                Triplet<Integer, Double, Boolean> currCost = this.bestInsertCustomer2(i, c);
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
                    this.flattenCustomers();
                    this.scheduleRoutes();
                    System.out.println(this.isFeasible());
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

    public Triplet<Integer, Double, Boolean> bestInsertCustomer(int i, Customer c) {

        return this.vehicles.get(i).bestInsertion(c);

    }

    public Triplet<Integer, Double, Boolean> bestInsertCustomer2(int i, Customer c) {

        return this.vehicles.get(i).bestInsertion2(c);

    }

    public boolean isFeasible() {
        for (Vehicle vehicle : this.vehicles) {
            if (!vehicle.getFeasibility()) {
                return false;
            }
        }
        return true;
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
