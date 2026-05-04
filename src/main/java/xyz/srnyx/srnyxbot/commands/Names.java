package xyz.srnyx.srnyxbot.commands;

import io.github.freya022.botcommands.api.commands.annotations.Command;
import io.github.freya022.botcommands.api.commands.annotations.UserPermissions;
import io.github.freya022.botcommands.api.commands.application.CommandScope;
import io.github.freya022.botcommands.api.commands.application.slash.GuildSlashEvent;
import io.github.freya022.botcommands.api.commands.application.slash.annotations.JDASlashCommand;
import io.github.freya022.botcommands.api.commands.application.slash.annotations.SlashOption;
import io.github.freya022.botcommands.api.commands.application.slash.annotations.TopLevelSlashCommandData;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.lazylibrary.emoji.LazyEmoji;


@Command
public class Names {
    @TopLevelSlashCommandData(scope = CommandScope.GUILD)
    @UserPermissions({Permission.NICKNAME_MANAGE, Permission.MANAGE_SERVER})
    @JDASlashCommand(
            name = "names",
            description = "Get all members with a specific name")
    public void names(@NotNull GuildSlashEvent event,
                      @SlashOption(description = "The substring to check for") @NotNull String substring) {
        event.deferReply(true).queue();
        event.getGuild().loadMembers()
                .onSuccess(members -> {
                    // Get members
                    final StringBuilder builder = new StringBuilder();
                    for (final Member member : members) if (member.getEffectiveName().contains(substring)) builder.append(member.getAsMention()).append("\n");
                    if (builder.isEmpty()) {
                        event.getHook().editOriginal(LazyEmoji.NO + " No members with that substring in their name!").queue();
                        return;
                    }

                    // Reply
                    event.getHook().editOriginal(builder.toString()).queue();
                });
    }
}
