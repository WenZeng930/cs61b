package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;

/**
 * define commit commands, including init, add, log... etc.
 *
 * @author Wen Zeng
 */


public class Command implements Serializable {

    /********************************** init **********************************/

    /**
     * Creates a new Gitlet version-control system in the current directory.
     * This system will automatically start with one commit:
     * a commit that contains no files
     * and has the commit message initial commit.
     */

    public Command() {

        Commit init = Commit.initCommit();
        File gitlet = new File(".gitlet");
        gitlet.mkdir();
        File commits = new File(".gitlet/commits");
        commits.mkdir();
        File staging = new File(".gitlet/staging");
        staging.mkdir();

        String initID = init.getCommitID();
        File initialFile = new File(".gitlet/commits/" + initID);
        Utils.writeContents(initialFile, Utils.serialize(init));

        _head = "master";
        _branches = new HashMap<String, String>();
        _branches.put("master", init.getCommitID());
        _stagingArea = new HashMap<String, String>();
        _untrackedFiles = new ArrayList<String>();
    }


    /********************************** add **********************************/

    /**
     * Adds a copy of the file as it currently exists to the staging area.
     * @param s add file name
     */

    public void add(String s) {
        File file = new File(s);
        if (!file.exists()) {
            Utils.message("File does not exist.");
            throw new GitletException();
        }
        String hashFile = Utils.sha1(Utils.readContentsAsString(file));
        Commit current = hashtocommit(getHead());
        HashMap<String, String> files = current.getFiles();

        File stagingBlob = new File(".gitlet/staging/" + hashFile);
        boolean blob = files == null;
        if (blob || !files.containsKey(s) || !files.get(s).equals(hashFile)) {
            _stagingArea.put(s, hashFile);
            String contents = Utils.readContentsAsString(file);
            Utils.writeContents(stagingBlob, contents);
        } else {
            if (stagingBlob.exists()) {
                _stagingArea.remove(s);
            }
        }
        if (_untrackedFiles.contains(s)) {
            _untrackedFiles.remove(s);
        }
    }

    /******************************* commit******************************/

    /**
     * Saves a snapshot of certain files in the current commit and staging area
     * so they can be restored at a later time,
     * creating a new commit.
     * @param msg (commit message as string "msg")
     */

    public void commit(String msg) {
        if (msg.trim().equals("")) {
            Utils.message("Please enter a commit message.");
            throw new GitletException();
        }
        Commit current = hashtocommit(getHead());
        HashMap<String, String> trackedFiles = current.getFiles();

        if (trackedFiles == null) {
            trackedFiles = new HashMap<String, String>();
        }

        if (_stagingArea.size() != 0 || _untrackedFiles.size() != 0) {
            for (String fileName : _stagingArea.keySet()) {
                trackedFiles.put(fileName, _stagingArea.get(fileName));
            }
            for (String fileName : _untrackedFiles) {
                trackedFiles.remove(fileName);
            }
        } else {
            Utils.message("No changes added to the commit.");
            throw new GitletException();
        }
        String[] parent = new String[]{current.getCommitID()};
        Commit newCommit = new Commit(msg, trackedFiles, parent, true);
        String newCommitD = newCommit.getCommitID();
        File newCommFile = new File(".gitlet/commits/" + newCommitD);
        Utils.writeObject(newCommFile, newCommit);

        _stagingArea = new HashMap<String, String>();
        _untrackedFiles = new ArrayList<String>();
        _branches.put(_head, newCommit.getCommitID());
    }

    /**
     * same commit function but use for merge.
     * @param msg commit message
     * @param parents the parents of commits
     */
    public void commit(String msg, String[] parents) {
        if (msg.trim().equals("")) {
            Utils.message("Please enter a commit message.");
            throw new GitletException();
        }
        Commit current = hashtocommit(getHead());
        HashMap<String, String> trackedFiles = current.getFiles();

        if (trackedFiles == null) {
            trackedFiles = new HashMap<String, String>();
        }

        if (_stagingArea.size() != 0 || _untrackedFiles.size() != 0) {
            for (String fileName : _stagingArea.keySet()) {
                trackedFiles.put(fileName, _stagingArea.get(fileName));
            }
            for (String fileName : _untrackedFiles) {
                trackedFiles.remove(fileName);
            }
        } else {
            Utils.message("No changes added to the commit.");
            throw new GitletException();
        }
        Commit newCommit = new Commit(msg, trackedFiles, parents, true);
        String newCommitID = newCommit.getCommitID();
        File newCommFile = new File(".gitlet/commits/" + newCommitID);
        Utils.writeObject(newCommFile, newCommit);

        _untrackedFiles = new ArrayList<String>();
        _stagingArea = new HashMap<String, String>();
        _branches.put(_head, newCommit.getCommitID());
    }

