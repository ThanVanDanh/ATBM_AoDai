package model;

public abstract class AId {
    protected int id;

    public AId(int id) {
        super();
        this.id = id;
    }

    public AId() {
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
}
