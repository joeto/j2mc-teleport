package to.joe.j2mc.teleport.command.admin;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.j2mc.core.command.MasterCommand;
import to.joe.j2mc.teleport.J2MC_Teleport;

public class HomeInvasionCommand extends MasterCommand{

    J2MC_Teleport plugin;
    
    public HomeInvasionCommand(J2MC_Teleport teleport){
        super(teleport);
        this.plugin = teleport;
    }
    
    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, boolean isPlayer) {
        if(isPlayer && player.hasPermission("j2mc.core.admin")){
            if(args.length == 1){
                String target = args[0];
                final HashMap<String, Location> warps = plugin.getWarps(target);
                if (warps.size() == 0) {
                    player.sendMessage(ChatColor.RED + "User has no homes.");
                } else {
                    final StringBuilder homesList = new StringBuilder();
                    final boolean first = true;
                    for (final String warpName : warps.keySet()) {
                        if (!first) {
                            homesList.append(", ");
                        }
                        homesList.append(warpName);
                    }
                    player.sendMessage(ChatColor.RED + target + "'s homes: " + ChatColor.WHITE + homesList);
                    player.sendMessage(ChatColor.RED + "To go to a home, say /hi " + target + " home");
                }
            }else{
                String target = args[0];
                String home = args[1];
                final Location location = plugin.getNamedWarp(target, home);
                if (location != null) {
                    player.sendMessage(ChatColor.RED + "Whoosh!");
                    ((J2MC_Teleport) plugin).teleport(player, location);
                } else {
                    player.sendMessage(ChatColor.RED + "That home does not exist. For a list, say /hi " + target);
                }
            }
        }
    }
}
