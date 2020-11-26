package csp;

public class Square {
    public int[][] arr;
    public int size;

    Square() {
        this.size = -1;
    }

    Square(int size) {
        this.size = size;
        arr = new int[size][size];
    }

    public void initArr() {
        String funcname = "initArr";
        if(arr != null) {
            CSP.error(funcname, "Square already set");
        }

        arr = new int[size][size];
    }

    public boolean isValid(int row, int col, int num) {
        for (int y = 0; y < size; y++) {
            if (arr[row][y] == num) return false;
        }
        for (int x = 0; x < size; x++) {
            if (arr[x][col] == num) return false;
        }

        return true;
    }

    public boolean solve(int row, int col) {

        if (row == size - 1 && col == size)
            return true;

        if (col == size) {
            row++;
            col = 0;
        }

        if (arr[row][col] > 0)
            return solve(row, col + 1);

        for (int num = 1; num <= size; num++) {
            if (isValid(row, col, num)) {
                arr[row][col] = num;

                if (solve(row, col + 1))
                    return true;
            }
            arr[row][col] = 0;
        }

        return false;
    }


    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("\n");
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size - 1; j++) {
                str.append(arr[i][j]).append(", ");
            }
            str.append(arr[i][size - 1]);
            str.append("\n");
        }
        str.append("\n");

        return String.valueOf(str);
    }
}
