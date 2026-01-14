package xyz.srnyx.srnyxbot.commands.mass;

import io.github.freya022.botcommands.api.commands.annotations.Command;
import io.github.freya022.botcommands.api.commands.application.ApplicationCommand;
import io.github.freya022.botcommands.api.commands.application.CommandScope;
import io.github.freya022.botcommands.api.commands.application.slash.GuildSlashEvent;
import io.github.freya022.botcommands.api.commands.application.slash.annotations.JDASlashCommand;
import io.github.freya022.botcommands.api.commands.application.slash.annotations.SlashOption;
import io.github.freya022.botcommands.api.commands.application.slash.annotations.TopLevelSlashCommandData;
import io.github.freya022.botcommands.api.components.Buttons;

import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.section.Section;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.entities.channel.attribute.ICategorizableChannel;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.lazylibrary.LazyEmoji;
import xyz.srnyx.lazylibrary.LazyLibrary;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


@Command
public class MassSay extends ApplicationCommand {
    @NotNull private final LazyLibrary library;
    @NotNull private final Buttons buttons;

    public MassSay(@NotNull LazyLibrary library, @NotNull Buttons buttons) {
        this.library = library;
        this.buttons = buttons;
    }

    @TopLevelSlashCommandData(scope = CommandScope.GUILD, defaultLocked = true)
    @JDASlashCommand(
            group = "mass",
            name = "message",
            description = "SRNYX | Sends a message in every channel of a category")
    public void massSay(@NotNull GuildSlashEvent event,
                        @SlashOption(description = "The message to send") @NotNull String message,
                        @SlashOption(description = "The category of the channels (empty for current)") @Nullable Category category) {
        if (library.checkNotOwner(event)) return;

        // Get category
        if (category == null) {
            category = ((ICategorizableChannel) event.getChannel()).getParentCategory();
            if (category == null) {
                event.reply(LazyEmoji.NO + " You must either specify a category or use this command in a channel within a category!").setEphemeral(true).queue();
                return;
            }
        }
        final String categoryName = "**" + category.getName() + "**";
        final List<GuildChannel> channels = category.getChannels();
        final String channelCount = "**" + channels.size() + "**";

        // Confirmation
        final AtomicBoolean cancel = new AtomicBoolean(false);
        event.replyComponents(
                TextDisplay.of(LazyEmoji.WARNING + " Are you sure you want to send the following message to all " + channelCount + " channels in " + categoryName + "?\n\n" + message),
                ActionRow.of(
                        buttons.success("Yes, send messages", LazyEmoji.YES_CLEAR.emoji).ephemeral()
                                .bindTo(button -> {
                                    // Add cancel button
                                    button.editComponents(Section.of(
                                            buttons.danger("Cancel", LazyEmoji.NO_CLEAR_DARK.emoji).ephemeral()
                                                    .bindTo(cancelButton -> {
                                                        cancel.set(true);
                                                        cancelButton.editComponents(TextDisplay.of(LazyEmoji.NO + " Mass message for " + categoryName + " cancelled!")).queue();
                                                    }).build(),
                                            TextDisplay.of(LazyEmoji.YES + " Sending message to all " + channelCount + " channels in " + categoryName + "..."))).useComponentsV2().queue();

                                    // Send message to all channels in category
                                    for (final GuildChannel channel : channels) {
                                        if (channel instanceof MessageChannel messageChannel) {
                                            if (cancel.get()) break;
                                            messageChannel.sendMessage(message).setCheck(() -> !cancel.get()).queue();
                                        }
                                    }
                                }).build(),
                        buttons.danger("No, DON'T send messages", LazyEmoji.NO_CLEAR_DARK.emoji).ephemeral()
                                .bindTo(button -> button.editComponents(TextDisplay.of(LazyEmoji.NO + " Mass message for " + categoryName + " cancelled!")).useComponentsV2().queue())
                                .build())).setEphemeral(true).useComponentsV2().queue();
    }
}
