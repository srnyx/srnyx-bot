package xyz.srnyx.srnyx_bot.listeners;

import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.srnyx.srnyx_bot.Main;

import java.util.List;


public class VoiceListener extends ListenerAdapter {

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

                String moveTo = "";
                boolean sizeCheck = false;
                if (channel.getId().equals(Main.getConfig("friends_vc"))) {
                    sizeCheck = size < 2;
                    moveTo = Main.getConfig("friends_waiting");
                }
                if (channel.getId().equals(Main.getConfig("friends_waiting"))) {
                    sizeCheck = size >= 2;
                    moveTo = Main.getConfig("friends_vc");
                }

                if (!sizeCheck || moveTo == null) return;
                for (final Member member : members) if (members.contains(member)) guild.moveVoiceMember(member, guild.getVoiceChannelById(moveTo)).queue();
            }
        }, 1000);
    }
}
