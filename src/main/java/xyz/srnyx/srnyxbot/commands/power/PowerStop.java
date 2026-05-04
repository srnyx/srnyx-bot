package xyz.srnyx.srnyxbot.commands.power;

import io.github.freya022.botcommands.api.commands.annotations.Command;
import io.github.freya022.botcommands.api.commands.application.slash.GlobalSlashEvent;
import io.github.freya022.botcommands.api.commands.application.slash.annotations.JDASlashCommand;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.lazylibrary.emoji.LazyEmoji;
import xyz.srnyx.lazylibrary.LazyLibrary;
import xyz.srnyx.lazylibrary.services.power.BotPower;


@Command
public class PowerStop {
    @NotNull private final LazyLibrary library;
    @NotNull private final BotPower power;

    public PowerStop(@NotNull LazyLibrary library, @NotNull BotPower power) {
        this.library = library;
        this.power = power;
    }

    @JDASlashCommand(
            name = "power",
            subcommand = "stop",
            description = "Stops the bot")
    public void stop(@NotNull GlobalSlashEvent event) {
        if (!library.checkNotOwner(event)) event.reply(LazyEmoji.YES + " Stopping the bot...").setEphemeral(true).queue(_ -> power.gracefulStop());
    }
}
