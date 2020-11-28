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

    public boolean backtrack(int row, int col) {
        CSP.nodeVisited++;

        if (row == size - 1 && col == size)
            return true;

        if (col == size) {
            row++;
            col = 0;
        }

        if (cells[row][col].val > 0) {
            return backtrack(row, col + 1);
        }

        for (int num = 1; num <= size; num++) {
            if (isValid(row, col, num)) {
                cells[row][col].val = num;

                if (backtrack(row, col + 1))
                    return true;
            }
            cells[row][col].val = 0;
        }

        return false;
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
    public boolean noPosVal() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (cells[i][j].val == 0 && cells[i][j].possVals.size() == 0)
                    return true;
            }
        }
        return false;
    }

    public boolean forwardCheck() {
        CSP.nodeVisited++;

        if (noPosVal()) return false;
        if (allAssigned()) return isSolved();

        // Cell cell = seqH();
        Cell cell = sdfH();
        int row = cell.row;
        int col = cell.col;
        Integer[] list = cell.possVals.toArray(new Integer[0]);

        for (Integer num : list) {
            cell.val = num;
            cell.possVals.clear();
            cell.possVals.add(num);

            for (int x = 0; x < size; x++) {
                cells[x][col].possVals.remove(num);
                cells[row][x].possVals.remove(num);
            }

            if (forwardCheck()) return true;

            cell.val = 0;
            cell.possVals.clear();
            for (int k = 1; k <= size; k++) {
                if (isValid(row, col, k))
                    cell.possVals.add(k);
            }

            for (int x = 0; x < size; x++) {
                if (isValid(x, col, num)
                        && !cells[x][col].possVals.contains(num)) {
                    cells[x][col].possVals.add(num);
                }
                if (isValid(row, x, num)
                        && !cells[row][x].possVals.contains(num)) {
                    cells[row][x].possVals.add(num);
                }
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
                if (cells[i][j].val == 0) {
                    unassigned.add(cells[i][j]);
                }
            }
        }

        int min = Integer.MAX_VALUE;
        Cell cell = null;
        for (int i = 0; i < unassigned.size(); i++) {
            if (unassigned.get(i).possVals.size() < min) {
                min = unassigned.get(i).possVals.size();
                cell = unassigned.get(i);
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
        // return btSeq(0, 0);
        // return backtrack(0,0);
        return forwardCheck();
        // return btMRVnLCV();
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
