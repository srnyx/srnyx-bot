package xyz.srnyx.srnyxbot.config;

import io.github.freya022.botcommands.api.core.service.annotations.BService;

import net.dv8tion.jda.api.entities.Guild;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.spongepowered.configurate.ConfigurationNode;

import xyz.srnyx.javautilities.manipulation.Mapper;

import xyz.srnyx.lazylibrary.LazyLibrary;
import xyz.srnyx.lazylibrary.config.LazyRole;

import xyz.srnyx.srnyxbot.SrnyxBot;

import java.util.*;
import java.util.function.Supplier;


@BService
public class SrnyxConfig {
    @NotNull private final SrnyxBot bot;

    @NotNull public final PlayHosting playHosting;
    @NotNull public final Map<Long, Advertising> advertising = new HashMap<>();

    // FRIENDS
    public final long friendsGuild;
    public final long friendsWaiting;
    public final long friendsVc;

    // APPROVALS
    @NotNull public final Set<Approval> approvals = new HashSet<>();

    public SrnyxConfig(@NotNull SrnyxBot bot) {
        this.bot = bot;
        final ConfigurationNode yaml = LazyLibrary.INSTANCE.fileSettings.file.yaml;

        playHosting = new PlayHosting(yaml.node("play-hosting"));

        // advertising
        final ConfigurationNode advertisingNode = yaml.node("advertising");
        for (final ConfigurationNode node : advertisingNode.childrenMap().values()) {
            final long guildId = Mapper.toLong(node.key()).orElseThrow(() -> new IllegalArgumentException("Invalid guild ID in advertising config: " + node.key()));
            advertising.put(guildId, new Advertising(guildId, node));
        }

        // FRIENDS
        final ConfigurationNode friendsNode = yaml.node("friends");
        friendsGuild = friendsNode.node("guild").getLong();
        friendsWaiting = friendsNode.node("waiting").getLong();
        friendsVc = friendsNode.node("vc").getLong();

        // APPROVALS
        final ConfigurationNode approvalsNode = yaml.node("approvals");
        for (final ConfigurationNode node : approvalsNode.childrenMap().values()) {
            Mapper.toLong(node.key()).ifPresent(aLong -> approvals.add(new Approval(bot.jda, aLong, node.node("role").getLong(), node.node("channel").getLong())));
        }
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

    public class PlayHosting implements Supplier<Guild> {
        @Nullable public final String token;
        public final long guildId;
        @NotNull public final LazyRole support;

        public PlayHosting(@NotNull ConfigurationNode node) {
            this.token = node.node("token").getString();
            this.guildId = node.node("guild").getLong();
            this.support = new LazyRole(this, node.node("support"));
        }

        @Override @NotNull
        public Guild get() {
            return Objects.requireNonNull(bot.jda.getGuildById(guildId));
        }
    }

    public class Advertising implements Supplier<Guild> {
        public final long guildId;
        @NotNull public final String invite;
        @NotNull public final LazyRole role;

        public Advertising(long guildId, @NotNull ConfigurationNode node) {
            this.guildId = guildId;
            this.invite = ".gg/" + Objects.requireNonNull(node.node("invite").getString());
            this.role = new LazyRole(this, node.node("role"));
        }

        @Override @NotNull
        public Guild get() {
            return Objects.requireNonNull(bot.jda.getGuildById(guildId));
        }
    }
}
