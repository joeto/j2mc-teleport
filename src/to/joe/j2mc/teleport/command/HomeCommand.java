package to.joe.j2mc.teleport.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.j2mc.core.command.MasterCommand;
import to.joe.j2mc.teleport.J2MC_Teleport;

public class HomeCommand extends MasterCommand<J2MC_Teleport> {

    public HomeCommand(J2MC_Teleport teleport) {
        super(teleport);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, boolean isPlayer) {
        if (isPlayer) {
            if (args.length == 0) {
                final HashMap<String, Location> warps = this.plugin.getWarps(player.getName());
                if (warps.size() == 0) {
                    player.sendMessage(ChatColor.RED + "You have no homes available.");
                    player.sendMessage(ChatColor.RED + "Use the command /sethome");
                } else {
                    final StringBuilder homesList = new StringBuilder();
                    for (final String warpName : warps.keySet()) {
                        homesList.append(warpName);
                        homesList.append(", ");
                    }
                    homesList.setLength(homesList.length() - 2);
                    player.sendMessage(ChatColor.RED + "Homes: " + ChatColor.WHITE + homesList);
                    player.sendMessage(ChatColor.RED + "To go to a home, say /home homename");
                }
            } else {
                final Location location = ((J2MC_Teleport) this.plugin).getNamedWarp(player.getName(), args[0]);
                if (location != null) {
                    player.sendMessage(ChatColor.RED + "Whoosh!");
                    ((J2MC_Teleport) this.plugin).teleport(player, location);
                } else {
                    player.sendMessage(ChatColor.RED + "That home does not exist. For a list, say /home");
                }
            }
        }
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (sender instanceof Player) {
            
            Player player = (Player) sender;
            
            if (args.length == 1) {
                final HashMap<String, Location> warps = this.plugin.getWarps(player.getName());
                
                List<String> sortedWarps = new ArrayList<String>(warps.keySet());
                Collections.sort(sortedWarps);
                
                List<String> potentialMatches = new ArrayList<String>();
                for (String warp : sortedWarps){
                    if (warp.startsWith(args[(args.length - 1)])) {
                        potentialMatches.add(warp);
                    }
                }
                
                return potentialMatches;
            }
        }
        
        return new ArrayList<String>();
    }
    
}