    /****************************** rm ******************************/

    /**
     * Unstage the file if it is currently staged.
     * If the file is tracked in the current commit,
     * mark it to indicate that it is not to be included in the next commit
     * (presumably you would store this mark somewhere
     * in the .gitlet directory),
     * and remove the file from the working directory
     * if the user has not already done so
     * (do not remove it unless it is tracked in the current commit).
     * @param arg file name that need to be removed
     */

    public void rm(String arg) {
        File file = new File(arg);
        Commit current = hashtocommit(getHead());
        HashMap<String, String> trackedFiles = current.getFiles();
        if (!file.exists() && !trackedFiles.containsKey(arg)) {
            Utils.message("File does not exist.");
            throw new GitletException();
        }
        boolean change = false;
        if (_stagingArea.containsKey(arg)) {
            _stagingArea.remove(arg);
            change = true;
        }
        if (trackedFiles != null
                && trackedFiles.containsKey(arg)) {
            _untrackedFiles.add(arg);
            File removefile = new File(arg);
            Utils.restrictedDelete(removefile);
            change = true;
        }
        if (!change) {
            Utils.message("No reason to remove the file.");
            throw new GitletException();
        }
    }

    /***************************** log *************************/

    /**
     * Starting at the current head commit,
     * display information about each commit backwards
     * along the commit tree until the initial commit,
     * following the first parent commit links,
     * ignoring any second parents found in merge commits.
     */

    public void log() {
        String head = getHead();
        while (head != null) {
            Commit first = hashtocommit(head);
            printCommit(head);
            head = first.getparentID();
        }
    }

    /**
     * print the commit record.
     *
     * @param commitiID print each log according to each commit id
     */
    private void printCommit(String commitiID) {
        Commit commit = hashtocommit(commitiID);
        if (commit.getParents() != null && commit.getParents().length > 1) {
            System.out.println("===");
            System.out.println("commit " + commitiID);
            String msg1 = commit.getParents()[0].substring(0, 7);
            String msg2 = commit.getParents()[1].substring(0, 7);
            System.out.println("Merge: " + msg1 + " " + msg2);
            System.out.println("Date: " + commit.getTimestamp());
            System.out.println(commit.getMessage());
            System.out.println();
        } else {
            System.out.println("===");
            System.out.println("commit " + commitiID);
            System.out.println("Date: " + commit.getTimestamp());
            System.out.println(commit.getMessage());
            System.out.println();
        }
    }

    /************************************ global-log ************************************/

    /**
     * Like log, except displays information about all commits ever made.
     * The order of the commits does not matter.
     */

    public void globalLog() {
        File allCommits = new File(".gitlet/commits");
        File[] commits = allCommits.listFiles();

        for (File file : commits) {
            printCommit(file.getName());
        }
    }

    /************************************ find ************************************/

    /**
     * Prints out the ids of all commits that have the given commit message,
     * one per line.
     * If there are multiple such commits,
     * it prints the ids out on separate lines.
     * The commit message is a single operand;
     * to indicate a multiword message,
     * put the operand in quotation marks,
     * as for the commit command below.
     * @param msg find file name/commit
     */

    public void find(String msg) {
        File allCommits = new File(".gitlet/commits");
        File[] commits = allCommits.listFiles();
        boolean found = false;

        for (File file : commits) {
            Commit commit = hashtocommit(file.getName());
            if (commit.getMessage().equals(msg)) {
                System.out.println(file.getName());
                found = true;
            }
        }
        if (!found) {
            Utils.message("Found no commit with that message.");
            throw new GitletException();
        }
    }

    /************************************ status ************************************/

    /**
     * Displays what branches currently exist, and marks the current branch with a *.
     * Also displays what files have been staged or marked for untracking.
     */

