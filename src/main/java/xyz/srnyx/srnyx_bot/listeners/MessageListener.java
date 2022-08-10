package xyz.srnyx.srnyx_bot.listeners;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import xyz.srnyx.srnyx_bot.Main;
import xyz.srnyx.srnyx_bot.managers.CrossChatManager;


public class MessageListener extends ListenerAdapter {
    /**
     * Called when a message is sent in a guild
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        final Message message = event.getMessage();
        final String channel = event.getChannel().getId();

        if (event.getAuthor().isBot()) return;

        // Cross-chat
        if (channel.equals(Main.getConfig("crosschat_one_channel")) || channel.equals(Main.getConfig("crosschat_two_channel"))) {
            new CrossChatManager(message).main(channel, event.getJDA());
        }

        // GeekSMP suggestion voting
        if (channel.equals(Main.getConfig("gsmp_suggestions"))) {
            message.addReaction("U+1F44D").queue();
            message.addReaction("U+1F44E").queue();
        }
    }
}
