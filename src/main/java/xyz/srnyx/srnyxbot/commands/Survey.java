package xyz.srnyx.srnyxbot.commands;

import io.github.freya022.botcommands.api.commands.annotations.Command;
import io.github.freya022.botcommands.api.commands.application.ApplicationCommand;
import io.github.freya022.botcommands.api.commands.application.CommandScope;
import io.github.freya022.botcommands.api.commands.application.slash.GuildSlashEvent;
import io.github.freya022.botcommands.api.commands.application.slash.annotations.JDASlashCommand;
import io.github.freya022.botcommands.api.commands.application.slash.annotations.SlashOption;
import io.github.freya022.botcommands.api.commands.application.slash.annotations.TopLevelSlashCommandData;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.lazylibrary.LazyEmoji;

import xyz.srnyx.srnyxbot.config.SrnyxConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


@Command
public class Survey extends ApplicationCommand {
    @NotNull private final SrnyxConfig config;

    public Survey(@NotNull SrnyxConfig config) {
        this.config = config;
    }

    @TopLevelSlashCommandData(
            scope = CommandScope.GUILD,
            defaultLocked = true)
    @JDASlashCommand(
            name = "survey",
            description = "CG | Add survey role to specified users")
    public void surveyCommand(@NotNull GuildSlashEvent event,
                              @SlashOption(description = "The users to add the role to (separate using '=:=')") @NotNull String users) {
        if (config.checkNotOwner(event)) return;
        final Guild guild = event.getGuild();
        if (guild.getIdLong() != 617280459717476353L) {
            event.reply(LazyEmoji.NO + " This command is only available in **CommandGeek Labs**").setEphemeral(true).queue();
            return;
        }
        final Role role = guild.getRoleById(1069366219515371650L);
        if (role == null) return;
        event.deferReply(true).queue();

        // Give role to users
        final List<String> failed = new ArrayList<>();
        for (final String string : users.split("=:=")) {
            // Get Member
            Member member;
            try {
                member = guild.retrieveMemberById(Long.parseLong(string)).complete();
            } catch (final NumberFormatException | ErrorResponseException e) {
                try {
                    member = guild.getMembersByName(string, false).getFirst();
                } catch (final NoSuchElementException e2) {
                    member = null;
                }
            }
            // Failed
            if (member == null) {
                failed.add(string);
                continue;
            }
            // Success, add role
            guild.addRoleToMember(member, role).queue();
        }

        // Get failed users
        final StringBuilder failedString = new StringBuilder();
        final int last = failed.size() - 1;
        for (int i = 0; i < failed.size(); i++) {
            failedString.append("`").append(failed.get(i)).append("`");
            if (i != last) failedString.append(", ");
        }

        // Send message
        String message = LazyEmoji.YES + " Finished adding survey role to users!";
        if (!failed.isEmpty()) message += "\n" + LazyEmoji.NO + " Failed to give to these users: " + failedString;
        event.getHook().editOriginal(message).queue();
    }
}
