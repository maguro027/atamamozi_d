package waterpunch.atamamozi_d.plugin.menus;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import waterpunch.atamamozi_d.plugin.race.Race;
import waterpunch.atamamozi_d.plugin.tool.Race_Type;

public class Menus {

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

     static ItemStack getRace_CheckPint(Race race, int i) {
          ItemStack CHECK = null;
          ItemMeta CHECK_Meta = null;

          if (i == 0) {
               CHECK = new ItemStack(Material.EMERALD_BLOCK);
               CHECK_Meta = CHECK.getItemMeta();
               CHECK_Meta.setDisplayName(ChatColor.GOLD + "Start : " + ChatColor.RED + i);
          } else {
               CHECK = new ItemStack(Material.MAP, i);
               CHECK_Meta = CHECK.getItemMeta();
               CHECK_Meta.setDisplayName(ChatColor.GOLD + "Check Point : " + ChatColor.RED + i);
          }

          List<String> lores = new ArrayList<String>();
          lores.add(ChatColor.GOLD + "X        : " + ChatColor.RED + race.getCheckPoint().get(i).getX());
          lores.add(ChatColor.GOLD + "Y        : " + ChatColor.RED + race.getCheckPoint().get(i).getY());
          lores.add(ChatColor.GOLD + "Z        : " + ChatColor.RED + race.getCheckPoint().get(i).getZ());
          lores.add(ChatColor.GOLD + "YAW     : " + ChatColor.RED + race.getCheckPoint().get(i).getYaw());
          lores.add(ChatColor.GOLD + "PITCH  : " + ChatColor.RED + race.getCheckPoint().get(i).getPitch());
          lores.add(ChatColor.GOLD + "r       : " + ChatColor.RED + race.getCheckPoint_R().get(i));
          CHECK_Meta.setLore(lores);
          CHECK.setItemMeta(CHECK_Meta);

          return CHECK;
     }

     public static Inventory getTop(Player player) {
          Inventory RACE_TOP_MENU = Bukkit.createInventory(player, 9, "RACE_TOP_MENU");
          RACE_TOP_MENU.setItem(1, new ItemStack(getRace_LIST()));
          RACE_TOP_MENU.setItem(4, new ItemStack(getRace_EDIT()));
          RACE_TOP_MENU.setItem(7, new ItemStack(getRace_CREATE()));
          return RACE_TOP_MENU;
     }

     public static Inventory getRaceList(Player player) {
          Inventory RACE_LIST = Bukkit.createInventory(player, 9 * 6, "RACE_LIST");
          setBorder(RACE_LIST);
          return RACE_LIST;
     }

     public static Inventory getRaceEdit(Player player) {
          Inventory RACE_EDIT = Bukkit.createInventory(player, 9 * 6, "RACE_EDIT");
          setBorder(RACE_EDIT);
          return RACE_EDIT;
     }

