package lists;

import antlr.collections.List;
import static org.junit.Assert.*;
import org.junit.Test;

/** FIXME
 *
 *  @author FIXME
 */

public class ListsTest {
    /** FIXME
     * @return
     */

    // It might initially seem daunting to try to set up
    // IntListList expected.
    //
    // There is an easy way to get the IntListList that you want in just
    // few lines of code! Make note of the IntListList.list method that
    // takes as input a 2D array. //

    @Test
    public void testNaturalRuns() {
        /** Sample assert statement for comparing integers.
         assertEquals(0, 0); */
        IntList L = IntList.list(1, 3, 7, 5, 4, 6, 9, 10, 10, 11);
        IntListList M = IntListList.list(IntList.list(1,3,7),IntList.list(5),IntList.list(4,6,9,10),IntList.list(10,11));
        assertEquals(M, Lists.naturalRuns(L));
    }

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(ListsTest.class));
    }
}
