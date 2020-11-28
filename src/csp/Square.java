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
        if(cells != null) {
            CSP.error(funcname, "Square already set");
        }

        cells = new Cell[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                cells[i][j] = new Cell();
                cells[i][j].row = i;
                cells[i][j].col = j;
                cells[i][j].possVals = new ArrayList<>(size);
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
                    cells[i][j].possVals.clear();
                    for (int k = 1; k <= size; k++) {
                        if (isValid(i, j, k))
                            cells[i][j].possVals.add(k);
                    }
                } else {
                    cells[i][j].possVals.clear();
                    cells[i][j].possVals.add(cells[i][j].val);
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
     * @see #isSolved()
     *
     * @return true if no unassigned cell
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
     * Checks if any unassigned cell has a nil domain.
     * <p>
     * Any unassigned cell having no domain means
     * the current state of the board is unsolvable.
     *
     * @return true if any unassigned cell have zero domain.
     */
    public boolean fowardcheck() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (cells[i][j].val == 0 && cells[i][j].possVals.size() == 0)
                    return true;
            }
        }
        return false;
    }

    /**
     * Assigns a value to a cell and updates the domains of its neighbours.
     *
     * @param cell the cell to be assigned val
     * @param val the value to be assigned to cell
     */
    public void assign(Cell cell, Integer val) {
        int row = cell.row;
        int col = cell.col;

        cell.val = val;
        cell.possVals.clear();
        cell.possVals.add(val);

        for (int x = 0; x < size; x++) {
            cells[x][col].possVals.remove(val);
            cells[row][x].possVals.remove(val);
        }
    }

    /**
     * Unassigns a cell and updates the domains its neighbours.
     */
    public void unassign(Cell cell, Integer val) {
        int row = cell.row;
        int col = cell.col;

        cell.val = 0;
        cell.possVals.clear();
        for (int k = 1; k <= size; k++) {
            if (isValid(row, col, k))
                cell.possVals.add(k);
        }

        for (int x = 0; x < size; x++) {
            if (isValid(x, col, val)
                    && !cells[x][col].possVals.contains(val)) {
                cells[x][col].possVals.add(val);
            }
            if (isValid(row, x, val)
                    && !cells[row][x].possVals.contains(val)) {
                cells[row][x].possVals.add(val);
            }
        }
    }

    public boolean backtrack() {
        CSP.nodeVisited++;

        if (allAssigned()) return isSolved();

        // Cell cell = seqH();
        Cell cell = sdfH();
        Integer[] list = cell.possVals.toArray(new Integer[0]);

        for (Integer val : list) {
            assign(cell, val);

            // if (backtrack()) return true;
            if (!fowardcheck() && backtrack()) return true;

            unassign(cell, val);
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
                if (cells[i][j].val == 0 && cells[i][j].possVals.size() < min) {
                    min = cells[i][j].possVals.size();
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
