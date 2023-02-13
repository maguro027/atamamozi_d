package waterpunch.atamamozi_d.plugin.race;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Server.Spigot;
import org.bukkit.entity.Player;

public class Editer {

     public static HashMap<Player, Race> Race_Editers = new HashMap<>();

     public static ArrayList<Player> CheckPoint_Editr = new ArrayList<>();
     public static ArrayList<Player> Name_Editr = new ArrayList<>();

     public static void StartCreate(Player player) {
          if (!Race_Editers.containsKey(player)) {
               Race_Editers.put(player, new Race(player));
               player.sendMessage(ChatColor.GOLD + "Race Create Start");
          }
     }

     public static HashMap<Player, Race> getRace_Editers() {
          return Race_Editers;
     }

     public static void DelCreate(Player player) {
          Race_Editers.remove(player);
     }

     public static void setName_editor(Player player) {
          player.sendMessage(ChatColor.GOLD + "Enter the name of the race in the chat");
          player.closeInventory();
          Name_Editr.add(player);
     }

     public static ArrayList<Player> getName_Edit() {
          return Name_Editr;
     }

     public static void remName_editor(Player player) {
          Name_Editr.remove(player);
     }

     public static void setCheckPoint_Editr(Player player) {
          if (!CheckPoint_Editr.contains(player)) {
               player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + ChatColor.WHITE + "Run " + "[" + ChatColor.RED + "/atamamozi_d addStartPoint" + ChatColor.WHITE + "]" + " at your destination");
               player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + ChatColor.WHITE + "Run " + "[" + ChatColor.RED + "/atamamozi_d addCheckPoint [r]" + ChatColor.WHITE + "]" + " at your destination");
               player.closeInventory();
               CheckPoint_Editr.add(player);
          }
     }

     public static ArrayList<Player> getCheckPoint_Editr() {
          return CheckPoint_Editr;
     }

     public static void viewCheckPoint(Player player) {
          if (!CheckPoint_Editr.contains(player)) return;
          for (int i = 0; i == Race_Editers.get(player).getCheckPointLoc().size(); i++) {
               Race_Editers.get(player).getCheckPointLoc().get(i);
          }
     }

     public static void remCheckPoint_Editr(Player player) {
          CheckPoint_Editr.remove(player);
     }
}
