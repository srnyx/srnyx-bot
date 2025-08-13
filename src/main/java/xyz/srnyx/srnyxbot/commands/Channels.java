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
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
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
        if (bot.config.checkNotOwner(event)) return;

        // Check bot permissions
        final Member selfMember = event.getGuild().getSelfMember();
        if (!selfMember.hasPermission(Permission.MANAGE_CHANNEL)) {
            event.reply(LazyEmoji.NO + " I need the `" + Permission.MANAGE_CHANNEL.getName() + "` permission to do this!").setEphemeral(true).queue();
            return;
        }

        // Confirmation
        final Long categoryId = category != null ? category.getIdLong() : null;
        event.reply(LazyEmoji.WARNING + " Are you sure you want to create a private channel for **EACH** member in this server?")
                .setEphemeral(true)
                .addActionRow(
                        Components.successButton(yes -> {
                            final Guild guild =  Objects.requireNonNull(yes.getGuild());

                            // Get Category
                            Category yesCategory = null;
                            if (categoryId != null) {
                                yesCategory = guild.getCategoryById(categoryId);
                                if (yesCategory == null) {
                                    yes.editMessage(LazyEmoji.NO + " The specified category no longer exists!").setComponents().queue();
                                    return;
                                }
                            }
                            final Category finalYesCategory = yesCategory;

                            // Defer edit
                            yes.deferEdit().queue();
                            final InteractionHook hook = yes.getHook();

                            // Create channels
                            guild.loadMembers()
                                    .onSuccess(members -> {
                                        int created = 0;
                                        final long selfId = selfMember.getIdLong();
                                        for (final Member member : members) {
                                            final User user = member.getUser();
                                            if (user.isBot()) continue;
                                            final long userId = user.getIdLong();
                                            if (userId == selfId) continue;
                                            created++;
                                            guild.createTextChannel(user.getName(), finalYesCategory)
                                                    .addMemberPermissionOverride(userId, Permission.VIEW_CHANNEL.getRawValue(), 0)
                                                    .queue();
                                        }
                                        hook.editOriginal(LazyEmoji.YES + " Created private channels for **" + created + "** members").setComponents().queue();
                                    })
                                    .onError(error -> hook.editOriginal("Failed to load members: " + error.getMessage()).queue());
                        }).build(LazyEmoji.YES_CLEAR.getButtonContent("Yes, create channels")),
                        Components.dangerButton(no -> no.editMessage(LazyEmoji.YES_CLEAR + " Cancelled channel creation").setComponents().queue())
                                .build(LazyEmoji.NO_CLEAR_DARK.getButtonContent("No, cancel")))
                .queue();
    }
}
