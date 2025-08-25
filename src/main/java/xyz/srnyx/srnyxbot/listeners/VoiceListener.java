package xyz.srnyx.srnyxbot.listeners;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.lazylibrary.LazyListener;
import xyz.srnyx.lazylibrary.events.GuildVoiceJoinEvent;
import xyz.srnyx.lazylibrary.events.GuildVoiceLeaveEvent;

import xyz.srnyx.srnyxbot.config.SrnyxConfig;

import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


//@BService
public class VoiceListener extends LazyListener {
    @NotNull public static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(1);

    @NotNull private final SrnyxConfig config;

    public VoiceListener(@NotNull SrnyxConfig config) {
        this.config = config;
    }

    /**
     * Called when a user joins a voice channel
     */
    @Override
    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event) {
        move(event.getChannelJoined());
    }

    /**
     * Called when a user leaves a voice channel
     */
    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {
        move(event.getChannelLeft());
    }

    /**
     * Moves users from either the Waiting for friend VC or the Friends VC to the other one
     */
    public void move(@NotNull AudioChannelUnion channel) {
        final long id = channel.getIdLong();
        final boolean isFriendsVc = id == config.friendsVc;
        if (!isFriendsVc && id != config.friendsWaiting) return;
        final Guild guild = channel.getGuild();

        EXECUTOR.schedule(new TimerTask() {
            public void run() {
                final List<Member> members = channel.getMembers().stream()
                        .filter(member -> !member.getUser().isBot())
                        .toList();
                final int size = members.size();

                long moveTo;
                if (isFriendsVc) {
                    if (size >= 2) return;
                    moveTo = config.friendsWaiting;
                } else {
                    if (size < 2) return;
                    moveTo = config.friendsVc;
                }

                final AudioChannel moveToChannel = guild.getChannelById(AudioChannel.class, moveTo);
                for (final Member member : members) if (members.contains(member)) guild.moveVoiceMember(member, moveToChannel).queue();
            }
        }, 1, TimeUnit.SECONDS);
    }
}
