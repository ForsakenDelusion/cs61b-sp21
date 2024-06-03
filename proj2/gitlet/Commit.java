package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.HashMap;
import java.util.Map;

import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    private Map<File, Blob> Blobs = new HashMap<File, Blob>();
    private Commit parentCommit;
    private Date commitTime;

    /* TODO: fill in the rest of this class. */
    public Commit(String message){
        this.message = message;
        this.commitTime = new Date();
        this.parentCommit = getCurrentCommit();
    }

    public Commit(){
        this.commitTime = new Date();
        this.parentCommit = null;
    }

    public static void addNewCommit(String message){
        Commit curCommit = fromFile();
        curCommit.message = message;
        curCommit.commitTime = new Date();
        curCommit.parentCommit = getCurrentCommit();
        writeObject(Repository.GITLET_COMMIT,curCommit);
    }

    private static Commit getCurrentCommit() {
        return readObject(Repository.GITLET_COMMIT,Commit.class);
    }

    private static Commit fromFile(){
        return readObject(Repository.GITLET_COMMIT, Commit.class);
    }
}
