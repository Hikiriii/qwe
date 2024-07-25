package org.za.generatorhikiri;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class event implements Listener {

    private final Set<Location> boxLocations = new HashSet<>();
    private final Random random = new Random();
    private final GeneratorHikiri plugin;

    public event(GeneratorHikiri plugin) {
        this.plugin = plugin;
        loadBoxLocations();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack itemStack = event.getItem();
            if (itemStack != null && itemStack.hasItemMeta()) {
                ItemMeta itemMeta = itemStack.getItemMeta();
                if (itemMeta.hasLore() && itemMeta.getLore().contains(ChatColor.BLACK + "1")) {
                    Block block = event.getClickedBlock();
                    if (block != null) {
                        Player player = event.getPlayer();
                        Vector direction = player.getLocation().getDirection().normalize();
                        Location offsetLocation = block.getLocation().add(direction.multiply(1.5)).add(0, 3, 0);

                        createBox(offsetLocation, 3, Material.STONE);
                        boxLocations.add(offsetLocation);

                        saveBoxLocation(offsetLocation);

                        String path = "blocks." + block.getWorld().getName() + "." + block.getLocation().getBlockX() + "." + block.getLocation().getBlockY() + "." + block.getLocation().getBlockZ();
                        plugin.getConfig().set(path + ".type", block.getType().name());
                        plugin.getConfig().set(path + ".lore", itemMeta.getLore());
                        plugin.saveConfig();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block brokenBlock = event.getBlock();
        Location brokenLocation = brokenBlock.getLocation();

        if (boxLocations.remove(brokenLocation)) {
            removeBlockFromConfig(brokenLocation);
        }

        for (Location boxLocation : boxLocations) {
            if (isInsideBox(brokenLocation, boxLocation, 3)) {
                if (hasBlockNearby(brokenBlock)) {
                    Material newBlockType = getRandomOre();
                    replaceBlock(brokenBlock, newBlockType);
                }
            }
        }
    }

    private void saveBoxLocation(Location location) {
        String path = "boxLocations." + location.getWorld().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();
        plugin.getConfig().set(path, true);
        plugin.saveConfig();
    }

    private void loadBoxLocations() {
        FileConfiguration config = plugin.getConfig();
        if (config.contains("boxLocations")) {
            for (String worldName : config.getConfigurationSection("boxLocations").getKeys(false)) {
                for (String xStr : config.getConfigurationSection("boxLocations." + worldName).getKeys(false)) {
                    for (String yStr : config.getConfigurationSection("boxLocations." + worldName + "." + xStr).getKeys(false)) {
                        for (String zStr : config.getConfigurationSection("boxLocations." + worldName + "." + xStr + "." + yStr).getKeys(false)) {
                            int x = Integer.parseInt(xStr);
                            int y = Integer.parseInt(yStr);
                            int z = Integer.parseInt(zStr);
                            Location loc = new Location(plugin.getServer().getWorld(worldName), x, y, z);
                            boxLocations.add(loc);
                        }
                    }
                }
            }
        }
    }

    private void removeBlockFromConfig(Location location) {
        String worldName = location.getWorld().getName();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        String path = "blocks." + worldName + "." + x + "." + y + "." + z;
        plugin.getConfig().set(path, null);

        String boxPath = "boxLocations." + worldName + "." + x + "." + y + "." + z;
        plugin.getConfig().set(boxPath, null);

        plugin.saveConfig();
    }

    private boolean hasBlockNearby(Block block) {
        Location blockLocation = block.getLocation();
        int radius = 3;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Location checkLocation = blockLocation.clone().add(x, y, z);
                    if (checkLocation.getBlock().getType() == Material.OBSERVER) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void replaceBlock(Block brokenBlock, Material newBlockType) {
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                brokenBlock.setType(newBlockType);
            }
        };
        task.runTaskLater(plugin, 20L);
    }

    private boolean isInsideBox(Location blockLocation, Location boxLocation, int size) {
        int halfSize = size / 2;
        int minX = boxLocation.getBlockX() - halfSize;
        int maxX = boxLocation.getBlockX() + halfSize;
        int minY = boxLocation.getBlockY() - halfSize;
        int maxY = boxLocation.getBlockY() + halfSize;
        int minZ = boxLocation.getBlockZ() - halfSize;
        int maxZ = boxLocation.getBlockZ() + halfSize;

        return blockLocation.getBlockX() >= minX && blockLocation.getBlockX() <= maxX &&
                blockLocation.getBlockY() >= minY && blockLocation.getBlockY() <= maxY &&
                blockLocation.getBlockZ() >= minZ && blockLocation.getBlockZ() <= maxZ;
    }

    private Material getRandomOre() {
        Material[] ores = {
                Material.COAL_ORE,
                Material.IRON_ORE,
                Material.GOLD_ORE,
                Material.REDSTONE_ORE,
                Material.DIAMOND_ORE,
                Material.EMERALD_ORE,
                Material.LAPIS_ORE
        };
        return ores[random.nextInt(ores.length)];
    }

    private void createBox(Location center, int size, Material material) {
        int halfSize = size / 2;
        int minX = center.getBlockX() - halfSize;
        int maxX = center.getBlockX() + halfSize;
        int minY = center.getBlockY() - halfSize;
        int maxY = center.getBlockY() + halfSize;
        int minZ = center.getBlockZ() - halfSize;
        int maxZ = center.getBlockZ() + halfSize;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location loc = new Location(center.getWorld(), x, y, z);
                    if (x == minX || x == maxX || y == minY || y == maxY || z == minZ || z == maxZ) {
                        loc.getBlock().setType(material);
                    }
                }
            }
        }
    }
}
