package io.github.mysixsenses.ElementPlugin;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin implements Listener{
	HashSet<Block> set;
	 public void onEnable() {
	        Bukkit.getServer().getPluginManager().registerEvents(this, this);
	        System.out.println("Starting Elements Plugin by RedKaneChironic");
	    }
	/**
	 	* Checks if a command input is equal to the string 
	*/
	private boolean command(Command cmd, String str) {
		return cmd.getName().equalsIgnoreCase(str);
	}
	private boolean checkplayer(CommandSender sender) {
		return sender instanceof Player;
	}
	/**
     * Get the cardinal compass direction of a player.
     * 
     * @param player
     * @return
     */
    public static String getCardinalDirection(Player player) {
        double rot = (player.getLocation().getYaw() - 90) % 360;
        if (rot < 0) {
            rot += 360.0;
        }
        return getDirection(rot);
    }

    /**
     * Converts a rotation to a cardinal direction name.
     * 
     * @param rot
     * @return direction
     * 
     */
    private static String getDirection(double rot) {
        if (0 <= rot && rot < 22.5) {
            return "North";
        } else if (22.5 <= rot && rot < 67.5) {
            return "Northeast";
        } else if (67.5 <= rot && rot < 112.5) {
            return "East";
        } else if (112.5 <= rot && rot < 157.5) {
            return "Southeast";
        } else if (157.5 <= rot && rot < 202.5) {
            return "South";
        } else if (202.5 <= rot && rot < 247.5) {
            return "Southwest";
        } else if (247.5 <= rot && rot < 292.5) {
            return "West";
        } else if (292.5 <= rot && rot < 337.5) {
            return "Northwest";
        } else if (337.5 <= rot && rot < 360.0) {
            return "North";
        } else {
            return null;
        }
    }
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(command(cmd, "summonhydrogen")) {
			if (!(checkplayer(sender))) {
				sender.sendMessage(ChatColor.GOLD + "[BOT] Charles:" + ChatColor.WHITE + "Sorry, this command can only be used by players.");
				return false;
			}
			Player p = (Player) sender;
			if (p.hasPermission("ElementPlugin.summonhydrogen")) {
				String Direction = getCardinalDirection(p);
				Location loc = p.getLocation();
				switch(Direction) {
				case "North":
					loc.setZ(loc.getZ() - 2);
					Block b = loc.getBlock();
					b.setType(Material.STONE);
					set.add(b);
				case "Northeast":
					loc.setZ(loc.getZ() - 1);
					loc.setX(loc.getX() + 1);
					Block b1 = loc.getBlock();
					b1.setType(Material.STONE);
					set.add(b1);
				case "South":
					loc.setX(loc.getX() + 2);
					Block b2 = loc.getBlock();
					b2.setType(Material.STONE);
					set.add(b2);
				case "Southwest":
					loc.setZ(loc.getZ() + 1);
					loc.setX(loc.getX() + 1);
					Block b3 = loc.getBlock();
					b3.setType(Material.STONE);
					set.add(b3);
				case "West":
					loc.setX(loc.getX() - 2);
					Block b4 = loc.getBlock();
					b4.setType(Material.STONE);
					set.add(b4);
				case "Northwest":
					loc.setZ(loc.getZ() - 1);
					loc.setX(loc.getX() - 1);
					Block b5 = loc.getBlock();
					b5.setType(Material.STONE);
					set.add(b5);
				}
				return true;
			} else {
				sender.sendMessage(ChatColor.GOLD + "[BOT] Charles:" + ChatColor.WHITE + "Sorry, you don't have the permissions to use this command");
			}
		}
		return false;
	 }
	@EventHandler (ignoreCancelled = true)
	public void OnBlockBreak(BlockBreakEvent e) {
		Block b = e.getBlock();
		if (set.contains(b)){
			Location location = b.getLocation();
			Block block = location.getBlock();
			block.setType(Material.FIRE);
		}
	}
}