import java.io.*;

public class CSP {

    private static int[][] arr;
    private static int n;

    private static final String errdata = "Data file corrupted";
    private static final String inputFile = "csp_task/data/d-10-01.txt.txt";

    private static void error(String funcname, String errmsg) {
        System.out.println("[ERROR] " + funcname + ": " + errmsg);
    }

    private static boolean readFile() {
        String funcname = "readFile";

        File file = new File(inputFile);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            line = br.readLine();
            if (line.charAt(0) != 'N'
                    || line.charAt(1) != '='
                    || line.charAt(line.length() - 1) != ';') {
                error(funcname, errdata);
                return false;
            }

            n = Integer.parseInt(line.substring(2, line.length() - 1));
            System.out.println("The size of the square: " + n);

            arr = new int[n][n];

            line = br.readLine();
            if (!line.equals("start=")) {
                error(funcname, errdata);
                return false;
            }

            line = br.readLine();
            if (!line.equals("[|")) {
                error(funcname, errdata);
                return false;
            }

            int loop = n;
            while(loop-- != 0) {
                line = br.readLine();
                if (loop == 0) {
                    line = line.substring(0, line.length() - 4);
                } else {
                    line = line.substring(0, line.length() - 2);
                }
                String [] tokens = line.split(", ");
                for (int i = 0; i < tokens.length; i++){
                    arr[n - loop - 1][i] = Integer.parseInt(tokens[i]);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private static void printSquare() {
        System.out.println();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(arr[i][j] + ",");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void main(String[] args) {
        String funcname = "main";
        System.out.println("-------[ CSP starting ]-------");

        if(!readFile()) {
            error(funcname, "File could not be read.");
        }

        printSquare();

        
    }
}
