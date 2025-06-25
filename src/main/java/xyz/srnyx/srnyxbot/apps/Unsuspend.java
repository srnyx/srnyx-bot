package xyz.srnyx.srnyxbot.apps;

import com.freya02.botcommands.api.annotations.CommandMarker;
import com.freya02.botcommands.api.annotations.Dependency;
import com.freya02.botcommands.api.application.ApplicationCommand;
import com.freya02.botcommands.api.application.context.annotations.JDAMessageCommand;
import com.freya02.botcommands.api.application.context.message.GuildMessageEvent;
import com.freya02.botcommands.api.components.Components;
import com.freya02.botcommands.api.components.InteractionConstraints;

import com.google.gson.JsonObject;

import net.dv8tion.jda.api.interactions.InteractionHook;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.javautilities.HttpUtility;

import xyz.srnyx.lazylibrary.LazyEmoji;

import xyz.srnyx.srnyxbot.SrnyxBot;

import java.net.HttpURLConnection;
import java.time.OffsetDateTime;
import java.util.function.Consumer;
import java.util.regex.Pattern;


@CommandMarker
public class Unsuspend extends ApplicationCommand {
    @NotNull private static final String USER_AGENT = "srnyx's Bot";
    @NotNull private static final String URL = "https://panel.play.hosting/api";
    @NotNull private static final Pattern PATTERN = Pattern.compile("panel\\.play\\.hosting/server/([a-z0-9]{8})");

    @Dependency private SrnyxBot bot;

    @JDAMessageCommand(
            name = "Unsuspend server",
            defaultLocked = true)
    public void unsuspend(@NotNull GuildMessageEvent event) {
        // Check if support
        if (bot.config.playHosting.support.checkDontHaveRole(event)) return;

        // Check if token is set
        if (bot.config.playHosting.token == null || bot.config.playHosting.token.isBlank()) {
            event.reply(LazyEmoji.NO + " **Play Hosting token is not set!** Please set it in the config...").setEphemeral(true).queue();
            return;
        }

        // Defer
        event.deferReply().queue();
        final InteractionHook hook = event.getHook();

        // Get server ID from message
        final String id = PATTERN.matcher(event.getTarget().getContentRaw())
                .results()
                .map(match -> match.group(1))
                .findFirst()
                .orElse(null);
        if (id == null) {
            hook.editOriginal(LazyEmoji.NO + " **No server link with an ID found in the message!** Your panel link looks like this: `panel.play.hosting/server/XXXXXXXX`, please send us that...").queue();
            return;
        }

        // Get client server
        final JsonObject clientServer = HttpUtility.getJson(USER_AGENT, URL + "/client/servers/" + id, getConnectionConsumer())
                .map(json -> json.getAsJsonObject().getAsJsonObject("attributes"))
                .orElse(null);
        if (clientServer == null) {
            hook.editOriginal(LazyEmoji.NO + " No client server found with ID `" + id + "`!").queue();
            return;
        }

        // Check if suspended
        if (!clientServer.has("is_suspended") || !clientServer.get("is_suspended").getAsBoolean()) {
            hook.editOriginal(LazyEmoji.NO + " **Server with ID `" + id + "` is not suspended!** Please check to make sure your server isn't currently in limbo...").queue();
            return;
        }

        // Get application server
        final String applicationServerUrl = URL + "/application/servers/" + clientServer.get("internal_id").getAsString();
        final JsonObject applicationServer = HttpUtility.getJson(USER_AGENT, applicationServerUrl, getConnectionConsumer())
                .map(json -> json.getAsJsonObject().getAsJsonObject("attributes"))
                .orElse(null);
        if (applicationServer == null) {
            hook.editOriginal(LazyEmoji.NO + " No application server found with client ID `" + id + "`!").queue();
            return;
        }

        // Check if last update was more than 2 hours ago
        final OffsetDateTime lastUpdated = OffsetDateTime.parse(applicationServer.get("updated_at").getAsString());
        final OffsetDateTime twoHoursAgo = OffsetDateTime.now().minusHours(2);
        if (lastUpdated.isAfter(twoHoursAgo)) {
            hook.editOriginal(LazyEmoji.NO + " **Server with ID `" + id + "` was updated recently!** As instructed in <#1332833015025500202>, please wait 2+ hours before opening a stuck ticket...")
                    .setActionRow(Components.secondaryButton(bypass -> bypass.deferEdit()
                                    .queue(bypassHook -> unsuspend(bypassHook, id, applicationServerUrl)))
                            .setConstraints(InteractionConstraints.ofRoleIds(bot.config.playHosting.support.id))
                            .build("Unsuspend anyways"))
                    .queue();
            return;
        }

        // Unsuspend server
        unsuspend(hook, id, applicationServerUrl);
    }

    @NotNull
    private Consumer<HttpURLConnection> getConnectionConsumer() {
        return connection -> {
            connection.setRequestProperty("Authorization", "Bearer " + bot.config.playHosting.token);
            connection.setRequestProperty("Accept", "application/json");
        };
    }

    private void unsuspend(@NotNull InteractionHook hook, @NotNull String id, @NotNull String applicationServerUrl) {
        // Unsuspend server
        final int response = HttpUtility.postJson(USER_AGENT, applicationServerUrl + "/unsuspend", null, getConnectionConsumer());
        if (response != 204) {
            hook.editOriginal(LazyEmoji.NO + " **Failed to unsuspend server with ID `" + id + "`!** Response code: " + response)
                    .setComponents()
                    .queue();
            return;
        }

        // Reply
        hook.editOriginal(LazyEmoji.YES + " Successfully unsuspended server with ID `" + id + "`")
                .setComponents()
                .queue();
    }
}
