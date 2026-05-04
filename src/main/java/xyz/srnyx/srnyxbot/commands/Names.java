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
import net.dv8tion.jda.api.utils.FileUpload;

import org.jetbrains.annotations.NotNull;


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
                    final StringBuilder builder = new StringBuilder("Name,ID,Mention");
                    for (final Member member : members) if (member.getEffectiveName().contains(substring)) builder.append("\n").append(member.getEffectiveName()).append(",").append(member.getId()).append(",").append(member.getAsMention());
                    event.getHook().editOriginalAttachments(FileUpload.fromData(builder.toString().getBytes(), "names_" + substring + ".csv")).queue();
                });
    }
}
