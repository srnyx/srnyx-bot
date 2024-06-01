package xyz.srnyx.srnyxbot.config;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public record Approval(@NotNull JDA jda, long guildId, long roleId, long channelId) {
    public boolean isNull() {
        return getRole() == null || getChannel() == null;
    }

    @Nullable
    public Guild getGuild() {
        return jda.getGuildById(guildId);
    }

    @Nullable
    public Role getRole() {
        final Guild guild = getGuild();
        if (guild == null) return null;
        return guild.getRoleById(roleId);
    }

    @Nullable
    public TextChannel getChannel() {
        final Guild guild = getGuild();
        if (guild == null) return null;
        return guild.getTextChannelById(channelId);
    }
}
