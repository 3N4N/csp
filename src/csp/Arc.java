package csp;

public class Arc {
    private Cell c1;
    private Cell c2;

    Arc(Cell c1, Cell c2) {
        this.c1 = c1;
        this.c2 = c2;
    }

    public Cell getC1() {
        return c1;
    }

    public void setC1(Cell c1) {
        this.c1 = c1;
    }

    public Cell getC2() {
        return c2;
    }

    public void setC2(Cell c2) {
        this.c2 = c2;
    }
}
