package to.joe.j2mc.teleport;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import to.joe.j2mc.core.J2MC_Manager;
import to.joe.j2mc.teleport.command.*;

public class J2MC_Teleport extends JavaPlugin implements Listener {

    private HashMap<String, HashMap<String, Location>> warps;
    public FileConfiguration protectList;

    public void addWarp(String owner, Location location) {
        //TODO omg
    }

    public Location getNamedWarp(String owner, String name) {
        Location location;
        synchronized (this) {
            location = this.warps.get(owner).get(name);
        }
        return location;
    }

    public HashMap<String, Location> getWarps(String owner) {
        HashMap<String, Location> warpsOwned;
        synchronized (this) {
            warpsOwned = new HashMap<String, Location>(this.warps.get(owner));
        }
        return warpsOwned;
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Teleport module disabled");
    }

    @Override
    public void onEnable() {
        this.protectList = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "protected.yml"));
        this.warps = new HashMap<String, HashMap<String, Location>>();
        //Handles reloads!
        for (final Player player : this.getServer().getOnlinePlayers()) {
            this.playerJoin(player.getName());
        }

        this.playerJoin("");//public warps

        this.getServer().getPluginManager().registerEvents(this, this);

        this.getCommand("home").setExecutor(new HomeCommand(this));
        this.getCommand("protectme").setExecutor(new ProtectMeCommand(this));
        this.getCommand("sethome").setExecutor(new SetHomeCommand(this));
        this.getCommand("spawn").setExecutor(new SpawnCommand(this));
        this.getCommand("tp").setExecutor(new TeleportCommand(this));
        this.getCommand("warp").setExecutor(new WarpCommand(this));

        this.getLogger().info("Teleport module enabled");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.playerJoin(event.getPlayer().getName());
        if (this.protectList.getBoolean(event.getPlayer().getName(), false)) {
            J2MC_Manager.getPermissions().addFlag(event.getPlayer(), 'p');
        }
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        synchronized (this) {
            this.warps.remove(event.getPlayer().getName());
        }
    }

    /**
     * Teleports a player and ejects from their vehicle
     * 
     * @param player
     * @param location
     */
    public void teleport(Player player, Location location) {
        final Entity vehicle = player.getVehicle();
        if (vehicle != null) {
            player.leaveVehicle();
            vehicle.remove();
        }
        player.teleport(location);
    }

    private void playerJoin(String name) {
        final HashMap<String, Location> playerWarps = new HashMap<String, Location>();
        try {
            PreparedStatement ps = J2MC_Manager.getMySQL().getFreshPreparedStatementHotFromTheOven("SELECT `warp_name`,`world`,`x`,`y`,`z`,`pitch`,`yaw` FROM `teleport` WHERE `owner`= ? and `server_id`= ?");
            ps.setString(0, name);
            ps.setInt(1, J2MC_Manager.getServerID());
            ResultSet resultSet = ps.executeQuery();
            if (resultSet != null) {
                while (resultSet.next()) {
                    final World world = this.getServer().getWorld(resultSet.getString("world"));
                    final Location location = new Location(world, resultSet.getDouble("x"), resultSet.getDouble("y"), resultSet.getDouble("z"), resultSet.getFloat("pitch"), resultSet.getFloat("yaw"));
                    playerWarps.put(resultSet.getString("warp_name"), location);
                }
            }
        } catch (final Exception e) {
        }
        synchronized (this) {
            this.warps.put(name, playerWarps);
        }
    }

}
