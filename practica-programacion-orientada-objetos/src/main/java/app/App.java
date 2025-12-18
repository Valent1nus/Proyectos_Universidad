package app;

import app.console.DependencyInjector;

public class App {
    public static void main(String[] args) {
        DependencyInjector.getInstance().run();
    }

}
