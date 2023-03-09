package xyz.srnyx.srnyxbot;

import com.freya02.botcommands.api.application.slash.GuildSlashEvent;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.NodeStyle;


public class SrnyxConfig {
    private JDA jda;
    @NotNull public final SrnyxFile file = new SrnyxFile("config", NodeStyle.BLOCK, true);

    @Nullable public final String token = file.yaml.node("token").getString();
    public final long owner = file.yaml.node("owner").getLong();

    // FRIENDS
    @NotNull private final ConfigurationNode friendsNode = file.yaml.node("friends");
    public final long friendsGuild = friendsNode.node("guild").getLong();
    public final long friendsWaiting = friendsNode.node("waiting").getLong();
    public final long friendsVc = friendsNode.node("vc").getLong();

    // CROSSCHAT
    @NotNull private final ConfigurationNode crosschatNode = file.yaml.node("crosschat");
    // one
    @NotNull public final ConfigurationNode crosschatOne = crosschatNode.node("one");
    public final long crosschatOneGuild = crosschatOne.node("guild").getLong();
    public final long crosschatOneChannel = crosschatOne.node("channel").getLong();
    // two
    @NotNull public final ConfigurationNode crosschatTwo = crosschatNode.node("two");
    public final long crosschatTwoGuild = crosschatTwo.node("guild").getLong();
    public final long crosschatTwoChannel = crosschatTwo.node("channel").getLong();

    public void setJda(@NotNull JDA jda) {
        this.jda = jda;
    }

    @Nullable
    public User getOwner() {
        return jda.getUserById(owner);
    }

    @Nullable
    public Guild getFriendsGuild() {
        return jda.getGuildById(friendsGuild);
    }

    @Nullable
    public AudioChannel getFriendsWaiting() {
        final Guild guild = getFriendsGuild();
        if (guild == null) return null;
        return guild.getChannelById(AudioChannel.class, friendsWaiting);
    }

    @Nullable
    public AudioChannel getFriendsVc() {
        final Guild guild = getFriendsGuild();
        if (guild == null) return null;
        return guild.getChannelById(AudioChannel.class, friendsVc);
    }

    @Nullable
    public Guild getCrosschatOneGuild() {
        return jda.getGuildById(crosschatOneGuild);
    }

    @Nullable
    public TextChannel getCrosschatOneChannel() {
        final Guild guild = getCrosschatOneGuild();
        if (guild == null) return null;
        return guild.getChannelById(TextChannel.class, crosschatOneChannel);
    }

    @Nullable
    public Guild getCrosschatTwoGuild() {
        return jda.getGuildById(crosschatTwoGuild);
    }

    @Nullable
    public TextChannel getCrosschatTwoChannel() {
        final Guild guild = getCrosschatTwoGuild();
        if (guild == null) return null;
        return guild.getChannelById(TextChannel.class, crosschatTwoChannel);
    }

    public boolean checkOwner(@NotNull GuildSlashEvent event, long id) {
        final boolean isOwner = id == owner;
        if (!isOwner) event.reply("You must be <@" + id + "> to use this command!").setEphemeral(true).queue();
        return isOwner;
    }
}
