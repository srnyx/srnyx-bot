package xyz.srnyx.srnyx_bot.listeners;

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
        // Cross-chat
        final String channel = event.getChannel().getId();
        if (!event.getAuthor().isBot() && (channel.equals(Main.getConfig("crosschat_one_channel")) || channel.equals(Main.getConfig("crosschat_two_channel")))) {
            new CrossChatManager(event.getMessage()).main(channel, event.getJDA());
        }
    }
}
