package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Formatter;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;

import static gitlet.Utils.*;

/** Comment.
        *  @author William Webster
        */
public class Repo implements Serializable {

    /** Comment. */
    private String head;
    /** Comment. */
    private String active;

    /** Comment. */
    public void init() {
        GITLET.mkdir();

        writeObject(STAGINGFILE, new Staging());

        BLOBDIR.mkdir();

        COMMITDIR.mkdir();
        Commit initialCommit = new Commit("initial commit", null);
        writeObject(join(COMMITDIR, initialCommit.getName()), initialCommit);

        head = initialCommit.getName();
        active = "master";

        writeObject(BRANCHFILE, new Branch(head));
    }

    /** Comment.
     * @return comment.*/
    public String getHead() {
        return this.head;
    }

    /** Comment.
     * @return comment.*/
    public String getActive() {
        return this.active;
    }

    /** Comment.
     * @return comment.*/
    public Commit getHeadCommit() {
        Commit temp = readObject(Utils.join(COMMITDIR, head), Commit.class);
        return temp;
    }

    /** Comment.
     * @return comment.*/
    public TreeMap getHeadMeta() {
        return getHeadCommit().getMetaData();
    }

    /** Comment.
     * @return comment.*/
    private Staging getStagingFile() {
        Staging temp = readObject(STAGINGFILE, Staging.class);
        return temp;
    }

    /** Comment.
     * @return comment.*/
    private Branch getBranchFile() {
        Branch temp = readObject(BRANCHFILE, Branch.class);
        return temp;
    }

    /** Comment.
     * @param fileName is a thing.*/
    public void add(String fileName) {
        if (new File(fileName).exists()) {
            Staging temp = getStagingFile();
            String blobID = sha1(Utils.readContents(new File(fileName)));

            if (getHeadCommit().getMetaData().containsKey(fileName)) {
                if (getHeadMeta().get(fileName).equals(blobID)) {
                    if (temp.getRemove().contains(fileName)) {
                        temp.getRemove().remove(fileName);
                        writeObject(STAGINGFILE, temp);
                        return;
                    }
                    return;
                }
            }

            temp.add(fileName, blobID);
            writeObject(STAGINGFILE, temp);
        } else {
            System.out.print("File does not exist.");
        }
    }

    /** Comment.
     * @param fileName is a thing. */
    public void remove(String fileName) {
        Staging temp = getStagingFile();

        if (getHeadCommit().getMetaData().containsKey(fileName)) {
            temp.commitRemove(fileName);
        } else {
            if (!temp.getStage().containsKey(fileName)) {
                System.out.print("No reason to remove the file.");
            } else {
                temp.stageRemove(fileName);
            }
        }

        writeObject(STAGINGFILE, temp);
    }

    /** Comment.
     * @param branch is a thing.*/
    public void rmBranch(String branch) {
        if (!getBranchFile().getBranches().containsKey(branch)) {
            System.out.print("A branch with that name does not exist.");
            return;
        } else if (branch.equals(active)) {
            System.out.print("Cannot remove the current branch.");
            return;
        } else {
            Branch temp = getBranchFile();
            temp.getBranches().remove(branch);
            writeObject(BRANCHFILE, temp);
        }
    }

    /** Comment.
     * @param branchName */
    public void branch(String branchName) {
        Branch temp = getBranchFile();

        if (temp.getBranches().containsKey(branchName)) {
            System.out.print("A branch with that name already exists.");
            return;
        }

        temp.branch(branchName, head);
        writeObject(BRANCHFILE, temp);
    }

    /** Comment.
     * @param message */
    public void commit(String message) {
        Commit temp = new Commit(message, head);
        temp.update();
        writeObject(join(COMMITDIR, temp.getName()), temp);

        this.head = temp.getName();

        Branch temp2 = getBranchFile();
        temp2.branch(active, head);
        writeObject(BRANCHFILE, temp2);
    }

    /** Comment.
     * @param mergeCommit is a thing.
     * @param message is a thing.
     * */
    public void mergeCommit(String message, String mergeCommit) {
        Commit temp = new Commit(message, head);
        temp.setMergeParent(mergeCommit);
        temp.update();
        writeObject(join(COMMITDIR, temp.getName()), temp);

        this.head = temp.getName();

        Branch temp2 = getBranchFile();
        temp2.branch(active, head);
        writeObject(BRANCHFILE, temp2);
    }

    /** Comment.
     * @param fileName is a thing.
     * */
    public void checkoutHead(String fileName) {
        String blobID = getHeadCommit().getMetaData().get(fileName);
        writeContents(new File(fileName), readContents(join(BLOBDIR, blobID)));
    }

