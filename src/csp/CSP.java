package csp;

import java.io.*;

import static java.lang.Integer.parseInt;

public class CSP {

    private static Square square;
    public static int nodeVisited = 0;

    private static final String errdata = "Data file corrupted.";
    private static final String inputFile = "csp_task/data/d-10-01.txt.txt";
    // private static final String inputFile = "csp_task/data/d-10-06.txt.txt";
    // private static final String inputFile = "csp_task/data/d-10-07.txt.txt";
    // private static final String inputFile = "csp_task/data/d-10-08.txt.txt";
    // private static final String inputFile = "csp_task/data/d-10-09.txt.txt";
    // private static final String inputFile = "csp_task/data/d-15-01.txt.txt";

    public static void error(String funcname, String errmsg) {
        System.out.println("[ERROR] " + funcname + ": " + errmsg);
    }

    private static String readNextLine(BufferedReader br) throws IOException {
        return br.readLine().replaceAll("\\s", "");
    }

    private static boolean init() {
        String funcname = "init";

        File file = new File(inputFile);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            line = readNextLine(br);
            if (line.charAt(0) != 'N'
                    || line.charAt(1) != '='
                    || line.charAt(line.length() - 1) != ';') {
                error(funcname, errdata);
                return false;
            }

            square.size = parseInt(line.substring(2, line.length() - 1));
            System.out.println("The size of the square: " + square.size);

            square.init();

            line = readNextLine(br);
            if (!line.equals("start=")) {
                error(funcname, errdata);
                return false;
            }

            line = readNextLine(br);
            if (!line.equals("[|")) {
                error(funcname, errdata);
                return false;
            }

            int loop = square.size;
            while(loop-- != 0) {
                line = readNextLine(br);
                if (loop == 0) {
                    line = line.substring(0, line.length() - 3);
                } else {
                    line = line.substring(0, line.length() - 1);
                }
                String [] tokens = line.split(",");
                for (int i = 0; i < tokens.length; i++){
                    square.cells[square.size - loop - 1][i].val = parseInt(tokens[i]);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static void main(String[] args) {
        String funcname = "main";
        System.out.println("-------[ CSP starting ]-------");

        square = new Square();
        if(!init()) {
            error(funcname, "File could not be read.");
            return;
        }

        System.out.println("\nGiven unsolved Latin Square:");
        System.out.println(square);

        square.update();

        nodeVisited = 0;
        if (square.solve()) {
            System.out.println("Solved Latin Square:");
            System.out.println(square);
        }
        System.out.println("Total nodes visited: " + nodeVisited);
    }
}
