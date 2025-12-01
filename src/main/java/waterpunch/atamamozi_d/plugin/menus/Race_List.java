package waterpunch.atamamozi_d.plugin.menus;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import waterpunch.atamamozi_d.plugin.race.Race_Core;
import waterpunch.atamamozi_d.plugin.race.Race_Runner;

public class Race_List {

     static int OnePage = 36;

     public static Inventory getMenu(Player player) {
          Inventory RACE_LIST = Bukkit.createInventory(player, 9 * 6, "RACE_LIST");
          Menu_Item.setBorder(RACE_LIST);
          RACE_LIST.setItem(49, new ItemStack(Menu_Item.getRace_LIST()));
          RACE_LIST.setItem(48, new ItemStack(Menu_Item.getDebug()));
          if (Race_Core.getRaces().size() == 0) return RACE_LIST;
          ///////
          Race_Runner Run = Race_Core.getRunner(player);
          int cash = 1;
          if (Run != null) cash = Run.getPage();
          for (int i = 0; i < Race_Core.getRaces().size(); i++) {
               RACE_LIST.setItem(i + 9, new ItemStack(Menus.getRace(Race_Core.getRaces().get(i + ((cash - 1) * OnePage)))));
               if (i + 1 > Race_Core.getRaces().size()) return RACE_LIST;
          }
          return RACE_LIST;
     }

     public static void updateSort() {}

     public static enum Sort_Type {
          NAME,
          CREATE_Day,
          TIME,
          SERVER,
          RANKING,
     }
}
