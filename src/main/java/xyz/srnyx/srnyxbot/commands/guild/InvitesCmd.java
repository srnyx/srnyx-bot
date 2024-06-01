package xyz.srnyx.srnyxbot.commands.guild;

import com.freya02.botcommands.api.annotations.CommandMarker;
import com.freya02.botcommands.api.annotations.Dependency;
import com.freya02.botcommands.api.annotations.UserPermissions;
import com.freya02.botcommands.api.application.ApplicationCommand;
import com.freya02.botcommands.api.application.CommandScope;
import com.freya02.botcommands.api.application.annotations.AppOption;
import com.freya02.botcommands.api.application.slash.GuildSlashEvent;
import com.freya02.botcommands.api.application.slash.annotations.ChannelTypes;
import com.freya02.botcommands.api.application.slash.annotations.JDASlashCommand;
import com.freya02.botcommands.api.application.slash.annotations.LongRange;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildChannel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.srnyxbot.SrnyxBot;


@CommandMarker @UserPermissions(Permission.ADMINISTRATOR)
public class InvitesCmd extends ApplicationCommand {
    @Dependency private SrnyxBot bot;

    @JDASlashCommand(
            scope = CommandScope.GUILD,
            name = "invites",
            description = "Creates multiple single-use invites for the specified channel")
    public void invitesCommand(@NotNull GuildSlashEvent event,
                               @AppOption(description = "The amount of invites to create (max 50)") @LongRange(from = 1, to = 50) int amount,
                               @AppOption(description = "The channel to create invites for") @ChannelTypes({ChannelType.NEWS, ChannelType.TEXT, ChannelType.VOICE, ChannelType.STAGE, ChannelType.FORUM}) @Nullable GuildChannel channel) {
        if (bot.config.checkNotOwner(event, event.getUser().getIdLong())) return;
        event.deferReply().queue();
        final StringBuilder builder = new StringBuilder();
        final StandardGuildChannel standardChannel = channel == null ? (StandardGuildChannel) event.getChannel() : (StandardGuildChannel) channel;
        for (int i = amount, age = 604800; i > 0; i--, age--) builder.append("<").append(standardChannel.createInvite().setMaxUses(1).setMaxAge(age).complete().getUrl()).append(">").append("\n");
        event.getHook().editOriginal(builder.toString()).queue();
    }
}