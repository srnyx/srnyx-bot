package xyz.srnyx.srnyx_bot.listeners;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import xyz.srnyx.srnyx_bot.Main;

import java.util.Collections;


public class MessageListener extends ListenerAdapter {
    /**
     * Called when a message is sent in a guild
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        final Message message = event.getMessage();
        final String channel = event.getChannel().getId();

        if (event.getAuthor().isBot()) return;

        if (channel.equals(Main.getConfig("crosschat_two_channel"))) {
            final String guildConfig = Main.getConfig("crosschat_one_guild");
            final String channelConfig = Main.getConfig("crosschat_one_channel");
            if ((guildConfig == null || guildConfig.equals("GUILD_ID_HERE")) || (channelConfig == null || channelConfig.equals("TC_ID_HERE"))) {
                Main.log.severe("Invalid config for crosschat_one");
                return;
            }

            final Guild guildOne = event.getJDA().getGuildById(guildConfig);
            if (guildOne == null) return;
            final TextChannel channelOne = guildOne.getTextChannelById(channelConfig);
            if (channelOne == null) return;

            send(message, channelOne);
            return;
        }

        if (channel.equals(Main.getConfig("crosschat_one_channel"))) {
            final String guildConfig = Main.getConfig("crosschat_two_guild");
            final String channelConfig = Main.getConfig("crosschat_two_channel");
            if ((guildConfig == null || guildConfig.equals("GUILD_ID_HERE")) || (channelConfig == null || channelConfig.equals("TC_ID_HERE"))) {
                Main.log.severe("Invalid config for crosschat_two");
                return;
            }

            final Guild guildTwo = event.getJDA().getGuildById(guildConfig);
            if (guildTwo == null) return;
            final TextChannel channelTwo = guildTwo.getTextChannelById(channelConfig);
            if (channelTwo == null) return;

            send(message, channelTwo);
        }
    }

    /**
     * Sends a cross-chat message to a channel
     *
     * @param   message Message to send
     * @param   channel Channel to send to
     */
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

    /**
     * Replies to a cross-chat message
     *
     * @param   reply   Message to reply to
     * @param   channel Channel to reply to
     *
     * @return          Message that was sent
     */
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
}
