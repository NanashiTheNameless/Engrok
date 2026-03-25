package io.github.nanashithenameless.engrok.initialization;

import io.github.nanashithenameless.engrok.config.EngrokConfig;

public interface LoadWorldInvoker{
    public void initialization(int port, EngrokConfig.regionSelectEnum region);
}
