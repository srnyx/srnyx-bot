package xyz.srnyx.srnyxbot.apps;

import com.google.gson.JsonObject;

import io.github.freya022.botcommands.api.commands.annotations.Command;
import io.github.freya022.botcommands.api.commands.application.ApplicationCommand;
import io.github.freya022.botcommands.api.commands.application.CommandScope;
import io.github.freya022.botcommands.api.commands.application.context.annotations.JDAMessageCommand;
import io.github.freya022.botcommands.api.commands.application.context.message.GuildMessageEvent;
import io.github.freya022.botcommands.api.components.Buttons;
import io.github.freya022.botcommands.api.components.data.InteractionConstraints;

import net.dv8tion.jda.api.interactions.InteractionHook;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.javautilities.HttpUtility;

import xyz.srnyx.lazylibrary.LazyEmoji;

import xyz.srnyx.srnyxbot.config.SrnyxConfig;

import java.net.HttpURLConnection;
import java.time.OffsetDateTime;
import java.util.function.Consumer;
import java.util.regex.Pattern;


@Command
public class Unsuspend extends ApplicationCommand {
    @NotNull private static final String USER_AGENT = "srnyx's Bot";
    @NotNull private static final String URL = "https://panel.play.hosting/api";
    @NotNull private static final Pattern PATTERN = Pattern.compile("panel\\.play\\.hosting/server/([a-z0-9]{8})");
    static {
        HttpUtility.DEBUG = true;
    }

    @NotNull private final SrnyxConfig config;
    @NotNull private final Buttons buttons;

    public Unsuspend(@NotNull SrnyxConfig config, @NotNull Buttons buttons) {
        this.config = config;
        this.buttons = buttons;
    }

    @JDAMessageCommand(
            scope = CommandScope.GUILD,
            name = "Unsuspend server",
            defaultLocked = true)
    public void unsuspend(@NotNull GuildMessageEvent event) {
        // Check if support
        if (config.playHosting.support.checkDontHaveRole(event)) return;

        // Check if token is set
        if (config.playHosting.token == null || config.playHosting.token.isBlank()) {
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
                    .setActionRow(buttons.secondary("Unsuspend anyways", LazyEmoji.WARNING.emoji).ephemeral()
                            .bindTo(bypass -> bypass.deferEdit().queue(bypassHook -> unsuspend(bypassHook, id, applicationServerUrl)))
                            .constraints(InteractionConstraints.ofRoleIds(config.playHosting.support.id))
                            .build())
                    .queue();
            return;
        }

        // Unsuspend server
        unsuspend(hook, id, applicationServerUrl);
    }

    @NotNull
    private Consumer<HttpURLConnection> getConnectionConsumer() {
        return connection -> {
            connection.setRequestProperty("Authorization", "Bearer " + config.playHosting.token);
            connection.setRequestProperty("Accept", "application/json");
        };
    }

    private void unsuspend(@NotNull InteractionHook hook, @NotNull String id, @NotNull String applicationServerUrl) {
        // Try to unsuspend server
        final HttpUtility.Response response = HttpUtility.postJson(USER_AGENT, applicationServerUrl + "/unsuspend", null, getConnectionConsumer()).orElse(null);
        if (response == null) {
            hook.editOriginal(LazyEmoji.NO + " **Failed to unsuspend server with ID `" + id + "`!** Contact <@242385234992037888> so he can check the console")
                    .setComponents()
                    .queue();
            return;
        }
        if (response.code < 200 || response.code >= 300) {
            hook.editOriginal(LazyEmoji.NO + " Failed to unsuspend server with ID `" + id + "`!\n**" + response.code + ":** " + response.message)
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
