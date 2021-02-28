package Models;

import Utilities.Utils;

import java.util.ArrayList;
import java.util.Map;

public class MDVRP {

    private Map<Integer, ArrayList<Double>> depots;
    private Map<Integer, ArrayList<Double>> customers;
    private int numDepots;
    private int numCustomers;
    private int maxVehicles;
    private double[][] distances;

    public MDVRP(
            Map<Integer, ArrayList<Double>> depots,
            Map<Integer, ArrayList<Double>> customers,
            int numDepots,
            int numCustomers,
            int maxVehicles) {
        this.depots = depots;
        this.customers = customers;
        this.numDepots = numDepots;
        this.numCustomers = numCustomers;
        this.maxVehicles = maxVehicles;
        this.distances = new double[numDepots + numCustomers][numDepots + numCustomers];

        this.initDistances();
    }

    public Map<Integer, ArrayList<Double>> getDepots() {
        return depots;
    }

    public void setDepots(Map<Integer, ArrayList<Double>> depots) {
        this.depots = depots;
    }

    public Map<Integer, ArrayList<Double>> getCustomers() {
        return customers;
    }

    public void setCustomers(Map<Integer, ArrayList<Double>> customers) {
        this.customers = customers;
    }

    public int getNumDepots() {
        return numDepots;
    }

    public void setNumDepots(int numDepots) {
        this.numDepots = numDepots;
    }

    public int getNumCustomers() {
        return numCustomers;
    }

    public void setNumCustomers(int numCustomers) {
        this.numCustomers = numCustomers;
    }

    public int getMaxVehicles() {
        return maxVehicles;
    }

    public void setMaxVehicles(int maxVehicles) {
        this.maxVehicles = maxVehicles;
    }

    public double[][] getDistances() {
        return distances;
    }

    public void setDistances(double[][] distances) {
        this.distances = distances;
    }

    public double getC2CDistance(Customer c1, Customer c2) {
        int c1Id = c1.getId();
        int c2Id = c2.getId();

        return this.distances[c1Id + numDepots][c2Id + numDepots];
    }

    public double getD2CDistance(Depot d1, Customer c2) {
        int d1Id = d1.getId();
        int c2Id = c2.getId();

        return this.distances[d1Id][c2Id + numDepots];
    }

    private void initDistances() {
        for(int depot1 = 0; depot1 < this.numDepots; depot1++) {

            ArrayList<Double> depot1Loc = this.depots.get(depot1);
            double x1 = depot1Loc.get(0);
            double y1 = depot1Loc.get(1);

            for(int depot2 = 0; depot2 <= depot1; depot2++) {

                if (depot1 == depot2) {

                    this.distances[depot1][depot2] = 0;

                } else {

                    ArrayList<Double> depot2Loc = this.depots.get(depot2);
                    double x2 = depot2Loc.get(0);
                    double y2 = depot2Loc.get(1);

                    double dist = Utils.EuclideanDist(x1, y1, x2, y2);
                    this.distances[depot1][depot2] = dist;
                    this.distances[depot2][depot1] = dist;

                }
            }

            for(int customer = 0; customer < numCustomers; customer++) {
                ArrayList<Double> customerLoc = this.customers.get(customer);
                double x2 = customerLoc.get(0);
                double y2 = customerLoc.get(1);

                double dist = Utils.EuclideanDist(x1, y1, x2, y2);
                this.distances[depot1][customer + numDepots] = dist;
                this.distances[customer + numDepots][depot1] = dist;
            }
        }

        for(int customer1 = 0; customer1 < numCustomers; customer1++) {

            ArrayList<Double> customer1Loc = this.customers.get(customer1);
            double x1 = customer1Loc.get(0);
            double y1 = customer1Loc.get(1);

            for(int customer2 = 0; customer2 <= customer1; customer2++) {

                ArrayList<Double> customer2Loc = this.customers.get(customer2);
                double x2 = customer2Loc.get(0);
                double y2 = customer2Loc.get(1);

                double dist = Utils.EuclideanDist(x1, y1, x2, y2);
                this.distances[customer1 + numDepots][customer2 + numDepots] = dist;
                this.distances[customer2 + numDepots][customer1 + numDepots] = dist;
            }
        }
    }
}
