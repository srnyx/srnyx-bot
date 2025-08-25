package xyz.srnyx.srnyxbot.listeners;

import io.github.freya022.botcommands.api.core.annotations.BEventListener;
import io.github.freya022.botcommands.api.core.service.annotations.BService;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.javautilities.MapGenerator;

import xyz.srnyx.srnyxbot.CrossChatManager;
import xyz.srnyx.srnyxbot.config.SrnyxConfig;

import java.util.*;


@BService
public record MessageListener(@NotNull SrnyxConfig config) {
    @NotNull private static final Map<Character, UnicodeEmoji> ALPHABET_EMOJIS = MapGenerator.HASH_MAP.mapOf(
            List.of('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'),
            List.of(Emoji.fromUnicode("U+1F1E6"), Emoji.fromUnicode("U+1F1E7"), Emoji.fromUnicode("U+1F1E8"), Emoji.fromUnicode("U+1F1E9"), Emoji.fromUnicode("U+1F1EA"),
                    Emoji.fromUnicode("U+1F1EB"), Emoji.fromUnicode("U+1F1EC"), Emoji.fromUnicode("U+1F1ED"), Emoji.fromUnicode("U+1F1EE"), Emoji.fromUnicode("U+1F1EF"),
                    Emoji.fromUnicode("U+1F1F0"), Emoji.fromUnicode("U+1F1F1"), Emoji.fromUnicode("U+1F1F2"), Emoji.fromUnicode("U+1F1F3"), Emoji.fromUnicode("U+1F1F4"),
                    Emoji.fromUnicode("U+1F1F5"), Emoji.fromUnicode("U+1F1F6"), Emoji.fromUnicode("U+1F1F7"), Emoji.fromUnicode("U+1F1F8"), Emoji.fromUnicode("U+1F1F9"),
                    Emoji.fromUnicode("U+1F1FA"), Emoji.fromUnicode("U+1F1FB"), Emoji.fromUnicode("U+1F1FC"), Emoji.fromUnicode("U+1F1FD"), Emoji.fromUnicode("U+1F1FE"),
                    Emoji.fromUnicode("U+1F1FF")));
    @NotNull private static final String REACT_TRIGGER = "react";
    @NotNull private static final Set<String> REACT_IGNORED = Set.of("reaction", "reactions", "reactivate");
    private static final int REACT_TRIGGER_LENGTH = REACT_TRIGGER.length();

    @BEventListener
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
        if (!author.isBot() && !author.isSystem()) new CrossChatManager(config, message);
    }
}
