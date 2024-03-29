package xyz.srnyx.srnyxbot.commands.guild;

import com.freya02.botcommands.api.annotations.CommandMarker;
import com.freya02.botcommands.api.annotations.Dependency;
import com.freya02.botcommands.api.annotations.UserPermissions;
import com.freya02.botcommands.api.application.ApplicationCommand;
import com.freya02.botcommands.api.application.CommandScope;
import com.freya02.botcommands.api.application.annotations.AppOption;
import com.freya02.botcommands.api.application.slash.GuildSlashEvent;
import com.freya02.botcommands.api.application.slash.annotations.JDASlashCommand;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.srnyxbot.SrnyxBot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@CommandMarker @UserPermissions(Permission.ADMINISTRATOR)
public class SurveyCmd extends ApplicationCommand {
    @Dependency private SrnyxBot bot;

    @JDASlashCommand(
            scope = CommandScope.GUILD,
            name = "survey",
            description = "Add survey role to specified users",
            defaultLocked = true)
    public void surveyCommand(@NotNull GuildSlashEvent event,
                              @AppOption(description = "The users to add the role to (separate using '=:=')") @NotNull String users) {
        if (!bot.config.checkOwner(event, event.getUser().getIdLong())) return;
        final Guild guild = event.getGuild();
        if (guild.getIdLong() != 617280459717476353L) {
            event.reply("This command is only available in **CommandGeek Labs**").setEphemeral(true).queue();
            return;
        }
        final Role role = guild.getRoleById(1069366219515371650L);
        if (role == null) return;

        event.reply("Adding survey role to users...").setEphemeral(true).queue();

        // Give role to users
        final List<String> failed = new ArrayList<>();
        final Map<Member, String> guildMemberTags = new HashMap<>();
        guild.loadMembers().onSuccess(members -> members.forEach(member -> guildMemberTags.put(member, member.getUser().getAsTag())));
        for (final String string : users.split("=:=")) {
            // Get Member
            Member member;
            try {
                member = guild.retrieveMemberById(Long.parseLong(string)).complete();
            } catch (final NumberFormatException | ErrorResponseException e) {
                member = guildMemberTags.entrySet().stream()
                        .filter(entry -> entry.getValue().equals(string))
                        .map(Map.Entry::getKey)
                        .findFirst()
                        .orElse(null);
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
        String message = "Finished adding survey role to users!";
        if (!failed.isEmpty()) message += "\nFailed to give to these users: " + failedString;
        event.getHook().editOriginal(message).queue();
    }
}
