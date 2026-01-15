package xyz.srnyx.srnyxbot.commands;

import io.github.freya022.botcommands.api.commands.annotations.Command;
import io.github.freya022.botcommands.api.commands.application.ApplicationCommand;
import io.github.freya022.botcommands.api.commands.application.CommandScope;
import io.github.freya022.botcommands.api.commands.application.slash.GuildSlashEvent;
import io.github.freya022.botcommands.api.commands.application.slash.annotations.JDASlashCommand;
import io.github.freya022.botcommands.api.commands.application.slash.annotations.SlashOption;
import io.github.freya022.botcommands.api.commands.application.slash.annotations.TopLevelSlashCommandData;
import io.github.freya022.botcommands.api.components.Buttons;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.interactions.InteractionHook;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.lazylibrary.LazyEmoji;
import xyz.srnyx.lazylibrary.LazyLibrary;

import java.util.Objects;


@Command
public class Channels extends ApplicationCommand {
    @NotNull private final LazyLibrary library;
    @NotNull private final Buttons buttons;

    public Channels(@NotNull LazyLibrary library, @NotNull Buttons buttons) {
        this.library = library;
        this.buttons = buttons;
    }

    @TopLevelSlashCommandData(scope = CommandScope.GUILD, defaultLocked = true)
    @JDASlashCommand(
            name = "channels",
            description = "SRNYX | Create a private channel for each person in the server")
    public void channels(@NotNull GuildSlashEvent event,
                         @SlashOption(description = "The category to create the channels in (inherits permissions)") @Nullable Category category) {
        if (library.checkNotOwner(event)) return;

        // Check bot permissions
        final Member selfMember = event.getGuild().getSelfMember();
        if (!selfMember.hasPermission(Permission.MANAGE_CHANNEL)) {
            event.reply(LazyEmoji.NO + " I need the `" + Permission.MANAGE_CHANNEL.getName() + "` permission to do this!").queue();
            return;
        }

        // Confirmation
        final Long categoryId = category != null ? category.getIdLong() : null;
        event.replyComponents(
                TextDisplay.of(LazyEmoji.WARNING + " Are you sure you want to create a private channel for **EACH** member in this server?"),
                ActionRow.of(
                        buttons.success("Yes, create channels", LazyEmoji.YES_CLEAR.emoji).ephemeral().bindTo(yes -> {
                            final Guild guild =  Objects.requireNonNull(yes.getGuild());

                            // Get Category
                            Category yesCategory = null;
                            if (categoryId != null) {
                                yesCategory = guild.getCategoryById(categoryId);
                                if (yesCategory == null) {
                                    yes.editComponents(TextDisplay.of(LazyEmoji.NO + " The specified category no longer exists!")).useComponentsV2().queue();
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
                                        hook.editOriginalComponents(TextDisplay.of(LazyEmoji.YES + " Created private channels for **" + created + "** members")).queue();
                                    })
                                    .onError(error -> hook.editOriginalComponents(TextDisplay.of("Failed to load members: " + error.getMessage())).queue());
                        }).build(),
                        buttons.danger("No, cancel", LazyEmoji.NO_CLEAR_DARK.emoji).ephemeral()
                                .bindTo(no -> no.editComponents(TextDisplay.of(LazyEmoji.YES_CLEAR + " Cancelled channel creation")).queue())
                                .build()))
                .setEphemeral(true).useComponentsV2().queue();
    }
}
