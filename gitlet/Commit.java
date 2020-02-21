package gitlet;

import java.util.Date;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;

/**
 * the commit class.
 * @author Wen Zeng
 */

public class Commit implements Serializable {

    /**
     * a commit command contains the following four variables.
     * @param msg commit message
     * @param files tracked files
     * @param parents parents
     * @param commit check whether it is committed.
     */
    public Commit(String msg, HashMap<String,String>
            files, String[] parents, boolean commit) {
        _message = msg;
        _files = files;
        _parents = parents;
        Date commitDate;
        if (commit) {
            commitDate = new Date();
            _timestamp = DATE_FORMAT.format(commitDate) + "-0800";
        } else {
            _timestamp = "Wed Dec 31 16:00:00 1969 -0800";
        }
        _commitID = hashCommit();
    }

    /**
     * the initial commit.
     * @return new initial commit
     */
    public static Commit initCommit() {
        return new Commit("initial commit",
                null, null, false);
    }


    /**
     * hash current commit based on commit message,
     * files, timestamp, and parents.
     * @return hash
     */
    public String hashCommit() {
        String files;
        if (_files == null) {
            files = "";
        } else {
            files = _files.toString();
        }
        String parents = Arrays.toString(_parents);
        return Utils.sha1(_message, files, _timestamp, parents);
    }


    /**
     * return the commit message of specific commit.
     */
    public String getMessage() {
        return _message;
    }

    /**
     * return the commit date.
     */
    public String getTimestamp() {
        return _timestamp;
    }

    /**
     * return the files in commit.
     */
    public HashMap<String, String> getFiles() {
        return _files;
    }

    /**
     * return the unqie commit id.
     */
    public String getCommitID() {
        return _commitID;
    }

    /**
     * return the parent's id of the first commit.
     */
    public String getparentID() {
        if (_parents != null) {
            return _parents[0];
        }
        return null;
    }

    /**
     * return all parents from one commit.
     */
    public String[] getParents() {
        return _parents;
    }

    /**
     * the message of commit.
     */
    private String _message;

    /**
     * commit date.
     */
    private String _timestamp;

    /**
     * date format.
     */
    public static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy");

    /**
     * hash of each commit.
     */
    private String _commitID;

    /**
     * a list of hash string that represent the tracked blobs.
     */
    private HashMap<String, String> _files;

    /**
     * an array of hashes of parents.
     */
    private String[] _parents;


}