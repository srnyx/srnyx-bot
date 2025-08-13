package xyz.srnyx.srnyxbot.commands;

import com.freya02.botcommands.api.annotations.CommandMarker;
import com.freya02.botcommands.api.annotations.Dependency;
import com.freya02.botcommands.api.annotations.UserPermissions;
import com.freya02.botcommands.api.application.ApplicationCommand;
import com.freya02.botcommands.api.application.CommandScope;
import com.freya02.botcommands.api.application.annotations.AppOption;
import com.freya02.botcommands.api.application.slash.GuildSlashEvent;
import com.freya02.botcommands.api.application.slash.annotations.JDASlashCommand;
import com.freya02.botcommands.api.components.Components;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.interactions.InteractionHook;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.lazylibrary.LazyEmoji;
import xyz.srnyx.srnyxbot.SrnyxBot;

import java.util.Objects;


@CommandMarker @UserPermissions(Permission.ADMINISTRATOR)
public class Channels extends ApplicationCommand {
    @Dependency private SrnyxBot bot;

    @JDASlashCommand(
            scope = CommandScope.GUILD,
            name = "channels",
            description = "SRNYX | Create a private channel for each person in the server")
    public void channels(@NotNull GuildSlashEvent event,
                         @AppOption(description = "The category to create the channels in (inherits permissions)") @Nullable Category category) {
        if (!bot.config.checkNotOwner(event)) event.reply(LazyEmoji.WARNING + " Are you sure you want to create a private channel for **EACH** member in this server?")
                .setEphemeral(true)
                .addActionRow(
                        Components.successButton(yes -> {
                            // Defer edit
                            yes.deferEdit().queue();
                            final InteractionHook hook = yes.getHook();

                            // Create channels
                            Objects.requireNonNull(yes.getGuild()).loadMembers().onSuccess(members -> {
                                members.forEach(member -> member.getGuild().createTextChannel(member.getUser().getName(), category)
                                        .addMemberPermissionOverride(member.getIdLong(), Permission.VIEW_CHANNEL.getRawValue(), 0)
                                        .queue());
                                hook.editOriginal("Created private channels for " + members.size() + " members.").queue();
                            }).onError(error -> hook.editOriginal("Failed to load members: " + error.getMessage()).queue());
                        }).build(LazyEmoji.YES_CLEAR.getButtonContent("Yes, create channels")),
                        Components.dangerButton(no -> no.editMessage(LazyEmoji.YES_CLEAR + " Cancelled channel creation").setComponents().queue())
                                .build(LazyEmoji.NO_CLEAR.getButtonContent("No, cancel")))
                .queue();
    }
}
