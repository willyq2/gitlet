package gitlet;

/** Comment.
 *  @author William Webster
 */
public class Errors {
    /** thing. */
    private static String thing;
    /** thing. */
    private static String thing2;
    /** thing. */
    private static String thing3;

    /** thing. */
    Errors() {
        this.thing = "A Gitlet version-control system "
                +
                "already exists in the current directory.";
        this.thing2 = "There is an untracked file in the "
                +
                "way; delete it, or add and commit it first.";
        this.thing3 = "Given branch is an ancestor "
                +
                "of the current branch.";
    }

    /** Comment.
     *  @return  Thing.*/
    public String getThing() {
        return thing;
    }
    /** Comment.
     *  @return  Thing.*/
    public String getThing2() {
        return thing2;
    }
    /** Comment.
     *  @return  Thing.*/
    public String getThing3() {
        return thing3;
    }

}
