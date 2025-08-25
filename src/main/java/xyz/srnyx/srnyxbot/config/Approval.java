package xyz.srnyx.srnyxbot.config;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;


public record Approval(@NotNull JDA jda, long guildId, long roleId, long channelId) {
    @NotNull
    public Optional<Guild> getGuild() {
        return Optional.ofNullable(jda.getGuildById(guildId));
    }

    @NotNull
    public Optional<Role> getRole() {
        return getGuild().map(guild -> guild.getRoleById(roleId));
    }

    @NotNull
    public Optional<TextChannel> getChannel() {
        return getGuild().map(guild -> guild.getTextChannelById(channelId));
    }
}
