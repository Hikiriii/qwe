package org.za.generatorhikiri;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class GeneratorHikiri extends JavaPlugin {
    public static GeneratorHikiri generatorHikiri;

    @Override
    public void onEnable() {
        generatorHikiri = this;
        getCommand("generator").setExecutor(new cmd());
        getServer().getPluginManager().registerEvents(new event(this),this);
    }
    public static String format(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static String hexColor(String text) {
        // Поддержка & цветных кодов (например, &c, &6)
        text = ChatColor.translateAlternateColorCodes('&', text);

        // Поддержка #RRGGBB цветных кодов (например, #ff0000)
        text = text.replaceAll("(?i)#([A-Fa-f0-9]{6})", "§x§$1");

        return text;
    }

    public static String combo(String msg) {
        msg = format(msg); // Применяем обычный формат
        msg = hexColor(msg); // Применяем HEX-формат
        return msg;
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
