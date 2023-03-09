package xyz.srnyx.srnyxbot.managers;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.srnyxbot.SrnyxBot;

import java.util.List;
import java.util.Timer;


public class FriendsManager {
    /**
     * Moves users from either the Waiting for friend VC or the Friends VC to the other one
     */
    public static void move(@NotNull SrnyxBot bot, @NotNull AudioChannelUnion channel) {
        final long id = channel.getIdLong();
        new Timer().schedule(new java.util.TimerTask() {
            public void run() {
                final List<Member> members = channel.getMembers().stream()
                        .filter(member -> !member.getUser().isBot())
                        .toList();
                final int size = members.size();

                Long moveTo = null;
                if (id == bot.config.friendsVc) {
                    if (size < 2) return;
                    moveTo = bot.config.friendsWaiting;
                }
                if (id == bot.config.friendsWaiting) {
                    if (size >= 2) return;
                    moveTo = bot.config.friendsVc;
                }

                if (moveTo == null) return;
                final Guild guild = channel.getGuild();
                for (final Member member : members) if (members.contains(member)) guild.moveVoiceMember(member, guild.getVoiceChannelById(moveTo)).queue();
            }
        }, 1000);
    }
}
