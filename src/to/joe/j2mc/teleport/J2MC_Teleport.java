package to.joe.j2mc.teleport;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import to.joe.j2mc.core.J2MC_Manager;
import to.joe.j2mc.teleport.command.*;

public class J2MC_Teleport extends JavaPlugin {

    private final Listen listener = new Listen(this);
    private HashMap<String, HashMap<String, Location>> warps;

    @Override
    public void onDisable() {
        J2MC_Manager.getLog().info("Teleport module disabled");
    }

    @Override
    public void onEnable() {
        synchronized (this) {
            this.warps = new HashMap<String, HashMap<String, Location>>();
        }
        Teleport_Manager.setTeleport(this);
        //Handles reloads!
        for (final Player player : this.getServer().getOnlinePlayers()) {
            this.playerJoin(player.getName());
        }

        this.playerJoin("");//public warps

        this.getServer().getPluginManager().registerEvent(PlayerJoinEvent.Type.PLAYER_JOIN, this.listener, PlayerJoinEvent.Priority.Normal, this);
        this.getServer().getPluginManager().registerEvent(PlayerQuitEvent.Type.PLAYER_QUIT, this.listener, PlayerQuitEvent.Priority.Normal, this);

        this.getCommand("home").setExecutor(new HomeCommand(this));
        this.getCommand("protectme").setExecutor(new ProtectMeCommand(this));
        this.getCommand("sethome").setExecutor(new SetHomeCommand(this));
        this.getCommand("spawn").setExecutor(new SpawnCommand(this));
        this.getCommand("tp").setExecutor(new TeleportCommand(this));
        this.getCommand("warp").setExecutor(new WarpCommand(this));

        J2MC_Manager.getLog().info("Teleport module enabled");
    }

    private void playerQuit(String player) {
        synchronized (this) {
            this.warps.remove(player);
        }
    }

    private void playerJoin(String player) {
        final HashMap<String, Location> playerWarps = new HashMap<String, Location>();
        try {
        	PreparedStatement ps = J2MC_Manager.getMySQL().getFreshPreparedStatementHotFromTheOven("SELECT `warp_name`,`world`,`x`,`y`,`z`,`pitch`,`yaw` FROM `teleport` WHERE `owner`= ? and `server_id`= ?");
            ps.setString(0, player);
            ps.setInt(1,J2MC_Manager.getServerID());
        	ResultSet resultSet=J2MC_Manager.getMySQL().executeQuery(ps);
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
            this.warps.put(player, playerWarps);
        }
    }

    public HashMap<String, Location> getWarps(String owner) {
        HashMap<String, Location> warpsOwned;
        synchronized (this) {
            warpsOwned = new HashMap<String, Location>(this.warps.get(owner));
        }
        return warpsOwned;
    }

    public Location getNamedWarp(String owner, String name) {
        Location location;
        synchronized (this) {
            location = this.warps.get(owner).get(name);
        }
        return location;
    }

    public void addWarp(String owner, Location location) {

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

    private class Listen extends PlayerListener {

        private final J2MC_Teleport plugin;

        public Listen(J2MC_Teleport plugin) {
            this.plugin = plugin;
        }

        @Override
        public void onPlayerJoin(PlayerJoinEvent event) {
            this.plugin.playerJoin(event.getPlayer().getName());
        }

        @Override
        public void onPlayerQuit(PlayerQuitEvent event) {
            this.plugin.playerQuit(event.getPlayer().getName());
        }
    }

}
