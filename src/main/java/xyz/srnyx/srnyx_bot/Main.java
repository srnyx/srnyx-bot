package xyz.srnyx.srnyx_bot;

import net.dv8tion.jda.api.JDABuilder;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.security.auth.login.LoginException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;


public class Main {
    /**
     * The main class of the bot
     *
     * @param   arguments   The arguments passed to the program
     */
    public static void main(String[] arguments) {
        // Create config
        if (!new File("config.json").exists()) {
            JSONObject tokenObject = new JSONObject();
            //noinspection unchecked
            tokenObject.put("token", "TOKEN_HERE");

            try (FileWriter file = new FileWriter("config.json")) {
                file.write(tokenObject.toJSONString());
                file.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Define token
        String token = null;
        try {
            token = ((JSONObject) new JSONParser().parse(new FileReader("config.json"))).get("token").toString();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        // Check if token is in config
        if (token == null || token.equals("TOKEN_HERE")) {
            // Stop server if token is not in config
            Logger.getLogger("srnyx-bot").severe("No token found in config.json!");
            System.exit(0);
        } else {
            // Start bot if token is in config
            try {
                JDABuilder.createDefault(token).build().awaitReady().addEventListener(new Listeners());
            } catch (LoginException | InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }
}
