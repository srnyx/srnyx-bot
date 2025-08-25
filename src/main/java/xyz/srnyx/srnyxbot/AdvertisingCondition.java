package xyz.srnyx.srnyxbot;

import io.github.freya022.botcommands.api.core.service.annotations.Condition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;


@Target({ElementType.TYPE}) @Condition(type = AdvertisingChecker.class)
public @interface AdvertisingCondition {}
