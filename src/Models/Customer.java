package Models;

import java.io.Serializable;

public class Customer implements Serializable {

    private final int id;
    private final double demand;

    public Customer(int id, double demand) {
        this.id = id;
        this.demand = demand;
    }

    public int getId() {
        return id;
    }

    public double getDemand() {
        return demand;
    }

}
