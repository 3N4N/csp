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
