package xyz.srnyx.srnyxbot.listeners;

import io.github.freya022.botcommands.api.core.annotations.BEventListener;
import io.github.freya022.botcommands.api.core.service.annotations.BService;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.user.update.UserUpdateActivitiesEvent;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.lazylibrary.LazyListener;
import xyz.srnyx.lazylibrary.utility.LazyUtilities;

import xyz.srnyx.srnyxbot.AdvertisingCondition;
import xyz.srnyx.srnyxbot.config.SrnyxConfig;

import java.util.List;


@BService @AdvertisingCondition
public class AdvertisingListener extends LazyListener {
    @NotNull private final SrnyxConfig config;

    public AdvertisingListener(@NotNull SrnyxConfig config) {
        this.config = config;
    }

    @BEventListener
    public void onUserUpdateActivities(@NotNull UserUpdateActivitiesEvent event) {
        final User user = event.getUser();
        if (user.isBot() || user.isSystem()) return;
        final Member member = event.getMember();
        final Guild guild = member.getGuild();
        final SrnyxConfig.Advertising advertising = config.advertising.get(guild.getIdLong());
        if (advertising == null) return;

        // Add role if user advertising
        final List<Activity> activities = event.getNewValue();
        if (activities != null && activities.stream().anyMatch(activity -> {
            if (activity.getType() != Activity.ActivityType.CUSTOM_STATUS) return false;
            final String name = activity.getName().toLowerCase();
            if (!name.contains(advertising.invite)) return false;
            for (final String word : name.split(" ")) if (word.endsWith(advertising.invite)) return true;
            return false;
        })) {
            final Role role = advertising.role.getRole(guild).orElse(null);
            if (role != null && !member.getRoles().contains(role)) guild.addRoleToMember(member, role).queue(null, LazyUtilities.IGNORE_UNKNOWN_MEMBER);
            return;
        }

        // Remove role if user not advertising
        final Role role = advertising.role.getRole(guild).orElse(null);
        if (role != null && member.getRoles().contains(role)) guild.removeRoleFromMember(member, role).queue(null, LazyUtilities.IGNORE_UNKNOWN_MEMBER);
    }
}
