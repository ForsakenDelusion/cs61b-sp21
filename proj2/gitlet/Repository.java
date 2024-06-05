package gitlet;

import java.io.File;
import java.util.Date;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File GITLET_OBJECTS = join(GITLET_DIR, "objects");
    public static final File GITLET_REFERENCE = join(GITLET_DIR, "refs");
    public static final File GITLET_HEAD = join(GITLET_DIR, "HEAD");
    public static final File GITLET_INDEX = join(GITLET_DIR, "index");
    public static final File GITLET_COMMIT = join(GITLET_DIR, "commit");


    /* TODO: fill in the rest of this class. */
    /** init repo */
    static void init(){
        if(GITLET_OBJECTS.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return ;
        }
        GITLET_DIR.mkdir();
        GITLET_OBJECTS.mkdir();
        GITLET_REFERENCE.mkdir();
        GITLET_COMMIT.mkdir();
        writeContents(GITLET_HEAD, new Commit().getId());
        writeObject(GITLET_INDEX, new Index());
    }

    /** copy the work file into .gitlet/object dictionary and create an index */
    static void add(String fileName) {
            Index.getCurrentIndex().addInBlobArray(Blob.createBlob(fileName));
    }

    /** Create a new commit and update the HEAD reference */
    static void commit(String message) {
        if(message == null) {
            System.out.println("Please enter a commit message.");
            return;
        }
        if (!Index.getCurrentIndex().getBlobArray().isEmpty()) {
            Commit commit = new Commit(message);
            Commit.updateHEAD(commit);
            Index.resetIndex();
            return;
        }
        System.out.println("No changes added to the commit.");
    }

    /** The rm command */
    static void rm(String fileName) {
        Index curIndex = Index.getCurrentIndex();
        Blob curBlob = new Blob(fileName);
        Commit curCommit = Commit.getCurrentCommit();
        if(!curIndex.getBlobArray().isEmpty()) {
            curIndex.removeInBlobArray(curBlob);
        } else {
            if(curCommit.containsSameHashBlob(curBlob)) {
                curIndex.addInBlobArray(curBlob);
                curCommit.removeInBlobArray(curBlob);
                Utils.restrictedDelete(join(CWD,fileName));
            } else System.out.println("No reason to remove the file.");
        }

    }

    static void log(){
        Commit.log();
    }

    static void globalLog(){
        Commit.globalLog();
    }

}
