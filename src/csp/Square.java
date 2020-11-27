package csp;

import java.util.*;

public class Square {
    int size;
    Cell[][] cells;

    Square() {
        this.size = -1;
    }

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

    public void update() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (cells[i][j].val == 0) {
                    cells[i][j].possVals.clear();
                    for (int k = 1; k <= size; k++) {
                        if (isValid(i, j, k)) cells[i][j].possVals.add(k);
                    }
                } else {
                    cells[i][j].possVals.clear();
                    cells[i][j].possVals.add(cells[i][j].val);
                }
            }
        }
    }

    public boolean isValid(int row, int col, int num) {
        for (int y = 0; y < size; y++) {
            if (cells[row][y].val == num) return false;
        }
        for (int x = 0; x < size; x++) {
            if (cells[x][col].val == num) return false;
        }

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

    public boolean allAssigned() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int val = cells[i][j].val;
                if (val == 0) return false;
            }
        }

        return true;
    }

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

        if (allAssigned()) {
            return isSolved();
        }

        // Cell cell = seqH();
        Cell cell = sdfH();
        int row = cell.row;
        int col = cell.col;
        Integer[] list = cell.possVals.toArray(new Integer[0]);

        for (int i = 0; i < list.length; i++) {
            Integer num = list[i];
            if (cell.possVals.contains(num)) {
            // if (isValid(row, col, num)) {
                cell.val = num;
                cell.possVals.clear();
                cell.possVals.add(num);

                for (int x = 0; x < size; x++) {
                    cells[x][col].possVals.remove(num);
                    cells[row][x].possVals.remove(num);
                }

                if (forwardCheck()) return true;
            }
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

    public Cell sdfH() {
        ArrayList<Cell> unassigned = new ArrayList<>(size);
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
