package xyz.srnyx.srnyxbot.listeners;

import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.srnyxbot.SrnyxBot;
import xyz.srnyx.srnyxbot.managers.FriendsManager;


public class VoiceListener extends ListenerAdapter {
    @NotNull private final SrnyxBot bot;

    public VoiceListener(@NotNull SrnyxBot bot) {
        this.bot = bot;
    }

    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        // Joined
        if (event.getChannelJoined() != null) {
            onGuildVoiceJoin(event);
            return;
        }

        // Left
        if (event.getChannelLeft() != null) onGuildVoiceLeave(event);
    }

    /**
     * Called when a user joins a voice channel
     */
    public void onGuildVoiceJoin(@NotNull GuildVoiceUpdateEvent event) {
        final AudioChannelUnion channelJoined = event.getChannelJoined();
        if (channelJoined != null) FriendsManager.move(bot, event.getChannelJoined());
    }

    /**
     * Called when a user leaves a voice channel
     */
    public void onGuildVoiceLeave(@NotNull GuildVoiceUpdateEvent event) {
        final AudioChannelUnion channelLeft = event.getChannelLeft();
        if (channelLeft != null) FriendsManager.move(bot, event.getChannelLeft());
    }
}
