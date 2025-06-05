package xyz.srnyx.srnyxbot.apps;

import com.freya02.botcommands.api.annotations.CommandMarker;
import com.freya02.botcommands.api.annotations.Dependency;
import com.freya02.botcommands.api.application.ApplicationCommand;
import com.freya02.botcommands.api.application.context.annotations.JDAMessageCommand;
import com.freya02.botcommands.api.application.context.message.GuildMessageEvent;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.javautilities.HttpUtility;

import xyz.srnyx.lazylibrary.LazyEmoji;

import xyz.srnyx.srnyxbot.SrnyxBot;

import java.net.HttpURLConnection;
import java.util.function.Consumer;
import java.util.regex.Pattern;


@CommandMarker
public class Unsuspend extends ApplicationCommand {
    @NotNull private static final String USER_AGENT = "srnyx's Bot";
    @NotNull private static final String URL = "https://panel.play.hosting/api/";
    @NotNull private static final Pattern PATTERN = Pattern.compile("panel\\.play\\.hosting/server/([a-z0-9]{8})");

    @Dependency private SrnyxBot bot;

    @JDAMessageCommand(
            name = "Unsuspend server",
            defaultLocked = true)
    public void unsuspend(@NotNull GuildMessageEvent event) {
        // Check if owner
        if (bot.config.checkNotOwner(event)) return;

        // Check if token is set
        if (bot.config.playHostingToken == null || bot.config.playHostingToken.isBlank()) {
            event.reply(LazyEmoji.NO + " **Play Hosting token is not set!** Please set it in the config...").setEphemeral(true).queue();
            return;
        }

        // Get server ID from message
        final String id = PATTERN.matcher(event.getTarget().getContentRaw())
                .results()
                .map(match -> match.group(1))
                .findFirst()
                .orElse(null);
        if (id == null) {
            event.reply(LazyEmoji.NO + " **No server link with ID found in message!** Your panel link looks like this: `panel.play.hosting/server/XXXXXXXX`").queue();
            return;
        }

        // Get internal ID
        final Integer internalId = HttpUtility.getJson(USER_AGENT, URL + "client/servers/" + id, getConnectionConsumer())
                .map(json -> json.getAsJsonObject()
                        .getAsJsonObject("attributes")
                        .get("internal_id").getAsInt())
                .orElse(null);
        if (internalId == null) {
            event.reply(LazyEmoji.NO + " No server found with ID `" + id + "`!").setEphemeral(true).queue();
            return;
        }

        // Unsuspend server
        final int response = HttpUtility.postJson(USER_AGENT, URL + "application/servers/" + internalId + "/unsuspend", null, getConnectionConsumer());
        if (response != 204) {
            event.reply(LazyEmoji.NO + " **Failed to unsuspend server with ID `" + id + "`!** Response code: " + response).setEphemeral(true).queue();
            return;
        }

        event.reply(LazyEmoji.YES + " Successfully unsuspended server with ID `" + id + "`!").setEphemeral(true).queue();
    }

    @NotNull
    private Consumer<HttpURLConnection> getConnectionConsumer() {
        return connection -> connection.setRequestProperty("Authorization", "Bearer " + bot.config.playHostingToken);
    }
}
