package xyz.srnyx.srnyxbot.listeners;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.user.update.UserUpdateActivitiesEvent;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.lazylibrary.LazyListener;
import xyz.srnyx.lazylibrary.utility.LazyUtilities;

import xyz.srnyx.srnyxbot.SrnyxBot;
import xyz.srnyx.srnyxbot.config.SrnyxConfig;

import java.util.List;


public class AdvertisingListener extends LazyListener {
    @NotNull private final SrnyxBot bot;

    public AdvertisingListener(@NotNull SrnyxBot bot) {
        this.bot = bot;
    }

    @Override
    public void onUserUpdateActivities(@NotNull UserUpdateActivitiesEvent event) {
        final User user = event.getUser();
        if (user.isBot() || user.isSystem()) return;
        final Member member = event.getMember();
        final Guild guild = member.getGuild();
        final SrnyxConfig.Advertising advertising = bot.config.advertising.get(guild.getIdLong());
        if (advertising == null) return;

        // Add role if user advertising
        final List<Activity> activities = event.getNewValue();
        if (activities != null && activities.stream().anyMatch(activity -> activity.getType() == Activity.ActivityType.CUSTOM_STATUS && activity.getName().toLowerCase().contains(advertising.status))) {
            final Role role = advertising.role.getRole(guild).orElse(null);
            if (role != null && !member.getRoles().contains(role)) guild.addRoleToMember(member, role).queue(null, LazyUtilities.IGNORE_UNKNOWN_MEMBER);
            return;
        }

        // Remove role if user not advertising
        final Role role = advertising.role.getRole(guild).orElse(null);
        if (role != null && member.getRoles().contains(role)) guild.removeRoleFromMember(member, role).queue(null, LazyUtilities.IGNORE_UNKNOWN_MEMBER);
    }
}
