package xyz.srnyx.srnyxbot.config;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.spongepowered.configurate.ConfigurationNode;

import xyz.srnyx.javautilities.manipulation.Mapper;

import xyz.srnyx.lazylibrary.LazyEmbed;

import xyz.srnyx.srnyxbot.SrnyxBot;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


public class SrnyxConfig {
    @NotNull private final SrnyxBot bot;

    @Nullable public final String playHostingToken;

    // FRIENDS
    public final long friendsGuild;
    public final long friendsWaiting;
    public final long friendsVc;

    // CROSSCHAT
    // one
    public final long crosschatOneGuild;
    public final long crosschatOneChannel;
    // two
    public final long crosschatTwoGuild;
    public final long crosschatTwoChannel;

    // APPROVALS
    @NotNull public final Set<Approval> approvals = new HashSet<>();

    public SrnyxConfig(@NotNull SrnyxBot bot) {
        this.bot = bot;
        final ConfigurationNode yaml = bot.settings.fileSettings.file.yaml;

        playHostingToken = yaml.node("play-hosting-token").getString();

        // FRIENDS
        final ConfigurationNode friendsNode = yaml.node("friends");
        friendsGuild = friendsNode.node("guild").getLong();
        friendsWaiting = friendsNode.node("waiting").getLong();
        friendsVc = friendsNode.node("vc").getLong();

        // CROSSCHAT
        final ConfigurationNode crosschatNode = yaml.node("crosschat");
        // one
        final ConfigurationNode crosschatOne = crosschatNode.node("one");
        crosschatOneGuild = crosschatOne.node("guild").getLong();
        crosschatOneChannel = crosschatOne.node("channel").getLong();
        // two
        final ConfigurationNode crosschatTwo = crosschatNode.node("two");
        crosschatTwoGuild = crosschatTwo.node("guild").getLong();
        crosschatTwoChannel = crosschatTwo.node("channel").getLong();

        // APPROVALS
        final ConfigurationNode approvalsNode = yaml.node("approvals");
        for (final ConfigurationNode node : approvalsNode.childrenMap().values()) {
            Mapper.toLong(node.key()).ifPresent(aLong -> approvals.add(new Approval(bot.jda, aLong, node.node("role").getLong(), node.node("channel").getLong())));
        }
    }

    @NotNull
    public Optional<Guild> getCrosschatOneGuild() {
        return Optional.ofNullable(bot.jda.getGuildById(crosschatOneGuild));
    }

    @NotNull
    public Optional<TextChannel> getCrosschatOneChannel() {
        return getCrosschatOneGuild().map(value -> value.getChannelById(TextChannel.class, crosschatOneChannel));
    }

    @NotNull
    public Optional<Guild> getCrosschatTwoGuild() {
        return Optional.ofNullable(bot.jda.getGuildById(crosschatTwoGuild));
    }

    @NotNull
    public Optional<TextChannel> getCrosschatTwoChannel() {
        return getCrosschatTwoGuild().map(value -> value.getChannelById(TextChannel.class, crosschatTwoChannel));
    }

    @NotNull
    public Optional<Approval> getApprovalFromGuild(long guildId) {
        return approvals.stream()
                .filter(approval -> approval.guildId() == guildId)
                .findFirst();
    }

    @NotNull
    public Optional<Approval> getApprovalFromChannel(long channelId) {
        return approvals.stream()
                .filter(approval -> approval.channelId() == channelId)
                .findFirst();
    }

    public boolean checkNotOwner(@NotNull IReplyCallback event) {
        final boolean notOwner = !bot.isOwner(event.getUser().getIdLong());
        if (notOwner) event.replyEmbeds(LazyEmbed.noPermission().build(bot)).setEphemeral(true).queue();
        return notOwner;
    }
}
