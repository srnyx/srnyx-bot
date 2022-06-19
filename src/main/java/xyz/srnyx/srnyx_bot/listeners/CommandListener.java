package xyz.srnyx.srnyx_bot.listeners;

import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.InviteAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class CommandListener extends ListenerAdapter {
    /**
     * Called when a slash command is ran
     */
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("invites")) {
            if (!event.getUser().getId().equals("242385234992037888")) return;

            final OptionMapping amount = event.getOption("amount");
            if (amount == null) return;

            final OptionMapping channelOption = event.getOption("channel");
            TextChannel channel = event.getTextChannel();
            if (channelOption != null && channelOption.getAsTextChannel() != null) channel = channelOption.getAsTextChannel();
            final TextChannel finalChannel = channel;

            event.deferReply().queue();

            int i = amount.getAsInt();
            int age = 604800;
            final List<InviteAction> actions = new ArrayList<>();
            while (i > 0) {
                actions.add(finalChannel.createInvite().setMaxUses(1).setMaxAge(age));
                age--;
                i--;
            }

            new Timer().schedule(new TimerTask() {public void run() {
                RestAction.allOf(actions).queue(invites -> {
                    final StringBuilder string = new StringBuilder();
                    for (Invite invite : invites) string.append("<").append(invite.getUrl()).append(">").append("\n");
                    event.getChannel().sendMessage(string.toString()).queue();
                });

                event.getHook().deleteOriginal().queue();
            }}, 1000L * amount.getAsInt());
        }
    }
}
