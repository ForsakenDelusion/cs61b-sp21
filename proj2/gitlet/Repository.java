package gitlet;

import java.io.File;
import java.util.*;

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
        writeContents(join(GITLET_REFERENCE,"master"), GITLET_HEAD);
        writeObject(GITLET_INDEX, new Index());
        writeContents(join(GITLET_REFERENCE,"HEAD"),"master");
    }

    /** copy the work file into .gitlet/object dictionary and create an index */
    static void add(String fileName) {
        File curFile = new File(CWD, fileName);
        Index.getCurrentIndex().addInBlobSet(curFile,Blob.createBlob(fileName));
    }

    /** Create a new commit and update the HEAD reference */
    static void commit(String message) {
        if(message == null) {
            System.out.println("Please enter a commit message.");
            return;
        }
        if (!Index.getCurrentIndex().getBlobSet().isEmpty()) {
            Commit commit = new Commit(message);
            Commit.updateHEAD(commit);
            Commit.updateBranch();
            Index.resetIndex();
            return;
        }
        System.out.println("No changes added to the commit.");
    }

    /** The rm command */
    static void rm(String fileName) {
        Index curIndex = Index.getCurrentIndex();
        Blob curBlob = new Blob(fileName);
        File curFile = new File(CWD, fileName);
        Commit curCommit = Commit.getCurrentCommit();
        if(!curIndex.getBlobSet().isEmpty()) {
            curIndex.removeInBlobSet(curBlob);
        } else {
            if(curCommit.containsSameHashBlob(curBlob)) {
                curIndex.addInBlobSet(curFile,curBlob);
                curCommit.removeInBlobSet(curBlob);
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

    static void find(String commitMessage) {
        Commit.find(commitMessage);
    }

    static void checkout(String[] args) {
        if(args.length == 2) {
            return;
        } else if (args.length == 3) {
            String fileName = args[2];
            File curFile = new File(CWD, fileName);
            Commit curCommit = Commit.getCurrentCommit();
            Map<File,Blob> curBlobs = curCommit.getBlobs();
            if(curBlobs.containsKey(curFile)) {
                writeContents(join(curFile),curBlobs.get(curFile).getContent());
                return;
            } else System.out.println("File does not exist in that commit.");
        } else if (args.length == 4) {
            String curCommitId = args[1];
            String curFileName = args[3];
            File curCommitObj = new File(GITLET_COMMIT, curCommitId);
            File curFile = new File(CWD, curFileName);
            if(curCommitObj.exists()) {
                Commit curCommit = Commit.getCommitById(curCommitId);
                Map<File,Blob> curBlobs = curCommit.getBlobs();
                if(curBlobs.containsKey(curFile)) {
                    writeContents(join(curFile),curBlobs.get(curFile).getContent());
                }
            } else System.out.println("File does not exist in that commit.");


        }

    }

    static void status(){
        Commit curCommit = Commit.getCurrentCommit();
        Index index = Index.getCurrentIndex();
        String branch = curCommit.getBranch();
        Map<File,Blob> stagingFiles = Index.getCurrentIndex().getBlobSet();


        System.out.println("=== Branches ===");
        System.out.println(branch);
        System.out.println();
        System.out.println("=== Staged Files ===");


    }

    public static void branch(String branchName) {
        File branchFile = new File(CWD, branchName);
        if(branchFile.exists()) {
            System.out.println("A branch with that name already exists.");
        } else {
            writeContents(join(GITLET_REFERENCE,branchName), GITLET_HEAD);
            writeContents(join(GITLET_REFERENCE,"HEAD"),branchName);
        }
    }

    public static void rmBranch(String deleteBranch) {
        File deleteBranchFile = new File(GITLET_REFERENCE, deleteBranch);
        if(deleteBranchFile.exists()) {
            if (readContentsAsString(join(GITLET_REFERENCE, "HEAD")).equals(deleteBranch)){
                System.out.println("Cannot remove the current branch.");
            }
            restrictedDelete(join(GITLET_REFERENCE,deleteBranch));
            return;
        }
        System.out.println("A branch with that name does not exist.");
    }
}