    /** Comment.
     * @param commitId is a thing.
     * @param fileName is a thing.
     * */
    public void checkoutCommit(String commitId, String fileName) {
        if (commitId.length() == 8) {
            List<String> commitList = plainFilenamesIn(COMMITDIR);

            for (String commit : commitList) {
                if (commit.contains(commitId)) {
                    commitId = commit;
                }
            }
        }

        String memory = this.head;

        while (!commitId.equals(head)) {
            if (head == null) {
                this.head = memory;
                System.out.print("No commit with that id exists.");
                return;
            } else {
                this.head = getHeadCommit().getParent();
            }
        }

        if (getHeadCommit().getMetaData().containsKey(fileName)) {
            String blobID = getHeadCommit().getMetaData().get(fileName);
            byte[] id = readContents(join(BLOBDIR, blobID));
            writeContents(new File(fileName), id);
        } else {
            System.out.print("File does not exist in that commit.");
            this.head = memory;
            return;
        }

        this.head = memory;
    }

    /** Comment.
     * @param branch is a thing.
     * */
    public void checkoutBranch(String branch) {
        if (!getBranchFile().getBranches().containsKey(branch)) {
            System.out.print("No such branch exists.");
            return;
        }
        if (branch.equals(active)) {
            System.out.print("No need to checkout the current branch.");
            return;
        }

        String memory = this.head;
        boolean temp = true;

        List<String> x = plainFilenamesIn(System.getProperty("user.dir"));
        for (String file : x) {
            while (!(head == null)) {
                if (getHeadCommit().getMetaData().containsKey(file)) {
                    temp = false;
                }
                this.head = getHeadCommit().getParent();
            }
            if (temp) {
                Errors y = new Errors();
                System.out.print(y.getThing2());
                return;
            } else {
                this.head = memory;
            }
        }

        this.head = memory;


        String branchID = getBranchFile().getBranches().get(branch);

        Commit c = readObject(Utils.join(COMMITDIR, branchID), Commit.class);
        Commit tempCommit = getHeadCommit();

        TreeMap<String, String> commitMetaData = tempCommit.getMetaData();
        TreeMap<String, String> branchMetaData = c.getMetaData();

        for (Map.Entry<String, String> entry : commitMetaData.entrySet()) {
            String key = entry.getKey();
            restrictedDelete(new File(key));
        }

        for (Map.Entry<String, String> entry : branchMetaData.entrySet()) {
            String key = entry.getKey();
            byte[] h = readContents(join(BLOBDIR, branchMetaData.get(key)));
            writeContents(new File(key), h);
        }

        active = branch;
        head = branchID;
    }

    /** Comment. */
    public void log() {
        String memory = this.head;
        Formatter log = new Formatter();

        while (!(head == null)) {
            log.format("===%ncommit ");
            log.format(head);
            log.format("%nDate: ");
            log.format(getHeadCommit().getTimestamp());
            log.format("%n");
            log.format(getHeadCommit().getMessage());
            log.format("%n%n");

            this.head = getHeadCommit().getParent();
        }

        this.head = memory;
        System.out.print(log);
    }

    /** Comment. */
    public void globalLog() {
        List<String> commitList = plainFilenamesIn(COMMITDIR);
        Formatter globalLog = new Formatter();

        for (String commit : commitList) {
            Commit p = readObject(Utils.join(COMMITDIR, commit), Commit.class);


            globalLog.format("===%ncommit ");
            globalLog.format(commit);
            globalLog.format("%nDate: ");
            globalLog.format(p.getTimestamp());
            globalLog.format("%n");
            globalLog.format(p.getMessage());
            globalLog.format("%n%n");
        }

        System.out.print(globalLog);
    }

    /** Comment. */
    public void status() {
        Formatter status = new Formatter();

        status.format("=== Branches ===");
        for (String key : getBranchFile().getBranches().keySet()) {
            if (key.equals(active)) {
                status.format("%n" + "*" + key);
            }
        }
        for (String key : getBranchFile().getBranches().keySet()) {
            if (!key.equals(active)) {
                status.format("%n" + key);
            }
        }
        status.format("%n%n=== Staged Files ===");
        for (String key : getStagingFile().getStage().keySet()) {
            status.format("%n" + key);
        }

        status.format("%n%n=== Removed Files ===");
        for (String removed : getStagingFile().getRemove()) {
            status.format("%n" + removed);
        }

        status.format("%n%n=== Modifications Not Staged For Commit ===");

        status.format("%n%n=== Untracked Files ===%n");
        for (String untracked : getStagingFile().getUntracked()) {
            List<String> x = plainFilenamesIn(System.getProperty("user.dir"));
            for (String file : x) {
                if (file.equals(untracked)) {
                    status.format("%n" + untracked);
                }
            }
        }

        System.out.print(status);
    }

