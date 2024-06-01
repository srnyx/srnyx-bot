package xyz.srnyx.srnyxbot.listeners;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.lazylibrary.LazyListener;

import xyz.srnyx.srnyxbot.SrnyxBot;
import xyz.srnyx.srnyxbot.CrossChatManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class MessageListener extends LazyListener {
    private static final Map<String, Emoji> alphabetEmojis = new HashMap<>();
    static  {
        alphabetEmojis.put("a", Emoji.fromUnicode("U+1F1E6"));
        alphabetEmojis.put("b", Emoji.fromUnicode("U+1F1E7"));
        alphabetEmojis.put("c", Emoji.fromUnicode("U+1F1E8"));
        alphabetEmojis.put("d", Emoji.fromUnicode("U+1F1E9"));
        alphabetEmojis.put("e", Emoji.fromUnicode("U+1F1EA"));
        alphabetEmojis.put("f", Emoji.fromUnicode("U+1F1EB"));
        alphabetEmojis.put("g", Emoji.fromUnicode("U+1F1EC"));
        alphabetEmojis.put("h", Emoji.fromUnicode("U+1F1ED"));
        alphabetEmojis.put("i", Emoji.fromUnicode("U+1F1EE"));
        alphabetEmojis.put("j", Emoji.fromUnicode("U+1F1EF"));
        alphabetEmojis.put("k", Emoji.fromUnicode("U+1F1F0"));
        alphabetEmojis.put("l", Emoji.fromUnicode("U+1F1F1"));
        alphabetEmojis.put("m", Emoji.fromUnicode("U+1F1F2"));
        alphabetEmojis.put("n", Emoji.fromUnicode("U+1F1F3"));
        alphabetEmojis.put("o", Emoji.fromUnicode("U+1F1F4"));
        alphabetEmojis.put("p", Emoji.fromUnicode("U+1F1F5"));
        alphabetEmojis.put("q", Emoji.fromUnicode("U+1F1F6"));
        alphabetEmojis.put("r", Emoji.fromUnicode("U+1F1F7"));
        alphabetEmojis.put("s", Emoji.fromUnicode("U+1F1F8"));
        alphabetEmojis.put("t", Emoji.fromUnicode("U+1F1F9"));
        alphabetEmojis.put("u", Emoji.fromUnicode("U+1F1FA"));
        alphabetEmojis.put("v", Emoji.fromUnicode("U+1F1FB"));
        alphabetEmojis.put("w", Emoji.fromUnicode("U+1F1FC"));
        alphabetEmojis.put("x", Emoji.fromUnicode("U+1F1FD"));
        alphabetEmojis.put("y", Emoji.fromUnicode("U+1F1FE"));
        alphabetEmojis.put("z", Emoji.fromUnicode("U+1F1FF"));
    }

    @NotNull private final SrnyxBot bot;

    public MessageListener(@NotNull SrnyxBot bot) {
        this.bot = bot;
    }

    /**
     * Indicates that a Message was received in a {@link net.dv8tion.jda.api.entities.channel.middleman.MessageChannel MessageChannel}.
     * <br>This includes {@link TextChannel TextChannel} and {@link PrivateChannel PrivateChannel}!
     *
     * <p>Can be used to detect that a Message is received in either a guild- or private channel. Providing a MessageChannel and Message.
     *
     * <p><b>Requirements</b><br>
     *
     * <p>This event requires at least one of the following intents (Will not fire at all if neither is enabled):
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.GatewayIntent#GUILD_MESSAGES GUILD_MESSAGES} to work in guild text channels</li>
     *     <li>{@link net.dv8tion.jda.api.requests.GatewayIntent#DIRECT_MESSAGES DIRECT_MESSAGES} to work in private channels</li>
     * </ul>
     */
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        final Member member = event.getMember();
        if (member == null) return;
        final Message message = event.getMessage();

        // "react" trigger
        if (member.hasPermission(Permission.MESSAGE_MANAGE, Permission.MESSAGE_ADD_REACTION)) {
            final String[] words = message.getContentRaw().split(" ");
            final String lastWord = words[words.length - 1];
            if (lastWord.startsWith("react") && !lastWord.startsWith("reaction") && lastWord.length() > 5) Arrays.stream(lastWord.substring(5).split(""))
                    .map(alphabetEmojis::get)
                    .filter(Objects::nonNull)
                    .forEach(emoji -> message.addReaction(emoji).queue());
        }

        // Cross-chat
        if (!event.getAuthor().isBot()) new CrossChatManager(bot, message);
    }
}
