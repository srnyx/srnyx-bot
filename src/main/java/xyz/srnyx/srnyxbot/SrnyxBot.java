package xyz.srnyx.srnyxbot;

import io.github.freya022.botcommands.api.core.annotations.BEventListener;
import io.github.freya022.botcommands.api.core.service.annotations.BService;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.lazylibrary.LazyEmbed;
import xyz.srnyx.lazylibrary.LazyLibrary;
import xyz.srnyx.lazylibrary.services.Bot;

import xyz.srnyx.srnyxbot.config.SrnyxConfig;
import xyz.srnyx.srnyxbot.listeners.VoiceListener;


@BService
public class SrnyxBot implements Bot {
    public JDA jda;

    @Override @NotNull
    public JDA getJDA() {
        return jda;
    }

    @BEventListener
    public void onReady(@NotNull ReadyEvent event, @NotNull SrnyxConfig config) {
        jda = event.getJDA();
        jda.addEventListener(new VoiceListener(config));
    }

    public static void main(@NotNull String[] arguments) {
        LazyLibrary.INSTANCE
                .searchPaths("xyz.srnyx.srnyxbot")
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
                .embedDefault(LazyEmbed.Key.FOOTER_ICON, "https://media.srnyx.com/r/circle.png")
                .build(SrnyxBot.class);
    }
}
