package arrays;

import org.junit.Test;
import static org.junit.Assert.*;

/** FIXME
 *  @author FIXME
 */

public class ArraysTest {
    /** FIXME
     */
    @Test
    public void testCatenate() {
        int [] a1 = {};
        int [] b1 = {};
        int [] a2 = {1, 2, 3};
        int [] b2 = {4, 5, 6};
        int [] c1 = {1, 2, 3, 4, 5, 6};
        Utils.print(Arrays.catenate(a1, a2));
        System.out.println();
        Utils.print(Arrays.catenate(a2, b2));
        System.out.println();
        Utils.print(Arrays.catenate(b2, a2));
        System.out.println();
    }

    @Test
    public void testRemove() {
        int [] a1 = {1, 2, 3, 4, 5, 6};
        int [] b1 = {4, 5, 6};
        Utils.print(Arrays.remove(a1, 2, 3));
        System.out.println();

        Utils.print(Arrays.remove(a1, 0, 5));
        System.out.println();
    }
    @Test
    public void testNaturalRuns() {
        int[] a1 = {1, 3, 7, 5, 4, 6, 9, 10};
        Utils.print(Arrays.naturalRuns(a1));
        System.out.println();
    }

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(ArraysTest.class));
    }
}
