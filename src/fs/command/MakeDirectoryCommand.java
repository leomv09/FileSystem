package fs.command;

import fs.App;
import fs.Disk;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 *
 * @author José Andrés García Sáenz <jags9415@gmail.com>
 */
public class MakeDirectoryCommand extends Command {

    public static final String COMMAND = "mkdir";

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            reportSyntaxError();
            return;
        }

        App app = App.getInstance();
        Disk disk = app.getDisk();
        String path;

        for (int i = 1; i < args.length; i++) {
            path = args[i];

            try {
                if (disk.exists(path)) {
                    System.out.print("There is already a directory with the same name. Do you want to override it? [y/n] ");
                    BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
                    String line = input.readLine();
                    if (!line.equalsIgnoreCase("y")) {
                        return;
                    }
                    disk.delete(path);
                }
                disk.createDirectory(path);
            } 
            catch (Exception ex) {
                reportError(ex);
            }
        }
    }

    @Override
    protected String getSyntax() {
        return getName() + " DIRECTORY...";
    }

    @Override
    protected String getDescription() {
        return "Create the DIRECTORY(ies), if they do not already exist.";
    }

    @Override
    protected String getName() {
        return COMMAND;
    }

}
