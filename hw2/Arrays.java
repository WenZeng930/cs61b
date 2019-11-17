package arrays;

/* NOTE: The file Arrays/Utils.java contains some functions that may be useful
 * in testing your answers. */

/** HW #2 */

/** Array utilities.
 *  @author
 */
class Arrays {
    /* C. */
    /** Returns a new array consisting of the elements of A followed by the
     *  the elements of B. */
    static int[] catenate(int[] A, int[] B) {
        /* *Replace this body with the solution. */
        int[] C = new int[A.length + B.length];
        int i=0;
        for( ; i<A.length; i++) {
            C[i] = A[i];
        }
        for(int j=0; j<B.length; j++) {
            C[i+j] = B[j];
        }
        return C;
    }

    /** Returns the array formed by removing LEN items from A,
     *  beginning with item #START. */
    static int[] remove(int[] A, int start, int len) {
        /* *Replace this body with the solution. */
        if(len <= 0 || A.length == 0) {
            return A;
        }
        int[] removed_array = new int[len];
        System.arraycopy(A, A[start], removed_array, 0, len);
        return removed_array;
    }

    /* E. */
    /** Returns the array of arrays formed by breaking up A into
     *  maximal ascending lists, without reordering.
     *  For example, if A is {1, 3, 7, 5, 4, 6, 9, 10}, then
     *  returns the three-element array
     *  {{1, 3, 7}, {5}, {4, 6, 9, 10}}. */
    static int[][] naturalRuns(int[] A) {
        /* *Replace this body with the solution. */
        if (A.length == 0){
            return new int[0][];
        }
        int leng = 1, arr[][];
        int j = 0;
        for (int i=1; i<A.length; i++) {
            if (A[i] < A[i-1]) {
                leng += 1;
            }
        }
        arr = new int[leng][];
        leng = 0;

        for (int i=1; i<A.length; i++) {
            if (A[i] < A[i-1]) {
                arr[leng++] = Utils.subarray(A, j, i-j);
                j = i;
            }
        }
        if (leng != arr.length) {
            arr[leng] = Utils.subarray(A, j, A.length-j);
        }
        return arr;
    }
}
