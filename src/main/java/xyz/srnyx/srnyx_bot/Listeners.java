package xyz.srnyx.srnyx_bot;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.InviteAction;

import java.util.*;


public class Listeners extends ListenerAdapter {
    /**
     * Called when a slash command is ran
     */
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("invites")) {
            final OptionMapping amount = event.getOption("amount");
            if (amount == null) return;

            final OptionMapping channelOption = event.getOption("channel");
            TextChannel channel = event.getTextChannel();
            if (channelOption != null && channelOption.getAsTextChannel() != null) channel = channelOption.getAsTextChannel();
            final TextChannel finalChannel = channel;

            if (!event.getUser().getId().equals("242385234992037888")) return;

            event.deferReply().queue();

            int i = amount.getAsInt();
            int age = 604800;
            final List<InviteAction> actions = new ArrayList<>();
            while (i > 0) {
                int finalAge = age;
                new Timer().schedule(new TimerTask() {public void run() {
                    actions.add(finalChannel.createInvite().setMaxUses(1).setMaxAge(finalAge));
                }}, 1000);

                age--;
                i--;
            }

            new Timer().schedule(new TimerTask() {public void run() {
                RestAction.allOf(actions).queue(invites -> {
                    final StringBuilder string = new StringBuilder();
                    for (Invite invite : invites) string.append("<").append(invite.getUrl()).append(">").append("\n");
                    event.getChannel().sendMessage(string.toString()).queue();
                });

                event.getHook().deleteOriginal().queue();
            }}, 1000L * amount.getAsInt());
        }
    }

    /**
     * Called when a message is sent in a guild
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        if (event.getChannel().getId().equals(Main.getConfig("crosschat_two_channel"))) {
            final String guildOne = Main.getConfig("crosschat_one_guild");
            final String channelOne = Main.getConfig("crosschat_one_channel");
            if ((guildOne == null || guildOne.equals("GUILD_ID_HERE")) || (channelOne == null || channelOne.equals("TC_ID_HERE"))) {
                Main.log.severe("Invalid config for crosschat_one");
                return;
            }

            final Guild vServer = event.getJDA().getGuildById(guildOne);
            if (vServer == null) return;

            final TextChannel vChannel = vServer.getTextChannelById(channelOne);
            if (vChannel == null) return;

            send(event.getMessage(), vChannel);
        }

        if (event.getChannel().getId().equals(Main.getConfig("crosschat_one_channel"))) {
            final String guildTwo = Main.getConfig("crosschat_two_guild");
            final String channelTwo = Main.getConfig("crosschat_two_channel");
            if ((guildTwo == null || guildTwo.equals("GUILD_ID_HERE")) || (channelTwo == null || channelTwo.equals("TC_ID_HERE"))) {
                Main.log.severe("Invalid config for crosschat_two");
                return;
            }

            final Guild sServer = event.getJDA().getGuildById(guildTwo);
            if (sServer == null) return;

            final TextChannel sChannel = sServer.getTextChannelById(channelTwo);
            if (sChannel == null) return;

            send(event.getMessage(), sChannel);
        }
    }

    private void send(Message message, TextChannel channel) {
        final User user = message.getAuthor();
        final String messageContent = message.getContentRaw();

        if (message.getType().equals(MessageType.INLINE_REPLY)) {
            final Message reply = message.getReferencedMessage();
            if (reply == null) return;

            final Message reply2 = reply(reply, channel);
            if (reply2 == null) return;

            channel.sendMessage("**`" + user.getAsTag() + "`** " + messageContent)
                    .reference(reply2)
                    .mentionRepliedUser(false)
                    .allowedMentions(Collections.singleton(Message.MentionType.CHANNEL))
                    .queue();
            return;
        }

        channel.sendMessage("**`" + user.getAsTag() + "`** " + messageContent)
                .allowedMentions(Collections.singleton(Message.MentionType.CHANNEL))
                .queue();
    }

    private Message reply(Message reply, TextChannel channel) {
        Message reply2 = null;

        if (reply.getAuthor().isBot()) {
            String replyContent = reply.getContentRaw();
            String remove = "";
            for (String split : replyContent.split(" ")) if (split.matches("\\*\\*`.*#\\d\\d\\d\\d`\\*\\*")) remove = split;
            replyContent = replyContent.replace(remove, "").trim();

            for (Message msg : channel.getHistory().retrievePast(10).complete()) if (msg.getContentRaw().trim().equals(replyContent)) reply2 = msg;
        }

        for (Message msg : channel.getHistory().retrievePast(10).complete()) {
            String msgContent = msg.getContentRaw();
            String remove = "";
            for (String split : msgContent.split(" ")) if (split.matches("\\*\\*`.*#\\d\\d\\d\\d`\\*\\*")) remove = split;
            msgContent = msgContent.replace(remove, "").trim();

            if (msgContent.equals(reply.getContentRaw())) reply2 = msg;
        }
        return reply2;
    }

    /**
     * Called when a user joins a voice channel
     */
    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        move(event.getChannelJoined());
    }

    /**
     * Called when a user moves to a new voice channel
     */
    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        move(event.getChannelJoined());
    }

    /**
     * Called when a user leaves a voice channel
     */
    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        move(event.getChannelLeft());
    }

    /**
     * Moves users from either the Waiting for friend VC or the Friends VC to the other one
     *
     * @param   channel Channel they joined/left
     */
    private void move(AudioChannel channel) {
        new java.util.Timer().schedule(new java.util.TimerTask() {
            public void run() {
                final List<Member> members = channel.getMembers();
                final Guild guild = channel.getGuild();

                int size = 0;
                for (final Member member : members) if (!member.getUser().isBot()) size++;

                boolean sizeCheck = false;
                String moveTo = "";
                if (channel.getId().equals(Main.getConfig("friends_vc"))) {
                    sizeCheck = size < 2;
                    moveTo = Main.getConfig("friends_waiting");
                }
                if (channel.getId().equals(Main.getConfig("friends_waiting"))) {
                    sizeCheck = size >= 2;
                    moveTo = Main.getConfig("friends_vc");
                }

                if (sizeCheck && moveTo != null) for (final Member member : members) guild.moveVoiceMember(member, guild.getVoiceChannelById(moveTo)).queue();
            }
        }, 1000);
    }
}
