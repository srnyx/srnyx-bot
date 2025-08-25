package xyz.srnyx.srnyxbot.listeners;

import io.github.freya022.botcommands.api.components.Buttons;
import io.github.freya022.botcommands.api.core.annotations.BEventListener;
import io.github.freya022.botcommands.api.core.service.annotations.BService;

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.lazylibrary.LazyEmoji;

import xyz.srnyx.srnyxbot.components.ApprovalButtons;
import xyz.srnyx.srnyxbot.config.Approval;
import xyz.srnyx.srnyxbot.config.SrnyxConfig;


@BService
public record GuildMemberListener(@NotNull SrnyxConfig config, @NotNull Buttons buttons) {
    @BEventListener
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        config.getApprovalFromGuild(event.getGuild().getIdLong())
                .flatMap(Approval::getChannel)
                .ifPresent(textChannel -> textChannel.sendMessage(event.getUser().getName())
                        .flatMap(message -> message.editMessage(event.getMember().getAsMention())
                                .setActionRow(
                                        buttons.success("Approve", LazyEmoji.YES_CLEAR.emoji).persistent().bindTo(ApprovalButtons.APPROVAL_BUTTON_YES).build(),
                                        buttons.danger("Deny", LazyEmoji.NO_CLEAR_DARK.emoji).persistent().bindTo(ApprovalButtons.APPROVAL_BUTTON_NO).build()))
                        .queue());
    }
}
