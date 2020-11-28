package csp;

import java.util.ArrayList;

/**
 * Holds the position,value, and domain
 * of a cell in a Latin Square.
 * <p>
 *
 */
public class Cell {
    int row;
    int col;
    int val;

    /**
     * A list of domain for this cell.
     * <p></p>
     * <ol>
     *     <li>The domain of an assigned cell is the value that cell holds.
     *     <li>The domain of an unassigned cell
     *     is all the valid values that cell can be assigned.
     *     <li>A value is valid for an unassigned cell if no other cell
     *     in the same row or same col of this cell holds that value.
     * <ol>
     */
    ArrayList<Integer> possVals;
}
