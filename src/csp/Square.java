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

    public boolean nildom() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (cells[i][j].val == 0 && cells[i][j].domain.size() == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if any unassigned cell has a nil domain.
     * <p>
     * Any unassigned cell having no domain means
     * the current state of the board is unsolvable.
     *
     * @return true if any unassigned cell have zero domain.
     */
    public boolean forwardCheck(Cell cell, Integer val) {
        assign(cell, val);
        boolean ret = nildom();
        unassign(cell, val);
        return ret;
    }

    /**
     * Assigns a value to a cell and updates the domains of its neighbours.
     *
     * @param cell the cell to be assigned val
     * @param val  the value to be assigned to cell
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

    public boolean backtrack() {
        CSP.nodeVisited++;

        if (allAssigned()) return isSolved();

        // Cell cell = seqH();
        Cell cell = sdfH();
        Integer[] list = cell.domain.toArray(new Integer[0]);

        for (Integer val : list) {
            // if (forwardCheck(cell, val)) {
            if (ac3(cell, val)) {
                assign(cell, val);

                if (backtrack()) return true;

                unassign(cell, val);
            }
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
     * If multiples cells tie, it choses the first one it found
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

    public boolean areSameCell(Cell c1, Cell c2) {
        return c1.row == c2.row && c1.col == c2.col;
    }

    public boolean areNeighbours(Cell c1, Cell c2) {
        return ((c1.row == c2.row || c1.col == c2.col));
    }

    public boolean revise(Cell c1, Cell c2) {
        if (!areNeighbours(c1, c2)) return false;
//        if (c1.val != 0) return false;

        boolean removed = false;
        ArrayList<Integer> tempList = new ArrayList<>();

        for (Integer val1 : c1.domain) {
            for (Integer val2 : c2.domain) {
//                if (c2.domain.get(0).equals(val1)) {
                if (val2.equals(val1)) {
                    System.out.println("lsdkfj");
                    tempList.add(val1);
                }
            }
        }

        if (tempList.size() > 0) {
            c1.domain.removeAll(tempList);
            removed = true;
        }

        return removed;
    }

    public boolean ac3(Cell cell, Integer val) {
        assign(cell, val);
        if (!nildom()) {
            unassign(cell, val);
            return false;
        }
        ArrayList<Arc> queue = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i != cell.row && j != cell.col)
                    queue.add(new Arc(cells[i][j], cell));
            }
        }

        while (!queue.isEmpty()) {
            Arc arc = queue.remove(0);
            Cell c1 = arc.getC1();
            Cell c2 = arc.getC2();

            if (revise(c1, c2)) {
                if (c1.domain.size() == 0) {
                    System.out.println("HEre:");
                    unassign(cell, val);
                    return false;
                }
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        if (cells[i][j] != c1 && cells[i][j] != c2)
                            queue.add(new Arc(cells[i][j], c1));
                    }
                }
            }
        }

        unassign(cell, val);
        return true;
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
