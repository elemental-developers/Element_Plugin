package io.github.elementaldevelopers.elementsplugin;

import java.lang.reflect.Field;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;


public final class Main extends JavaPlugin implements Listener{

	/**
	 * Returns skull given url.
	 * @author zylem on spigotmc.org
	 * @
	 * Source https://www.spigotmc.org/threads/tutorial-player-skull-with-custom-skin.143323/
	 * @param url
	 * @return head
	 */
	public ItemStack getSkull(String url, UUID uuid) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        if (url == null || url.isEmpty())
            return skull;
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        GameProfile profile = new GameProfile(uuid, null);
        byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField = null;
        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        profileField.setAccessible(true);
        try {
            profileField.set(skullMeta, profile);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        skull.setItemMeta(skullMeta);
        return skull;
    }
	@Override
	public void onEnable(){
		this.getServer().getPluginManager().registerEvents(this, this);
		getLogger().info("Starting the ElementsPlguinRewrite! Thanks for using us :D!");
	}
	
	@Override
	public void onDisable() {
		getLogger().info("Stopping the ElementsPlguinRewrite! Thanks for using us :D!");
	}
	public boolean command(Command cmd, String str) {
		return cmd.getName().equalsIgnoreCase(str);
	}
	public boolean checkPlayer(CommandSender sender) {
		return sender instanceof Player;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (command(cmd, "summonhydrogen")){
			if(!checkPlayer(sender)) {
				sender.sendMessage(ChatColor.GOLD + "[BOT]" + ChatColor.WHITE + " Charles: This command can only be used by a player! D:");
				return false;
			} else {
				Player p = (Player) sender;
				Inventory i = p.getInventory();
				ItemStack Item = getSkull("http://textures.minecraft.net/texture/9322c9aeb5c13a4d35592dbd8b2a5a13029f51585011c9faafda9aef3922ceda", UUID.fromString("c7c92b81-3987-4220-9069-207790a780d7"));
				SkullMeta meta = (SkullMeta) Item.getItemMeta();
				meta.setDisplayName(ChatColor.BLUE + "Hydrogen");
				Item.setItemMeta(meta);
				i.addItem(Item);
				sender.sendMessage(ChatColor.GOLD + "[BOT]" + ChatColor.WHITE + " Charles: Gave you a Hydrogen Block :D");
				return true;
			}
		}
		return false; 
	}
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Block block = e.getBlock();
		Location loc = block.getLocation();
		if (block.getType() == Material.PLAYER_HEAD|| block.getType() == Material.PLAYER_WALL_HEAD){
			//Skull skull = (Skull) block;
			//TODO: Make a comparison mechanism that actually works because this will only work if we only have hydrogen.
			loc.getBlock().setType(Material.WATER);
		}
	}
}
