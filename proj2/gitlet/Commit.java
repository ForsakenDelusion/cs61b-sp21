package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static gitlet.Repository.GITLET_DIR;
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
    String id;
    String message;
    String parentCommit;
    String branch;
    String date;
    Map<String, Blob> Blobs = new HashMap<>();
    /* TODO: fill in the rest of this class. */

    Commit(String message){
        this.message = message;
        this.date = new Date().toString();
        this.branch = getCurrentCommit().getBranch();
        this.parentCommit = getCurrentCommit().getId();
    }

    Commit(){
        this.date = "00:00:00 UTC, Thursday, 1 January 1970";
        this.branch = "master";
        this.parentCommit = null;
    }

    /** Get current commit */
    static Commit getCurrentCommit() {
        return readObject(join(GITLET_DIR,"HEAD"),Commit.class);
    }

    /** parentId getter */
    String getParentCommit(){
        return this.parentCommit;
    }

    /** Id getter */
    String getId(){
        return this.id;
    }

    /** Branch getter */
    String getBranch(){
        return this.branch;
    }



}