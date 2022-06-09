package xyz.srnyx.srnyx_bot;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import xyz.srnyx.srnyx_bot.listeners.*;

import javax.security.auth.login.LoginException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;


public class Main {
    public static Logger log;

    /**
     * The main class of the bot
     *
     * @param   arguments   The arguments passed to the program
     */
    public static void main(String[] arguments) {
        // Console logger
        final ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new Format());
        log = Logger.getLogger("srnyx-Bot");
        log.setUseParentHandlers(false);
        log.addHandler(handler);

        // Create default config
        createConfig();

        // Stop server if token is invalid
        final String token = getConfig("token");
        if (token == null || token.equals("TOKEN_HERE")) {
            log.severe("No token found in config.json!");
            System.exit(0);
            return;
        }

        // Start bot
        try {
            final JDA jda = JDABuilder.createDefault(token).build().awaitReady();

            jda.addEventListener(new CommandListener());
            jda.addEventListener(new MessageListener());
            jda.addEventListener(new VoiceListener());

            jda.updateCommands().addCommands(
                    Commands.slash("invites", "Creates X invites")
                            .addOption(OptionType.INTEGER, "amount", "The number of invites to create", true)
                            .addOption(OptionType.CHANNEL, "channel", "The channel to create the invites in", false)
            ).queue();
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Creates config.json if it doesn't exist
     */
    @SuppressWarnings("unchecked")
    public static void createConfig() {
        if (!new File("config.json").exists()) {
            JSONObject tokenObject = new JSONObject();
            // Token
            tokenObject.put("token", "TOKEN_HERE");
            // Friend stuff
            tokenObject.put("friends_vc", "VC_ID_HERE");
            tokenObject.put("friends_waiting", "VC_ID_HERE");
            // Cross-chat
            tokenObject.put("crosschat_one_guild", "GUILD_ID_HERE");
            tokenObject.put("crosschat_one_channel", "TC_ID_HERE");
            tokenObject.put("crosschat_two_guild", "GUILD_ID_HERE");
            tokenObject.put("crosschat_two_channel", "TC_ID_HERE");

            try (FileWriter file = new FileWriter("config.json")) {
                // Write pretty JSON using GSON to file
                file.write(new GsonBuilder().setPrettyPrinting().create().toJson(JsonParser.parseString(tokenObject.toJSONString())));
                file.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Gets a config value from config.json
     *
     * @param   key The location of the config value in the file
     *
     * @return      The config value
     */
    public static String getConfig(String key) {
        try {
            return ((JSONObject) new JSONParser().parse(new FileReader("config.json"))).get(key).toString();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
