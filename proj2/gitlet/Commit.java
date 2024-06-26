package gitlet;



import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Repository.*;
import static gitlet.Utils.*;
import static gitlet.Utils.plainFilenamesIn;

/** Represents a gitlet commit object.
 *
 *  does at a high level.
 *
 *  @author Forsaken Delusion
 */
public class Commit implements Serializable {
    /**
     *
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    String id;
    String message;
    String parentCommit;
    String mergeCommit;
    String branch;
    String date;
    Map<File, Blob> blobs = new HashMap<>();
    SimpleDateFormat dateFormat = new SimpleDateFormat("E MMM dd HH:mm:ss yyyy Z", Locale.ENGLISH);

    Commit(String message) {
        this.message = message;
        this.date = dateFormat.format(new Date());
        this.branch = readContentsAsString(join(GITLET_REFERENCE, "HEAD"));
        this.parentCommit = getCurrentCommit().getId();
        this.mergeCommit = "null";
        blobs.putAll(getCommitById(this.parentCommit).getBlobs());
        blobs.putAll(Index.getCurrentIndex().getBlobSet());
        Map<File, Blob> deletBlobs = Index.getCurrentIndex().getDeleteBlobs();
        if (deletBlobs != null) {
            for (File file : deletBlobs.keySet()) {
                this.removeInBlobsDontSave(file);
            }
        }
        setID();
        saveCommit();
    }

    Commit(String message, String mergeCommit) {
        this.message = message;
        this.date = dateFormat.format(new Date());
        this.branch = readContentsAsString(join(GITLET_REFERENCE, "HEAD"));
        this.parentCommit = getCurrentCommit().getId();
        this.mergeCommit = mergeCommit;
        blobs.putAll(getCommitById(this.parentCommit).getBlobs());
        blobs.putAll(Index.getCurrentIndex().getBlobSet());
        Map<File, Blob> deletBlobs = Index.getCurrentIndex().getDeleteBlobs();
        for (File file : deletBlobs.keySet()) {
            this.removeInBlobsDontSave(file);
        }
        setID();
        saveCommit();
        updateHEAD(this);
        updateBranch(this.getBranch());
        Index.resetIndex();
    }

    /** Init Commit constructor */
    Commit() {
        this.message = "initial commit";
        this.date = dateFormat.format(new Date(0));
        this.branch = "master";
        this.parentCommit = "null";
        this.mergeCommit = "null";
        setID();
        saveCommit();
    }

    /** Get current commit */
    static Commit getCurrentCommit() {
        return getCommitById(readContentsAsString(GITLET_HEAD));
    }

    /** Get a Commit by ID */
    static Commit getCommitById(String id) {
        List<String> commitList = plainFilenamesIn(GITLET_COMMIT);
        if (commitList.contains(id)) {
            File curCommitFile = join(GITLET_COMMIT, id);
            return readObject(curCommitFile, Commit.class);
        }
        return null;
    }


    /** parentId getter */
    String getParentCommit() {
        return this.parentCommit;
    }

    String getMergeCommit() {
        return this.mergeCommit;
    }


    /** Branch getter */
    String getBranch() {
        return this.branch;
    }

    /** Save the Commit obj (Persistence) */
    void saveCommit() {
        writeObject(join(GITLET_COMMIT, getId()), this);
    }

    /** Id getter */
    String getId() {
        return this.id;
    }

    /** BlobArray getter */
    Map<File, Blob> getBlobs() {
        return this.blobs;
    }

    /** Set the commit id using sha1 */
    void setID() {
        List<Object> vals = new ArrayList<Object>();
        vals.add(this.message);
        vals.add(this.parentCommit);
        vals.add(this.mergeCommit);
        vals.add(this.date);
        vals.add(this.branch);
        for (Blob b : blobs.values()) {
            vals.add(b.toString());
        }
        this.id = sha1(vals);
    }

