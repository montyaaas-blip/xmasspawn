package cz.vanoce.xmasspawn;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class XmasSpawnPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
    }

    /** Cíl teleportu, nebo null, pokud svět neexistuje. */
    private Location target() {
        String name = getConfig().getString("world", "Christmas");
        World world = getServer().getWorld(name);
        if (world == null) {
            for (World w : getServer().getWorlds()) {
                if (w.getName().equalsIgnoreCase(name)) {
                    world = w;
                    break;
                }
            }
        }
        if (world == null) {
            return null;
        }
        // +0.5 = střed bloku, ať hráč nestojí na hraně
        return new Location(world,
                getConfig().getDouble("x") + 0.5,
                getConfig().getDouble("y"),
                getConfig().getDouble("z") + 0.5,
                (float) getConfig().getDouble("yaw", 0),
                (float) getConfig().getDouble("pitch", 0));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        teleport(player);
        // Pojistka: pokud hráče ještě něco přesune jinam (jiný plugin, načtení pozice), vrátíme ho znovu
        getServer().getScheduler().runTaskLater(this, () -> teleport(player), 5L);
        getServer().getScheduler().runTaskLater(this, () -> teleport(player), 20L);
    }

    private void teleport(Player player) {
        if (!player.isOnline()) {
            return;
        }
        Location location = target();
        if (location == null) {
            getLogger().warning("Svět '" + getConfig().getString("world") + "' neexistuje, hráč "
                    + player.getName() + " nebyl teleportován.");
            return;
        }
        player.teleport(location);
    }
}
