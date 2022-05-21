package xyz.srnyx.srnyx_bot;

import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
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
     * Called when a user leaves a voice channel
     */
    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        move(event.getChannelLeft());
    }

    /**
     * Moves users from either the Waiting for friend VC or the Friends VC to the other one
     *
     * @param   channel Channel they joined/left
     */
    private void move(AudioChannel channel) {
        new java.util.Timer().schedule(new java.util.TimerTask() {
            public void run() {
                final List<Member> members = channel.getMembers();
                final Guild guild = channel.getGuild();

                int size = 0;
                for (final Member member : members) if (!member.getUser().isBot()) size++;

                boolean sizeCheck = false;
                String moveTo = "";
                if (channel.getId().equals("960999744753250315")) {
                    sizeCheck = size < 2;
                    moveTo = "977313982320873503";
                }
                if (channel.getId().equals("977313982320873503")) {
                    sizeCheck = size >= 2;
                    moveTo = "960999744753250315";
                }

                if (sizeCheck) for (final Member member : members) guild.moveVoiceMember(member, guild.getVoiceChannelById(moveTo)).queue();
            }
        }, 1000);
    }

    // 960999744753250315 Friends VC
    // 977313982320873503 Waiting for friend VC
}
