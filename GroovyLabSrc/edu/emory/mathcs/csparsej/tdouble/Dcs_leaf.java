

package edu.emory.mathcs.csparsej.tdouble;

/**
 * Determine if j is a leaf of the skeleton matrix and find lowest common
 * ancestor (lca).
 * 
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 * 
 */
public class Dcs_leaf {

    /**
     * Determines if j is a leaf of the skeleton matrix and find lowest common
     * ancestor (lca).
     * 
     * @param i
     * @param j
     * @param first
     * @param first_offset
     * @param maxfirst
     * @param maxfirst_offset
     * @param prevleaf
     * @param prevleaf_offset
     * @param ancestor
     * @param ancestor_offset
     * @param jleaf
     * @return lca(jprev,j)
     */
    public static int cs_leaf(int i, int j, int[] first, int first_offset, int[] maxfirst, int maxfirst_offset,
            int[] prevleaf, int prevleaf_offset, int[] ancestor, int ancestor_offset, int[] jleaf) {
        int q, s, sparent, jprev;
        if (first == null || maxfirst == null || prevleaf == null || ancestor == null || jleaf == null)
            return (-1);
        jleaf[0] = 0;
        if (i <= j || first[first_offset + j] <= maxfirst[maxfirst_offset + i])
            return (-1); /* j not a leaf */
        maxfirst[maxfirst_offset + i] = first[first_offset + j]; /* update max first[j] seen so far */
        jprev = prevleaf[prevleaf_offset + i]; /* jprev = previous leaf of ith subtree */
        prevleaf[prevleaf_offset + i] = j;
        jleaf[0] = (jprev == -1) ? 1 : 2; /* j is first or subsequent leaf */
        if (jleaf[0] == 1)
            return (i); /* if 1st leaf, q = root of ith subtree */
        for (q = jprev; q != ancestor[ancestor_offset + q]; q = ancestor[ancestor_offset + q])
            ;
        for (s = jprev; s != q; s = sparent) {
            sparent = ancestor[ancestor_offset + s]; /* path compression */
            ancestor[ancestor_offset + s] = q;
        }
        return (q); /* q = least common ancestor (jprev,j) */
    }

}
