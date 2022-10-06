package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;

import static gitlet.Utils.*;

/** Comment.
 *  @author William Webster
 */
public class Commit implements Serializable {

    /** Comment. */
    private String message;
    /** Comment. */
    private String parent;
    /** Comment. */
    private String mergeParent = null;
    /** Comment. */
    private String timestamp;
    /** Comment. */
    private String name;
    /** Comment. */
    private TreeMap<String, String> metaData;

    /** Initialize new commit object.
     * @param message1 Bruh.
     * @param parent1 */
    public Commit(String message1, String parent1) {
        this.message = message1;
        this.parent = parent1;

        if (this.parent == null) {
            timestamp = "Wed Dec 31 16:00:00 1969 -0800";
            metaData = new TreeMap<>();
        } else {
            Date thisDate = new Date();
            String p = "EEE MMM d HH:mm:ss yyyy Z";
            SimpleDateFormat dateForm = new SimpleDateFormat(p);
            this.timestamp = dateForm.format(thisDate);

            File h = Utils.join(COMMITDIR, parent);
            Commit parentCommit = readObject(h, Commit.class);
            metaData = parentCommit.getMetaData();
        }

        name = sha1(message + timestamp + parent);
    }

    /** Comment.
     * @param parent2 */
    public void setMergeParent(String parent2) {
        this.mergeParent = parent2;
    }

    /** Comment.
     * @return thing.*/
    public String getMergeParent() {
        return this.mergeParent;
    }

    /** Comment.
     * @return thing.*/
    public String getName() {
        return this.name;
    }

    /** Comment.
     * @return thing.*/
    public String getMessage() {
        return this.message;
    }

    /** Comment.
     * @return thing.*/
    public String getParent() {
        return this.parent;
    }

    /** Comment.
     * @return thing.*/
    public String getTimestamp() {
        return this.timestamp;
    }

    /** Comment.
     * @return thing.*/
    public TreeMap<String, String> getMetaData() {
        return this.metaData;
    }

    /** Add key(fileName) + value(sha1 of contents). */
    public void update() {
        Staging temp = readObject(STAGINGFILE, Staging.class);

        if (temp.getStage().isEmpty() && temp.getRemove().isEmpty()) {
            System.out.print("No changes added to the commit.");
        } else {
            this.metaData.putAll(temp.getStage());

            for (String fileName: temp.getRemove()) {
                this.metaData.remove(fileName);
            }

            for (String fileName: temp.getRemove()) {
                restrictedDelete(new File(fileName));
            }

            temp.clearStage();
            temp.clearRemove();
            writeObject(STAGINGFILE, temp);
        }
    }

}