    /** update the HEAD to the given commit */
    static void updateHEAD(Commit commit) {
        writeContents(GITLET_HEAD, commit.getId());
    }

    String getMessage() {
        return this.message;
    }

    String getDate() {
        return this.date;
    }

/*
    */
/** Remove the same Hash obj in blobs *//*

    void removeInBlobSet(Blob blob) {
        String hashId = blob.getHashId();
        for (Blob b : blobs.values()) {
            if (b.getHashId().equals(hashId)) {
                blobs.remove(b);
            }
        }
        this.saveCommit();
    }

    */
/** Check for obj of the same hash in blobs *//*

    boolean containsSameHashBlob(Blob blob) {
        String hashId = blob.getHashId();
        for(Blob b : blobs.values()) {
            if(b.getHashId().equals(hashId)){
                return true;
            }
        }
        return false;
    }
*/

    void removeInBlobs(File file) {
        this.getBlobs().remove(file);
        this.saveCommit();
    }

    void removeInBlobsDontSave(File file) {
        this.getBlobs().remove(file);
    }

    /** Formatted output log */
    static void printCommit(Commit commit) {
        String curCommitID = commit.getId();
        String curCommitMessage = commit.getMessage();
        String curCommitDate = commit.getDate();
        String curMergeCommit = commit.getMergeCommit();
        System.out.println("===");
        System.out.println("commit " + curCommitID);
        if (!Objects.equals(curMergeCommit, "null")) {
            String parentCommit = commit.getParentCommit();
            System.out.println("Merge: " + parentCommit.substring(0, 7) + " " + curMergeCommit.substring(0, 7));
        }
        System.out.println("Date: " + curCommitDate);
        System.out.println(curCommitMessage);
        System.out.println();
    }

    /** The log command */
    static void log() {
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

    static List<String> getCommitList(String commitId) {
        Commit curCommit = getCommitById(commitId);
        List<String> commitList = new ArrayList<>();
        if (Objects.equals(curCommit.getMergeCommit(), "null")) {
            while (true) {
                commitList.add(curCommit.getId());
                // Check if the parent commit ID is a "null" string, and stop the loop if it is
                String parentCommitId = curCommit.getParentCommit();
                if ("null".equals(parentCommitId)) {
                    break;
                }
                curCommit = getCommitById(parentCommitId);
            }
        } else {
            commitList.add(curCommit.getId());
            // Check if the parent commit ID is a "null" string, and stop the loop if it is
            String parentCommitId = curCommit.getMergeCommit();
            curCommit = getCommitById(parentCommitId);
            while (true) {
                commitList.add(curCommit.getId());
                // Check if the parent commit ID is a "null" string, and stop the loop if it is
                parentCommitId = curCommit.getParentCommit();
                if ("null".equals(parentCommitId)) {
                    break;
                }
                curCommit = getCommitById(parentCommitId);
            }
        }

        return commitList;
    }

    /** The global-log command */
    static void globalLog() {
        List<String> files = plainFilenamesIn(GITLET_COMMIT);
        for (String filename : files) {
            printCommit(getCommitById(filename));
        }
    }

    static void find(String message) {
        List<String> commits = plainFilenamesIn(GITLET_COMMIT);
        int counter = 0;
        for (String filename : commits) {
            if (Objects.equals(getCommitById(filename).getMessage(), message)) {
                System.out.println(getCommitById(filename).getId());
                counter++;
            }
        }
        if (counter == 0) {
            System.out.println("Found no commit with that message.");
        }
    }

    static void updateBranch(String branchName) {
        writeContents(join(GITLET_REFERENCE, branchName), readContentsAsString(GITLET_HEAD));
    }

    public static String findCommit(String commitID) {
        String commit = null;
        List<String> allCommits = plainFilenamesIn(GITLET_COMMIT);
        for (String c : allCommits) {
            if (c.startsWith(commitID)) {
                commit = c;
                break;
            }
        }
        // If no commit with the given id exists.
        if (commit == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        return commit;
    }

}
