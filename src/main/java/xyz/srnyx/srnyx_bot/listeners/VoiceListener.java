package xyz.srnyx.srnyx_bot.listeners;

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import xyz.srnyx.srnyx_bot.managers.FriendsManager;


public class VoiceListener extends ListenerAdapter {

    /**
     * Called when a user joins a voice channel
     */
    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        new FriendsManager().move(event.getChannelJoined());
    }

    /**
     * Called when a user moves to a new voice channel
     */
    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        new FriendsManager().move(event.getChannelJoined());
    }

    /**
     * Called when a user leaves a voice channel
     */
    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        new FriendsManager().move(event.getChannelLeft());
    }
}
