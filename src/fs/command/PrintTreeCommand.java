package fs.command;

import fs.App;
import fs.Disk;
import fs.Node;
import fs.StringUtils;
import fs.Tree;
import java.io.IOException;

/**
 *
 * @author José Andrés García Sáenz <jags9415@gmail.com>
 */
public class PrintTreeCommand extends Command {

    public static final String COMMAND = "tree";
    
    @Override
    public void execute(String[] args) {
        App app = App.getInstance();
        Disk disk = app.getDisk();
        String directory;
        
        switch (args.length) {
            case 1:
                directory = disk.getCurrentDirectory();
                break;
            case 2:
                directory = args[1];
                break;
            default:
                reportSyntaxError();
                return;
        }
        
        try {
            Tree<Node> tree = disk.getTree(directory);
            printTree(tree);
        } 
        catch (IOException ex) {
            reportError(ex);
        }
    }
    
    private void printTree(Tree<Node> tree, int level) {
        Node node;
        for (Tree<Node> child : tree.children()) {
            node = child.getData();
            System.out.print(StringUtils.repeat("  ", level));
            System.out.print(node.getName());
            if (node.isDirectory()) {
                System.out.println("/");
                printTree(child, level + 1);
            }
            else {
                System.out.println();
            }
        }
    }
    
    private void printTree(Tree<Node> tree) {
        if (tree.isRoot()) {
            System.out.println("/");
        }
        else {
            System.out.println(tree.getData().getName() + "/");
        }
        printTree(tree, 1);
    }

    @Override
    protected String getName() {
        return PrintTreeCommand.COMMAND;
    }

    @Override
    protected String getDescription() {
        return "Print the DIRECTORY subtree.";
    }

    @Override
    protected String getSyntax() {
        return getName() + " <DIRECTORY>";
    }
    
}
