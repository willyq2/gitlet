package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.TreeMap;

import static gitlet.Utils.*;

/** Comment.
 *  @author William Webster
 */
public class Staging implements Serializable {

    /** Comment. */
    private TreeMap<String, String> stagingArea;
    /** Comment. */
    private ArrayList<String> removalArea;
    /** Comment. */
    private ArrayList<String> untracked;

    /** Comment. */
    public Staging() {
        this.stagingArea = new TreeMap<>();
        this.removalArea = new ArrayList<String>();
        this.untracked = new ArrayList<String>();
    }

    /** Comment.
     * @return thing.*/
    public TreeMap<String, String> getStage() {
        return this.stagingArea;
    }

    /** Comment.
     * @return thing.*/
    public ArrayList<String> getRemove() {
        return this.removalArea;
    }

    /** Comment.
     * @return thing.*/
    public ArrayList<String> getUntracked() {
        return this.untracked;
    }

    /** Comment. */
    public void clearStage() {
        stagingArea.clear();
    }
    /** Comment. */
    public void clearRemove() {
        removalArea.clear();
    }

    /** Create correspondent blob and add key(fileName).
     * @param fileName thing.
     * @param blobID */
    public void add(String fileName, String blobID) {
        File path = join(BLOBDIR, blobID);
        writeContents(path, readContents(new File(fileName)));

        stagingArea.put(fileName, blobID);
    }

    /** Create correspondent blob and add key(fileName).
     * @param fileName */
    public void stageRemove(String fileName) {
        stagingArea.remove(fileName);
        untracked.add(fileName);
    }

    /** Create correspondent blob and add key(fileName).
     * @param fileName */
    public void commitRemove(String fileName) {
        removalArea.add(fileName);
        restrictedDelete(new File(fileName));
    }

}
