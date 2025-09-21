package xyz.srnyx.srnyxbot.components;

import io.github.freya022.botcommands.api.components.Buttons;
import io.github.freya022.botcommands.api.components.annotations.JDAButtonListener;
import io.github.freya022.botcommands.api.components.event.ButtonEvent;
import io.github.freya022.botcommands.api.core.annotations.Handler;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.UserSnowflake;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.lazylibrary.LazyEmbed;
import xyz.srnyx.lazylibrary.LazyEmoji;

import xyz.srnyx.srnyxbot.config.Approval;
import xyz.srnyx.srnyxbot.config.SrnyxConfig;

import java.util.Optional;


@Handler
public record ApprovalButtons(@NotNull SrnyxConfig config, @NotNull Buttons buttons) {
    @NotNull public static final String APPROVAL_BUTTON_YES = "approval_button_yes";
    @NotNull public static final String APPROVAL_BUTTON_NO = "approval_button_no";

    @JDAButtonListener(APPROVAL_BUTTON_YES)
    public void onApprovalButtonYes(@NotNull ButtonEvent event) {
        // Check permissions
        final Member clicker = event.getMember();
        if (clicker == null || !clicker.hasPermission(Permission.MANAGE_ROLES)) {
            event.replyEmbeds(LazyEmbed.noPermission().build()).setEphemeral(true).queue();
            return;
        }

        // Get role
        final Optional<Role> role = config.getApprovalFromChannel(event.getChannel().getIdLong()).flatMap(Approval::getRole);
        if (role.isEmpty()) {
            event.reply(LazyEmoji.NO + " This is not an approval channel!").setEphemeral(true).queue();
            return;
        }

        // Edit message
        event.editComponents(ActionRow.of(buttons.success("Approved!", LazyEmoji.YES_CLEAR.emoji)
                        .persistent()
                        .bindTo(APPROVAL_BUTTON_YES)
                        .build().asDisabled()))
                .queue();

        // Add role
        final String content = event.getMessage().getContentRaw();
        clicker.getGuild().addRoleToMember(UserSnowflake.fromId(content.substring(2, content.length() - 1)), role.get())
                .reason("Approval accepted by " + clicker.getUser().getName())
                .queue();
    }

    @JDAButtonListener(APPROVAL_BUTTON_NO)
    public void onApprovalButtonNo(@NotNull ButtonEvent event) {
        // Check permissions
        final Member clicker = event.getMember();
        if (clicker == null || !clicker.hasPermission(Permission.MANAGE_ROLES)) {
            event.replyEmbeds(LazyEmbed.noPermission().build()).setEphemeral(true).queue();
            return;
        }

        // Edit message
        event.editComponents(ActionRow.of(buttons.danger("Denied!", LazyEmoji.NO_CLEAR_DARK.emoji)
                        .persistent()
                        .bindTo(APPROVAL_BUTTON_NO)
                        .build().asDisabled()))
                .queue();

        // Kick member
        final String content = event.getMessage().getContentRaw();
        clicker.getGuild().kick(UserSnowflake.fromId(content.substring(2, content.length() - 1)))
                .reason("Approval denied by " + clicker.getUser().getName())
                .queue();
    }
}
