package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Repository.*;
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
    ArrayList<Blob> blobs = new ArrayList<>();
    /* TODO: fill in the rest of this class. */
    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z");

    Commit(String message) {
        this.message = message;
        this.date = dateFormat.format(new Date());
        this.branch = getCurrentCommit().getBranch();
        this.parentCommit = getCurrentCommit().getId();
        blobs.addAll(Index.getCurrentIndex().getBlobArray());
        setID();
        saveCommit();
    }

    /** Init Commit constructor */
    Commit() {
        this.message = "initial commit";
        this.date = dateFormat.format(new Date(0));
        this.branch = "master";
        this.parentCommit = "null";
        setID();
        saveCommit();
    }

    /** Get current commit */
    static Commit getCurrentCommit() {
        return getCommitById(readContentsAsString(GITLET_HEAD));
    }

    /** Get a Commit by ID */
    static Commit getCommitById(String id) {
        File curCommitFile = join(GITLET_COMMIT,id);
        return readObject(curCommitFile,Commit.class);
    }

    /** parentId getter */
    String getParentCommit() {
        return this.parentCommit;
    }


    /** Branch getter */
    String getBranch() {
        return this.branch;
    }

    /** Save the Commit obj (Persistence) */
    void saveCommit(){
        writeObject(join(GITLET_COMMIT,getId()),this);
    }

    /** Id getter */
    String getId() {
        return this.id;
    }

    /** BlobArray getter */
    List<Blob> getBlobs() {
        return this.blobs;
    }

    /** Set the commit id using sha1 */
    void setID() {
        List<Object> vals = new ArrayList<Object>();
        vals.add(this.message);
        vals.add(this.parentCommit);
        vals.add(this.date);
        vals.add(this.branch);
        for(Blob b : blobs) {
            vals.add(b.toString());
        }
        this.id = sha1(vals);
    }

    /** update the HEAD to the given commit */
    static void updateHEAD(Commit commit) {
        writeContents(GITLET_HEAD,commit.getId());
    }

    String getMessage() {
        return this.message;
    }

    String getDate() {
        return this.date;
    }

    /** Remove the same Hash obj in blobs */
    void removeInBlobArray(Blob blob) {
        String hashId = blob.getHashId();
        this.getBlobs().removeIf(b -> b.getHashId().equals(hashId));
        this.saveCommit();
    }

    /** Check for obj of the same hash in blobs */
    boolean containsSameHashBlob(Blob blob) {
        String hashId = blob.getHashId();
        for(Blob b : blobs) {
            if(b.getHashId().equals(hashId)){
                return true;
            }
        }
        return false;
    }

    static void printCommit(Commit commit) {
        String curCommitID = commit.getId();
        String curCommitMessage = commit.getMessage();
        String curCommitDate = commit.getDate();
        System.out.println("===");
        System.out.println("commit " + curCommitID);
        System.out.println("Date: " + curCommitDate);
        System.out.println(curCommitMessage);
        System.out.println();
    }

    /** The log command */
    static void log(){
        Commit tempCommit = getCurrentCommit();
        while (true) {
            printCommit(tempCommit);
            // Check if the parent commit ID is a "null" string, and stop the loop if it is
            String parentCommitId = tempCommit.getParentCommit();
            if ("null".equals(parentCommitId)) {
                break;
            }
            // loop
            tempCommit = getCommitById(parentCommitId);
        }
    }

    static void globalLog(){
        List<String> files = plainFilenamesIn(GITLET_COMMIT);
        for (String filename : files) {
            printCommit(getCommitById(filename));
        }
    }
}