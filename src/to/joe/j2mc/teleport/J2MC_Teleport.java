package to.joe.j2mc.teleport;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

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
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

import to.joe.j2mc.core.J2MC_Manager;
import to.joe.j2mc.teleport.command.BackCommand;
import to.joe.j2mc.teleport.command.BedCommand;
import to.joe.j2mc.teleport.command.HomeCommand;
import to.joe.j2mc.teleport.command.ProtectMeCommand;
import to.joe.j2mc.teleport.command.RemoveHomeCommand;
import to.joe.j2mc.teleport.command.SetHomeCommand;
import to.joe.j2mc.teleport.command.SpawnCommand;
import to.joe.j2mc.teleport.command.TeleportCommand;
import to.joe.j2mc.teleport.command.WarpCommand;
import to.joe.j2mc.teleport.command.admin.HomeInvasionCommand;
import to.joe.j2mc.teleport.command.admin.TeleportBanCommand;
import to.joe.j2mc.teleport.command.admin.TeleportHereCommand;
import to.joe.j2mc.teleport.util.ImmutableLocation;

public class J2MC_Teleport extends JavaPlugin implements Listener {

    private HashMap<String, HashMap<String, Location>> warps;
    private FileConfiguration protectList;
    private File protectListFile;
    public HashMap<String, Integer> tpBannedPlayers = new HashMap<String, Integer>();
    public Map<String, ImmutableLocation> lastLocations = new HashMap<String, ImmutableLocation>();

    public void addWarp(String owner, String name, Location location) {
        this.warps.get(owner).put(name, location);
        PreparedStatement ps;
        try {
            ps = J2MC_Manager.getMySQL().getFreshPreparedStatementHotFromTheOven("insert into `teleport` (`warp_name`,`world`,`x`,`y`,`z`,`pitch`,`yaw`,`owner`,`server_id`) values (?,?,?,?,?,?,?,?,?)");
            ps.setString(1, name);
            ps.setString(2, location.getWorld().getName());
            ps.setDouble(3, location.getX());
            ps.setDouble(4, location.getY());
            ps.setDouble(5, location.getZ());
            ps.setFloat(6, location.getPitch());
            ps.setFloat(7, location.getYaw());
            ps.setString(8, owner);
            ps.setInt(9, J2MC_Manager.getServerID());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteWarp(String owner, String name) {
        this.warps.get(owner).remove(name);
        try {
            PreparedStatement ps = J2MC_Manager.getMySQL().getFreshPreparedStatementHotFromTheOven("DELETE FROM `teleport` WHERE `warp_name` = ? AND `owner` = ?");
            ps.setString(1, name);
            ps.setString(2, owner);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public boolean isProtected(String name) {
        return this.protectList.getBoolean(name.toLowerCase(), false);
    }

    public void setProtected(String name, boolean protect) {
        this.protectList.set(name.toLowerCase(), protect);
        try {
            this.protectList.save(this.protectListFile);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdirs();
        }
        this.protectListFile = new File(this.getDataFolder(), "protected.yml");
        this.protectList = YamlConfiguration.loadConfiguration(this.protectListFile);
        this.warps = new HashMap<String, HashMap<String, Location>>();
        //Handles reloads!
        for (final Player player : this.getServer().getOnlinePlayers()) {
            this.playerJoin(player);
        }

        this.warpLoad("");//public warps

        this.getServer().getPluginManager().registerEvents(this, this);

        this.getCommand("home").setExecutor(new HomeCommand(this));
        this.getCommand("removehome").setExecutor(new RemoveHomeCommand(this));
        this.getCommand("protectme").setExecutor(new ProtectMeCommand(this));
        this.getCommand("sethome").setExecutor(new SetHomeCommand(this));
        this.getCommand("spawn").setExecutor(new SpawnCommand(this));
        this.getCommand("tp").setExecutor(new TeleportCommand(this));
        this.getCommand("warp").setExecutor(new WarpCommand(this));
        this.getCommand("hi").setExecutor(new HomeInvasionCommand(this));
        this.getCommand("tphere").setExecutor(new TeleportHereCommand(this));
        this.getCommand("tpban").setExecutor(new TeleportBanCommand(this));
        this.getCommand("back").setExecutor(new BackCommand(this));
        this.getCommand("bed").setExecutor(new BedCommand(this));

        this.getLogger().info("Teleport module enabled");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.playerJoin(event.getPlayer());
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        synchronized (this) {
            this.warps.remove(event.getPlayer().getName());
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        this.lastLocations.put(player.getName().toLowerCase(), ImmutableLocation.fromLocation(event.getFrom()));
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

    private void playerJoin(Player player) {
        if (this.tpBannedPlayers.containsKey(player.getName())) {
            J2MC_Manager.getPermissions().addFlag(player, 'T');
        }
        this.warpLoad(player.getName());
        if (this.protectList.getBoolean(player.getName(), false)) {
            J2MC_Manager.getPermissions().addFlag(player, 'p');
        }
    }

    public void warpLoad(String name) {
        final HashMap<String, Location> playerWarps = new HashMap<String, Location>();
        try {
            final PreparedStatement ps = J2MC_Manager.getMySQL().getFreshPreparedStatementHotFromTheOven("SELECT `warp_name`,`world`,`x`,`y`,`z`,`pitch`,`yaw` FROM `teleport` WHERE `owner`= ? and `server_id`= ?");
            ps.setString(1, name);
            ps.setInt(2, J2MC_Manager.getServerID());
            final ResultSet resultSet = ps.executeQuery();
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
