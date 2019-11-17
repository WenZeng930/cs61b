import static org.junit.Assert.*;
import org.junit.Test;

public class IntListTest {

    /** Sample test that verifies correctness of the IntList.list static
     *  method. The main point of this is to convince you that
     *  assertEquals knows how to handle IntLists just fine.
     */

    @Test
    public void testList() {
        IntList one = new IntList(1, null);
        IntList twoOne = new IntList(2, one);
        IntList threeTwoOne = new IntList(3, twoOne);

        IntList x = IntList.list(3, 2, 1);
        assertEquals(threeTwoOne, x);
    }

    /** Do not use the new keyword in your tests. You can create
     *  lists using the handy IntList.list method.
     *
     *  Make sure to include test cases involving lists of various sizes
     *  on both sides of the operation. That includes the empty list, which
     *  can be instantiated, for example, with
     *  IntList empty = IntList.list().
     *
     *  Keep in mind that dcatenate(A, B) is NOT required to leave A untouched.
     *  Anything can happen to A.
     */

    @Test
    public void testDcatenate() {
        IntList one = new IntList(1, null);
        IntList twoOne = new IntList(2, one);
        IntList threeTwoOne = new IntList(3, twoOne);

        // sample cases test
        assertEquals(IntList.dcatenate(IntList.list(), IntList.list()), IntList.list());
        assertEquals(IntList.dcatenate(IntList.list(3,2,1),IntList.list()), threeTwoOne);
        assertEquals(IntList.dcatenate(IntList.list(),IntList.list(3,2,1)), threeTwoOne);
        assertEquals(IntList.dcatenate(IntList.list(2),IntList.list(1)), twoOne);

        // general cases
        IntList a = IntList.list(3, 2);
        IntList b = IntList.list(1);
        IntList c = IntList.dcatenate(a, b);
    
        
        assertEquals(threeTwoOne, c);

    }

    /** Tests that subtail works properly. Again, don't use new.
     *
     *  Make sure to test that subtail does not modify the list.
     */

    @Test
    public void testSubtail() {
        IntList one = new IntList(1, null);
        IntList twoOne = new IntList(2, one);
        IntList threeTwoOne = new IntList(3, twoOne);

        IntList sub_tail = IntList.subTail(threeTwoOne, -1);
        assertEquals(sub_tail, null);

        sub_tail = IntList.subTail(threeTwoOne, 4);
        assertEquals(sub_tail, null);

        sub_tail = IntList.subTail(threeTwoOne, 0);
        assertEquals(threeTwoOne, sub_tail);

        sub_tail = IntList.subTail(threeTwoOne, 1);
        assertEquals(twoOne, sub_tail);

    }

    /** Tests that sublist works properly. Again, don't use new.
     *
     *  Make sure to test that sublist does not modify the list.
     */

    @Test
    public void testSublist() {
        IntList one = new IntList(1, null);
        IntList twoOne = new IntList(2, one);
        IntList threeTwoOne = new IntList(3, twoOne);

        IntList sub_list = IntList.sublist(threeTwoOne, 1, 2);
        assertEquals(sub_list, twoOne);

        sub_list = IntList.sublist(threeTwoOne, 2, 1);
        assertEquals(sub_list, one);

        sub_list = IntList.sublist(threeTwoOne, 0, 0);
        assertEquals(sub_list, null);

        sub_list = IntList.sublist(threeTwoOne, 3, 4);
        assertEquals(sub_list, null);

    }

    /** Tests that dSublist works properly. Again, don't use new.
     *
     *  As with testDcatenate, it is not safe to assume that list passed
     *  to dSublist is the same after any call to dSublist
     */

    @Test
    public void testDsublist() {
        IntList one = new IntList(1, null);
        IntList twoOne = new IntList(2, one);
        IntList threeTwoOne = new IntList(3, twoOne);

        IntList dsub_list = IntList.dsublist(threeTwoOne, 1, 2);
        assertEquals(dsub_list, twoOne);

        dsub_list = IntList.dsublist(threeTwoOne, 0, 3);
        assertEquals(dsub_list, threeTwoOne);

        dsub_list = IntList.dsublist(threeTwoOne, 0, 0);
        assertEquals(dsub_list, null);

    }


    /* Run the unit tests in this file. */
    public static void main(String... args) {
        System.exit(ucb.junit.textui.runClasses(IntListTest.class));
    }
}
