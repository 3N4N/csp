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

    public boolean btSeq(int row, int col) {
        CSP.nodeVisited++;

        if (row == size - 1 && col == size)
            return true;

        if (col == size) {
            row++;
            col = 0;
        }

        if (cells[row][col].val > 0) {
            return btSeq(row, col + 1);
        }

        for (int num = 1; num <= size; num++) {
            if (isValid(row, col, num)) {
                cells[row][col].val = num;

                if (btSeq(row, col + 1))
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

    public boolean btMRV() {
        CSP.nodeVisited++;

        if (allAssigned()) {
            return isSolved();
        }

        Cell cell = mrv();
        int row = cell.row;
        int col = cell.col;

        for (int num = 1; num <= size; num++) {
            boolean validFlag = false;
            if (cell.possVals.contains(num)) {
            // if (isValid(row, col, num)) {
                validFlag = true;
                cell.val = num;
                cell.possVals.clear();
                cell.possVals.add(num);

                for (int x = 0; x < size; x++) {
                    cells[x][col].possVals.remove(Integer.valueOf(num));
                }
                for (int y = 0; y < size; y++) {
                    cells[row][y].possVals.remove(Integer.valueOf(num));
                }

                if (btMRV())
                    return true;
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

    public boolean btMRVnLCV() {
        CSP.nodeVisited++;

        if (allAssigned()) {
            return isSolved();
        }

        Cell cell = mrv();
        int row = cell.row;
        int col = cell.col;
        Integer[] list = lcv(cell);

        for (int i = 0; i < list.length; i++) {
            Integer num = list[i];
            if (cell.possVals.contains(num)) {
            // if (isValid(row, col, num)) {
                cell.val = num;
                cell.possVals.clear();
                cell.possVals.add(num);

                for (int x = 0; x < size; x++) {
                    cells[x][col].possVals.remove(num);
                }
                for (int y = 0; y < size; y++) {
                    cells[row][y].possVals.remove(num);
                }

                if (btMRVnLCV())
                    return true;
            }
            cell.val = 0;
            cell.possVals.clear();
            for (int k = 1; k <= size; k++) {
                if (isValid(row, col, k))
                    cell.possVals.add(k);
            }

            for (int x = 0; x < size; x++) {
                if (isValid(x, col, num) && !cells[x][col].possVals.contains(num)) {
                    cells[x][col].possVals.add(num);
                }
                if (isValid(row, x, num) && !cells[row][x].possVals.contains(num)) {
                    cells[row][x].possVals.add(num);
                }
            }
        }

        return false;
    }

    public boolean solve() {
        // return btSeq(0, 0);
        // return btMRV();
        return btMRVnLCV();
    }

    public Cell mrv() {
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

    public Integer[] bubbleSort(Integer[] arr, int row, int col) {
        int n = arr.length;
        for (int i = 0; i < n-1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                int cnt1 = 0;
                int cnt2 = 0;
                for (int y = 0; y < size; y++) {
                    if (cells[row][y].val == arr[j]) {
                        cnt1++;
                    }
                    if (cells[row][y].val == arr[j + 1]) {
                        cnt2++;
                    }
                }
                for (int x = 0; x < size; x++) {
                    if (cells[x][col].val == arr[j]) {
                        cnt1++;
                    }
                    if (cells[row][col].val == arr[j + 1]) {
                        cnt2++;
                    }
                }
                if (cnt1 > cnt2) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
        return arr;
    }

    public Integer[] lcv(Cell cell) {
        Integer[] possVals = cell.possVals.toArray(new Integer[0]);
        int row = cell.row;
        int col = cell.col;

        return possVals; // bubbleSort(possVals, row, col);
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
