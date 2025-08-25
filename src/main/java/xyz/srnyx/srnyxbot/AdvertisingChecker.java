package xyz.srnyx.srnyxbot;

import io.github.freya022.botcommands.api.core.service.CustomConditionChecker;
import io.github.freya022.botcommands.api.core.service.ServiceContainer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.srnyxbot.config.SrnyxConfig;


public class AdvertisingChecker implements CustomConditionChecker<AdvertisingCondition> {
    @Override @NotNull
    public Class<AdvertisingCondition> getAnnotationType() {
        return AdvertisingCondition.class;
    }

    @Override @Nullable
    public String checkServiceAvailability(@NotNull ServiceContainer serviceContainer, @NotNull Class<?> checkedClass, @NotNull AdvertisingCondition annotation) {
        return serviceContainer.getService(SrnyxConfig.class).advertising.isEmpty() ? "Advertising is not configured on this bot" : null;
    }
}
