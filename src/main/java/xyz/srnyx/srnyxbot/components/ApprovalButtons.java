package xyz.srnyx.srnyxbot.components;

import com.freya02.botcommands.api.annotations.CommandMarker;
import com.freya02.botcommands.api.annotations.Dependency;
import com.freya02.botcommands.api.application.ApplicationCommand;
import com.freya02.botcommands.api.components.Components;
import com.freya02.botcommands.api.components.annotations.JDAButtonListener;
import com.freya02.botcommands.api.components.event.ButtonEvent;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.components.ActionRow;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.lazylibrary.LazyEmoji;

import xyz.srnyx.srnyxbot.SrnyxBot;
import xyz.srnyx.srnyxbot.config.Approval;


@CommandMarker
public class ApprovalButtons extends ApplicationCommand {
    @NotNull public static final String APPROVAL_BUTTON_YES = "approval_button_yes";
    @NotNull public static final String APPROVAL_BUTTON_NO = "approval_button_no";

    @Dependency private SrnyxBot bot;

    @JDAButtonListener(name = APPROVAL_BUTTON_YES)
    public void onApprovalButtonYes(@NotNull ButtonEvent event) {
        // Check permissions
        final Member clicker = event.getMember();
        if (clicker == null || !clicker.hasPermission(Permission.MANAGE_ROLES)) {
            event.reply(LazyEmoji.NO + " You don't have permission to do that!").setEphemeral(true).queue();
            return;
        }

        // Get role
        final Approval approval = bot.config.getApprovalFromChannel(event.getChannel().getIdLong());
        if (approval == null) {
            event.reply(LazyEmoji.NO + " This is not an approval channel!").setEphemeral(true).queue();
            return;
        }
        final Role role = approval.getRole();
        if (role == null) {
            event.reply(LazyEmoji.NO + " Invalid approval role!").setEphemeral(true).queue();
            return;
        }

        // Add role and edit message
        final Guild guild = clicker.getGuild();
        final Message message = event.getMessage();
        final String content = message.getContentRaw();
        event.deferEdit().flatMap(hook -> hook.editOriginalComponents(ActionRow.of(Components.successButton(APPROVAL_BUTTON_YES).build(LazyEmoji.YES_CLEAR.getButtonContent("Approved!")).asDisabled())))
                .flatMap(msg -> guild.retrieveMemberById(content.replace("<@", "").replace(">", "")))
                .flatMap(member -> guild.addRoleToMember(member, role).reason("Approval accepted by " + clicker.getUser().getName()))
                .queue(s -> {}, f -> {});
    }

    @JDAButtonListener(name = APPROVAL_BUTTON_NO)
    public void onApprovalButtonNo(@NotNull ButtonEvent event) {
        // Check permissions
        final Member clicker = event.getMember();
        if (clicker == null || !clicker.hasPermission(Permission.MANAGE_ROLES)) {
            event.reply(LazyEmoji.NO + " You don't have permission to do that!").setEphemeral(true).queue();
            return;
        }

        // Kick member and edit message
        final Guild guild = clicker.getGuild();
        final Message message = event.getMessage();
        final String content = message.getContentRaw();
        event.deferEdit().flatMap(hook -> hook.editOriginalComponents(ActionRow.of(Components.dangerButton(APPROVAL_BUTTON_NO).build(LazyEmoji.NO_CLEAR_DARK.getButtonContent("Denied!")).asDisabled())))
                .flatMap(msg -> guild.retrieveMemberById(content.replace("<@", "").replace(">", "")))
                .flatMap(member -> guild.kick(member).reason("Approval denied by " + clicker.getUser().getName()))
                .queue(s -> {}, f -> {});
    }
}
