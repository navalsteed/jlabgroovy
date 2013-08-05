

package edu.emory.mathcs.csparsej.tfloat;

/**
 * Postorder a tree or forest.
 * 
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 * 
 */
public class Scs_post {

    /**
     * Postorders a tree of forest.
     * 
     * @param parent
     *            defines a tree of n nodes
     * @param n
     *            length of parent
     * @return post[k]=i, null on error
     */
    public static int[] cs_post(int[] parent, int n) {
        int j, k = 0, post[], w[], head[], next[], stack[];
        if (parent == null)
            return (null); /* check inputs */
        post = new int[n]; /* allocate result */
        w = new int[3 * n]; /* get workspace */
        head = w;
        next = w;
        int next_offset = n;
        stack = w;
        int stack_offset = 2 * n;
        for (j = 0; j < n; j++)
            head[j] = -1; /* empty linked lists */
        for (j = n - 1; j >= 0; j--) /* traverse nodes in reverse order*/
        {
            if (parent[j] == -1)
                continue; /* j is a root */
            next[next_offset + j] = head[parent[j]]; /* add j to list of its parent */
            head[parent[j]] = j;
        }
        for (j = 0; j < n; j++) {
            if (parent[j] != -1)
                continue; /* skip j if it is not a root */
            k = Scs_tdfs.cs_tdfs(j, k, head, 0, next, next_offset, post, 0, stack, stack_offset);
        }
        return post;
    }

}
