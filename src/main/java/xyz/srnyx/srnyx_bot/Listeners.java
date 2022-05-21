package xyz.srnyx.srnyx_bot;

import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;


public class Listeners extends ListenerAdapter {
    /**
     * Called when a user joins a voice channel
     */
    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        move(event.getChannelJoined());
    }

    /**
     * Called when a user moves to a new voice channel
     */
    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        move(event.getChannelJoined());
    }

    /**
     * Moves a user to a new voice channel
     *
     * @param   channel The channel to move the user to
     */
    private void move(AudioChannel channel) {
        final List<Member> members = channel.getMembers();
        final Guild guild = channel.getGuild();

        if (channel.getId().equals("977313982320873503") && members.size() >= 2) {
            for (final Member member : members) guild.moveVoiceMember(member, guild.getVoiceChannelById("960999744753250315")).queue();
        }
    }
}
