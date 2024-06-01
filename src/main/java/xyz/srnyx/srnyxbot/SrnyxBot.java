package xyz.srnyx.srnyxbot;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.managers.Presence;
import net.dv8tion.jda.api.requests.GatewayIntent;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.lazylibrary.LazyLibrary;

import xyz.srnyx.srnyxbot.config.SrnyxConfig;
import xyz.srnyx.srnyxbot.listeners.GuildMemberListener;
import xyz.srnyx.srnyxbot.listeners.MessageListener;
import xyz.srnyx.srnyxbot.listeners.VoiceListener;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class SrnyxBot extends LazyLibrary {
    @NotNull private static final Random RANDOM = new Random();

    @NotNull public SrnyxConfig config = new SrnyxConfig(this);

    public SrnyxBot() {
        // Register listeners
        jda.addEventListener(
                new GuildMemberListener(this),
                new MessageListener(this),
                new VoiceListener(this));

        // Get statuses
        final List<Guild> guilds = jda.getGuilds();
        final String[] statuses = {"srnyx.com", guilds.size() + " servers", guilds.stream().mapToInt(guild -> guild.loadMembers().get().size()).sum() + " users"};
        final int length = statuses.length;

        // Set status
        final Presence presence = jda.getPresence();
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> presence.setActivity(Activity.watching(statuses[RANDOM.nextInt(length)])), 0, 20, TimeUnit.SECONDS);
    }

    @Override
    public void setSettings() {
        settings
                .searchPaths(
                        "xyz.srnyx.srnyxbot.components",
                        "xyz.srnyx.srnyxbot.commands")
                .gatewayIntents(
                        GatewayIntent.SCHEDULED_EVENTS,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.DIRECT_MESSAGES,
                        GatewayIntent.GUILD_PRESENCES,
                        GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_MESSAGE_TYPING,
                        GatewayIntent.GUILD_VOICE_STATES);
    }

    public static void main(@NotNull String[] arguments) {
        new SrnyxBot();
    }
}