    /** Comment.
     * @param message is a thing.*/
    public void find(String message) {
        Formatter find = new Formatter();
        List<String> commitList = plainFilenamesIn(COMMITDIR);

        for (String commit : commitList) {
            Commit v = readObject(Utils.join(COMMITDIR, commit), Commit.class);

            if (v.getMessage().equals(message)) {
                find.format(v.getName() + "%n");
            }
        }
        if (find.toString().equals("")) {
            System.out.print("Found no commit with that message.");
        } else {
            System.out.print(find);
        }
    }

    /** Comment.
     * @param branch is a thing.*/
    public void merge(String branch) {
        if (branch.equals(active)) {
            System.out.print("Cannot merge a branch with itself.");
            return;
        }
        if (!(getBranchFile().getBranches().containsKey(branch))) {
            System.out.print("A branch with that name does not exist.");
            return;
        }
        List<String> f = plainFilenamesIn(System.getProperty("user.dir"));
        for (String file : f) {
            if (!getHeadCommit().getMetaData().containsKey(file)) {
                if (getStagingFile().getStage().containsKey(file)) {
                    System.out.print("You have uncommitted changes.");
                } else {
                    Errors g = new Errors();
                    System.out.print(g.getThing2());
                }
                return;
            }
        }

        if (findSplitPoint(branch).equals("error")) {
            return;
        } else {
            mergeHelper(branch);
        }
    }

    /** Comment.
     * @param branch is a thing.*/
    public void mergeHelper(String branch) {
        String s = findSplitPoint(branch);
        File i = Utils.join(COMMITDIR, s);
        Commit splitPoint = readObject(i, Commit.class);

        if (splitPoint.equals(getBranchFile().getBranches().get(active))) {
            System.out.print("Current branch fast-forwarded.");
            return;
        }

        Commit o = getHeadCommit();
        String bruh = getBranchFile().getBranches().get(branch);
        Commit t = readObject(Utils.join(COMMITDIR, bruh), Commit.class);

        for (Map.Entry<String, String> entry : o.getMetaData().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (splitPoint.getMetaData().containsKey(key)) {
                if (splitPoint.getMetaData().get(key).equals(value)) {
                    if (t.getMetaData().containsKey(key)) {
                        if (!t.getMetaData().get(key).equals(value)) {
                            File b = join(BLOBDIR, t.getMetaData().get(key));
                            byte[] c = readContents(b);
                            writeObject(new File(key), c);
                            this.add(key);
                        }
                    }
                }
            }
            if (splitPoint.getMetaData().containsKey(key)) {
                if (splitPoint.getMetaData().get(key).equals(value)) {
                    if (!(t.getMetaData().containsKey(key))) {
                        remove(key);
                    }
                }
            }
        }
        for (Map.Entry<String, String> entry : t.getMetaData().entrySet()) {
            String key = entry.getKey();
            if (!(splitPoint.getMetaData().containsKey(key))) {
                if (!(o.getMetaData().containsKey(key))) {
                    File k = join(BLOBDIR, t.getMetaData().get(key));
                    writeContents(new File(key), readContents(k));
                    this.add(key);
                }
            }
        }

        lilHelper(o, t, splitPoint);

        mergeCommit("Merged " + branch + " into " + active + ".", t.getName());
    }

    /** Comment.
     * @param o is a thing.
     * @param other is a thing.
     * @param splitPoint is a thing.*/
    public void lilHelper(Commit o, Commit other, Commit splitPoint) {
        for (Map.Entry<String, String> entry : o.getMetaData().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (other.getMetaData().containsKey(key)) {
                if (!(other.getMetaData().get(key).equals(value))) {
                    Formatter merged = new Formatter();
                    merged.format("<<<<<<< HEAD%n");
                    File l = join(BLOBDIR, o.getMetaData().get(key));
                    merged.format(readContentsAsString(l));
                    merged.format("=======%n");
                    File g = join(BLOBDIR, other.getMetaData().get(key));
                    merged.format(readContentsAsString(g));
                    merged.format(">>>>>>>%n");
                    String finalMerged = merged.toString();
                    writeContents(new File(key), finalMerged);
                    this.add(key);
                    System.out.print("Encountered a merge conflict.");
                }
            }
            if (!(other.getMetaData().containsKey(key))) {
                if (splitPoint.getMetaData().containsKey(key)) {
                    if (!(splitPoint.getMetaData().get(key).equals(value))) {
                        Formatter merged = new Formatter();
                        merged.format("<<<<<<< HEAD%n");
                        File b = join(BLOBDIR, o.getMetaData().get(key));
                        merged.format(readContentsAsString(b));
                        merged.format("=======%n");
                        merged.format(">>>>>>>%n");
                        String finalMerged = merged.toString();
                        writeContents(new File(key), finalMerged);
                        this.add(key);
                        System.out.print("Encountered a merge conflict.");
                    }
                }
            }
        }
    }


