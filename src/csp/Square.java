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
     * <p>
     * Holds values. The value is zero if unassigned.
     */
    int[][] cells;

    Square(int size) {
        this.size = size;

        cells = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                cells[i][j] = 0;
            }
        }
    }

    /**
     * Checks if two cells are neighbours.
     *
     * @param p1 a pair containing the row and col of the first cell
     * @param p2 a pair containing the row and col of the second cell
     * @return true if p1 and p2 are neighbours
     */
    public boolean areNeighbours(Pair p1, Pair p2) {
        return areNeighbours(p1.first, p1.second, p2.first, p2.second);
    }

    /**
     * Checks if two cells are neighbours.
     *
     * @param r1 row of the first cell
     * @param c1 col of the first cell
     * @param r2 row of the second cell
     * @param c2 col of the second cell
     * @return true if the cells are neighbours
     */
    public boolean areNeighbours(int r1, int c1, int r2, int c2) {
        /* If the cells are on the same row and the same col
         * then they are the same cell and cannot be neighbours. */
        if (r1 == r2 && c1 == c2) return false;

        /* If the cells are either on the same row or the same col
         * then they are neighbours. */
        return r1 == r2 || c1 == c2;
    }

    /**
     * Returns a list of unassigned cells.
     *
     * @return a list of unassigned cells.
     */
    public ArrayList<Pair> getVariables() {
        ArrayList<Pair> variables = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                /* The cells with value zero are still unassigned. */
                if (cells[i][j] == 0)
                    variables.add(new Pair(i, j));
            }
        }
        return variables;
    }

    /**
     * Returns a list of domains for the unassigned cells.
     *
     * @param variables a list of unassigned cells
     * @return a list of domains for the unassigned cells
     */
    public ArrayList<ArrayList<Integer>> getDomains(ArrayList<Pair> variables) {
        ArrayList<ArrayList<Integer>> domains = new ArrayList<>();

        for (int i = 0; i < variables.size(); i++) {
            domains.add(new ArrayList<>());
            Pair pair = variables.get(i);
            int row = pair.first;
            int col = pair.second;

            /* If the cell is assigned then ignore it. */
            if (cells[row][col] != 0) continue;

            for (int num = 1; num <= size; num++) {
                if (isValid(row, col, num))
                    domains.get(i).add(num);
            }
        }

        return domains;
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
            /* If a cell in either the same row or same col
             * holds the value num, the assignment is invalid. */
            if (cells[row][x] == num) return false;
            if (cells[x][col] == num) return false;
        }

        /* If no in either the same row or same col
         * holds the value num, the assignment is valid. */
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
                if (cells[i][j] == 0) return false;
                for (int x = 0; x < size; x++) {
                    if (x != i && cells[x][j] == cells[i][j]) {
                        return false;
                    }
                    if (x != j && cells[i][x] == cells[i][j]) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Checks if this Latin Square is consistent with the constraints.
     * <p>
     * A Latin Square is consistent if no two cells
     * in the a row or a col holds the same value.
     *
     * @return true if this square is consistent
     */
    public boolean isConsistent() {

        /* Loop over all the cells */
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                /* If cell is unassigned, ignore it. */
                if (cells[i][j] == 0) continue;

                /* Loop over cell's neighbours. */
                for (int x = 0; x < size; x++) {

                    /* If neighbour holds the same value
                     * return inconsistent. */
                    if ( x != i && cells[x][j] == cells[i][j])
                        return false;
                    if (x != j && cells[i][x] == cells[i][j])
                        return false;
                }
            }
        }

        return true;
    }

    /**
     * Updates the domain of a cell in keeping with that of a neighbour cell.
     * <p>
     * The cells must be neighbouring. Otherwise the method will malfunction.
     * So ensure calling this method only with IDs neighbouring cells.
     *
     * @param domains a list of domains of all unassigned cells
     * @param i the idx of an unassigned cell
     * @param j the idx of a neighbouring unassigned cell
     * @return true if any alternation was needed and false otherwise
     */
    public boolean revise(ArrayList<ArrayList<Integer>> domains, int i, int j) {
        boolean removed = false;

        ArrayList<Integer> tempList = new ArrayList<>();
        for (Integer val : domains.get(i)) {
            if (domains.get(j).size() == 1 && domains.get(j).get(0).equals(val)) {
                tempList.add(val);
                removed = true;
            }
        }
        domains.get(i).removeAll(tempList);

        return removed;
    }

    public boolean forwardCheck(ArrayList<Pair> variables,
                                ArrayList<ArrayList<Integer>> domains,
                                int cv,
                                boolean isLookAheadWanted) {
        ArrayList<Pair> queue = new ArrayList<>();

        for (int i = 0; i < variables.size(); i++) {
            if (!areNeighbours(variables.get(i), variables.get(cv))) continue;
            queue.add(new Pair(i, cv));
        }

        while (!queue.isEmpty()) {
            Pair arc = queue.remove(0);
            int k = arc.first;
            int m = arc.second;

            if (revise(domains, k, m)) {
                if (domains.get(k).size() == 0) {
                    return false;
                }

                if (isLookAheadWanted) {
                    for (int i = 0; i < variables.size(); i++) {
                        if (i == k || i == m) continue;
                        if (areNeighbours(variables.get(i), variables.get(k))) {
                            queue.add(new Pair(i, k));
                        }
                    }
                }
            }
        }

        return true;
    }

    public boolean backtrack(ArrayList<Pair> variables,
                             ArrayList<ArrayList<Integer>> domains,
                             boolean isForwardCheckWanted,
                             boolean isLookAheadWanted) {
        CSP.nodeVisited++;

        if (!isForwardCheckWanted && !isConsistent()) return false;
        if (variables.size() == 0) return true;

        int varID = 0;
        // int varID = sdfH(variables, domains);
        // int varID = variables.size() - 1;
        Pair pair = variables.get(varID);
        ArrayList<Integer> domain = domains.get(varID);

        Collections.swap(variables, varID, variables.size() - 1);
        Collections.swap(domains, varID, domains.size() - 1);

        for (int i = 0; i < domain.size(); i++) {
            Integer val = domain.get(i);
            cells[pair.first][pair.second] = val;

            ArrayList<ArrayList<Integer>> newDomains = new ArrayList<>();
            for (int a = 0; a < domains.size(); a++) {
                newDomains.add(new ArrayList<>());
                for (int b = 0; b < domains.get(a).size(); b++) {
                    newDomains.get(a).add(domains.get(a).get(b));
                }
            }

            newDomains.get(newDomains.size() - 1).clear();
            newDomains.get(newDomains.size() - 1).add(val);

            boolean resForwardCheck;
            if (isForwardCheckWanted) {
                resForwardCheck = forwardCheck(variables,
                                               newDomains,
                                               variables.size() - 1,
                                               isLookAheadWanted);
            }
            else resForwardCheck = true;

            if (resForwardCheck) {
                variables.remove(variables.size() - 1);
                newDomains.remove(newDomains.size() - 1);
                if (backtrack(variables,
                              newDomains,
                              isForwardCheckWanted,
                              isLookAheadWanted)) {
                    return true;
                }
                variables.add(pair);
            }

            cells[pair.first][pair.second] = 0;
        }

        Collections.swap(variables, varID, variables.size() - 1);
        Collections.swap(domains, varID, domains.size() - 1);

        return false;
    }

    /**
     * Chooses an unassigned cell using SDF heuristic.
     * <p>
     * SDF: Smallest Domain First.
     * It prefers the unassigned cell with smaller domain.
     * <p>
     * If multiples cells tie, it chooses the first one it found
     * while searching in a sequential manner.
     *
     * @return the unassigned cell with the smallest domain
     */
    public int sdfH(ArrayList<Pair> variables,
                    ArrayList<ArrayList<Integer>> domains) {
        int min = Integer.MAX_VALUE;
        int id = -1;

        for (int i = 0; i < variables.size(); i++) {
            Pair pair = variables.get(i);

            if (cells[pair.first][pair.second] != 0) continue;

            if (domains.get(i).size() < min) {
                min = domains.get(i).size();
                id = i;
            }
        }

        return id;
    }

    /**
     * Solves this Latin Square.
     *
     * @return true if the square is solved.
     */
    public boolean solve() {
        ArrayList<Pair> variables = getVariables();
        ArrayList<ArrayList<Integer>> domains = getDomains(variables);

        boolean isForwardCheckWanted = false;
        boolean isLookAheadWanted = false;
        isForwardCheckWanted = true;
        isLookAheadWanted = true;
        return backtrack(variables, domains, isForwardCheckWanted, isLookAheadWanted);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("\n");
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size - 1; j++) {
                str.append(cells[i][j]).append(", ");
            }
            str.append(cells[i][size - 1]);
            str.append("\n");
        }
        str.append("\n");

        return String.valueOf(str);
    }
}
