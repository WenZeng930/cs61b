package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Driver class for Gitlet, the tiny stupid version-control system.
 *
 * @author Wen Zeng
 */
public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND> ....
     */
    public static void main(String... args) {
        try {
            if (args.length == 0) {
                Utils.message("Please enter a command.");
                throw new GitletException();
            }
            if (valid(args[0])) {
                String[] operands = Arrays.copyOfRange(args, 1, args.length);
                if (repoInit()) {
                    repo = recoverRepo();
                    start(args, operands);
                    File file = new File(repoPath);
                    Utils.writeObject(file, repo);
                } else {
                    if (args[0].equals("init")) {
                        repo = new Command();
                        File file = new File(repoPath);
                        Utils.writeObject(file, repo);
                    } else {
                        Utils.message("Not in an initialized Gitlet directory.");
                        throw new GitletException();
                    }
                }
            } else {
                Utils.message("No command with that name exists.");
                throw new GitletException();
            }
        } catch (GitletException e) {
            System.exit(0);
        }

    }

    /**
     * check the initialization of the gitlet.
     */
    public static boolean repoInit() {
        String s = System.getProperty("user.dir");
        File temp = new File(s + "/.gitlet");
        if (temp.exists()) {
            return true;
        }
        return false;
    }

    /**
     * check command's validation.
     */
    private static boolean valid(String arg) {
        for (String command: commands) {
            if (arg.equals(command)) {
                return true;
            }
        }
        return false;
    }

    /**
     * cases by different commands.
     * @param args
     * @param operands
     */
    private static void start(String[] args, String[] operands) {
        String already = "A Gitlet version-control system " +
                "already exists in the current directory.";
        switch (args[0]) {
            case "init":
                Utils.message(already);
                throw new GitletException();
            case "add":
                repo.add(operands[0]);
                break;
            case "commit":
                repo.commit(operands[0]);
                break;
            case "rm":
                repo.rm(operands[0]);
                break;
            case "log":
                repo.log();
                break;
            case "global-log":
                repo.globalLog();
                break;
            case "find":
                repo.find(operands[0]);
                break;
            case "status":
                repo.status();
                break;
            case "checkout":
                if (operands.length == 1) {
                    repo.checkout(operands[0]);
                } else {
                    repo.checkout(operands);
                }
                break;
            case "branch":
                repo.branch(operands[0]);
                break;
            case "rm-branch":
                repo.rmBranch(operands[0]);
                break;
            case "reset":
                repo.reset(operands[0]);
                break;
            case "merge":
                repo.merge(operands[0]);
                break;
            default:
                System.exit(0);
        }
    }

    /**
     * return exsited repo.
     */
    public static Command recoverRepo() {
        File file =  new File(repoPath);
        return Utils.readObject(file, Command.class);
    }


    /**
     * return commands.
     */
    public String[] getCommands() {
        return commands;
    }

    /**
     * return the path.
     */
    public String getRepoPath() {
        return repoPath;
    }

    /**
     * return my repo.
     */
    public Command getRepo() {
        return repo;
    }

    /**
     * available commands.
     */
    private static String[] commands = new String[]{
            "init", "add", "commit", "rm",
            "log", "global-log", "find",
            "status", "checkout", "branch",
            "rm-branch", "reset", "merge"};

    /**
     * class command that can do all the operations.
     */
    private static Command repo;

    /**
     * repo path.
     */
    private static final String repoPath = ".gitlet/repo";
}
