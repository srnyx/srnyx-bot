package xyz.srnyx.srnyxbot.listeners;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.javautilities.MapGenerator;

import xyz.srnyx.lazylibrary.LazyListener;

import xyz.srnyx.srnyxbot.SrnyxBot;
import xyz.srnyx.srnyxbot.CrossChatManager;

import java.util.*;


public class MessageListener extends LazyListener {
    @NotNull private static final Map<Character, UnicodeEmoji> ALPHABET_EMOJIS = MapGenerator.HASH_MAP.mapOf(
            List.of('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'),
            List.of(Emoji.fromUnicode("U+1F1E6"), Emoji.fromUnicode("U+1F1E7"), Emoji.fromUnicode("U+1F1E8"), Emoji.fromUnicode("U+1F1E9"), Emoji.fromUnicode("U+1F1EA"),
                    Emoji.fromUnicode("U+1F1EB"), Emoji.fromUnicode("U+1F1EC"), Emoji.fromUnicode("U+1F1ED"), Emoji.fromUnicode("U+1F1EE"), Emoji.fromUnicode("U+1F1EF"),
                    Emoji.fromUnicode("U+1F1F0"), Emoji.fromUnicode("U+1F1F1"), Emoji.fromUnicode("U+1F1F2"), Emoji.fromUnicode("U+1F1F3"), Emoji.fromUnicode("U+1F1F4"),
                    Emoji.fromUnicode("U+1F1F5"), Emoji.fromUnicode("U+1F1F6"), Emoji.fromUnicode("U+1F1F7"), Emoji.fromUnicode("U+1F1F8"), Emoji.fromUnicode("U+1F1F9"),
                    Emoji.fromUnicode("U+1F1FA"), Emoji.fromUnicode("U+1F1FB"), Emoji.fromUnicode("U+1F1FC"), Emoji.fromUnicode("U+1F1FD"), Emoji.fromUnicode("U+1F1FE"),
                    Emoji.fromUnicode("U+1F1FF")));
    @NotNull private static final String REACT_TRIGGER = "react";
    @NotNull private static final Set<String> REACT_IGNORED = Set.of("reaction", "reactivate");
    private static final int REACT_TRIGGER_LENGTH = REACT_TRIGGER.length();

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

        // Reactions
        if (member.hasPermission(Permission.MESSAGE_MANAGE, Permission.MESSAGE_ADD_REACTION)) {
            final String[] words = message.getContentRaw().split(" ");
            final String lastWord = words[words.length - 1];
            if (lastWord.startsWith(REACT_TRIGGER) && lastWord.length() > REACT_TRIGGER_LENGTH && !REACT_IGNORED.contains(lastWord)) {
                for (final char character : lastWord.substring(REACT_TRIGGER_LENGTH).toCharArray()) {
                    final UnicodeEmoji emoji = ALPHABET_EMOJIS.get(character);
                    if (emoji != null) message.addReaction(emoji).queue();
                }
            }
        }

        // Cross-chat
        final User author = event.getAuthor();
        if (!author.isBot() && !author.isSystem()) new CrossChatManager(bot, message);
    }
}
