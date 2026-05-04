package xyz.srnyx.srnyxbot.commands;

import io.github.freya022.botcommands.api.commands.annotations.Command;
import io.github.freya022.botcommands.api.commands.application.CommandScope;
import io.github.freya022.botcommands.api.commands.application.slash.GuildSlashEvent;
import io.github.freya022.botcommands.api.commands.application.slash.annotations.*;

import net.dv8tion.jda.api.entities.channel.attribute.IInviteContainer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.lazylibrary.LazyLibrary;

import java.util.Objects;


@Command
public class Invites {
    @NotNull private final LazyLibrary library;

    public Invites(@NotNull LazyLibrary library) {
        this.library = library;
    }

    @TopLevelSlashCommandData(scope = CommandScope.GUILD, defaultLocked = true)
    @JDASlashCommand(
            name = "invites",
            description = "SRNYX | Create multiple single-use invites for the specified channel")
    public void invites(@NotNull GuildSlashEvent event,
                        @SlashOption(description = "The amount of invites to create (max 50)") @LongRange(from = 1, to = 50) int amount,
                        @SlashOption(description = "The channel to create invites for") @Nullable IInviteContainer channel) {
        if (library.checkNotOwner(event)) return;
        event.deferReply().queue();
        final StringBuilder builder = new StringBuilder();
        final IInviteContainer container = Objects.requireNonNullElse(channel, (IInviteContainer) event.getChannel());
        for (int i = amount, age = 604800; i > 0; i--, age--) builder.append("<").append(container.createInvite().setMaxUses(1).setMaxAge(age).complete().getUrl()).append(">").append("\n");
        event.getHook().sendMessage(builder.toString()).queue();
    }
}