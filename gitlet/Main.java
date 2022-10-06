package gitlet;
import static gitlet.Utils.writeObject;
import static gitlet.Utils.*;


/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author William Webster
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        Errors bruh = new Errors();
        if (args.length == 0) {
            System.out.print("Please enter a command.");
            return;
        }
        if (REPOOBJ.exists()) {
            Repo temp = readObject(REPOOBJ, Repo.class);
            if (args[0].equals("add")) {
                temp.add(args[1]);
            } else if (args[0].equals("rm")) {
                temp.remove(args[1]);
            } else if (args[0].equals("rm-branch")) {
                temp.rmBranch(args[1]);
            } else if (args[0].equals("commit")) {
                if (args[1].equals("")) {
                    System.out.print("Please enter a commit message.");
                } else {
                    temp.commit(args[1]);
                }
            } else if (args[0].equals("checkout")) {
                if (args.length == 3) {
                    if (!args[1].equals("--")) {
                        System.out.print("Incorrect operands.");
                    }
                    temp.checkoutHead(args[2]);
                } else if (args.length == 4) {
                    if (!args[2].equals("--")) {
                        System.out.print("Incorrect operands.");
                    }
                    temp.checkoutCommit(args[1], args[3]);
                } else if (args.length == 2) {
                    temp.checkoutBranch(args[1]);
                }
            } else if (args[0].equals("log")) {
                temp.log();
            } else if (args[0].equals("global-log")) {
                temp.globalLog();
            } else if (args[0].equals("branch")) {
                temp.branch(args[1]);
            } else if (args[0].equals("status")) {
                temp.status();
            } else if (args[0].equals("find")) {
                temp.find(args[1]);
            } else if (args[0].equals("merge")) {
                temp.merge(args[1]);
            } else if (args[0].equals("reset")) {
                temp.reset(args[1]);
            } else if (args[0].equals("init")) {
                System.out.print(bruh.getThing());
            } else {
                System.out.print("No command with that name exists.");
            }
            writeObject(REPOOBJ, temp);
        } else if (args[0].equals("init")) {
            initializeRepo();
        } else if (args[0].equals("status")) {
            System.out.print("Not in an initialized Gitlet directory.");
        }
    }

    /** Initialize repo. */
    public static void initializeRepo() {
        Repo temp = new Repo();
        temp.init();
        writeObject(REPOOBJ, temp);
    }

}
