/*
 * Copyright (C) 2020 2020 elementaldevelopers on github.com
 *
 * Licensed under the MIT License:
 * Copyright (c) 2020 elementaldevelopers on github.com
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.elementaldevelopers.elementsplugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
//import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public final class Main extends JavaPlugin implements Listener{
	static private JsonParser parser = new JsonParser();
    static private String API_PROFILE_LINK = "https://sessionserver.mojang.com/session/minecraft/profile/";
	public boolean giveElement(ChatColor color, String element, CommandSender sender, String[] args, UUID id, String UsernametoGet, String OverwriteURL) {
		if(!checkPlayer(sender) && args.length == 0) {
			sender.sendMessage(ChatColor.GOLD + "[BOT]" + ChatColor.WHITE + " Charles: This command with no arguments can only be used by a player! D:");
			return false;
		} else {
			Player p;
			if (args.length == 0) {
				p = (Player) sender;
			} else {
				p = Bukkit.getServer().getPlayer(args[0]);
				if (p == null) {
					sender.sendMessage("Player isn't online!");
					return false;
				}
			}
			Inventory i = p.getInventory();
			ItemStack Item = getSkull(UsernametoGet, id, OverwriteURL);
			SkullMeta meta = (SkullMeta) Item.getItemMeta();
			meta.setDisplayName(color + element);
			Item.setItemMeta(meta);
			i.addItem(Item);
			p.sendMessage(ChatColor.GOLD + "[BOT]" + ChatColor.WHITE + " Charles: Gave you a " + element + " Block :D");
			return true;
		}
	}
	/**
	 * Returns skull given url.
	 * @author zylem on spigotmc.org
	 * <br> Source https://www.spigotmc.org/threads/tutorial-player-skull-with-custom-skin.143323/
	 * Edited by the ElementalDevelopers
	 * @param username uuid OverwriteURL
	 * @return head
	 */
	public ItemStack getSkull(String username, UUID uuid, String OverwriteURL) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        String url;
        if (OverwriteURL.isEmpty()) {
        	url = getSkinUrl(username);
        } else {
        	url = OverwriteURL;
        }
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
	//https://www.spigotmc.org/threads/offlineplayer-game-profile.234998/#post-2373355
	public static String getSkinUrl(String uuid){
    		if (uuid == null||uuid.isEmpty()) {
    			throw new NullPointerException("Username cannot be null");
    		}
            String json = getContent(API_PROFILE_LINK + uuid);
            JsonObject o = parser.parse(json).getAsJsonObject();
            String jsonBase64 = o.get("properties").getAsJsonArray().get(0).getAsJsonObject().get("value").getAsString();
    
            o = parser.parse(new String(Base64.decodeBase64(jsonBase64))).getAsJsonObject();
            String skinUrl = o.get("textures").getAsJsonObject().get("SKIN").getAsJsonObject().get("url").getAsString();
            return skinUrl;
    }
    //https://www.spigotmc.org/threads/offlineplayer-game-profile.234998/#post-2373355
    public static String getContent(String link){
            try {
                URL url = new URL(link);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String outputLine = "";
            
                String inputLine;
                while ((inputLine = br.readLine()) != null) {
                    outputLine += inputLine;
                }
                br.close();
                return outputLine;
        
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
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
			return giveElement(ChatColor.DARK_PURPLE, "Hydrogen", sender, args, UUID.fromString("c7c92b81-3987-4220-9069-207790a780d7"), "c7c92b81-3987-4220-9069-207790a780d7", "");
		} else if (command(cmd, "summonHelium")) {
			return giveElement(ChatColor.DARK_PURPLE, "Helium", sender, args, UUID.fromString("872b4a42-d467-459f-bcbb-51564bfc9d66"), "", "http://textures.minecraft.net/texture/3f04867d9d4f3891531bdee1cb184044e87d114fda87854140b9422c1a602019");
		} else if (command(cmd, "summonLithium")) {
			return giveElement(ChatColor.GRAY, "Lithium", sender, args, UUID.fromString("55b456f4-2723-4b57-b31d-6b5dc947606e"), "", "http://textures.minecraft.net/texture/f1874fd3ff80de7f700ca4649e4a2aa7be09ea69e1fa0536448d11c1b195cc7e");
		} else if (command(cmd, "summonBeryllium")) {
			return giveElement(ChatColor.GRAY, "Beryllium", sender, args, UUID.fromString("3c3c7f6b-bcdf-4dd1-ba27-3a7b0883f298"), "", "http://textures.minecraft.net/texture/e79c7dcd51040ddbd752e295ff97535bda197ae870ee1b421eb3136f0fb62313");
		} else if (command(cmd, "summonBoron")) {
			return giveElement(ChatColor.BLACK, "Boron", sender, args, UUID.fromString("37835b88-4e7c-456b-8b8f-b4fbf80fce8e"), "", "http://textures.minecraft.net/texture/9631597dce4e4051e8d5a543641966ab54fbf25a0ed6047f11e6140d88bf48f");
		} else if (command(cmd, "summonCarbonDiamond")) {
			return giveElement(ChatColor.BLUE, "Carbon (Diamond)", sender, args, UUID.fromString("462bafcf-023f-4bc6-8be9-0b924e8b11db"), "", "http://textures.minecraft.net/texture/93b9b4c74bacca1ee9f5d348c216d4ce4e74bbcbea776002d821186a32e1b29e");
		} else if (command(cmd, "summonCarbonCoal")) {
			return giveElement(ChatColor.BLACK, "Carbon (Coal)", sender, args, UUID.fromString("045f2cef-4771-493c-9e92-f71d540f6669"), "", "http://textures.minecraft.net/texture/d7f5766d2928dc0df1b3404c3bd073c9476d26c80573b0332e7cce73df15482a");
		} else if (command(cmd, "summonNitrogen")) {
			return giveElement(ChatColor.BLACK, "Nitrogen", sender, args, UUID.fromString("30ca6346-23ae-464d-b8e3-176097f6c3ad"), "", "https://textures.minecraft.net/texture/2825df1a24aed14a5c398f7f152ff2e118d7164aafd30bca8c893a40e0b35a78");
		} else if (command(cmd, "summonOxygen")) {
			return giveElement(ChatColor.BLACK, "Oxygen", sender, args, UUID.fromString("1948139d-0f97-44a2-a675-e798af812ce5"), "", "http://textures.minecraft.net/texture/11d6d8f887f3554ccf407d7ee2f5cf942ab1a268595aff36e9c37f2bc4ad551d");
		} else if (command(cmd, "summonFluorine")) {
			return giveElement(ChatColor.BLACK, "Fluorine", sender, args, UUID.fromString("9edde086-69d3-4341-a725-4d10eed1eaf1"), "", "https://textures.minecraft.net/texture/8644fd826b25ee10363d67d3e08a4e276c2b72935d6484abcd4a2823a57a01a1");
		} else if (command(cmd, "summonNeon")) {
			return giveElement(ChatColor.RED, "Neon", sender, args, UUID.fromString("4f1c308a-8b98-480d-8e40-a53acb16118c"), "", "https://textures.minecraft.net/texture/9de58dc6c363b1ce4f1a5c2e02c51f97c5316b5b6e7cccaf4575b8eb1ff51b68");
		} else if (command(cmd, "summonSodium")) {
			return giveElement(ChatColor.GRAY, "Sodium", sender, args, UUID.fromString("c87510d0-0a78-43a1-bab3-c3c479aacc97"), "", "https://textures.minecraft.net/texture/450acad548b445da4afecae96c3ae14afed2cf038b1ce4cd013d298d6a293bb1");
		} else if (command(cmd, "summonMagnesium")) {
			return giveElement(ChatColor.GRAY, "Magnesium", sender, args, UUID.fromString("8c30208d-861a-4962-ba53-066bca3219b8"), "", "http://textures.minecraft.net/texture/eb1fa631faa2afe9be65bb6bce8c26781add0fc271bb4cfd52a97bdc891882d3");
		} else if (command(cmd, "summonAluminum")) {
			return giveElement(ChatColor.GRAY, "Aluminum", sender, args, UUID.fromString("e191a8f3-5a26-4c3b-9265-33f53ee994e5"), "", "https://textures.minecraft.net/texture/96c18ebd6023915b4a542d37a565670bb23c84e316c2167d448ddab4c748098b ");
		} else if (command(cmd, "summonSilicon")) {
			return giveElement(ChatColor.GRAY, "Silicon", sender, args, UUID.fromString("7673abcb-6046-4729-92a6-1ce11df607ca"), "", "https://textures.minecraft.net/texture/b7b44563e06c2553f94709341e4110abed0e23ccc050a1e9f99243ef28173e51");
		} else if (command(cmd, "summonVioletPhosphorus")) {
			return giveElement(ChatColor.DARK_PURPLE, "Violet Phosphorus", sender, args, UUID.fromString("f98f76cd-2f10-4881-9b7d-6a877f5843a4"), "", "https://textures.minecraft.net/texture/ede2c014a7a812f91e0339665513ea4b23f4554e3bded69918708868d4f441ef");
		} else if (command(cmd, "summonRedPhosphorus")) {
			return giveElement(ChatColor.RED, "Red Phosphorus", sender, args, UUID.fromString("d7221687-b882-4003-aaa9-d5b8f97395b8"), "", "https://textures.minecraft.net/texture/ea13c7d32660e239dfc40c8d226acfcb2581f9922987b5e1f7d482bb4e7e6370");
		} else if (command(cmd, "summonWhitePhosphorus")) {
			return giveElement(ChatColor.WHITE, "White Phosphorus", sender, args, UUID.fromString("41bcc900-b359-405c-ac50-fdecc489673f"), "", "https://textures.minecraft.net/texture/c8649b949e0e99a07d89dfaa4a3772b22b03618d71fa79ab26e2656783797530");
		} else if (command(cmd, "summonBlackPhosphorus")) {
			return giveElement(ChatColor.BLACK, "Black Phosphorus", sender, args, UUID.fromString("e6e33873-09e9-40fe-9aff-cfaca7f8fe03"), "", "https://textures.minecraft.net/texture/914a33db09bb4a059a9ac5588c75e98b888ca4dad31fd528562a9b92079dd14e");
		} else if (command(cmd, "summonSulfur")) {
			return giveElement(ChatColor.YELLOW, "Sulfur", sender, args, UUID.fromString("b45da052-c66f-4e98-b472-8a4994d5a945"), "", "https://textures.minecraft.net/texture/f2fd2df280e9e9529ba981f4f79b8392e4fd74d2edc3896b621b0fa4015b9e86");
		} else if (command(cmd, "summonChlorine")) {
			return giveElement(ChatColor.YELLOW, "Chlorine", sender, args, UUID.fromString("1e91d917-e774-4ba8-8495-f0f6d4925276"), "", "https://textures.minecraft.net/texture/194e1abfb8fc84848ffd37020a5cfcb15a2857ef2402e596a5975d17c7ece5ec");
		} else if (command(cmd, "summonArgon")) {
			return giveElement(ChatColor.BLACK, "Argon", sender, args, UUID.fromString("713711b2-4fa9-4745-9418-ac250bbbcf40"), "", "https://textures.minecraft.net/texture/342e21e3da653473634fd56783ec2eb37081629b55eb008dda0a38e84b2978e8");
		} else if (command(cmd, "summonPotassium")) {
			return giveElement(ChatColor.GRAY, "Potassium", sender, args, UUID.fromString("b088e244-cf5d-4de5-87a9-73f17d379f39"), "", "https://textures.minecraft.net/texture/62cdc807aa2c7b5b24264ed3d8b4c1d5e63f3ae1e9c0977c1bb02ea19b5c5bce");
		} else if (command(cmd, "summonCalcium")) {
			return giveElement(ChatColor.GRAY, "Calcium", sender, args, UUID.fromString("081710cc-0883-4d36-a63c-c9f4f8869881"), "", "https://textures.minecraft.net/texture/71d18823e4a4cab5271aea5e1eae3ffa6b23f1bddce3389fa3d30aab40bffc22");
		} else if (command(cmd, "summonScandium")) {
			return giveElement(ChatColor.GRAY, "Scandium", sender, args, UUID.fromString("26b8c131-f488-48f1-86cb-a25592534528"), "", "https://textures.minecraft.net/texture/bbebc5eb656b165156783eae5945fcd0f84e8e8e38b98e65cff47ca2b66f5513");
		} else if (command(cmd, "summonTitanium")) {
			return giveElement(ChatColor.GRAY, "Titanium", sender, args, UUID.fromString("033ce3ca-37a5-4067-9cc8-81926c23a789"), "", "https://textures.minecraft.net/texture/1506656e20faa51b471afe56de4bcd2ab7446b2283f446b686ab392805661726");
		} else if (command(cmd, "summonVanadium")) {
			return giveElement(ChatColor.GRAY, "Vanadium", sender, args, UUID.fromString("59b9ba96-cc56-424f-9b5b-59aef4fd86c5"), "", "https://textures.minecraft.net/texture/24af890eaa7dabc2f3b67c8761ec19db018dbb13791c01063a7d6d2e7fb5c38e");
		} else if (command(cmd, "summonChromium")) {
			return giveElement(ChatColor.GRAY, "Chromium", sender, args, UUID.fromString("8e0a90b8-6aba-4e81-8e17-5e6beacc703c"), "", "https://textures.minecraft.net/texture/fa87378b4fcfae8f3490ed12a4e1a493e741de153e8f5c5bcf757e2f8f7585ea");
		} else if (command(cmd, "summonManganese")) {
			return giveElement(ChatColor.GRAY, "Manganese", sender, args, UUID.fromString("bf846f4d-4d03-4d2f-9b98-7b573a4fc7da"), "", "https://textures.minecraft.net/texture/7a838d46e63a06a3fa8a1d0d985ad65d72c57d406acc5a597faf574d86f72f75");
		} else if (command(cmd, "summonIron")) {
			return giveElement(ChatColor.GRAY, "Iron", sender, args, UUID.fromString("682f330e-9dbf-4510-bc1e-518e309413c0"), "", "https://textures.minecraft.net/texture/967139f5289cd3961ac3a09e70028cf733b5ebc686b0097fd5d0168111f0fd4c");
		} else if (command(cmd, "summonCobalt")) {
			return giveElement(ChatColor.GRAY, "Cobalt", sender, args, UUID.fromString("c90250a1-7bf7-44dd-8eae-d515947aec3f"), "", "https://textures.minecraft.net/texture/95b62d1bd53609113dcd11c10222658ad546a4b63a3c6a40dcd24b8546cae243");
		} else if (command(cmd, "summonNickel")) {
			return giveElement(ChatColor.GRAY, "Nickel", sender, args, UUID.fromString("dedc5d1a-0ecc-4a2a-b580-8e020fdda1a9"), "", "https://textures.minecraft.net/texture/3989214fb9ad838fd0e5a6fc46cc9eae7f8cfeb0996754ad84fe4050aa0ab2d4");
		} else if (command(cmd, "summonCopper")) {
			return giveElement(ChatColor.GOLD, "Copper", sender, args, UUID.fromString("c108f139-5a7a-4685-bf64-59e72bc89d19"), "", "https://textures.minecraft.net/texture/a5e1a6b822015136ccb70773fdcfe0470df2ce0f2a252b5f29668f841336351c");
		} else if (command(cmd, "summonZinc")) {
			return giveElement(ChatColor.GRAY, "Zinc", sender, args, UUID.fromString("ea331551-27a1-4573-a881-114098e48f87"), "", "https://textures.minecraft.net/texture/21037b5bd6dc6376550b03d78bb28e4be387b72342a254fb93ad8bc5f6c2484");
		} else if (command(cmd, "summonGallium")) {
			return giveElement(ChatColor.GRAY, "Gallium", sender, args, UUID.fromString("350a668c-869d-4633-a9a3-ab789e30e330"), "", "https://textures.minecraft.net/texture/21037b5bd6dc6376550b03d78bb28e4be387b72342a254fb93ad8bc5f6c2484");
		} else if (command(cmd, "summonGermanium")) {
			return giveElement(ChatColor.GRAY, "Germanium", sender, args, UUID.fromString("350a668c-869d-4633-a9a3-ab789e30e330"), "", "https://textures.minecraft.net/texture/bbebc5eb656b165156783eae5945fcd0f84e8e8e38b98e65cff47ca2b66f5513");
		} else if (command(cmd, "summonGrayArsenic")) {
			return giveElement(ChatColor.GRAY, "Gray Arsenic", sender, args, UUID.fromString("545cdd8a-11a4-4b86-beba-147aa650bfc8"), "", "https://textures.minecraft.net/texture/31550f3a639029d6c81c1f725f2a77e6563ee8b88e3e62b0b8b5a50ae78d3f2e");
		} else if (command(cmd, "summonYellowArsenic")) {
			return giveElement(ChatColor.YELLOW, "Yellow Arsenic", sender, args, UUID.fromString("208fa758-0eb1-43a5-9bdf-6b4b3df0b4ec"), "", "https://textures.minecraft.net/texture/9cdac32a7af7a6a3ee276fcc5276437e0d9cbdc2d8e29c7923a5bb24bc726278");
		} else if (command(cmd, "summonBlackArsenic")) {
			return giveElement(ChatColor.BLACK, "Black Arsenic", sender, args, UUID.fromString("07d96959-f564-40b4-8555-ddc9def9cd3c"), "", "https://textures.minecraft.net/texture/3524159efaa39a78ddd41ae48cea47c63b5eb6d90fe807412cffaf0fd69a8b5e");
		} else if (command(cmd, "summonBlackSelenium")) {
			return giveElement(ChatColor.BLACK, "Black Selenium", sender, args, UUID.fromString("75ed9df9-9a81-49d6-8660-9b2621edae37"), "", "https://textures.minecraft.net/texture/3b6aeee666de33b180823b350086d59d9ebeaca8469b0a351f0fb5f1bda056a0");
		} else if (command(cmd, "summonRedSelenium")) {
			return giveElement(ChatColor.RED, "Red Selenium", sender, args, UUID.fromString("ad5bcded-7f16-4b71-be7a-0d536f8fa9c1"), "", "https://textures.minecraft.net/texture/38352694f74e613e05601b959fca50119f5b38a346c1a72c4de5be812417f307");
		} else if (command(cmd, "summonBromine")) {
			return giveElement(ChatColor.RED, "Bromine", sender, args, UUID.fromString("bf7c4680-5289-4953-a7c3-113d6f107823"), "", "https://textures.minecraft.net/texture/12245a2c6af25e093f61c598ece8bbe750c005e7f0c5786dd912cc32f7ba1708");
		} else if (command(cmd, "summonKrypton")) {
			return giveElement(ChatColor.BLACK, "Krypton", sender, args, UUID.fromString("9405125f-4433-4a8d-9933-b479b1a9917f"), "", "https://textures.minecraft.net/texture/374267841617298015c9cf9318756645e42d510620125adfd74425336ed7c9dc");
		} else if (command(cmd, "summonRubidium")) {
			return giveElement(ChatColor.GRAY, "Rubidium", sender, args, UUID.fromString("ef12dceb-cbeb-4eed-b4d1-21b8d71a32aa"), "", "https://textures.minecraft.net/texture/919e95bbd14a80052c5f4b9c4ce68894765da9ca5c774cac4add49424f7dc3d8");
		} else if (command(cmd, "summonStrontium")) {
			return giveElement(ChatColor.GRAY, "Strontium", sender, args, UUID.fromString("a6d126ac-f3bd-496e-8583-b396662346e9"), "", "https://textures.minecraft.net/texture/5c091a8f337aeffb481ea4d1ab40dde0b23fe60dbcb919b2f63d7b0a6108cd47");
		} else if (command(cmd, "summonYttrium")) {
			return giveElement(ChatColor.GRAY, "Yttrium", sender, args, UUID.fromString("31470eaf-3a9c-4027-8975-0be67e756751"), "", "https://textures.minecraft.net/texture/de001414da7794e0f8fde88691cd53eb40171011c1a110ceff6fb23262cc670d");
		} else if (command(cmd, "summonZirconium")) {
			return giveElement(ChatColor.GRAY, "Zirconium", sender, args, UUID.fromString("03123afd-7c4c-4deb-b696-3fa5ab423850"), "", "https://textures.minecraft.net/texture/b5f880eed48d519850adcf7c425a2317c73de2d60f3746030c3515b3b815a7ea");
		} else if (command(cmd, "summonNiobium")) {
			return giveElement(ChatColor.GRAY, "Niobium", sender, args, UUID.fromString("df6da10d-28ba-41e1-8c0a-e36587a2e06a"), "", "https://textures.minecraft.net/texture/b5f880eed48d519850adcf7c425a2317c73de2d60f3746030c3515b3b815a7ea");
		} else if (command(cmd, "summonMolybdenum")) {
			return giveElement(ChatColor.GRAY, "Molybdenum", sender, args, UUID.fromString("6f33ffbd-b5f1-4246-baef-59debe843629"), "", "https://textures.minecraft.net/texture/a3ea1888d9b9dc9ca13e4da2e550707d6fa49d42a716bda59bb4a8a6446e3e30");
		} else if (command(cmd, "summonTechnetium")) {
			return giveElement(ChatColor.GRAY, "Technetium", sender, args, UUID.fromString("7b901927-0510-41fa-be28-f9cbc417224f"), "", "https://textures.minecraft.net/texture/d8429d5efe17c6c38bd37192f00892e15a655327296be44046971fcbfddae6c4");
		} else if (command(cmd, "summonRuthenium")) {
			return giveElement(ChatColor.GRAY, "Ruthenium", sender, args, UUID.fromString("ae24cf09-a760-47e6-8a30-493609ec974a"), "", "https://textures.minecraft.net/texture/e8e89f8ed2818e11f5d9d924c5672988f3e787017bf75942ec03431062ed6b5e");
		} else if (command(cmd, "summonRhodium")) {
			return giveElement(ChatColor.GRAY, "Rhodium", sender, args, UUID.fromString("144c0f29-9bf7-47e9-9768-ad1a02dcc335"), "", "https://textures.minecraft.net/texture/c456c78a0dc482220b600862bdf9bb74dc87fe83c0e82692dc1c953d3b386da8");
		} else if (command(cmd, "summonPalladium")) {
			return giveElement(ChatColor.GRAY, "Palladium", sender, args, UUID.fromString("14988e95-c118-4e97-b002-7dc87e366476"), "", "https://textures.minecraft.net/texture/cd61a456567f2ce378ec441541210bdb01202a1f6abb04992114f47d2146cad5");
		} else if (command(cmd, "summonSilver")) {
			return giveElement(ChatColor.GRAY, "Silver", sender, args, UUID.fromString("0df967eb-90de-4c68-9773-7cdcc0818ec7"), "", "https://textures.minecraft.net/texture/d85b68901e8ea39085cdd8a39700fb2b7d73e7aff80b9d2a1a3d2de09175bcaddo");
		} else if (command(cmd, "summonCadmium")) {
			return giveElement(ChatColor.GRAY, "Cadmium", sender, args, UUID.fromString("5fd14dea-a82e-41c7-bfaa-95a923c8e4b9"), "", "https://textures.minecraft.net/texture/b5f880eed48d519850adcf7c425a2317c73de2d60f3746030c3515b3b815a7ea");
		} else if (command(cmd, "summonIndium")) {
			return giveElement(ChatColor.GRAY, "Indium", sender, args, UUID.fromString("c22d52c8-f8bb-427b-ad72-7c27f6f2bf1d"), "", "https://textures.minecraft.net/texture/57e7ba1fc30c584711da63dabea71036a0f482c57d9a382a31d74b967bc79531");
		} else if (command(cmd, "summonTin")) {
			return giveElement(ChatColor.GRAY, "Tin", sender, args, UUID.fromString("9f0ac83d-47aa-4112-935d-3832ec8de884"), "", "https://textures.minecraft.net/texture/f847a31d7236fd6ba3a3b57cba44d55fc98e2ac0699137865cff03da87de1f5a");
		} else if (command(cmd, "summonAntimony")) {
			return giveElement(ChatColor.GRAY, "Antimony", sender, args, UUID.fromString("032c85e7-fd31-45a9-b41c-6f34f783cd68"), "", "https://textures.minecraft.net/texture/b5f880eed48d519850adcf7c425a2317c73de2d60f3746030c3515b3b815a7ea");
		} else if (command(cmd, "summonTellurium")) {
			return giveElement(ChatColor.GRAY, "Tellurium", sender, args, UUID.fromString("2fee09f9-d0e8-4324-a74e-0b240fac7860"), "", "todotodo");
		} else if (command(cmd, "summonIodine")) {
			return giveElement(ChatColor.GRAY, "Iodine", sender, args, UUID.fromString("25d5f4f7-b6dd-4cca-9c59-19d79b9a984c"), "", "https://textures.minecraft.net/texture/a3ea1888d9b9dc9ca13e4da2e550707d6fa49d42a716bda59bb4a8a6446e3e30");
		} else if (command(cmd, "summonXenon")) {
			return giveElement(ChatColor.BLACK, "Xenon", sender, args, UUID.fromString("d2524eee-64c7-425c-b50d-5062379090e9"), "", "https://textures.minecraft.net/texture/de4ba7d405c91da2f4b90dd80f74d8ee7ea316915cb03cf5b32ce6694a997a3a");
		} else if (command(cmd, "summonCesium")) {
			return giveElement(ChatColor.GRAY, "Cesium", sender, args, UUID.fromString("11baba35-d7bc-473d-8260-865f24004a32"), "", "https://textures.minecraft.net/texture/99e37aae797a262590ad516c5b066b92b5d4e62cfcf42c02427ae43230179b2b");
		} else if (command(cmd, "summonBarium")) {
			return giveElement(ChatColor.GRAY, "Barium", sender, args, UUID.fromString("aa98d0be-dade-409f-91af-d4b42b6519d2"), "", "https://textures.minecraft.net/texture/b4dd088c002e46a8f468e7699656a132ab490b87947578e3dcd4ba38a9c8d7");
		} else if (command(cmd, "summonLanthanum")) {
			return giveElement(ChatColor.GRAY, "Lanthanum", sender, args, UUID.fromString("fa281ffa-bef9-43d0-941f-c1496c9a03a3"), "", "todotodo");
		} else if (command(cmd, "summonCerium")) {
			return giveElement(ChatColor.GRAY, "Cerium", sender, args, UUID.fromString("a5053933-442f-4527-8682-44285293565a"), "", "todotodo");
		} else if (command(cmd, "summonPraseodymium")) {
			return giveElement(ChatColor.GRAY, "Praseodymium", sender, args, UUID.fromString("ba7d4bad-6ac1-4fa2-951b-77a7226d5489"), "", "todotodo");
		} else if (command(cmd, "summonNeodymium")) {
			return giveElement(ChatColor.GRAY, "Neodymium", sender, args, UUID.fromString("e0dedacb-3a86-4ef5-b36d-663179968ddb"), "", "todotodo");
		} else if (command(cmd, "summonPromethium")) {
			return giveElement(ChatColor.GRAY, "Promethium", sender, args, UUID.fromString("73d6c0b9-5b9a-4da6-88fa-f2f15871555d"), "", "todotodo");
		} else if (command(cmd, "summonSamarium")) {
			return giveElement(ChatColor.GRAY, "Samarium", sender, args, UUID.fromString("92ea9475-ef82-44be-8338-7e3fcc968379"), "", "todotodo");
		} else if (command(cmd, "summonEuropium")) {
			return giveElement(ChatColor.GRAY, "Europium", sender, args, UUID.fromString("cb4606a5-56ef-4f24-8ece-2ac54fc81e69"), "", "todotodo");
		} else if (command(cmd, "summonGadolinium")) {
			return giveElement(ChatColor.GRAY, "Gadolinium", sender, args, UUID.fromString("84c3ab19-3b81-4424-bc18-2ff2a2d03e8e"), "", "todotodo");
		} else if (command(cmd, "summonTerbium")) {
			return giveElement(ChatColor.GRAY, "Terbium", sender, args, UUID.fromString("19f8caf4-06cc-4db1-836c-7df7467d45af"), "", "todotodo");
		} else if (command(cmd, "summonDysprosium")) {
			return giveElement(ChatColor.GRAY, "Dysprosium", sender, args, UUID.fromString("776c8a3f-9115-44a3-9de8-45e081491637"), "", "todotodo");
		} else if (command(cmd, "summonHolmium")) {
			return giveElement(ChatColor.GRAY, "Holmium", sender, args, UUID.fromString("ff44ff80-f567-4d86-82c4-155456415ae6"), "", "todotodo");
		} else if (command(cmd, "summonErbium")) {
			return giveElement(ChatColor.GRAY, "Erbium", sender, args, UUID.fromString("6aadbc7d-7493-4194-aea0-f8b774bd3c02"), "", "todotodo");
		} else if (command(cmd, "summonThulium")) {
			return giveElement(ChatColor.GRAY, "Thulium", sender, args, UUID.fromString("5416a8dd-e18d-4ee5-9a2c-ee4ca765c8f5"), "", "todotodo");
		} else if (command(cmd, "summonYtterbium")) {
			return giveElement(ChatColor.GRAY, "Ytterbium", sender, args, UUID.fromString("41a67856-4e22-4bf4-99c0-a774190cf2c9"), "", "todotodo");
		} else if (command(cmd, "summonLutetium")) {
			return giveElement(ChatColor.GRAY, "Lutetium", sender, args, UUID.fromString("8b5e0c6f-8d33-4813-8371-06330ab91c38"), "", "todotodo");
		} else if (command(cmd, "summonHafnium")) {
			return giveElement(ChatColor.GRAY, "Hafnium", sender, args, UUID.fromString("ca93e099-bedd-4903-8d92-dc7d5cf85416"), "", "todotodo");
		} else if (command(cmd, "summonTantalum")) {
			return giveElement(ChatColor.GRAY, "Tantalum", sender, args, UUID.fromString("39925028-8660-47d9-91a4-e662f3f9dbf9"), "", "todotodo");
		} else if (command(cmd, "summonTungsten")) {
			return giveElement(ChatColor.GRAY, "Tungsten", sender, args, UUID.fromString("b62899d3-a0e5-46fd-a12f-f7bc7fc6547b"), "", "todotodo");
		} else if (command(cmd, "summonRhenium")) {
			return giveElement(ChatColor.GRAY, "Rhenium", sender, args, UUID.fromString("80dbb5fc-bd98-4c27-81ff-70543799caf9"), "", "todotodo");
		} else if (command(cmd, "summonOsmium")) {
			return giveElement(ChatColor.GRAY, "Osmium", sender, args, UUID.fromString("846348b3-b1e1-4c05-8eb6-cbd0b4bd41ca"), "", "todotodo");
		} else if (command(cmd, "summonIridium")) {
			return giveElement(ChatColor.GRAY, "Iridium", sender, args, UUID.fromString("2b65f243-b56b-45be-ac49-cb1d55864d20"), "", "todotodo");
		} else if (command(cmd, "summonPlatinum")) {
			return giveElement(ChatColor.GRAY, "Platinum", sender, args, UUID.fromString("daedbb76-5145-406c-86f6-798441ead560"), "", "todotodo");
		} else if (command(cmd, "summonGold")) {
			return giveElement(ChatColor.GOLD, "Gold", sender, args, UUID.fromString("86d4ae4b-947f-48f0-bf0b-5d22151cb3d1"), "", "https://textures.minecraft.net/texture/da778ab8b98f321be8510238040bc5653347fa52ee10a8b060ddb766e507cc02");
		} else if (command(cmd, "summonMercury")) {
			return giveElement(ChatColor.GRAY, "Mercury", sender, args, UUID.fromString("90fa6bf9-6576-41ce-a61c-900af847438e"), "", "todotodo");
		} else if (command(cmd, "summonThallium")) {
			return giveElement(ChatColor.GRAY, "Thallium", sender, args, UUID.fromString("24af4110-fb4f-4f15-9956-82c9bad2a8b6"), "", "todotodo");
		} else if (command(cmd, "summonLead")) {
			return giveElement(ChatColor.GRAY, "Lead", sender, args, UUID.fromString("e0fafa83-a0fc-450d-a48f-d8e761dcf3b3"), "", "todotodo");
		} else if (command(cmd, "summonBismuth")) {
			return giveElement(ChatColor.GRAY, "Bismuth", sender, args, UUID.fromString("28fd0ce3-f59c-4d03-bbb5-56f9d863fe71"), "", "https://textures.minecraft.net/texture/5abb1fa47fe3dc76ec88614b4eb7645e8e88fcf36bb62bc2bfdd17ede7502dd1");
		} else if (command(cmd, "summonPolonium")) {
			return giveElement(ChatColor.GRAY, "Polonium", sender, args, UUID.fromString("9b8085e0-c53d-49e5-ab2f-f4de5eca1029"), "", "https://textures.minecraft.net/texture/f2fd2df280e9e9529ba981f4f79b8392e4fd74d2edc3896b621b0fa4015b9e86");
		} else if (command(cmd, "summonAstatine")) {
			return giveElement(ChatColor.GRAY, "Astatine", sender, args, UUID.fromString("f03f0f3d-1340-4e24-853e-6fec4d50cf37"), "", "todotodo");
		} else if (command(cmd, "summonRadon")) {
			return giveElement(ChatColor.BLACK, "Radon", sender, args, UUID.fromString("3ded254a-23b1-4d93-9a4f-42c23c4a7bc2"), "", "todotodo");
		} else if (command(cmd, "summonFrancium")) {
			return giveElement(ChatColor.GRAY, "Francium", sender, args, UUID.fromString("00ebbe6e-7656-40f9-97f1-c9b2e36d4e46"), "", "todotodo");
		} else if (command(cmd, "summonRadium")) {
			return giveElement(ChatColor.GRAY, "Radium", sender, args, UUID.fromString("7517c8d0-65d7-47ac-ad64-a5632bfcde55"), "", "https://textures.minecraft.net/texture/3e1244801e11772f54e90c26180a1a584a85e4a24dbc11a8f8a29b75f1e245a7");
		} else if (command(cmd, "summonActinium")) {
			return giveElement(ChatColor.GRAY, "Actinium", sender, args, UUID.fromString("d1734d1c-b6cf-4a45-b666-b575f5b82aac"), "", "todotodo");
		} else if (command(cmd, "summonThorium")) {
			return giveElement(ChatColor.GRAY, "Thorium", sender, args, UUID.fromString("52d46db8-6c71-480c-9ac0-df43ae5def91"), "", "https://textures.minecraft.net/texture/1f46c0cf082a092832f0d67fa5051a5ca05ec21bd560552169fa6cce6939ea85");
		} else if (command(cmd, "summonProtactinium")) {
			return giveElement(ChatColor.GRAY, "Protactinium", sender, args, UUID.fromString("f84c98e6-6346-46db-9f49-ac97e43e4d59"), "", "todotodo");
		} else if (command(cmd, "summonUranium")) {
			return giveElement(ChatColor.GRAY, "Uranium", sender, args, UUID.fromString("3bf8b261-1d16-4481-ad92-82cdcf3c6ba2"), "", "todotodo");
		} else if (command(cmd, "summonNeptunium")) {
			return giveElement(ChatColor.GRAY, "Neptunium", sender, args, UUID.fromString("eb30eef4-bd67-446e-bae5-a8c2aef306dd"), "", "todotodo");
		}
//			} else if (command(cmd, "summonPlutonium")) {
//			return giveElement(ChatColor.GRAY, "Plutonium", sender, args, UUID.fromString("9866e791-29a6-47d2-8a5c-222d68f060e1"), "", "todotodo");
//		} else if (command(cmd, "summonAmericium")) {
//			return giveElement(ChatColor.GRAY, "Americium", sender, args, UUID.fromString("633d27d9-16bc-4f1d-86de-81a8319ffdfa"), "", "todotodo");
//		} else if (command(cmd, "summonCurium")) {
//			return giveElement(ChatColor.GRAY, "Curium", sender, args, UUID.fromString("1f24c146-3b18-4ece-990b-414e78e4485a"), "", "todotodo");
//		}// else if (command(cmd, "summonBerkelium")) {
//			return giveElement(ChatColor.RESET, "Berkelium", sender, args, UUID.fromString("80ca88e2-d9c1-4665-a4b7-450409a373a3"), "", "todotodo");
//		} else if (command(cmd, "summonCalifornium")) {
//			return giveElement(ChatColor.RESET, "Californium", sender, args, UUID.fromString("ee729053-cbaf-48ac-aeea-6c8bad08668c"), "", "todotodo");
//		} else if (command(cmd, "summonEinsteinium")) {
//			return giveElement(ChatColor.RESET, "Einsteinium", sender, args, UUID.fromString("a33bc4f1-ac41-4b10-8259-aaa490a8fa2d"), "", "todotodo");
//		} else if (command(cmd, "summonFermium")) {
//			return giveElement(ChatColor.RESET, "Fermium", sender, args, UUID.fromString("da41fc54-1d1b-4235-8b66-40165bc5f7e7"), "", "todotodo");
//		} else if (command(cmd, "summonMendelevium")) {
//			return giveElement(ChatColor.RESET, "Mendelevium", sender, args, UUID.fromString("563d6133-5f24-4151-bd99-ed69228f7af8"), "", "todotodo");
//		} else if (command(cmd, "summonNobelium")) {
//			return giveElement(ChatColor.RESET, "Nobelium", sender, args, UUID.fromString("bdb495a8-6084-40a4-a5d0-bd4ddacc76d6"), "", "todotodo");
//		} else if (command(cmd, "summonLawrencium")) {
//			return giveElement(ChatColor.RESET, "Lawrencium", sender, args, UUID.fromString("fcf0bd14-fb90-4049-ab6c-f165729a8fc6"), "", "todotodo");
//		} else if (command(cmd, "summonRutherfordium")) {
//			return giveElement(ChatColor.RESET, "Rutherfordium", sender, args, UUID.fromString("232a914d-7c76-40ae-aa6e-853f2228f59d"), "", "todotodo");
//		} else if (command(cmd, "summonDubnium")) {
//			return giveElement(ChatColor.RESET, "Dubnium", sender, args, UUID.fromString("fdfe3099-07aa-4700-b19e-8ad9d4cb0e66"), "", "todotodo");
//		} else if (command(cmd, "summonSeaborgium")) {
//			return giveElement(ChatColor.RESET, "Seaborgium", sender, args, UUID.fromString("05016969-b297-4290-8cd1-5b23c031c9c2"), "", "todotodo");
//		} else if (command(cmd, "summonBohrium")) {
//			return giveElement(ChatColor.RESET, "Bohrium", sender, args, UUID.fromString("94efdf35-0671-418c-9a57-fb2e98fd713d"), "", "todotodo");
//		} else if (command(cmd, "summonHassium")) {
//			return giveElement(ChatColor.RESET, "Hassium", sender, args, UUID.fromString("398d6716-f554-4815-87e2-187c6b967579"), "", "todotodo");
//		}
		return false;
	}
	public boolean matchingSkull(Skull skull, String uuid) {
		return skull.getOwningPlayer() == Bukkit.getOfflinePlayer(UUID.fromString(uuid));
	}
