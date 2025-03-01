import server.BankAPIServer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {

        try {
            BankAPIServer.main(args);
        } catch (IOException e) {
            LOGGER.log(Level.INFO, "Error while starting server", e);
        }
    }
}
