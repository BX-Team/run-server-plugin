package org.bxteam.plugin.test;

import org.bukkit.plugin.java.JavaPlugin;

public class TestPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("TestPlugin enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("TestPlugin disabled");
    }
}
