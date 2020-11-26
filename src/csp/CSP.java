package csp;

import java.io.*;

public class CSP {

    private static Square square;

    private static final String errdata = "Data file corrupted";
    private static final String inputFile = "csp_task/data/d-10-01.txt.txt";

    public static void error(String funcname, String errmsg) {
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

            square.size = Integer.parseInt(line.substring(2, line.length() - 1));
            System.out.println("The size of the square: " + square.size);

            square.initArr();

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

            int loop = square.size;
            while(loop-- != 0) {
                line = br.readLine();
                if (loop == 0) {
                    line = line.substring(0, line.length() - 4);
                } else {
                    line = line.substring(0, line.length() - 2);
                }
                String [] tokens = line.split(", ");
                for (int i = 0; i < tokens.length; i++){
                    square.arr[square.size - loop - 1][i] = Integer.parseInt(tokens[i]);
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
        System.out.println("-------[ csp.CSP starting ]-------");

        square = new Square();
        if(!readFile()) {
            error(funcname, "File could not be read.");
        }

        System.out.println(square);


    }
}
