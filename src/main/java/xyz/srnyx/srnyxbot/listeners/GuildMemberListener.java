package xyz.srnyx.srnyxbot.listeners;

import com.freya02.botcommands.api.components.Components;

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.lazylibrary.LazyEmoji;
import xyz.srnyx.lazylibrary.LazyListener;

import xyz.srnyx.srnyxbot.SrnyxBot;
import xyz.srnyx.srnyxbot.components.ApprovalButtons;
import xyz.srnyx.srnyxbot.config.Approval;


public class GuildMemberListener extends LazyListener {
    @NotNull private final SrnyxBot bot;

    public GuildMemberListener(@NotNull SrnyxBot bot) {
        this.bot = bot;
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        bot.config.getApprovalFromGuild(event.getGuild().getIdLong())
                .flatMap(Approval::getChannel)
                .ifPresent(textChannel -> textChannel.sendMessage(event.getUser().getName())
                        .flatMap(message -> message.editMessage(event.getMember().getAsMention())
                                .setActionRow(
                                        Components.successButton(ApprovalButtons.APPROVAL_BUTTON_YES).build(LazyEmoji.YES_CLEAR.getButtonContent("Approve")),
                                        Components.dangerButton(ApprovalButtons.APPROVAL_BUTTON_NO).build(LazyEmoji.NO_CLEAR_DARK.getButtonContent("Deny"))))
                        .queue());
    }
}
