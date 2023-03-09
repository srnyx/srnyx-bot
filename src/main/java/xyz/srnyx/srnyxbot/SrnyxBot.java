package xyz.srnyx.srnyxbot;

import com.freya02.botcommands.api.CommandsBuilder;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.managers.Presence;
import net.dv8tion.jda.api.requests.GatewayIntent;

import org.jetbrains.annotations.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.srnyx.srnyxbot.listeners.MessageListener;
import xyz.srnyx.srnyxbot.listeners.VoiceListener;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class SrnyxBot {
    @NotNull public static final Logger LOGGER = LoggerFactory.getLogger("srnyxBot");

    public JDA jda;
    @NotNull public SrnyxConfig config = new SrnyxConfig();

    public SrnyxBot() {
        // Start bot
        try {
            jda = JDABuilder.create(config.token,
                            GatewayIntent.SCHEDULED_EVENTS,
                            GatewayIntent.MESSAGE_CONTENT,
                            GatewayIntent.DIRECT_MESSAGES,
                            GatewayIntent.GUILD_PRESENCES,
                            GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
                            GatewayIntent.GUILD_MEMBERS,
                            GatewayIntent.GUILD_MESSAGES,
                            GatewayIntent.GUILD_MESSAGE_TYPING,
                            GatewayIntent.GUILD_VOICE_STATES)
                    .build().awaitReady();
        } catch (final InterruptedException | IllegalArgumentException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
            System.exit(0);
            return;
        }
        config.setJda(jda);

        // Register commands
        try {
            CommandsBuilder.newBuilder(config.owner)
                    .extensionsBuilder(extensions -> extensions.registerCommandDependency(SrnyxBot.class, () -> this))
                    .textCommandBuilder(textCommands -> textCommands.disableHelpCommand(true))
                    .addSearchPath("xyz.srnyx.srnyxbot.commands")
                    .build(jda);
        } catch (final IOException e) {
            e.printStackTrace();
        }

        // Register listeners
        jda.addEventListener(
                new MessageListener(this),
                new VoiceListener(this));

        // Get statuses
        final List<Guild> guilds = jda.getGuilds();
        final String[] statuses = {"srnyx.xyz", guilds.size() + " servers", guilds.stream().mapToInt(guild -> guild.loadMembers().get().size()).sum() + " users"};
        final int length = statuses.length;

        // Set status
        final Presence presence = jda.getPresence();
        final Random random = new Random();
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> presence.setActivity(Activity.watching(statuses[random.nextInt(length)])), 0, 20, TimeUnit.SECONDS);

        // stop command
        new Thread(() -> {
            final Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) if (scanner.nextLine().equals("stop")) System.exit(0);
        }).start();
    }

    /**
     * The main class of the bot
     *
     * @param   arguments   The arguments passed to the program
     */
    public static void main(@NotNull String[] arguments) {
        new SrnyxBot();
    }
}
