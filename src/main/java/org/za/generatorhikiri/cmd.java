package org.za.generatorhikiri;

import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static org.za.generatorhikiri.GeneratorHikiri.combo;

public class cmd implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String name = args[0];
        Player player = Bukkit.getPlayer(name);
        if(args[0].equals(name)){
            ItemStack block = new ItemStack(Material.OBSERVER);
            ItemMeta bmeta = block.getItemMeta();
            bmeta.setDisplayName(combo("&Покращений Генератор"));
            List<String> lore = new ArrayList<>();
            lore.add(combo(ChatColor.BLACK+"1"));
            bmeta.addEnchant(Enchantment.DAMAGE_UNDEAD,1,true);
            bmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            bmeta.setLore(lore);
            block.setItemMeta(bmeta);
            player.getInventory().addItem(block);
        }
        return true;
    }
}