//	TODO: Make Radioactive Elements disappear after a short period
//	@EventHandler
//	public void BlockPlace(BlockPlaceEvent e) {
//		Block b = e.getBlock();
//		if ((b.getType() == Material.PLAYER_HEAD|| b.getType() == Material.PLAYER_WALL_HEAD) && b.getState() instanceof Skull){
//			Skull bl = (Skull) b.getState();
//			if (matchingSkull(bl, "7b901927-0510-41fa-be28-f9cbc417224f")) {
//				Bukkit.getServer().getScheduler().runTaskLater(this, new Runnable()
//			      {
//			        @Override
//			        public void run()
//			        {
//			            bl.setType(Material.AIR);
//			            
//			        }
//			      }, (20*60));
//			}
//		}
//	}
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Block block = e.getBlock();
		Location loc = block.getLocation();
		if ((block.getType() == Material.PLAYER_HEAD|| block.getType() == Material.PLAYER_WALL_HEAD) && block.getState() instanceof Skull){
			Skull skull = (Skull) block.getState();
			if (matchingSkull(skull, "c7c92b81-3987-4220-9069-207790a780d7")) {
				//Hydrogen
				loc.getBlock().setType(Material.WATER);
			} else if (matchingSkull(skull,"872b4a42-d467-459f-bcbb-51564bfc9d66")) {
				//Helium, Do nothing.
			} else if (matchingSkull(skull, "55b456f4-2723-4b57-b31d-6b5dc947606e")) {
				//Lithium
				loc.getBlock().getRelative(BlockFace.DOWN).setType(Material.FIRE);
			} else if (matchingSkull(skull, "37835b88-4e7c-456b-8b8f-b4fbf80fce8e")) {
				//Beryllium, Do Nothing
			} else if (matchingSkull(skull, "37835b88-4e7c-456b-8b8f-b4fbf80fce8e")) {
				//Boron, Do nothing
			} else if (matchingSkull(skull, "462bafcf-023f-4bc6-8be9-0b924e8b11db")) {
				//Diamond, Drop a Diamond 
				ItemStack item = new ItemStack(Material.DIAMOND);
				loc.getWorld().dropItemNaturally(loc, item);
			} else if (matchingSkull(skull, "045f2cef-4771-493c-9e92-f71d540f6669")) {
				//Coal, Drop a Coal 
				ItemStack item = new ItemStack(Material.COAL);
				loc.getWorld().dropItemNaturally(loc, item);
			} else if (matchingSkull(skull, "30ca6346-23ae-464d-b8e3-176097f6c3ad")) {
				//Nitrogen, Do nothing.
			} else if (matchingSkull(skull, "1948139d-0f97-44a2-a675-e798af812ce5")) {
				//Oxygen, Do nothing.
			} else if (matchingSkull(skull, "9edde086-69d3-4341-a725-4d10eed1eaf1")) {
				//Fluorine, Do nothing
			} else if (matchingSkull(skull, "4f1c308a-8b98-480d-8e40-a53acb16118c")) {
				//Neon, Do nothing
			} else if (matchingSkull(skull, "c87510d0-0a78-43a1-bab3-c3c479aacc97")) {
				//Sodium
				loc.getWorld().createExplosion(loc, 4F);
			} else if (matchingSkull(skull, "8c30208d-861a-4962-ba53-066bca3219b8")) {
				//Magnesium
				loc.getBlock().getRelative(BlockFace.DOWN).setType(Material.FIRE);
			} else if (matchingSkull(skull, "e191a8f3-5a26-4c3b-9265-33f53ee994e5")) {
				//Aluminum
			} else if (matchingSkull(skull, "7673abcb-6046-4729-92a6-1ce11df607ca")) {
				//Silicon
			} else if (matchingSkull(skull, "d7221687-b882-4003-aaa9-d5b8f97395b8")) {
				//Red Phosphorus
			} else if (matchingSkull(skull, "41bcc900-b359-405c-ac50-fdecc489673f")) {
				//White Phosphorus
				loc.getBlock().getRelative(BlockFace.DOWN).setType(Material.FIRE);
			} else if (matchingSkull(skull, "e6e33873-09e9-40fe-9aff-cfaca7f8fe03")) {
				//Black Phosphorus
			} else if (matchingSkull(skull, "f98f76cd-2f10-4881-9b7d-6a877f5843a4")) {
				//Violet Phosphorus
			} else if (matchingSkull(skull, "b45da052-c66f-4e98-b472-8a4994d5a945")) {
				//Sulfur
			} else if (matchingSkull(skull, "1e91d917-e774-4ba8-8495-f0f6d4925276")) {
				//Chlorine
			} else if (matchingSkull(skull, "713711b2-4fa9-4745-9418-ac250bbbcf40")) {
				//Argon
			} else if (matchingSkull(skull, "b088e244-cf5d-4de5-87a9-73f17d379f39")) {
				//Potassium
				loc.getWorld().createExplosion(loc, 5F);
			} else if (matchingSkull(skull, "081710cc-0883-4d36-a63c-c9f4f8869881")) {
				//Calcium
			} else if (matchingSkull(skull, "26b8c131-f488-48f1-86cb-a25592534528")) {
				//Scandium
			} else if (matchingSkull(skull, "033ce3ca-37a5-4067-9cc8-81926c23a789")) {
				//Titanium
			} else if (matchingSkull(skull, "59b9ba96-cc56-424f-9b5b-59aef4fd86c5")) {
				//Vanadium
			} else if (matchingSkull(skull, "8e0a90b8-6aba-4e81-8e17-5e6beacc703c")) {
				//Maganese
			} else if (matchingSkull(skull, "682f330e-9dbf-4510-bc1e-518e309413c0")) {
				//Iron
				ItemStack drops = new ItemStack(Material.IRON_INGOT);
				loc.getWorld().dropItemNaturally(loc, drops);
			} else if (matchingSkull(skull, "c90250a1-7bf7-44dd-8eae-d515947aec3f")) {
				//Cobalt
			} else if (matchingSkull(skull, "dedc5d1a-0ecc-4a2a-b580-8e020fdda1a9")) {
				//Nickel
			} else if (matchingSkull(skull, "c108f139-5a7a-4685-bf64-59e72bc89d19")) {
				//Copper
			} else if (matchingSkull(skull, "ea331551-27a1-4573-a881-114098e48f87")) {
				//Zinc
			} else if (matchingSkull(skull, "350a668c-869d-4633-a9a3-ab789e30e330")){
				//Gallium
			} else if (matchingSkull(skull, "350a668c-869d-4633-a9a3-ab789e30e330")) {
				//Gray Arsenic
			} else if (matchingSkull(skull, "07d96959-f564-40b4-8555-ddc9def9cd3c")) {
				//Black Arsenic
			} else if (matchingSkull(skull, "75ed9df9-9a81-49d6-8660-9b2621edae37")) {
				//Black Selenium
			} else if (matchingSkull(skull, "ad5bcded-7f16-4b71-be7a-0d536f8fa9c1")) {
				//Red Selenium
			} else if (matchingSkull(skull, "bf7c4680-5289-4953-a7c3-113d6f107823")) {
				//Bromine
			} else if (matchingSkull(skull, "9405125f-4433-4a8d-9933-b479b1a9917f")) {
				//Krypton
			} else if (matchingSkull(skull, "ef12dceb-cbeb-4eed-b4d1-21b8d71a32aa")) {
				//Rubidium
				loc.getBlock().getRelative(BlockFace.DOWN).setType(Material.FIRE);
			} else if (matchingSkull(skull, "a6d126ac-f3bd-496e-8583-b396662346e9")) {
				//Strontium
			} else if (matchingSkull(skull, "31470eaf-3a9c-4027-8975-0be67e756751")) {
				//Yttrium
			} else if (matchingSkull(skull, "03123afd-7c4c-4deb-b696-3fa5ab423850")) {
				//Zirconium
			} else if (matchingSkull(skull, "df6da10d-28ba-41e1-8c0a-e36587a2e06a")) {
				//Niobium
			} else if (matchingSkull(skull, "6f33ffbd-b5f1-4246-baef-59debe843629")) {
				//Molybdenum
			} else if (matchingSkull(skull, "7b901927-0510-41fa-be28-f9cbc417224f")) {
				//Technetium
			} else if (matchingSkull(skull, "ae24cf09-a760-47e6-8a30-493609ec974a")) {
				//Ruthenium
			} else if (matchingSkull(skull, "144c0f29-9bf7-47e9-9768-ad1a02dcc335")) {
				//Rhodium
			} else if (matchingSkull(skull, "14988e95-c118-4e97-b002-7dc87e366476")) {
				//Palladium
				loc.getBlock().getRelative(BlockFace.DOWN).setType(Material.FIRE);
			} else if (matchingSkull(skull, "0df967eb-90de-4c68-9773-7cdcc0818ec7")) {
				//Silver
			} else if (matchingSkull(skull, "5fd14dea-a82e-41c7-bfaa-95a923c8e4b9")) {
				loc.getBlock().getRelative(BlockFace.DOWN).setType(Material.FIRE);
			} else if (matchingSkull(skull, "c22d52c8-f8bb-427b-ad72-7c27f6f2bf1d")) {
				//Indium
			} else if (matchingSkull(skull, "9f0ac83d-47aa-4112-935d-3832ec8de884")) {
				//Tin
			} else if (matchingSkull(skull, "9f0ac83d-47aa-4112-935d-3832ec8de884")) {
				//Antimony
			} else if (matchingSkull(skull, "2fee09f9-d0e8-4324-a74e-0b240fac7860")) {
				//Tellurium
			} else if (matchingSkull(skull, "25d5f4f7-b6dd-4cca-9c59-19d79b9a984c")) {
				//Iodine
			} else if (matchingSkull(skull, "d2524eee-64c7-425c-b50d-5062379090e9")) {
				//Xenon
			} else if (matchingSkull(skull, "11baba35-d7bc-473d-8260-865f24004a32")) {
				//Cesium
				loc.getWorld().createExplosion(loc, 6F);
			} else if (matchingSkull(skull, "aa98d0be-dade-409f-91af-d4b42b6519d2")) {
				//Barium
			} else if (matchingSkull(skull, "fa281ffa-bef9-43d0-941f-c1496c9a03a3")) {
				//Lanthanum
			} else if (matchingSkull(skull, "ba7d4bad-6ac1-4fa2-951b-77a7226d5489")) {
				//Praseodynium
			} else if (matchingSkull(skull, "e0dedacb-3a86-4ef5-b36d-663179968ddb")) {
				//Neodynium
			} else if (matchingSkull(skull, "73d6c0b9-5b9a-4da6-88fa-f2f15871555d")) {
				//Promethium
			} else if (matchingSkull(skull, "92ea9475-ef82-44be-8338-7e3fcc968379")) {
				//Samarium
			} else if (matchingSkull(skull, "cb4606a5-56ef-4f24-8ece-2ac54fc81e69")) {
				//Europium
			} else if (matchingSkull(skull, "84c3ab19-3b81-4424-bc18-2ff2a2d03e8e")) {
				//Gadolinium
			} else if (matchingSkull(skull, "19f8caf4-06cc-4db1-836c-7df7467d45af")) {
				//Terbium
			} else if (matchingSkull(skull, "776c8a3f-9115-44a3-9de8-45e081491637")) {
				//Dysprosium
			} else if (matchingSkull(skull, "6aadbc7d-7493-4194-aea0-f8b774bd3c02")) {
				//Holmium
			} else if (matchingSkull(skull, "6aadbc7d-7493-4194-aea0-f8b774bd3c02")) {
				//Erbium
			} else if (matchingSkull(skull, "5416a8dd-e18d-4ee5-9a2c-ee4ca765c8f5")) {
				//Thulium
			} else if (matchingSkull(skull, "41a67856-4e22-4bf4-99c0-a774190cf2c9")) {
				//Ytterbium
			} else if (matchingSkull(skull, "8b5e0c6f-8d33-4813-8371-06330ab91c38")) {
				//Lutetium
			} else if (matchingSkull(skull, "ca93e099-bedd-4903-8d92-dc7d5cf85416")) {
				//Hafnium
			} else if (matchingSkull(skull, "39925028-8660-47d9-91a4-e662f3f9dbf9")) {
				//Tantalum
			} else if (matchingSkull(skull, "b62899d3-a0e5-46fd-a12f-f7bc7fc6547b")) {
				//Tungsten
			} else if (matchingSkull(skull, "80dbb5fc-bd98-4c27-81ff-70543799caf9")) {
				//Rhenium
			} else if (matchingSkull(skull, "846348b3-b1e1-4c05-8eb6-cbd0b4bd41ca")) {
				//Osmium
			} else if (matchingSkull(skull, "2b65f243-b56b-45be-ac49-cb1d55864d20")) {
				//Iridium
			} else if (matchingSkull(skull, "86d4ae4b-947f-48f0-bf0b-5d22151cb3d1")) {
				//Platinum
			} else if (matchingSkull(skull, "86d4ae4b-947f-48f0-bf0b-5d22151cb3d1")) {
				//Gold
				ItemStack gold = new ItemStack(Material.GOLD_INGOT);
				loc.getWorld().dropItemNaturally(loc, gold);
			} else if (matchingSkull(skull, "90fa6bf9-6576-41ce-a61c-900af847438e")) {
				//Mercury
			} else if (matchingSkull(skull, "24af4110-fb4f-4f15-9956-82c9bad2a8b6")) {
				//Thallium
			} else if (matchingSkull(skull, "e0fafa83-a0fc-450d-a48f-d8e761dcf3b3")) {
				//Lead
			} else if (matchingSkull(skull, "28fd0ce3-f59c-4d03-bbb5-56f9d863fe71")) {
				//Bismuth
				loc.getBlock().getRelative(BlockFace.DOWN).setType(Material.FIRE);
			} else if (matchingSkull(skull, "9b8085e0-c53d-49e5-ab2f-f4de5eca1029")) {
				//Polonium
				loc.getBlock().getRelative(BlockFace.DOWN).setType(Material.FIRE);
			} else if (matchingSkull(skull, "f03f0f3d-1340-4e24-853e-6fec4d50cf37")) {
				//Astatine
			} else if (matchingSkull(skull, "00ebbe6e-7656-40f9-97f1-c9b2e36d4e46")) {
				//Radium
				loc.getBlock().getRelative(BlockFace.DOWN).setType(Material.FIRE);
			} else if (matchingSkull(skull, "7517c8d0-65d7-47ac-ad64-a5632bfcde55")) {
				//Radium
			} else if (matchingSkull(skull, "d1734d1c-b6cf-4a45-b666-b575f5b82aac")) {
				//Actinium
			} else if (matchingSkull(skull, "52d46db8-6c71-480c-9ac0-df43ae5def91")) {
				//Thorium
				loc.getBlock().getRelative(BlockFace.DOWN).setType(Material.FIRE);
			} else if (matchingSkull(skull, "f84c98e6-6346-46db-9f49-ac97e43e4d59")) {
				//Protactinium
			} else if (matchingSkull(skull, "3bf8b261-1d16-4481-ad92-82cdcf3c6ba2")) {
				//Uranium
			}
		}
	}
}
