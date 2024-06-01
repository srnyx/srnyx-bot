package xyz.srnyx.srnyxbot.config;

import com.freya02.botcommands.api.application.slash.GuildSlashEvent;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.spongepowered.configurate.ConfigurationNode;

import xyz.srnyx.javautilities.manipulation.Mapper;

import xyz.srnyx.lazylibrary.LazyEmbed;
import xyz.srnyx.srnyxbot.SrnyxBot;

import java.util.HashSet;
import java.util.Set;


public class SrnyxConfig {
    @NotNull private final SrnyxBot bot;

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
        for (final ConfigurationNode node : yaml.node("approvals").childrenMap().values()) {
            final Long guildId = Mapper.toLong(node.key());
            if (guildId == null) continue;
            final Approval approval = new Approval(bot.jda, guildId, node.node("role").getLong(), node.node("channel").getLong());
            if (!approval.isNull()) approvals.add(approval);
        }
    }

    @Nullable
    public Guild getCrosschatOneGuild() {
        return bot.jda.getGuildById(crosschatOneGuild);
    }

    @Nullable
    public TextChannel getCrosschatOneChannel() {
        final Guild guild = getCrosschatOneGuild();
        if (guild == null) return null;
        return guild.getChannelById(TextChannel.class, crosschatOneChannel);
    }

    @Nullable
    public Guild getCrosschatTwoGuild() {
        return bot.jda.getGuildById(crosschatTwoGuild);
    }

    @Nullable
    public TextChannel getCrosschatTwoChannel() {
        final Guild guild = getCrosschatTwoGuild();
        if (guild == null) return null;
        return guild.getChannelById(TextChannel.class, crosschatTwoChannel);
    }

    @Nullable
    public Approval getApprovalFromGuild(long guildId) {
        return approvals.stream()
                .filter(approval -> approval.guildId() == guildId)
                .findFirst()
                .orElse(null);
    }

    @Nullable
    public Approval getApprovalFromChannel(long channelId) {
        return approvals.stream()
                .filter(approval -> approval.channelId() == channelId)
                .findFirst()
                .orElse(null);
    }

    public boolean checkNotOwner(@NotNull GuildSlashEvent event, long id) {
        final boolean notOwner = !bot.isOwner(id);
        if (notOwner) event.replyEmbeds(LazyEmbed.noPermission().build(bot)).setEphemeral(true).queue();
        return notOwner;
    }
}
