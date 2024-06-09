package gitlet;

import java.io.File;
import java.util.*;

import static gitlet.Utils.*;



/** Represents a gitlet repository.
 *
 *  does at a high level.
 *
 *  @author Forsaken Delusion
 */
public class Repository {
    /**
     *
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



    /** init repo */
    static void init() {
        if (GITLET_OBJECTS.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }
        GITLET_DIR.mkdir();
        GITLET_OBJECTS.mkdir();
        GITLET_REFERENCE.mkdir();
        GITLET_COMMIT.mkdir();
        writeContents(GITLET_HEAD, new Commit().getId());
        writeContents(join(GITLET_REFERENCE, "master"), readContentsAsString(GITLET_HEAD));
        writeObject(GITLET_INDEX, new Index());
        writeContents(join(GITLET_REFERENCE, "HEAD"), "master");
    }

    /** copy the work file into .gitlet/object dictionary and create an index */
    static void add(String fileName) {
        File curFile = new File(CWD, fileName);
        Index curIndex = Index.getCurrentIndex();
        Blob curBlob = Blob.createBlob(fileName);
        Blob commitBlob = Commit.getCurrentCommit().getBlobs().get(curFile);
        if (!curFile.exists()) {
            System.out.println("File does not exist.");
        } else {
            if (curIndex.getDeleteBlobs().containsKey(curFile)) {
                curIndex.removeInDeleteBlobSet(curFile);
            } else {
                if (commitBlob != null && !Objects.equals(curBlob.getHashId(), commitBlob.getHashId())) {
                    curIndex.addInBlobSet(curFile, Blob.createBlob(fileName));
                } else if (commitBlob == null) {
                    curIndex.addInBlobSet(curFile, Blob.createBlob(fileName));
                }
            }

        }
    }

    /** Create a new commit and update the HEAD reference */
    static void commit(String message) {
        if (message.isEmpty()) {
            System.out.println("Please enter a commit message.");
        } else {
            Index curIndex = Index.getCurrentIndex();
            boolean indexBlobIsEmpty = curIndex.getBlobSet().isEmpty();
            boolean indexDeletedBlob = curIndex.getDeleteBlobs().isEmpty();
            if (!indexBlobIsEmpty || !indexDeletedBlob) {
                Commit commit = new Commit(message);
                Commit.updateHEAD(commit);
                Commit.updateBranch(commit.getBranch());
                Index.resetIndex();
            } else {
                System.out.println("No changes added to the commit.");
            }
        }


    }

    /** The rm command */
    static void rm(String fileName) {
        Index curIndex = Index.getCurrentIndex();
        File curFile = new File(CWD, fileName);
        Commit curCommit = Commit.getCurrentCommit();
        if (curFile.exists()) {
            Blob curBlob = new Blob(fileName);
            if (!curIndex.getBlobSet().isEmpty()) {
                curIndex.removeInBlobSet(curFile);
            } else {
                if (curCommit.getBlobs().containsKey(curFile)) {
                    curIndex.removeInBlobSet(curFile);
                    curIndex.addInDeleteBlobSet(curFile, curBlob);
                    restrictedDelete(join(CWD, fileName));
                } else {
                    System.out.println("No reason to remove the file.");
                }
            }
        } else {
            curIndex.addInDeleteBlobSet(curFile, curCommit.getBlobs().get(curFile));
        }
    }

    static void log() {
        Commit.log();
    }

    static void globalLog() {
        Commit.globalLog();
    }

    static void find(String commitMessage) {
        Commit.find(commitMessage);
    }

    static void checkout(String[] args) {
        if (args.length == 2) {
            String branchName = args[1];
            File branchFile = join(GITLET_REFERENCE, branchName);
            if (!branchFile.exists()) {
                System.out.println("No such branch exists.");
            } else {
                if (Objects.equals(branchName, readContentsAsString(join(GITLET_REFERENCE, "HEAD")))) {
                    System.out.println("No need to checkout the current branch.");
                } else {
                    String commitId = readContentsAsString(join(GITLET_REFERENCE, branchName));
                    checkoutById(commitId);
                    writeContents(join(GITLET_REFERENCE, "HEAD"), branchName);
                    writeContents(GITLET_HEAD, commitId);
                }
            }
        } else if (args.length == 3 && Objects.equals(args[1], "--")) {
            String fileName = args[2];
            File curFile = new File(CWD, fileName);
            Commit curCommit = Commit.getCurrentCommit();
            Map<File, Blob> curBlobs = curCommit.getBlobs();
            if (curBlobs.containsKey(curFile)) {
                writeContents(join(curFile), curBlobs.get(curFile).getContent());
            } else {
                System.out.println("File does not exist in that commit.");
            }
        } else if (args.length == 4 && args[2].equals("--")) {
            String curCommitId = args[1];
            String curFileName = args[3];
            File curCommitObj = new File(GITLET_COMMIT, curCommitId);
            File curFile = new File(CWD, curFileName);
            if (curCommitObj.exists()) {
                Commit curCommit = Commit.getCommitById(curCommitId);
                Map<File, Blob> curBlobs = curCommit.getBlobs();
                if (curBlobs.containsKey(curFile)) {
                    writeContents(join(curFile), curBlobs.get(curFile).getContent());
                } else {
                    System.out.println("File does not exist in that commit.");
                }
            } else {
                System.out.println("No commit with that id exists.");
            }
        } else {
            System.out.println("Incorrect operands.");
        }

    }

