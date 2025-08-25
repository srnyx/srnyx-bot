package xyz.srnyx.srnyxbot.commands;

import io.github.freya022.botcommands.api.commands.annotations.Command;
import io.github.freya022.botcommands.api.commands.application.ApplicationCommand;
import io.github.freya022.botcommands.api.commands.application.CommandScope;
import io.github.freya022.botcommands.api.commands.application.slash.GuildSlashEvent;
import io.github.freya022.botcommands.api.commands.application.slash.annotations.*;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildChannel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.srnyxbot.config.SrnyxConfig;


@Command
public class Invites extends ApplicationCommand {
    @NotNull private final SrnyxConfig config;

    public Invites(@NotNull SrnyxConfig config) {
        this.config = config;
    }

    @TopLevelSlashCommandData(
            scope = CommandScope.GUILD,
            defaultLocked = true)
    @JDASlashCommand(
            name = "invites",
            description = "SRNYX | Create multiple single-use invites for the specified channel")
    public void invites(@NotNull GuildSlashEvent event,
                        @SlashOption(description = "The amount of invites to create (max 50)") @LongRange(from = 1, to = 50) int amount,
                        @SlashOption(description = "The channel to create invites for") @ChannelTypes({ChannelType.NEWS, ChannelType.TEXT, ChannelType.VOICE, ChannelType.STAGE, ChannelType.FORUM}) @Nullable GuildChannel channel) {
        if (config.checkNotOwner(event)) return;
        event.deferReply().queue();
        final StringBuilder builder = new StringBuilder();
        final StandardGuildChannel standardChannel = channel == null ? (StandardGuildChannel) event.getChannel() : (StandardGuildChannel) channel;
        for (int i = amount, age = 604800; i > 0; i--, age--) builder.append("<").append(standardChannel.createInvite().setMaxUses(1).setMaxAge(age).complete().getUrl()).append(">").append("\n");
        event.getHook().editOriginal(builder.toString()).queue();
    }
}