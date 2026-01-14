package xyz.srnyx.srnyxbot;

import io.github.freya022.botcommands.api.core.annotations.BEventListener;
import io.github.freya022.botcommands.api.core.events.InjectedJDAEvent;
import io.github.freya022.botcommands.api.core.service.annotations.BService;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.lazylibrary.LazyLibrary;

import xyz.srnyx.srnyxbot.config.SrnyxConfig;
import xyz.srnyx.srnyxbot.listeners.VoiceListener;


@BService
public class SrnyxBot {
    public JDA jda;

    @BEventListener
    public void onInject(@NotNull InjectedJDAEvent event, @NotNull SrnyxConfig config) {
        jda = event.getJda();
        jda.addEventListener(new VoiceListener(config));
    }

    public static void main(@NotNull String[] arguments) {
        LazyLibrary.INSTANCE
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
                .activities(Activity.watching("srnyx.com"))
                .startBot(SrnyxBot.class);
    }
}