    /** Comment.
     * @param branch is a thing.
     * @return hello. */
    public String findSplitPoint(String branch) {
        String headMemory = this.head;
        String u = getBranchFile().getBranches().get(branch);
        File y = Utils.join(COMMITDIR, u);
        Commit branchCommit = readObject(y, Commit.class);

        while (!(head == null)) {
            if (head.equals(getBranchFile().getBranches().get(branch))) {
                Errors c = new Errors();
                System.out.print(c.getThing3());
                return ("error");
            }
            this.head = getHeadCommit().getParent();
        }
        this.head = headMemory;

        while (!(branchCommit.getParent() == null)) {
            if (branchCommit.getName().equals(head)) {
                System.out.print("Current branch fast-forwarded.");
                checkoutBranch(branch);
                return ("error");
            }
            File w = Utils.join(COMMITDIR, branchCommit.getParent());
            branchCommit = readObject(w, Commit.class);
        }
        String p = getBranchFile().getBranches().get(branch);
        File t = Utils.join(COMMITDIR, p);
        branchCommit = readObject(t, Commit.class);

        while (!(head == null)) {
            while (!(branchCommit.getParent() == null)) {
                if (head.equals(branchCommit.getParent())) {
                    String splitPoint = head;
                    this.head = headMemory;
                    return splitPoint;
                } else {
                    if (!(branchCommit.getMergeParent() == null)) {
                        String o = branchCommit.getMergeParent();
                        File a = Utils.join(COMMITDIR, o);
                        branchCommit = readObject(a, Commit.class);
                    } else {
                        String z = branchCommit.getParent();
                        File x = Utils.join(COMMITDIR, z);
                        branchCommit = readObject(x, Commit.class);
                    }
                }
            }
            String k = getBranchFile().getBranches().get(branch);
            File q = Utils.join(COMMITDIR, k);
            branchCommit = readObject(q, Commit.class);
            if (!(getHeadCommit().getMergeParent() == null)) {
                this.head = getHeadCommit().getMergeParent();
            } else {
                this.head = getHeadCommit().getParent();
            }
        }
        throw error("Bruh you messed up.");
    }

    /** Comment.
     * @param commitID is a thing.*/
    public void reset(String commitID) {
        List<String> commitList = plainFilenamesIn(COMMITDIR);
        boolean bool = true;
        for (String commit : commitList) {
            if (commit.equals(commitID)) {
                bool = false;
            }
        }
        if (bool) {
            System.out.print("No commit with that id exists.");
            return;
        }

        File c = Utils.join(COMMITDIR, commitID);
        Commit checkedCommit = readObject(c, Commit.class);
        Commit currentCommit = getHeadCommit();

        TreeMap<String, String> commitMetaData = currentCommit.getMetaData();
        TreeMap<String, String> checkedMetaData = checkedCommit.getMetaData();

        List<String> f = plainFilenamesIn(System.getProperty("user.dir"));
        for (String file : f) {
            if (!commitMetaData.containsKey(file)) {
                if (!getStagingFile().getStage().containsKey(file)) {
                    Errors b = new Errors();
                    System.out.print(b.getThing2());
                    return;
                }
            }
            if (getStagingFile().getStage().containsKey(file)) {
                restrictedDelete(new File(file));
            }
        }

        for (Map.Entry<String, String> entry : commitMetaData.entrySet()) {
            String key = entry.getKey();
            restrictedDelete(new File(key));
        }

        for (Map.Entry<String, String> entry : checkedMetaData.entrySet()) {
            String key = entry.getKey();
            File path = join(BLOBDIR, checkedMetaData.get(key));
            writeContents(new File(key), readContents(path));
        }

        Branch temp2 = getBranchFile();
        temp2.getBranches().put(active, commitID);
        writeObject(BRANCHFILE, temp2);

        Staging stage = getStagingFile();
        stage.clearStage();
        writeObject(STAGINGFILE, stage);

        this.head = commitID;
    }

}