     public static Inventory getRaceCreate(Player player) {
          waterpunch.atamamozi_d.plugin.race.Editer.StartCreate(player);
          Inventory RACE_CREATE = Bukkit.createInventory(player, 9 * 6, "RACE_CREATE");
          setBorder(RACE_CREATE);

          ItemStack SET_NAME = new ItemStack(Material.NAME_TAG);
          ItemMeta SET_NAME_Meta = SET_NAME.getItemMeta();
          if (waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).getRace_name().equals("DEFAULT")) {
               SET_NAME_Meta.setDisplayName(ChatColor.GOLD + "EDIT NAME");
          } else {
               SET_NAME_Meta.setDisplayName(ChatColor.GOLD + "RACE NAME : " + ChatColor.RED + waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).getRace_name());
          }

          ItemStack RACE_TYPE = new ItemStack(Material.DIAMOND_AXE);
          ItemMeta RACE_TYPE_Meta = RACE_TYPE.getItemMeta();
          RACE_TYPE_Meta.setDisplayName(ChatColor.GOLD + "RACE TYPE : " + ChatColor.RED + "Loading...");
          switch (waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).getRace_Type()) {
               case RUN:
                    RACE_TYPE = new ItemStack(Material.TOTEM_OF_UNDYING);
                    RACE_TYPE_Meta = RACE_TYPE.getItemMeta();
                    RACE_TYPE_Meta.setDisplayName(ChatColor.GOLD + "RACE TYPE : " + ChatColor.RED + "RUN");
                    break;
               case BOAT:
                    RACE_TYPE = new ItemStack(Material.OAK_BOAT);
                    RACE_TYPE_Meta = RACE_TYPE.getItemMeta();
                    RACE_TYPE_Meta.setDisplayName(ChatColor.GOLD + "RACE TYPE : " + ChatColor.RED + "BOAT");
                    break;
               default:
                    player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setWarning() + "[" + waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).getRace_Type() + "] is ERR");
                    break;
          }

          ItemStack AMOUNT = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
          ItemMeta AMOUNT_Meta = AMOUNT.getItemMeta();
          if (waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).getJoin_Amount() == 0) {
               AMOUNT_Meta.setDisplayName(ChatColor.GOLD + "Max Member : " + ChatColor.RED + "No Limit");
          } else {
               AMOUNT_Meta.setDisplayName(ChatColor.GOLD + "Max Member : " + ChatColor.RED + String.valueOf(waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).getJoin_Amount()));
          }

          ItemStack ICON = new ItemStack(waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).getIcon());
          ItemMeta ICON_Meta = ICON.getItemMeta();
          ICON_Meta.setDisplayName(ChatColor.GOLD + "This item is Icon");

          ItemStack CHECKPOINT = new ItemStack(Material.COMPASS);
          ItemMeta CHECKPOINT_Meta = CHECKPOINT.getItemMeta();
          CHECKPOINT_Meta.setDisplayName(ChatColor.GOLD + "Edit Check Point");

          ItemStack CREATE = new ItemStack(Material.WRITABLE_BOOK);
          ItemMeta CREATE_Meta = CREATE.getItemMeta();
          CREATE_Meta.setDisplayName(ChatColor.GOLD + "EDIT COMPLETE");
          List<String> lores = new ArrayList<String>();

          waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).setErrorCount(0);

          if (waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).getRace_name().equals("DEFAULT")) {
               lores.add(ChatColor.RED + "[ER]" + ChatColor.GOLD + "Race Name : " + ChatColor.RED + "<DEFAULT>");
               waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).addErrorCount();
          } else {
               lores.add(ChatColor.GREEN + "[OK]" + ChatColor.GOLD + "Race Name : " + waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).getRace_name());
          }

          switch (waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).getRace_Type()) {
               case RUN:
                    lores.add(ChatColor.GREEN + "[OK]" + ChatColor.GOLD + "Race Type : RUN");
                    break;
               case BOAT:
                    lores.add(ChatColor.GREEN + "[OK]" + ChatColor.GOLD + "Race Type : BOAT");
                    break;
               default:
                    lores.add(ChatColor.RED + "[ER]" + ChatColor.GOLD + "Race Type : " + ChatColor.RED + "NULL");
                    waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).addErrorCount();
                    break;
          }

          if (waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).getJoin_Amount() == 0) {
               lores.add(ChatColor.GREEN + "[OK]" + ChatColor.GOLD + "Race Member : No Limit");
          } else {
               lores.add(ChatColor.GREEN + "[OK]" + ChatColor.GOLD + "Race Member : " + ChatColor.RED + String.valueOf(waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).getJoin_Amount()));
          }

          lores.add(ChatColor.GREEN + "[OK]" + ChatColor.GOLD + "Race ICON  : " + waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).getIcon().toString());

          if (waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).getCheckPoint().size() >= 2) {
               lores.add(ChatColor.GREEN + "[OK]" + ChatColor.GOLD + "Race CheckPoint : " + waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).getCheckPoint().size());
          } else {
               lores.add(ChatColor.RED + "[ER]" + ChatColor.GOLD + "Race CheckPoint : " + ChatColor.RED + "Need over 2");
               waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).addErrorCount();
          }

          if (!(waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).getErrorCount() == 0)) {
               lores.add("");
               lores.add(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setWarning() + ChatColor.RED + waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).getErrorCount() + "Error");
          }

          CREATE_Meta.setLore(lores);
          SET_NAME.setItemMeta(SET_NAME_Meta);
          RACE_TYPE.setItemMeta(RACE_TYPE_Meta);
          AMOUNT.setItemMeta(AMOUNT_Meta);
          ICON.setItemMeta(ICON_Meta);
          CHECKPOINT.setItemMeta(CHECKPOINT_Meta);
          CREATE.setItemMeta(CREATE_Meta);

          RACE_CREATE.setItem(10, new ItemStack(SET_NAME));
          RACE_CREATE.setItem(12, new ItemStack(RACE_TYPE));
          RACE_CREATE.setItem(14, new ItemStack(AMOUNT));
          RACE_CREATE.setItem(16, new ItemStack(ICON));
          RACE_CREATE.setItem(28, new ItemStack(CHECKPOINT));
          RACE_CREATE.setItem(49, new ItemStack(CREATE));

          return RACE_CREATE;
     }

     public static Inventory getRaceType(Player player) {
          Inventory RACE_CREATE_TYPE = Bukkit.createInventory(player, 9 * 6, "RACE_CREATE_TYPE");
          setBorder(RACE_CREATE_TYPE);

          ItemStack RUN = new ItemStack(Material.TOTEM_OF_UNDYING);
          ItemMeta RUN_Meta = RUN.getItemMeta();
          RUN_Meta.setDisplayName(ChatColor.GOLD + "RUN");
          RUN.setItemMeta(RUN_Meta);

          ItemStack BOAT = new ItemStack(Material.OAK_BOAT);
          ItemMeta BOAT_Meta = BOAT.getItemMeta();
          BOAT_Meta.setDisplayName(ChatColor.GOLD + "BOAT");
          BOAT.setItemMeta(BOAT_Meta);

          ItemStack I = new ItemStack(Material.LIME_DYE);
          ItemMeta I_Meta = I.getItemMeta();
          I_Meta.setDisplayName(ChatColor.GREEN + "This Active");
          I.setItemMeta(I_Meta);

          ItemStack O = new ItemStack(Material.GRAY_DYE);
          ItemMeta O_Meta = O.getItemMeta();
          O_Meta.setDisplayName(" ");
          O.setItemMeta(O_Meta);

          RACE_CREATE_TYPE.setItem(20, new ItemStack(RUN));
          RACE_CREATE_TYPE.setItem(24, new ItemStack(BOAT));
          RACE_CREATE_TYPE.setItem(29, new ItemStack(O));
          RACE_CREATE_TYPE.setItem(33, new ItemStack(O));
          if (waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).getRace_Type() == Race_Type.RUN) RACE_CREATE_TYPE.setItem(29, new ItemStack(I));
          if (waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).getRace_Type() == Race_Type.BOAT) RACE_CREATE_TYPE.setItem(33, new ItemStack(I));

          return RACE_CREATE_TYPE;
     }

     public static Inventory getRaceAmount(Player player) {
          Inventory RACE_CREATE_AMOUNT = Bukkit.createInventory(player, 9 * 6, "RACE_CREATE_AMOUNT");
          setBorder(RACE_CREATE_AMOUNT);

          ItemStack AMOUNT = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
          ItemMeta AMOUNT_Meta = AMOUNT.getItemMeta();
          if (waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).getJoin_Amount() == 0) {
               AMOUNT_Meta.setDisplayName(ChatColor.GOLD + "Max Member : " + ChatColor.RED + "No Limit");
          } else {
               AMOUNT_Meta.setDisplayName(ChatColor.GOLD + "Max Member : " + ChatColor.RED + String.valueOf(waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).getJoin_Amount()));
          }
          AMOUNT.setItemMeta(AMOUNT_Meta);

          ItemStack UP = new ItemStack(Material.JACK_O_LANTERN);
          ItemMeta UP_Meta = UP.getItemMeta();
          UP_Meta.setDisplayName(ChatColor.GREEN + "UP");
          UP.setItemMeta(UP_Meta);

          ItemStack DOWN = new ItemStack(Material.CARVED_PUMPKIN);
          ItemMeta DOWN_Meta = DOWN.getItemMeta();
          DOWN_Meta.setDisplayName(ChatColor.GREEN + "DOWN");
          DOWN.setItemMeta(DOWN_Meta);

          ItemStack I = new ItemStack(Material.LIME_DYE);
          ItemMeta I_Meta = I.getItemMeta();
          I_Meta.setDisplayName(ChatColor.GREEN + "This Active");
          I.setItemMeta(I_Meta);

          RACE_CREATE_AMOUNT.setItem(22, new ItemStack(AMOUNT));
          RACE_CREATE_AMOUNT.setItem(20, new ItemStack(UP));
          RACE_CREATE_AMOUNT.setItem(24, new ItemStack(DOWN));

          return RACE_CREATE_AMOUNT;
     }

     public static Inventory getRaceIcon(Player player) {
          Inventory RACE_CREATE_ICON = Bukkit.createInventory(player, 9 * 6, "RACE_CREATE_ICON");
          setBorder(RACE_CREATE_ICON);

          ItemStack ICOM = new ItemStack(waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).getIcon());
          ItemMeta ICON_Meta = ICOM.getItemMeta();
          ICON_Meta.setDisplayName(ChatColor.GREEN + "Click to New Icon item on you Inventory");
          ICOM.setItemMeta(ICON_Meta);

          RACE_CREATE_ICON.setItem(22, new ItemStack(ICOM));
          return RACE_CREATE_ICON;
     }

     public static Inventory getRaceCheckPoint(Player player) {
          Inventory RACE_CREATE_CHECKPOINT = Bukkit.createInventory(player, 9 * 6, "RACE_CREATE_POINT");
          setBorder(RACE_CREATE_CHECKPOINT);

          ItemStack CHECKPOINT = new ItemStack(Material.COMPASS);
          ItemMeta CHECKPOINT_Meta = CHECKPOINT.getItemMeta();
          CHECKPOINT_Meta.setDisplayName(ChatColor.GOLD + "Check Point Editor");
          List<String> lores = new ArrayList<String>();
          lores.add(ChatColor.GOLD + "RightClick  : " + ChatColor.RED + "Updeta CheckPoint");
          lores.add(ChatColor.GOLD + "LeftClick   : " + ChatColor.RED + "View CheckPoint");
          lores.add(ChatColor.GOLD + "WheelClick  : " + ChatColor.RED + "Delete CheckPoint");

          CHECKPOINT_Meta.setLore(lores);
          CHECKPOINT.setItemMeta(CHECKPOINT_Meta);

          RACE_CREATE_CHECKPOINT.setItem(4, new ItemStack(CHECKPOINT));

          //		for (int i = 0; i < waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).getCheckPoint().size(); i++) RACE_CREATE_CHECKPOINT.setItem(i + 9, new ItemStack(getRace_CheckPint(player, i, waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).getCheckPoint().get(i))));
          for (int i = 0; i < waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).getCheckPoint().size(); i++) RACE_CREATE_CHECKPOINT.setItem(i + 9, new ItemStack(getRace_CheckPint(waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player), i)));

          return RACE_CREATE_CHECKPOINT;
     }
}
