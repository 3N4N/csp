package csp;

import java.util.*;

/**
 * Holds the state of a Latin Square.
 */
public class Square {
    /**
     * The size of the Latin Square.
     */
    int size;
    /**
     * The cells in a Latin Square.
     */
    Cell[][] cells;

    /**
     * Constructor.
     * <p>
     * Does NOT initialize cells.
     *
     * @see #init()
     */
    Square() {
        this.size = -1;
    }

    /**
     * Initializes the cells.
     */
    public void init() {
        String funcname = "initArr";
        if (cells != null) {
            CSP.error(funcname, "Square already set");
        }

        cells = new Cell[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                cells[i][j] = new Cell();
                cells[i][j].row = i;
                cells[i][j].col = j;
                cells[i][j].domain = new ArrayList<>(size);
            }
        }
    }

    /**
     * Updates the domains of the cells according to the current state.
     */
    public void update() {
        /*
         * Loop over all the cells.
         */
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {

                /*
                 * If this cell is unassigned, update its domain list.
                 * Else, clear its domain list except the current value.
                 */
                if (cells[i][j].val == 0) {
                    cells[i][j].domain.clear();
                    for (int k = 1; k <= size; k++) {
                        if (isValid(i, j, k))
                            cells[i][j].domain.add(k);
                    }
                } else {
                    cells[i][j].domain.clear();
                    cells[i][j].domain.add(cells[i][j].val);
                }

            }
        }
    }

    /**
     * Checks if a value is a valid assignment for a cell.
     * <p>
     * A value is valid if no other cell in the same row or col
     * already holds that value.
     *
     * @param row the row of the cell
     * @param col the col of the cell
     * @param num the val to be assigned
     * @return true if num is a valid assignment
     */
    public boolean isValid(int row, int col, int num) {
        for (int x = 0; x < size; x++) {
            /*
             * If a cell in either the same row or same col
             * holds the value num, the assignment is invalid.
             */
            if (cells[row][x].val == num) return false;
            if (cells[x][col].val == num) return false;
        }

        /*
         * If no in either the same row or same col
         * holds the value num, the assignment is valid.
         */
        return true;
    }

    /**
     * Checks if all the cells are assigned a value.
     * <p>
     * Does NOT check if the cells holds valid values.
     *
     * @return true if no unassigned cell
     * @see #isSolved()
     */
    public boolean allAssigned() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int val = cells[i][j].val;
                if (val == 0) return false;
            }
        }

        return true;
    }

    /**
     * Checks if the Latin Square is solved.
     * <p>
     * A Latin Square is solved when all its cells are assigned
     * and no cell holds a value the same as any other cell in its row and col.
     *
     * @return true if the square is solved
     */
    public boolean isSolved() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int val = cells[i][j].val;
                if (val == 0) {
                    return false;
                }
                for (int x = 0; x < size; x++) {
                    if (x != i && cells[x][j].val == val) {
                        return false;
                    }
                    if (x != j && cells[i][x].val == val) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Assigns a value to a cell and updates the domains of its neighbours.
     *
     * @param cell the cell
     * @param val  the value
     */
    public void assign(Cell cell, Integer val) {
        int row = cell.row;
        int col = cell.col;

        cell.val = val;
        cell.domain.clear();
        cell.domain.add(val);

        for (int x = 0; x < size; x++) {
            if (x != row) cells[x][col].domain.remove(val);
            if (x != col) cells[row][x].domain.remove(val);
        }
    }

    /**
     * Unassigns a cell and updates the domains its neighbours.
     *
     * @param cell the cell
     * @param val  the value
     */
    public void unassign(Cell cell, Integer val) {
        int row = cell.row;
        int col = cell.col;

        cell.val = 0;
        cell.domain.clear();
        for (int k = 1; k <= size; k++) {
            if (isValid(row, col, k))
                cell.domain.add(k);
        }

        for (int x = 0; x < size; x++) {
            if (isValid(x, col, val) && x != row
                    && !cells[x][col].domain.contains(val)) {
                cells[x][col].domain.add(val);
            }
            if (isValid(row, x, val) && x != col
                    && !cells[row][x].domain.contains(val)) {
                cells[row][x].domain.add(val);
            }
        }
    }


    /**
     * Checks if two cells are neighbours.
     * <p>
     * Two cells are neighbours if they reside
     * in the same row or the same col.
     * @param c1 one of the cells
     * @param c2 the other cell
     * @return true if c1 and c2 are neighbours
     */
    public boolean areNeighbours(Cell c1, Cell c2) {
        if (c1 == c2) return false;
        return (c1.row == c2.row || c1.col == c2.col);
    }

    /**
     * Checks the consistency of two neighbouring cells.
     * @param c1 one of the cells
     * @param c2 the other cell
     * @return true if c1 and c2 are consistent
     */
    public boolean revise(Cell c1, Cell c2) {
        boolean removed = false;

        ArrayList<Integer> templist = new ArrayList<>();
        for (Integer val : c1.domain) {
            if (c2.domain.size() == 1 && c2.domain.get(0).equals(val)) {
                // System.out.println("C1: " + c1.row + "," + c1.col + c1.domain);
                // System.out.println("C2: " + c2.row + "," + c2.col + c2.domain);
                // scn.nextLine();
                templist.add(val);
                removed = true;
            }
        }
        c1.domain.removeAll(templist);

        return removed;
    }

    public boolean forwardCheck(Cell cell, boolean lookahead) {
        // assign(cell, val);

        ArrayList<Arc> queue = new ArrayList<>();

        // System.out.println("Cell: " + cell.row + "," + cell.col + cell.domain);
        for (int x = 0; x < size; x++) {
            Cell ck = cells[x][cell.col];
            Cell cm = cells[cell.row][x];
            if (ck != cell && ck.val == 0) {
                queue.add(new Arc(ck, cell));
                // System.out.println("C1: " + ck.row + "," + ck.col + ck.domain);
            }
            if (cm != cell && cm.val == 0) {
                queue.add(new Arc(cm, cell));
                // System.out.println("C2: " + cm.row + "," + cm.col + cm.domain);
            }
        }

        boolean consistent = true;
        while (!queue.isEmpty() && consistent) {
            Arc arc = queue.remove(0);
            Cell c1 = arc.getC1();
            Cell c2 = arc.getC2();

            if (revise(c1, c2)) {
                if (lookahead) {
                    for (int x = 0; x < size; x++) {
                        Cell ci = cells[x][c1.col];
                        Cell cj = cells[c1.row][x];
                        if (ci != c1 && ci != c2 && ci.val == 0) {
                            queue.add(new Arc(ci, c1));
                        }
                        if (cj != c1 && cj != c2 && cj.val == 0) {
                            queue.add(new Arc(cj, c1));
                        }
                    }
                }
                if (c1.domain.size() == 0) {
                    System.out.println("HEre:");
                    consistent = false;
                }
            }
        }

        // unassign(cell, val);
        return consistent;
    }

    public boolean backtrack() {
        CSP.nodeVisited++;

        if (allAssigned()) return isSolved();

        // Cell cell = seqH();
        Cell cell = sdfH();
        Integer[] list = cell.domain.toArray(new Integer[0]);

        for (Integer val : list) {
            ArrayList<Integer> tmpDom = new ArrayList<>(cell.domain);
            cell.val = val;
            cell.domain.clear();
            cell.domain.add(val);
            if (forwardCheck(cell, true)) {
                // assign(cell, val);
                if (backtrack()) return true;
                // unassign(cell, val);
            }
            cell.val = 0;
            cell.domain.clear();
            cell.domain.addAll(tmpDom);
        }

        return false;
    }

    /**
     * Chooses an unassigned cell using a sequential search.
     *
     * @return an unassigned cell.
     */
    public Cell seqH() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (cells[i][j].val == 0) {
                    return cells[i][j];
                }
            }
        }
        return null;
    }

    /**
     * Chooses an unassigned cell using SDF heuristic.
     * <p>
     * SDF: Smallest Domain First.
     * It prefers the unassigned cell with smaller domain.
     * <p>
     * If multiples cells tie, it chooses the first one it found
     * while searching in a sequential manner.
     *
     * @return the unassigned cell with the smallest domain
     */
    public Cell sdfH() {
        int min = Integer.MAX_VALUE;
        Cell cell = null;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (cells[i][j].val == 0 && cells[i][j].domain.size() < min) {
                    min = cells[i][j].domain.size();
                    cell = cells[i][j];
                }
            }
        }

        return cell;
    }

    /**
     * Solves this Latin Square.
     *
     * @return true if the square is solved.
     */
    public boolean solve() {
        return backtrack();
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("\n");
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size - 1; j++) {
                str.append(cells[i][j].val).append(", ");
            }
            str.append(cells[i][size - 1].val);
            str.append("\n");
        }
        str.append("\n");

        return String.valueOf(str);
    }
}
