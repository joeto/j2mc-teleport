package to.joe.j2mc.teleport.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.j2mc.core.J2MC_Manager;
import to.joe.j2mc.core.command.MasterCommand;
import to.joe.j2mc.core.exceptions.BadPlayerMatchException;
import to.joe.j2mc.teleport.J2MC_Teleport;
import to.joe.j2mc.teleport.Teleport_Manager;

public class TeleportCommand extends MasterCommand {

    public TeleportCommand(J2MC_Teleport plugin) {
        super(plugin);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, boolean isPlayer) {
        if (isPlayer && player.hasPermission("j2mc.teleport.to")) {
            if (args.length == 0) {
                player.sendMessage(ChatColor.RED + "Usage: /tp playername");
                return;
            }
            final String targetName = args[0];
            Player target = null;
            try{
            	if(player.hasPermission("j2mc.teleport.admin")){
            		target = J2MC_Manager.getVisibility().getPlayer(targetName, null);
            	}else{
            	target = J2MC_Manager.getVisibility().getPlayer(targetName, player);
            	}
            }catch(BadPlayerMatchException e){
            	player.sendMessage(ChatColor.RED + e.getMessage());
            	return;
            }
            if (target.hasPermission("j2mc.teleport.protected") && !player.hasPermission("j2mc.teleport.admin")) {
                player.sendMessage(ChatColor.RED + "Cannot teleport to protected player.");
            } else if (target.getName().equalsIgnoreCase(player.getName())) {
                player.sendMessage(ChatColor.RED + "Can't teleport to yourself");
            } else {
                Teleport_Manager.tele().teleport(player, target.getLocation());
                player.sendMessage("OH GOD I'M FLYING AAAAAAAAH");
                J2MC_Manager.getLog().info(player.getName() + " teleported to " + target.getName());
            }
        }
    }
}
