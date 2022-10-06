package gitlet;

import java.io.Serializable;
import java.util.TreeMap;

/** Comment.
 *  @author William Webster
 */
public class Branch implements Serializable {

    /** Comment. */
    private TreeMap<String, String> branches;

    /** Comment.
     * @return thing.*/
    public TreeMap<String, String> getBranches() {
        return this.branches;
    }

    /** Comment.
     * @param initialCommit */
    public Branch(String initialCommit) {
        this.branches = new TreeMap<>();
        branches.put("master", initialCommit);
    }

    /** Comment.
     * @param branchName thing.
     * @param head */
    public void branch(String branchName, String head) {
        branches.put(branchName, head);
    }

}
