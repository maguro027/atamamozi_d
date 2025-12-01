package waterpunch.atamamozi_d.plugin.menus;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import waterpunch.atamamozi_d.plugin.race.Race_Core;
import waterpunch.atamamozi_d.plugin.race.Race_Runner;

public class Menu_Item {

     static Inventory setBorder(Inventory inv) {
          ItemStack cash = null;
          for (int i = 0; i < 54; ++i) {
               if (i > 8 && i < 45) continue;
               cash = getBlack();
               if (i == 45) cash = getBack();
               inv.setItem(i, new ItemStack(cash));
          }
          return inv;
     }

     static ItemStack getBlack() {
          ItemStack BLACK = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
          ItemMeta BLACK_Meta = BLACK.getItemMeta();
          BLACK_Meta.setDisplayName(" ");
          BLACK.setItemMeta(BLACK_Meta);
          return BLACK;
     }

     static ItemStack getBack() {
          ItemStack BACK = new ItemStack(Material.ARROW);
          ItemMeta BACK_Meta = BACK.getItemMeta();
          BACK_Meta.setDisplayName("BACK");
          BACK.setItemMeta(BACK_Meta);
          return BACK;
     }

     static ItemStack getRace_LIST() {
          ItemStack race_LIST = new ItemStack(Material.BOOKSHELF);
          ItemMeta race_LIST_Meta = race_LIST.getItemMeta();
          race_LIST_Meta.setDisplayName(ChatColor.GOLD + "RACE_LIST");
          race_LIST.setItemMeta(race_LIST_Meta);
          return race_LIST;
     }

     static ItemStack getDebug() {
          ItemStack Debug = new ItemStack(Material.REDSTONE_TORCH);
          ItemMeta Debug_Meta = Debug.getItemMeta();
          Debug_Meta.setDisplayName(ChatColor.GOLD + "Debug");
          List<String> lores = new ArrayList<String>();
          lores.add(ChatColor.AQUA + "-Runner-");
          if (!Race_Core.getRunners().isEmpty()) {
               lores.add(ChatColor.AQUA + "- size :" + Race_Core.getRunners().size() + " -");
               for (Race_Runner val : Race_Core.getRunners()) lores.add(ChatColor.AQUA + "- " + val.getPlayer().getUniqueId());
          }

          Debug_Meta.setLore(lores);
          Debug.setItemMeta(Debug_Meta);
          return Debug;
     }

     static ItemStack getRace_EDIT() {
          ItemStack race_EDIT = new ItemStack(Material.COMPASS);
          ItemMeta race_EDIT_Meta = race_EDIT.getItemMeta();
          race_EDIT_Meta.setDisplayName(ChatColor.GOLD + "RACE_EDIT");
          race_EDIT.setItemMeta(race_EDIT_Meta);
          return race_EDIT;
     }

     static ItemStack getRace_CREATE() {
          ItemStack race_CREATE = new ItemStack(Material.ANVIL);
          ItemMeta race_CREATE_Meta = race_CREATE.getItemMeta();
          race_CREATE_Meta.setDisplayName(ChatColor.GOLD + "RACE_CREATE");
          race_CREATE.setItemMeta(race_CREATE_Meta);
          return race_CREATE;
     }

     static ItemStack getUP() {
          ItemStack UP = new ItemStack(Material.JACK_O_LANTERN);
          ItemMeta UP_Meta = UP.getItemMeta();
          UP_Meta.setDisplayName(ChatColor.GREEN + "UP");
          UP.setItemMeta(UP_Meta);
          return UP;
     }

     static ItemStack getDown() {
          ItemStack DOWN = new ItemStack(Material.CARVED_PUMPKIN);
          ItemMeta DOWN_Meta = DOWN.getItemMeta();
          DOWN_Meta.setDisplayName(ChatColor.GREEN + "DOWN");
          DOWN.setItemMeta(DOWN_Meta);
          return DOWN;
     }

     static ItemStack getRefresh() {
          ItemStack Refresh = new ItemStack(Material.EMERALD_BLOCK);
          ItemMeta Refresh_Meta = Refresh.getItemMeta();
          Refresh_Meta.setDisplayName(ChatColor.GREEN + "Refresh menu");
          Refresh.setItemMeta(Refresh_Meta);
          return Refresh;
     }
}
