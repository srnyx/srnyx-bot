package xyz.srnyx.srnyxbot;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.lazylibrary.LazyEmbed;
import xyz.srnyx.lazylibrary.LazyLibrary;

import xyz.srnyx.srnyxbot.config.SrnyxConfig;
import xyz.srnyx.srnyxbot.listeners.AdvertisingListener;
import xyz.srnyx.srnyxbot.listeners.GuildMemberListener;
import xyz.srnyx.srnyxbot.listeners.MessageListener;
import xyz.srnyx.srnyxbot.listeners.VoiceListener;

import java.util.List;


public class SrnyxBot extends LazyLibrary {
    @NotNull public SrnyxConfig config = new SrnyxConfig(this);

    public SrnyxBot() {
        // Register listeners
        jda.addEventListener(
                new GuildMemberListener(this),
                new MessageListener(this),
                new VoiceListener(this));
        if (!config.advertising.isEmpty()) jda.addEventListener(new AdvertisingListener(this));

        // Status log message
        LOGGER.info("srnyx's Bot has finished starting!");
    }

    @Override
    public void setSettings() {
        settings
                .searchPaths(
                        "xyz.srnyx.srnyxbot.apps",
                        "xyz.srnyx.srnyxbot.commands",
                        "xyz.srnyx.srnyxbot.components")
                .gatewayIntents(
                        GatewayIntent.SCHEDULED_EVENTS,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.DIRECT_MESSAGES,
                        GatewayIntent.GUILD_PRESENCES,
                        GatewayIntent.GUILD_EXPRESSIONS,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_MESSAGE_TYPING,
                        GatewayIntent.GUILD_VOICE_STATES)
                .embedDefault(LazyEmbed.Key.COLOR, 3840960)
                .embedDefault(LazyEmbed.Key.FOOTER_TEXT, "srnyx's Bot")
                .embedDefault(LazyEmbed.Key.FOOTER_ICON, "https://media.srnyx.com/r/circle.png");
    }

    @Override
    public void onReady() {
        final List<Guild> guilds = jda.getGuilds();
        settings.activities(
                Activity.watching("srnyx.com"),
                Activity.watching(guilds.size() + " servers"),
                Activity.watching(guilds.stream().mapToInt(guild -> guild.loadMembers().get().size()).sum() + " users"));
    }

    public static void main(@NotNull String[] arguments) {
        new SrnyxBot();
    }
}