    static void status() {
        String curBranch = readContentsAsString(join(GITLET_REFERENCE, "HEAD"));
        List<String> branchList = plainFilenamesIn(GITLET_REFERENCE);
        Index curIndex = Index.getCurrentIndex();
        Map<File, Blob> curStagedFiles = curIndex.getBlobSet();
        Map<File, Blob> removeFiles = curIndex.getDeleteBlobs();
        Commit curCommit = Commit.getCurrentCommit();
        Map<File, Blob> curCommitStagedBlobs = curCommit.getBlobs();
        List<String> cwdFiles = plainFilenamesIn(CWD);

        System.out.println("=== Branches ===");
        System.out.println("*" + curBranch);
        for (String branch : branchList) {
            if (!Objects.equals(branch, curBranch) && !branch.equals("HEAD")) {
                System.out.println(branch);
            }
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        for (File file : curStagedFiles.keySet()) {
            System.out.println(file.getName());
        }

        System.out.println();
        System.out.println("=== Removed Files ===");
        for (File file : removeFiles.keySet()) {
            System.out.println(file.getName());
        }


        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        for (Map.Entry<File, Blob> entry : curCommitStagedBlobs.entrySet()) {
            String fileName = entry.getKey().getName();
            File curFile = entry.getKey();
            Blob fileBlob = entry.getValue();
            Blob cwdBlob = Blob.createBlob(fileName);
            Map<File, Blob> indexBlobs = curIndex.getBlobSet();
            if (cwdFiles != null && cwdBlob != null) {
                if (!cwdFiles.contains(fileName) && !curStagedFiles.containsKey(curFile)) {
                    System.out.println(fileName + " (deleted)");
                } else if (!Objects.equals(cwdBlob.getHashId(), fileBlob.getHashId())) {
                    System.out.println(fileName + " (modified)");
                    if (indexBlobs.containsKey(curFile) && !indexBlobs.get(curFile).equals(cwdBlob)) {
                        System.out.println(fileName + " (modified)");
                    }
                }
            } else if (cwdBlob == null && !removeFiles.containsKey(curFile)) {
                System.out.println(fileName + " (deleted)");
            }

        }

        System.out.println();
        System.out.println("=== Untracked Files ===");
        Set<String> untrackedFiles = untrackedFiles();
        for (String fileName : untrackedFiles) {
            System.out.println(fileName);
        }
        System.out.println();
    }

    public static void branch(String branchName) {
        File branchFile = new File(GITLET_REFERENCE, branchName);
        if (branchFile.exists()) {
            System.out.println("A branch with that name already exists.");
        } else {
            writeContents(join(GITLET_REFERENCE, branchName), readContentsAsString(GITLET_HEAD));
        }
    }

    public static void rmBranch(String deleteBranch) {
        File deleteBranchFile = new File(GITLET_REFERENCE, deleteBranch);
        if (!deleteBranchFile.exists()) {
            System.out.println("A branch with that name does not exist.");
        } else {
            if (readContentsAsString(join(GITLET_REFERENCE, "HEAD")).equals(deleteBranch)) {
                System.out.println("Cannot remove the current branch.");
            } else {
                deleteBranchFile.delete();
            }
        }
    }

    static Set<File> trackedFiles() {
        Set<File> trackedFileSet = new HashSet<>();
        Commit tempCommit = Commit.getCurrentCommit();
        while (true) {
            trackedFileSet.addAll(tempCommit.getBlobs().keySet());
            String parentCommitId = tempCommit.getParentCommit();
            if ("null".equals(parentCommitId)) {
                break;
            }
            tempCommit = Commit.getCommitById(parentCommitId);
        }
        return trackedFileSet;
    }

    static Set<String> untrackedFiles() {
        Set<String> untrackedFileSet = new HashSet<>();
        Set<File> trackedFileSet = trackedFiles();
        List<String> cwdFiles = plainFilenamesIn(CWD);
        Index curIndex = Index.getCurrentIndex();
        Map<File, Blob> curStagedFiles = curIndex.getBlobSet();
        if (cwdFiles != null) {
            for (String fileName : cwdFiles) {
                File curFile = join(CWD, fileName);
                if (!curStagedFiles.containsKey(curFile) && !trackedFileSet.contains(curFile)) {
                    untrackedFileSet.add(fileName);
                }
            }
        }
        return untrackedFileSet;
    }

    static void reset(String commitId) {
        checkoutById(commitId);
        writeContents(join(GITLET_REFERENCE, readContentsAsString(join(GITLET_REFERENCE, "HEAD"))), commitId);
        writeContents(GITLET_HEAD, commitId);
    }

    static void checkoutById(String commitId) {
        Index.resetIndex();
        Commit curCommit = Commit.getCommitById(commitId);
        Map<File, Blob> curBlobs = curCommit.getBlobs();
        Set<String> unTrackedFiles = untrackedFiles();
        Set<File> trackedFiles = trackedFiles();
        for (Blob curBlob : curBlobs.values()) {
            if (unTrackedFiles.contains(curBlob.getFileName())) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
            } else {
                writeContents(join(CWD, curBlob.getFileName()), curBlob.getContent());
            }
        }
        for (File trackedFile : trackedFiles) {
            if (!curBlobs.containsKey(trackedFile)) {
                restrictedDelete(trackedFile);
            }
        }
    }
}
