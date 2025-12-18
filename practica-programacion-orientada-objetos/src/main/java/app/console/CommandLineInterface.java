package app.console;

import app.console.CommandNamesException.ComandNotExistException;
import app.data.models.Usuario;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CommandLineInterface {
    public static final String DELIMITER_COLON_OR_RETURN = "[:,\\r\\n]";
    private static final String EXIT_NAME = "exit";
    private static final String EXIT_HELP = ": termina la ejecución.";
    private static final String HELP_NAME = "help";
    private static final String HELP_HELP = ": muestra la ayuda.";
    private static final String LOGIN_NAME = "login";
    private static final String LOGOUT_NAME = "logout";

    private final Map<String, Command> commands;
    private final View view;
    private Usuario usuarioLogueado = null;

    public CommandLineInterface(View view) {
        this.commands = new HashMap<>();
        this.view = view;
    }

    public void add(Command command) {
        this.commands.put(command.name(), command);
    }

    public boolean runCommands() {
        Scanner scanner = new Scanner(System.in).useDelimiter(DELIMITER_COLON_OR_RETURN);
        boolean exit;
        do {
            exit = runCommand(scanner);
        } while (!exit);
        return true;
    }

    private boolean runCommand(Scanner scanner) {
        this.view.showCommand();
        String commandName = scanner.next();
        boolean exit = false;
        if (HELP_NAME.equals(commandName)) {
            this.help();
        } else if (EXIT_NAME.equals(commandName)) {
            exit = true;
        } else {
            if (LOGIN_NAME.equals(commandName)) {
                this.commands.get(commandName).execute(scanner.next().split(";"), usuarioLogueado);
                this.usuarioLogueado = this.commands.get(commandName).getUsuarioLogueado();
            } else if (LOGOUT_NAME.equals(commandName)) {
                this.commands.get(commandName).execute(scanner.next().split(";"), usuarioLogueado);
                this.usuarioLogueado = this.commands.get(commandName).getUsuarioLogueado();
            } else if (this.commands.containsKey(commandName)) {
                this.commands.get(commandName).execute(scanner.next().split(";"), usuarioLogueado);
            } else {
                throw new ComandNotExistException("Comando '" + commandName + "' no existe.");
            }
        }
        return exit;
    }

    private void help() {
        this.view.show(EXIT_NAME + EXIT_HELP);
        this.view.show(HELP_NAME + HELP_HELP);
        for (Command command : this.commands.values()) {
            this.view.show(command.help());
        }
    }
}

