package Models;

public class Customer {

    private final int id;
    private final double demand;
    private int depotId;
    private double depotDistance;

    public Customer(int id, double demand, int depotId, double depotDistance) {
        this.id = id;
        this.demand = demand;
        this.depotId = depotId;
        this.depotDistance = depotDistance;
    }

    public int getId() {
        return id;
    }

    public double getDemand() {
        return demand;
    }

    public int getDepotId() {
        return depotId;
    }

    public double getDepotDistance() {
        return depotDistance;
    }
}
