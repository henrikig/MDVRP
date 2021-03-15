package Models;

import Utilities.Parameters;
import Utilities.Utils;
import org.javatuples.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;

public class MDVRP implements Serializable {

    private final Map<Integer, ArrayList<Double>> depots;
    private final Map<Integer, ArrayList<Double>> customers;
    private final ArrayList<ArrayList<Integer>> swappable;
    private boolean isCalculated;
    private final int numDepots;
    private final int numCustomers;
    private final int maxVehicles;
    private final double maxLoad;
    private final double maxLength;
    private final double[][] distances;
    private final Random random = new Random();

    public MDVRP(
            Map<Integer, ArrayList<Double>> depots,
            Map<Integer, ArrayList<Double>> customers,
            int numDepots,
            int numCustomers,
            int maxVehicles,
            double maxLoad,
            double maxLength) {
        this.depots = depots;
        this.customers = customers;
        this.numDepots = numDepots;
        this.numCustomers = numCustomers;
        this.maxVehicles = maxVehicles;
        this.maxLoad = maxLoad;
        this.maxLength = maxLength;
        this.distances = new double[numDepots + numCustomers][numDepots + numCustomers];
        this.swappable = new ArrayList<>();
        this.isCalculated = false;

        this.initDistances();
    }

    public Map<Integer, ArrayList<Double>> getDepots() {
        return depots;
    }

    public Map<Integer, ArrayList<Double>> getCustomers() {
        return customers;
    }

    public int getNumDepots() {
        return numDepots;
    }

    public int getNumCustomers() {
        return numCustomers;
    }

    public int getMaxVehicles() {
        return maxVehicles;
    }

    public double getMaxLoad() { return maxLoad; }

    public double getMaxLength() {
        return maxLength;
    }

    public double getC2CDistance(int c1, int c2) {
        return this.distances[c1 + numDepots][c2 + numDepots];
    }

    public double getD2CDistance(int d1, int c2) {
        return this.distances[d1][c2 + numDepots];
    }

    public ArrayList<Double> getCustomer(int customerId) {
        return this.customers.get(customerId);
    }

    public ArrayList<Double> getDepot(int depotId) {
        return this.customers.get(depotId);
    }

    public boolean isCalculated() {
        return isCalculated;
    }

    public void setCalculated() {
        isCalculated = true;
    }

    private void initDistances() {
        for (int depot1 = 0; depot1 < this.numDepots; depot1++) {

            ArrayList<Double> depot1Loc = this.depots.get(depot1);
            double x1 = depot1Loc.get(0);
            double y1 = depot1Loc.get(1);

            for (int depot2 = 0; depot2 <= depot1; depot2++) {

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

            for (int customer = 0; customer < numCustomers; customer++) {
                ArrayList<Double> customerLoc = this.customers.get(customer);
                double x2 = customerLoc.get(0);
                double y2 = customerLoc.get(1);

                double dist = Utils.EuclideanDist(x1, y1, x2, y2);
                this.distances[depot1][customer + numDepots] = dist;
                this.distances[customer + numDepots][depot1] = dist;
            }
        }

        for (int customer1 = 0; customer1 < numCustomers; customer1++) {

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

    public int getClosestDepot(int customerId) {
        double minDistance = Double.POSITIVE_INFINITY;
        int closestDepot = 0;


        for (int depotId = 0; depotId < this.numDepots; depotId++ ) {

            double currentDistance = getD2CDistance(depotId, customerId);

            if (currentDistance < minDistance) {
                minDistance = currentDistance;
                closestDepot = depotId;
            }
        }

        return closestDepot;
    }

    public void getSecondClosestDepot(int customerId, int closestDepot) {
        double minDistance = Double.POSITIVE_INFINITY;
        int secondClosest = -1;

        double closestDistance = getD2CDistance(closestDepot, customerId);

        for (int depotId = 0; depotId < this.numDepots; depotId++ ) {
            if (depotId == closestDepot) {
                continue;
            }

            double currentDistance = getD2CDistance(depotId, customerId);

            double swapCriteria = (currentDistance - closestDistance) / closestDistance;

            if (currentDistance < minDistance && swapCriteria <= Parameters.SWAP_BOUND) {
                minDistance = currentDistance;
                secondClosest = depotId;
            }
        }

        if (secondClosest >= 0) {
            swappable.add(new ArrayList<>(Arrays.asList(customerId, closestDepot, secondClosest)));
        }
    }

    public ArrayList<Integer> getRandomSwappable() {
        return this.swappable.get(random.nextInt(this.swappable.size()));
    }
}
