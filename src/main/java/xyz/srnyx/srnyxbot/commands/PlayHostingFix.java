package xyz.srnyx.srnyxbot.commands;

import com.freya02.botcommands.api.annotations.CommandMarker;
import com.freya02.botcommands.api.annotations.Dependency;
import com.freya02.botcommands.api.application.ApplicationCommand;
import com.freya02.botcommands.api.application.slash.GuildSlashEvent;
import com.freya02.botcommands.api.application.slash.annotations.JDASlashCommand;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.lazylibrary.LazyEmoji;

import xyz.srnyx.srnyxbot.SrnyxBot;


@CommandMarker
public class PlayHostingFix extends ApplicationCommand {
    @Dependency private SrnyxBot bot;

    @JDASlashCommand(
            name = "playhostingfix",
            description = "Fixes Play Hosting tickets",
            defaultLocked = true)
    public void playHostingFix(@NotNull GuildSlashEvent event) {
        // Check if owner
        if (bot.config.checkNotOwner(event, event.getUser().getIdLong())) return;

        // Check guild
        final Guild guild = event.getGuild();
        if (guild.getIdLong() != 1332830032480960542L) {
            event.reply(LazyEmoji.NO + " This command is only available in the **Play Hosting** server!").setEphemeral(true).queue();
            return;
        }

        // Get Support role
        final Role role = guild.getRoleById(1332832981328597093L);
        if (role == null) {
            event.reply(LazyEmoji.NO + " Support role not found!").setEphemeral(true).queue();
            return;
        }

        // Get ticket channels (start with ticket_ / limbo_ / creator_)
        for (final TextChannel channel : guild.getTextChannels()) {
            final String name = channel.getName();
            if (name.startsWith("ticket_") || name.startsWith("limbo_") || name.startsWith("creator_")) {
                channel.upsertPermissionOverride(role).grant(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_HISTORY).queue();
            }
        }

        event.reply(LazyEmoji.YES + " Fixing tickets...").setEphemeral(true).queue();
    }
}
