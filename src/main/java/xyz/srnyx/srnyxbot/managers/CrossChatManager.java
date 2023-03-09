package xyz.srnyx.srnyxbot.managers;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.srnyxbot.SrnyxBot;

import java.util.Collections;


public class CrossChatManager {
    @NotNull private final Message message;

    /**
     * Constructor for CrossChatManager
     *
     * @param   message Message to send
     */
    public CrossChatManager(@NotNull SrnyxBot bot, @NotNull Message message) {
        this.message = message;
        final long channel = message.getChannel().getIdLong();

        if (channel == bot.config.crosschatTwoChannel) {
            final MessageChannel otherChannel = bot.config.getCrosschatOneChannel();
            if (otherChannel != null) send(otherChannel);
            return;
        }

        if (channel == bot.config.crosschatOneChannel) {
            final MessageChannel otherChannel = bot.config.getCrosschatTwoChannel();
            if (otherChannel != null) send(otherChannel);
        }
    }

    /**
     * Sends a cross-chat message to a channel
     *
     * @param   channel Channel to send to
     */
    private void send(@NotNull MessageChannel channel) {
        final User user = message.getAuthor();
        final String messageContent = message.getContentRaw();

        if (message.getType().equals(MessageType.INLINE_REPLY)) {
            final Message reply = message.getReferencedMessage();
            if (reply == null) return;
            final Message reply2 = reply(reply, channel);
            if (reply2 != null) reply2.reply("**`" + user.getAsTag() + "`** " + messageContent)
                    .setAllowedMentions(Collections.singleton(Message.MentionType.CHANNEL))
                    .mentionRepliedUser(false)
                    .queue();
            return;
        }

        channel.sendMessage("**`" + user.getAsTag() + "`** " + messageContent)
                .setAllowedMentions(Collections.singleton(Message.MentionType.CHANNEL))
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
    @Nullable
    private Message reply(@NotNull Message reply, @NotNull MessageChannel channel) {
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
