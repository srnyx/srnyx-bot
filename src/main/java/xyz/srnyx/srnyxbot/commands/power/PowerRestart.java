package xyz.srnyx.srnyxbot.commands.power;

import io.github.freya022.botcommands.api.commands.annotations.Command;
import io.github.freya022.botcommands.api.commands.application.CommandScope;
import io.github.freya022.botcommands.api.commands.application.slash.GlobalSlashEvent;
import io.github.freya022.botcommands.api.commands.application.slash.annotations.JDASlashCommand;
import io.github.freya022.botcommands.api.commands.application.slash.annotations.TopLevelSlashCommandData;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.lazylibrary.emoji.LazyEmoji;
import xyz.srnyx.lazylibrary.LazyLibrary;
import xyz.srnyx.lazylibrary.services.power.BotPower;


@Command
public class PowerRestart {
    @NotNull private final LazyLibrary library;
    @NotNull private final BotPower power;

    public PowerRestart(@NotNull LazyLibrary library, @NotNull BotPower power) {
        this.library = library;
        this.power = power;
    }

    @TopLevelSlashCommandData(scope = CommandScope.GLOBAL)
    @JDASlashCommand(
            name = "power",
            subcommand = "restart",
            description = "Restarts the bot")
    public void restart(@NotNull GlobalSlashEvent event) {
        if (!library.checkNotOwner(event)) event.reply(LazyEmoji.YES + " Restarting the bot...").setEphemeral(true).queue(_ -> power.gracefulRestart());
    }
}
