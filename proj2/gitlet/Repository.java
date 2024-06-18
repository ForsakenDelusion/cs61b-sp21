package gitlet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
                    curIndex.addInBlobSet(curFile, curBlob);
                } else if (commitBlob == null) {
                    curIndex.addInBlobSet(curFile, curBlob);
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
        Blob curBlob = Blob.createBlob(fileName);
        Commit curCommit = Commit.getCurrentCommit();
        Map<File, Blob> stageBlobs = curIndex.getBlobSet();
        Map<File, Blob> trackedBlobs = curCommit.getBlobs();
        if (stageBlobs.containsKey(curFile)) {
            curIndex.removeInBlobSet(curFile);
        } else if (trackedBlobs.containsKey(curFile) && !stageBlobs.containsKey(curFile)) {
            curIndex.addInDeleteBlobSet(curFile, curBlob);

            curFile.delete();
        } else if (!stageBlobs.containsKey(curFile) && !trackedBlobs.containsKey(curFile)) {
            System.out.println("No reason to remove the file.");
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
            String findCommitById = Commit.findCommit(curCommitId);
            File curFile = new File(CWD, curFileName);
            Commit curCommit = Commit.getCommitById(findCommitById);
            Map<File, Blob> curBlobs = curCommit.getBlobs();
            if (curBlobs.containsKey(curFile)) {
                writeContents(join(curFile), curBlobs.get(curFile).getContent());
            } else {
                System.out.println("File does not exist in that commit.");
            }

        } else {
            System.out.println("Incorrect operands.");
        }

    }

    static void status() {
        if (!(new File(CWD, ".gitlet")).isDirectory()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
        String curBranch = readContentsAsString(join(GITLET_REFERENCE, "HEAD"));
        List<String> branchList = plainFilenamesIn(GITLET_REFERENCE);
        Index curIndex = Index.getCurrentIndex();
        Map<File, Blob> curStagedFiles = curIndex.getBlobSet();
        Map<File, Blob> removeFiles = curIndex.getDeleteBlobs();
        Commit curCommit = Commit.getCurrentCommit();
        Set<File> curCommitStagedBlobs = trackedFiles();
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
        for (File File: curCommitStagedBlobs) {
            String fileName = File.getName();
            File curFile = File;
            Blob fileBlob = Commit.getCurrentCommit().getBlobs().get(File);
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
        return tempCommit.getBlobs().keySet();
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
        if (checkoutById(commitId)) {
        writeContents(join(GITLET_REFERENCE, readContentsAsString(join(GITLET_REFERENCE, "HEAD"))), commitId);
        writeContents(GITLET_HEAD, commitId);
        }
    }

    static boolean checkoutById(String commitId) {
        Index.resetIndex();
        Commit curCommit = Commit.getCommitById(commitId);
        if (curCommit == null) {
            System.out.println("No commit with that id exists.");
            return false;
        }
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
        return true;
    }

    public static void merge(String branch) {
        File branchFile = new File(GITLET_REFERENCE, branch);
        if (!branchFile.exists()) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        boolean flag = false;
        String branchId = readContentsAsString(join(GITLET_REFERENCE, branch));
        List<String> curCommitList = getCurBranchCommitIdList();
        List<String> givenCommitList = getGivenBranchCommitIdList(branch);
        Commit splitCommit = null;
        Commit curCommit = Commit.getCurrentCommit();
        Commit givenCommit = Commit.getCommitById(readContentsAsString(join(GITLET_REFERENCE, branch)));
        Map<File, Blob> curCommitMap = curCommit.getBlobs();
        Map<File, Blob> givenCommitMap = givenCommit.getBlobs();
        String splitCommitId;
        Set<String> untrackedFiles = untrackedFiles();
        if (!readObject(GITLET_INDEX, Index.class).blobs.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            return;
        }
        else if (Objects.equals(branch, readContentsAsString(join(GITLET_REFERENCE, "HEAD")))){
            System.out.println("Cannot merge a branch with itself.");
            return;
        } else if (givenCommitList.contains(curCommitList.get(0))) {
            System.out.println("Current branch fast-forwarded.");
            return;
        } else if (curCommitList.contains(givenCommitList.get(0))) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }


        for (String curCommitId : curCommitList) {
            if (givenCommitList.contains(curCommitId)) {
                splitCommitId = curCommitId;
                splitCommit = Commit.getCommitById(splitCommitId);
                break;
            }
        }
        Map<File, Blob> splitCommitMap = null;
        if (splitCommit != null) {
            splitCommitMap = splitCommit.getBlobs();
        }

        if (splitCommitMap != null) {
            for (File splitCommitFile : splitCommitMap.keySet()) {
                Blob splitCommitBlob = null;
                Blob curCommitBlob = null;
                Blob givenCommitBlob = null;
                splitCommitBlob = splitCommitMap.get(splitCommitFile);
                if (curCommitMap.containsKey(splitCommitFile)) {
                    curCommitBlob = curCommitMap.get(splitCommitFile);
                }
                if (givenCommitMap.containsKey(splitCommitFile)) {
                    givenCommitBlob = givenCommitMap.get(splitCommitFile);
                }

                if (curCommitBlob != null && areBlobsEqual(curCommitBlob, splitCommitBlob)) {
                    if (givenCommitBlob != null && !areBlobsEqual(givenCommitBlob, splitCommitBlob)) {
                        if (untrackedFiles.contains(splitCommitFile.getName())) {
                            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                            return;
                        }
                        writeContents(splitCommitFile, givenCommitBlob.getContent());
                        add(splitCommitFile.getName());
                        curCommitMap.remove(splitCommitFile);
                        givenCommitMap.remove(splitCommitFile);
                    } else if (givenCommitBlob == null && areBlobsEqual(curCommitBlob, splitCommitBlob)) {
                        if (untrackedFiles.contains(splitCommitFile.getName())) {
                            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                            return;
                        }
                        rm(splitCommitFile.getName());
                        curCommitMap.remove(splitCommitFile);
                        givenCommitMap.remove(splitCommitFile);
                    }
                } else if(givenCommitBlob !=null && !areBlobsEqual(givenCommitBlob, splitCommitBlob)) {
                    if (curCommitBlob != null && areBlobsEqual(curCommitBlob, splitCommitBlob)) {
                        if (untrackedFiles.contains(splitCommitFile.getName())) {
                            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                            return;
                        }
                        writeContents(splitCommitFile, curCommitBlob.getContent());
                        add(splitCommitFile.getName());
                        curCommitMap.remove(splitCommitFile);
                        givenCommitMap.remove(splitCommitFile);
                    } else if (curCommitBlob == null && areBlobsEqual(givenCommitBlob, splitCommitBlob)) {
                        if (untrackedFiles.contains(splitCommitFile.getName())) {
                            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                            return;
                        }
                        rm(splitCommitFile.getName());
                        curCommitMap.remove(splitCommitFile);
                        givenCommitMap.remove(splitCommitFile);
                    }
                }

                if (!areBlobsEqual(givenCommitBlob, splitCommitBlob) && !areBlobsEqual(curCommitBlob, splitCommitBlob) && !areBlobsEqual(givenCommitBlob, curCommitBlob)) {
                    String newContent = getString(curCommitBlob, givenCommitBlob);
                    writeContents(splitCommitFile, newContent);
                    add(splitCommitFile.getName());
                    flag = true;
                    curCommitMap.remove(splitCommitFile);
                    givenCommitMap.remove(splitCommitFile);
                }

                if (areBlobsEqual(givenCommitBlob, curCommitBlob)) {
                    curCommitMap.remove(splitCommitFile);
                    givenCommitMap.remove(splitCommitFile);
                }

                if (curCommitBlob == null && areBlobsEqual(givenCommitBlob, splitCommitBlob)) {
                    curCommitMap.remove(splitCommitFile);
                    givenCommitMap.remove(splitCommitFile);
                }
            }

        }

        for (File curCommitFile : curCommitMap.keySet()) {
            Blob splitCommitBlob = null;
            Blob curCommitBlob = null;
            Blob givenCommitBlob = null;
            curCommitBlob = curCommitMap.get(curCommitFile);
            if (splitCommitMap != null) {
                splitCommitBlob = splitCommitMap.get(curCommitFile);
            }
            givenCommitBlob = givenCommitMap.get(curCommitFile);
            if (splitCommitBlob == null && givenCommitBlob == null && curCommitBlob != null) {
                add(curCommitFile.getName());
                curCommitMap.remove(curCommitFile);
                givenCommitMap.remove(curCommitFile);
            }

            if (!areBlobsEqual(givenCommitBlob, splitCommitBlob) && !areBlobsEqual(curCommitBlob, splitCommitBlob) && !areBlobsEqual(givenCommitBlob, curCommitBlob)) {
                String newContent = getString(curCommitBlob, givenCommitBlob);
                writeContents(curCommitFile, newContent);
                add(curCommitFile.getName());
                flag = true;
                curCommitMap.remove(curCommitFile);
                givenCommitMap.remove(curCommitFile);
            }
        }

        for (File givenCommitFile : givenCommitMap.keySet()) {
            Blob splitCommitBlob = null;
            Blob curCommitBlob = null;
            Blob givenCommitBlob = null;
            givenCommitBlob = givenCommitMap.get(givenCommitFile);
            if (splitCommitMap != null) {
                splitCommitBlob = splitCommitMap.get(givenCommitFile);
            }
            curCommitBlob = curCommitMap.get(givenCommitFile);
            if (splitCommitBlob == null && givenCommitBlob != null && curCommitBlob == null) {
                if (untrackedFiles.contains(givenCommitFile.getName())) {
                    System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                    return;
                }
                checkout(new String[]{"checkout", readContentsAsString(join(GITLET_REFERENCE,branch)),"--",givenCommitFile.getName()});
                add(givenCommitFile.getName());
                curCommitMap.remove(givenCommitFile);
                givenCommitMap.remove(givenCommitFile);
            }

            if (!areBlobsEqual(givenCommitBlob, splitCommitBlob) && !areBlobsEqual(curCommitBlob, splitCommitBlob) && !areBlobsEqual(givenCommitBlob, curCommitBlob)) {
                String newContent = getString(curCommitBlob, givenCommitBlob);
                writeContents(givenCommitFile, newContent);
                add(givenCommitFile.getName());
                flag = true;
                curCommitMap.remove(givenCommitFile);
                givenCommitMap.remove(givenCommitFile);
            }
        }



        if (flag) {
            System.out.println("Encountered a merge conflict.");
        }
        new Commit("Merged "+branch+" into "+readContentsAsString(join(GITLET_REFERENCE,"HEAD"))+".", branchId);
    }

    private static String getString(Blob curCommitBlob, Blob givenCommitBlob) {
        byte[] curFileContent = new byte[0];
        if (curCommitBlob != null) {
            curFileContent = curCommitBlob.getContent().getBytes(StandardCharsets.UTF_8);
        } else {
            curFileContent = new byte[0];
        }
        byte[] givenFileContent = null;
        if (givenCommitBlob != null) {
            givenFileContent = givenCommitBlob.getContent().getBytes(StandardCharsets.UTF_8);
        } else {
            givenFileContent = new byte[0];
        }
        byte[] head = "<<<<<<< HEAD\n".getBytes(StandardCharsets.UTF_8);
        byte[] body = "=======\n".getBytes(StandardCharsets.UTF_8);
        byte[] feet = ">>>>>>>\n".getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream str = new ByteArrayOutputStream();
        try {
            str.write(head);
            str.write(curFileContent);
            str.write(body);
            str.write(givenFileContent);
            str.write(feet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return str.toString();
    }


    static List<String> getCurBranchCommitIdList() {
        return Commit.getCommitList(readContentsAsString(GITLET_HEAD));
    }

    static List<String> getGivenBranchCommitIdList(String branch) {
        return Commit.getCommitList(readContentsAsString(join(GITLET_REFERENCE, branch)));
    }

    static boolean areBlobsEqual(Blob curBlob, Blob givenBlob) {
        String curBlobHash = "null";
        String givenBlobHash = "null";
        if (curBlob != null && givenBlob != null) {
            curBlobHash = curBlob.getHashId();
            givenBlobHash = givenBlob.getHashId();
            return curBlobHash.equals(givenBlobHash);
        } else {
            if (curBlob != null) {
                curBlobHash = curBlob.getHashId();
            }
            }
            if (givenBlob != null) {
                givenBlobHash = givenBlob.getHashId();
            }
            return curBlobHash.equals(givenBlobHash);
        }

    }