    public void status() {
        System.out.println("=== Branches ===");
        Object[] keys = _branches.keySet().toArray();
        Arrays.sort(keys);
        for (Object branch : keys) {
            if (branch.equals(_head)) {
                System.out.println("*" + branch);
            } else {
                System.out.println(branch);
            }
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        Object[] stageing = _stagingArea.keySet().toArray();
        Arrays.sort(stageing);
        for (Object staged : stageing) {
            System.out.println(staged);
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        Object[] untracked = _untrackedFiles.toArray();
        Arrays.sort(untracked);
        for (Object removed : untracked) {
            System.out.println(removed);
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();

        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    /************************************ checkout ************************************/

    /**
     * Checkout is a kind of general command that can do
     * a few different things depending on what its arguments are.
     * case 1: java gitlet.Main checkout -- [file name]
     * case 2: java gitlet.Main checkout [commit id] -- [file name].
     * @param args general command argument
     */

    public void checkout(String[] args) {
        String commitID;
        String fileName;
        if (args.length == 2 && args[0].equals("--")) {
            fileName = args[1];
            commitID = getHead();
        } else if (args.length == 3 && args[1].equals("--")) {
            commitID = args[0];
            fileName = args[2];
        } else {
            Utils.message("Incorrect operands");
            throw new GitletException();
        }
        commitID = convertID(commitID);
        Commit comm = hashtocommit(commitID);
        HashMap<String, String> trackedFiles = comm.getFiles();
        if (trackedFiles.containsKey(fileName)) {
            File f = new File(fileName);
            String p = ".gitlet/staging/";
            String blobFileName = p + trackedFiles.get(fileName);
            File g = new File(blobFileName);
            String contents = Utils.readContentsAsString(g);
            Utils.writeContents(f, contents);
        } else {
            Utils.message("File does not exist in that commit.");
            throw new GitletException();
        }
    }

    /**
     * shorten the commit id that can abbreviate commits with a unique prefix.
     *
     * @param id commit id
     * @return short version of commit id
     */
    private String convertID(String id) {
        if (id.length() == Utils.UID_LENGTH) {
            return id;
        }
        File allCommits = new File(".gitlet/commits");
        File[] commits = allCommits.listFiles();

        for (File file : commits) {
            if (file.getName().contains(id)) {
                return file.getName();
            }
        }
        Utils.message("No commit with that id exists.");
        throw new GitletException();
    }

    /**
     * case 3: java gitlet.Main checkout [branch name]
     */
    public void checkout(String branchName) {
        if (!_branches.containsKey(branchName)) {
            Utils.message("No such branch exists.");
            throw new GitletException();
        }
        if (_head.equals(branchName)) {
            Utils.message("No need to checkout the current branch.");
            throw new GitletException();
        }
        String commitID = _branches.get(branchName);
        Commit commit = hashtocommit(commitID);
        HashMap<String, String> files = commit.getFiles();
        String tempString = System.getProperty("user.dir");
        File temp = new File(tempString);
        checkUntracked(temp);
        for (File file : temp.listFiles()) {
            if (files == null) {
                Utils.restrictedDelete(file);
            } else {
                boolean b = !files.containsKey(file.getName());
                if (b && !file.getName().equals(".gitlet")) {
                    Utils.restrictedDelete(file);
                }
            }
        }
        if (files != null) {
            for (String file : files.keySet()) {
                String g = ".gitlet/staging/"
                        + files.get(file);
                File f = new File(g);
                String contents = Utils.readContentsAsString(f);
                Utils.writeContents(new File(file), contents);
            }
        }
        _stagingArea = new HashMap<String, String>();
        _untrackedFiles = new ArrayList<String>();
        _head = branchName;

    }

    /**
     * check untracked files.
     *
     * @param temp the name of the untracked file
     */
    private void checkUntracked(File temp) {
        Commit current = hashtocommit(getHead());
        HashMap<String, String> trackedFiles = current.getFiles();
        for (File file : temp.listFiles()) {
            if (trackedFiles == null) {
                if (temp.listFiles().length > 1) {
                    Utils.message("There is an untracked file in the way; " +
                            "delete it or add it first.");
                    throw new GitletException();
                }
            } else {
                if (!trackedFiles.containsKey(file.getName())
                        && !file.getName().equals(".gitlet")
                        && !_stagingArea.containsKey(file.getName())) {
                    Utils.message("There is an untracked file in the way; " +
                            "delete it or add it first.");
                    throw new GitletException();
                }
            }
        }
    }


    /************************************ branch ************************************/

    /**
     * Creates a new branch with the given name, and points it at the current head node.
     * A branch is nothing more than a name for a reference (a SHA-1 identifier) to a commit node.
     * This command does NOT immediately switch to the newly created branch (just as in real Git).
     */

    public void branch(String arg) {
        if (!_branches.containsKey(arg)) {
            _branches.put(arg, getHead());
        } else {
            Utils.message("A branch with that name already exists.");
            throw new GitletException();
        }
    }

    /************************************ rm-branch ************************************/

    /**
     * Deletes the branch with the given name.
     * This only means to delete the pointer associated with the branch;
     * @param name branch name
     */

    public void rmBranch(String name) {
        if (_head.equals(name)) {
            Utils.message("Cannot remove the current branch.");
            throw new GitletException();
        }
        if (_branches.containsKey(name)) {
            _branches.remove(name);
        } else {
            Utils.message("A branch with that name does not exist.");
            throw new GitletException();
        }
    }


    /************************************ reset ************************************/

    /**
     * Checks out all the files tracked by the given commit.
     * Removes tracked files that are not present in that commit
     * Also moves the current branch's head to that commit node.
     * @param commitID commit id
     */

    public void reset(String commitID) {
        commitID = convertID(commitID);
        Commit commit = hashtocommit(commitID);
        HashMap<String, String> files = commit.getFiles();

        String tempString = System.getProperty("user.dir");
        File temp = new File(tempString);
        checkUntracked(temp);

        for (File file : temp.listFiles()) {
            if (!files.containsKey(file.getName())) {
                Utils.restrictedDelete(file);
            }
        }
        for (String file : files.keySet()) {
            File f = new File(".gitlet/staging/" + files.get(file));
            String contents = Utils.readContentsAsString(f);
            Utils.writeContents(new File(file), contents);
        }
        _stagingArea = new HashMap<String, String>();
        _branches.put(_head, commitID);
    }

    /************************************ merge ************************************/

    /**
     * Merges files from the given branch into the current branch.
     * failure cases: 1) If there are staged additions or removals present, print the error message;
     * 2) If a branch with the given name does not exist
     * 3) If attempting to merge a branch with itself...
     * @param branchName branch name
     */

    public void merge(String branchName) {
        if (_stagingArea.size() != 0 || _untrackedFiles.size() != 0) {
            Utils.message("You have uncommitted changes.");
            throw new GitletException();
        }
        if (!_branches.containsKey(branchName)) {
            Utils.message("A branch with that name does not exist.");
            throw new GitletException();
        }
        if (branchName.equals(_head)) {
            Utils.message("Cannot merge a branch with itself.");
            throw new GitletException();
        }
        String split = splitPoint(branchName, _head);
        if (split.equals(_branches.get(branchName))) {
            Utils.message("Given branch is an ancestor of the current branch.");
            return;
        }
        if (split.equals(_branches.get(_head))) {
            _branches.put(_head, _branches.get(branchName));
            Utils.message("Current branch fast-forwarded.");
            return;
        }

        Commit splitCommit = hashtocommit(split);
        HashMap<String, String> splitFiles = splitCommit.getFiles();

        merge2(branchName);

        Commit currComm = hashtocommit(getHead());
        HashMap<String, String> current = currComm.getFiles();
        Commit prevComm = hashtocommit(_branches.get(branchName));
        HashMap<String, String> given = prevComm.getFiles();

        for (String fileName : given.keySet()) {
            if (!splitFiles.containsKey(fileName)) {
                if (!current.containsKey(fileName)) {
                    String b = _branches.get(branchName);
                    checkout(new String[] {b, "--", fileName});
                    _stagingArea.put(fileName, given.get(fileName));
                } else if (!given.containsKey(fileName)) {
                    continue;
                } else if (modified(fileName, given, current)) {
                    String s = ".gitlet/staging/";
                    File file1 = new File(s + current.get(fileName));
                    File file2 = new File(s + given.get(fileName));
                    String content = "<<<<<<< HEAD\n";
                    content += Utils.readContentsAsString(file1);
                    content += "=======\n";
                    content += Utils.readContentsAsString(file2) + ">>>>>>>";
                    Utils.writeContents(new File(fileName), content);
                    add(fileName);
                    Utils.message("Encountered a merge conflict.");
                }
            }
        }
        String[] parents = new String[]{getHead(), _branches.get(branchName)};
        commit("Merged " + branchName + " into " + _head + ".", parents);
    }

    /**
     * split point in two branches.
     * @param branch1 branch 1
     * @param branch2 branch 2
     */
    private String splitPoint(String branch1, String branch2) {
        ArrayList<String> branch1Commits = new ArrayList<String>();
        ArrayList<String> branch2Commits = new ArrayList<String>();

        String parent1 = _branches.get(branch1);
        String parent2 = _branches.get(branch2);

        while (parent1 != null) {
            branch1Commits.add(parent1);
            Commit commit1 = hashtocommit(parent1);
            parent1 = commit1.getparentID();
        }
        while (parent2 != null) {
            branch2Commits.add(parent2);
            Commit commit2 = hashtocommit(parent2);
            parent2 = commit2.getparentID();
        }
        for (String commit : branch1Commits) {
            if (branch2Commits.contains(commit)) {
                return commit;
            }
        }
        return "";
    }

    /**
     * check whether files have been modified.
     * @param file unchecked file
     * @param branch1 branch1
     * @param branch2 branch2
     */
    boolean modified(String file, HashMap<String, String> branch1, HashMap<String, String> branch2) {
        if (branch1.containsKey(file) && branch2.containsKey(file)) {
            String hash1 = branch1.get(file);
            String hash2 = branch2.get(file);
            if (!hash1.equals(hash2)) {
                return true;
            }
        } else if (branch1.containsKey(file) || branch2.containsKey(file)) {
            return true;
        }
        return false;
    }

    /**
     * handle split point in merge.
     * @param branchName branch name
     */
    private void merge2(String branchName) {
        String split = splitPoint(branchName, _head);
        Commit splitCommit = hashtocommit(split);
        HashMap<String, String> splitFiles = splitCommit.getFiles();
        Commit currComm = hashtocommit(getHead());
        HashMap<String, String> current = currComm.getFiles();
        Commit prevComm = hashtocommit(_branches.get(branchName));
        HashMap<String, String> given = prevComm.getFiles();

        String tempString = System.getProperty("user.dir");
        File temp = new File(tempString);
        checkUntracked(temp);

        for (String fileName : splitFiles.keySet()) {
            boolean givenShowed = given.containsKey(fileName);
            boolean currentModified = modified(fileName, splitFiles, current);
            boolean givenModified = modified(fileName, splitFiles, given);
            if (!currentModified) {
                if (!givenShowed) {
                    Utils.restrictedDelete(new File(fileName));
                    rm(fileName);
                    continue;
                }
                if (givenModified) {
                    String b = _branches.get(branchName);
                    checkout(new String[]{b, "--", fileName});
                    add(fileName);
                }
            }
            if (currentModified && givenModified) {
                if (modified(fileName, given, current)) {
                    mergeConflict(branchName, fileName);
                }
            }
        }
    }

    /**
     * check if there is a merge conflict.
     * @param branch branch name
     * @param file file name
     */
    private void mergeConflict(String branch, String file) {
        String split = splitPoint(branch, _head);
        Commit splitCommit = hashtocommit(split);

        HashMap<String, String> splitFiles = splitCommit.getFiles();
        Commit currComm = hashtocommit(getHead());

        HashMap<String, String> current = currComm.getFiles();
        Commit prevComm = hashtocommit(_branches.get(branch));
        HashMap<String, String> prev = prevComm.getFiles();

        String s = ".gitlet/staging/";
        File file1;
        String file1Content;
        if (current.containsKey(file)) {
            file1 = new File(s + current.get(file));
            file1Content = Utils.readContentsAsString(file1);
        } else {
            file1 = null;
            file1Content = "";
        }
        File file2;
        String file2Content;
        if (prev.containsKey(file)) {
            file2 = new File(s + prev.get(file));
            file2Content = Utils.readContentsAsString(file2);
        } else {
            file2 = null;
            file2Content = "";
        }
        String content = "<<<<<<< HEAD\n";
        content += file1Content;
        content += "=======\n" + file2Content;
        content += ">>>>>>>\n";
        Utils.writeContents(new File(file), content);
        add(file);
        Utils.message("Encountered a merge conflict.");
    }


    /************************************ OTHER ************************************/

    /**
     * access between hash and commit.
     *
     * @param commitID commit id that guide us to certain commit/hash
     */
    public Commit hashtocommit(String commitID) {
        File file = new File(".gitlet/commits/" + commitID);
        if (file.exists()) {
            return Utils.readObject(file, Commit.class);
        } else {
            Utils.message("No commit with that id exists.");
            throw new GitletException();
        }
    }

    /**
     * return the current head of the commit.
     */
    public String getHead() {
        return _branches.get(_head);
    }

    /**
     * return the branches of my commit.
     */
    public HashMap<String, String> getBranches() {
        return _branches;
    }

    /**
     * return the staging area.
     */
    public HashMap<String, String> getStagingArea() {
        return _stagingArea;
    }

    /**
     * return the untracked files.
     */
    public ArrayList<String> getUntrackedFiles() {
        return _untrackedFiles;
    }

    /**
     * the head pointer which refer to the current commit.
     */
    private String _head;

    /**
     * the commit branches.
     */
    private HashMap<String, String> _branches;

    /**
     * the staging area that check the staging files whether commit or not.
     */
    private HashMap<String, String> _stagingArea;

    /**
     * the untracked files, outside of the staging area.
     */
    private ArrayList<String> _untrackedFiles;

}
