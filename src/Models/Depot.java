package Models;

public class Depot {

    private int id;
    private double depotX;
    private double depotY;

    public Depot(int id, double depotX, double depotY) {
        this.id = id;
        this.depotX = depotX;
        this.depotY = depotY;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
