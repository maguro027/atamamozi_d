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
import waterpunch.atamamozi_d.plugin.race.Race_Runner;
import waterpunch.atamamozi_d.plugin.tool.Race_Mode;
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

     static ItemStack getRace(Race race) {
          ItemStack race_item = new ItemStack(race.getIcon());
          ItemMeta race_item_Meta = race_item.getItemMeta();
          race_item_Meta.setDisplayName(race.getRace_name());
          List<String> lores = new ArrayList<String>();

          lores.add(ChatColor.GOLD + "Creator : " + ChatColor.RED + race.getCreator());
          lores.add(ChatColor.GOLD + "Race Type : " + ChatColor.RED + race.getRace_Type());
          if (race.getJoin_Amount() == 0) {
               lores.add(ChatColor.GOLD + "Max Join : " + ChatColor.RED + "No Limit");
          } else {
               lores.add(ChatColor.GOLD + "Max Join : " + ChatColor.RED + race.getJoin_Amount());
          }
          lores.add(ChatColor.GOLD + "CheckPoint : " + ChatColor.RED + race.getCheckPointLoc().size());

          race_item_Meta.setLore(lores);
          race_item.setItemMeta(race_item_Meta);
          return race_item;
     }

     static ItemStack getRace_StartPint_Item(Race race, int i) {
          ItemStack START = new ItemStack(Material.EMERALD_BLOCK);
          ItemMeta START_Meta = START.getItemMeta();
          START_Meta.setDisplayName(ChatColor.GOLD + "Start Point : " + ChatColor.RED + i);

          List<String> lores = new ArrayList<String>();
          lores.add(ChatColor.GOLD + "X : " + ChatColor.RED + race.getStartPointLoc().get(i).getLocation().getX());
          lores.add(ChatColor.GOLD + "Y : " + ChatColor.RED + race.getStartPointLoc().get(i).getLocation().getY());
          lores.add(ChatColor.GOLD + "Z : " + ChatColor.RED + race.getStartPointLoc().get(i).getLocation().getZ());
          lores.add(ChatColor.GOLD + "YAW : " + ChatColor.RED + race.getStartPointLoc().get(i).getLocation().getYaw());
          lores.add(ChatColor.GOLD + "PITCH : " + ChatColor.RED + race.getStartPointLoc().get(i).getLocation().getPitch());
          START_Meta.setLore(lores);
          START.setItemMeta(START_Meta);

          return START;
     }

     static ItemStack getRace_CheckPint_Item(Race race, int i) {
          ItemStack CHECK = new ItemStack(Material.MAP, i);
          ItemMeta CHECK_Meta = CHECK.getItemMeta();
          CHECK_Meta.setDisplayName(ChatColor.GOLD + "Check Point : " + ChatColor.RED + i);

          List<String> lores = new ArrayList<String>();
          lores.add(ChatColor.GOLD + "X : " + ChatColor.RED + race.getCheckPointLoc().get(i).getLocation().getX());
          lores.add(ChatColor.GOLD + "Y : " + ChatColor.RED + race.getCheckPointLoc().get(i).getLocation().getY());
          lores.add(ChatColor.GOLD + "Z : " + ChatColor.RED + race.getCheckPointLoc().get(i).getLocation().getZ());
          lores.add(ChatColor.GOLD + "YAW : " + ChatColor.RED + race.getCheckPointLoc().get(i).getLocation().getYaw());
          lores.add(ChatColor.GOLD + "PITCH : " + ChatColor.RED + race.getCheckPointLoc().get(i).getLocation().getPitch());
          lores.add(ChatColor.GOLD + "r  : " + ChatColor.RED + race.getCheckPointLoc().get(i).getr());
          CHECK_Meta.setLore(lores);
          CHECK.setItemMeta(CHECK_Meta);

          return CHECK;
     }

     public static Inventory getTop(Player player) {
          Inventory RACE_TOP_MENU = Bukkit.createInventory(player, 9, "RACE_TOP_MENU");
          RACE_TOP_MENU.setItem(1, new ItemStack(getRace_LIST()));
          // RACE_TOP_MENU.setItem(4, new ItemStack(getRace_EDIT()));
          RACE_TOP_MENU.setItem(7, new ItemStack(getRace_CREATE()));
          return RACE_TOP_MENU;
     }

     public static Inventory getRaceList(Player player) {
          Inventory RACE_LIST = Bukkit.createInventory(player, 9 * 6, "RACE_LIST");
          setBorder(RACE_LIST);
          if (waterpunch.atamamozi_d.plugin.race.Race_Core.Race_list.size() == 0) return RACE_LIST;

          for (int i = 0; i < waterpunch.atamamozi_d.plugin.race.Race_Core.Race_list.size(); i++) RACE_LIST.setItem(i + 9, new ItemStack(getRace(waterpunch.atamamozi_d.plugin.race.Race_Core.Race_list.get(i))));
          return RACE_LIST;
     }

     public static Inventory getRaceEdit(Player player) {
          Inventory RACE_EDIT = Bukkit.createInventory(player, 9 * 6, "RACE_EDIT");
          setBorder(RACE_EDIT);
          return RACE_EDIT;
     }

     public static Inventory getRaceCreate(Player player) {
          Race_Runner run = waterpunch.atamamozi_d.plugin.race.Race_Core.getRuner(player);
          if (run == null || !(run.getMode() == Race_Mode.EDIT)) {
               player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Race Creating Start");
               run = null;
               if (waterpunch.atamamozi_d.plugin.race.Race_Core.isJoin(player)) waterpunch.atamamozi_d.plugin.race.Race_Core.removeRunner(player);
               run = new Race_Runner(player, new Race(player), 0);
               run.setMode(Race_Mode.EDIT);
               run.UpdateScoreboard();
          }
          Inventory RACE_CREATE = Bukkit.createInventory(player, 9 * 6, "RACE_CREATE");
          setBorder(RACE_CREATE);
          ItemStack SET_NAME = new ItemStack(Material.NAME_TAG);
          ItemMeta SET_NAME_Meta = SET_NAME.getItemMeta();
          if (run.getRace().getRace_name().equals("DEFAULT")) {
               SET_NAME_Meta.setDisplayName(ChatColor.GOLD + "EDIT NAME");
          } else {
               SET_NAME_Meta.setDisplayName(ChatColor.GOLD + "RACE NAME : " + ChatColor.RED + run.getRace().getRace_name());
          }
          List<String> SET_NAME_lores = new ArrayList<String>();
          SET_NAME_lores.add(ChatColor.GREEN + "/atamamozi_d setName");

          ItemStack RACE_TYPE = new ItemStack(Material.DIAMOND_AXE);
          ItemMeta RACE_TYPE_Meta = RACE_TYPE.getItemMeta();
          RACE_TYPE_Meta.setDisplayName(ChatColor.GOLD + "RACE TYPE : " + ChatColor.RED + "Loading...");
          switch (run.getRace().getRace_Type()) {
               case WALK:
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
                    player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setWarning() + "[" + run.getRace().getRace_Type() + "] is ERR");
                    break;
          }
          ItemStack RAP = new ItemStack(Material.COMPARATOR);
          ItemMeta RAP_Meta = RAP.getItemMeta();
          RAP_Meta.setDisplayName(ChatColor.GOLD + "RAP : " + ChatColor.RED + String.valueOf(run.getRace().getRap()));

          ItemStack AMOUNT = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
          ItemMeta AMOUNT_Meta = AMOUNT.getItemMeta();

          AMOUNT_Meta.setDisplayName(ChatColor.GOLD + "Max Member : " + ChatColor.RED + String.valueOf(run.getRace().getJoin_Amount()));

          ItemStack ICON = new ItemStack(run.getRace().getIcon());
          ItemMeta ICON_Meta = ICON.getItemMeta();
          ICON_Meta.setDisplayName(ChatColor.GOLD + "This item is Icon");

          ItemStack CHECKPOINT = new ItemStack(Material.COMPASS);
          ItemMeta CHECKPOINT_Meta = CHECKPOINT.getItemMeta();
          CHECKPOINT_Meta.setDisplayName(ChatColor.GOLD + "Edit Check Point");
          List<String> CHECKPOINT_lores = new ArrayList<String>();
          CHECKPOINT_lores.add(ChatColor.GREEN + "/atamamozi_d addCheckPoint");

          ItemStack STARTPOINT = new ItemStack(Material.TARGET);
          ItemMeta STARTPOINT_Meta = STARTPOINT.getItemMeta();
          STARTPOINT_Meta.setDisplayName(ChatColor.GOLD + "Edit Start Point");
          List<String> STARTPOINT_lores = new ArrayList<String>();
          STARTPOINT_lores.add(ChatColor.GREEN + "/atamamozi_d addStartPoint");

          ItemStack CREATE = new ItemStack(Material.WRITABLE_BOOK);
          ItemMeta CREATE_Meta = CREATE.getItemMeta();
          CREATE_Meta.setDisplayName(ChatColor.GOLD + "EDIT COMPLETE");
          List<String> lores = new ArrayList<String>();

          run.getRace().setErrorCount(0);

          if (run.getRace().getRace_name().equals("DEFAULT")) {
               lores.add(ChatColor.RED + "[ER]" + ChatColor.GOLD + "Race Name : " + ChatColor.RED + "<DEFAULT>");
               run.getRace().addErrorCount();
          } else {
               lores.add(ChatColor.GREEN + "[OK]" + ChatColor.GOLD + "Race Name : " + ChatColor.GREEN + run.getRace().getRace_name());
          }

          switch (run.getRace().getRace_Type()) {
               case WALK:
                    lores.add(ChatColor.GREEN + "[OK]" + ChatColor.GOLD + "Race Type : " + ChatColor.GREEN + "RUN");
                    break;
               case BOAT:
                    lores.add(ChatColor.GREEN + "[OK]" + ChatColor.GOLD + "Race Type : " + ChatColor.GREEN + "BOAT");
                    break;
               default:
                    lores.add(ChatColor.RED + "[ER]" + ChatColor.GOLD + "Race Type : " + ChatColor.RED + "NULL");
                    run.getRace().addErrorCount();
                    break;
          }

          if (run.getRace().getJoin_Amount() == 0) {
               lores.add(ChatColor.GREEN + "[OK]" + ChatColor.GOLD + "Race Member : No Limit");
          } else {
               lores.add(ChatColor.GREEN + "[OK]" + ChatColor.GOLD + "Race Member : " + ChatColor.GREEN + String.valueOf(run.getRace().getJoin_Amount()));
          }
          lores.add(ChatColor.GREEN + "[OK]" + ChatColor.GOLD + "Race Rap  : " + ChatColor.GREEN + run.getRace().getRap());
          lores.add(ChatColor.GREEN + "[OK]" + ChatColor.GOLD + "Race ICON  : " + ChatColor.GREEN + run.getRace().getIcon().toString());

          if (run.getRace().getStartPointLoc().size() == run.getRace().getJoin_Amount()) {
               if (run.getRace().getJoin_Amount() == 0) {
                    lores.add(ChatColor.RED + "[ER]" + ChatColor.GOLD + "Race StartPoint : " + ChatColor.RED + "Need  over 1");
                    run.getRace().addErrorCount();
               } else {
                    lores.add(ChatColor.GREEN + "[OK]" + ChatColor.GOLD + "Race StartPoint :" + ChatColor.GREEN + run.getRace().getStartPointLoc().size());
               }
          } else {
               lores.add(ChatColor.RED + "[ER]" + ChatColor.GOLD + "Race StartPoint : " + ChatColor.RED + "Need " + run.getRace().getJoin_Amount());
               run.getRace().addErrorCount();
          }

          if (run.getRace().getCheckPointLoc().size() >= 2) {
               lores.add(ChatColor.GREEN + "[OK]" + ChatColor.GOLD + "Race CheckPoint : " + ChatColor.GREEN + run.getRace().getCheckPointLoc().size());
          } else {
               lores.add(ChatColor.RED + "[ER]" + ChatColor.GOLD + "Race CheckPoint : " + ChatColor.RED + "Need over 2");
               run.getRace().addErrorCount();
          }

          if (!(run.getRace().getErrorCount() == 0)) {
               lores.add("");
               lores.add(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setWarning() + ChatColor.RED + run.getRace().getErrorCount() + "Error");
          }

          STARTPOINT_Meta.setLore(STARTPOINT_lores);
          CHECKPOINT_Meta.setLore(CHECKPOINT_lores);
          SET_NAME_Meta.setLore(SET_NAME_lores);
          CREATE_Meta.setLore(lores);
          SET_NAME.setItemMeta(SET_NAME_Meta);
          RACE_TYPE.setItemMeta(RACE_TYPE_Meta);
          AMOUNT.setItemMeta(AMOUNT_Meta);
          RAP.setItemMeta(RAP_Meta);
          ICON.setItemMeta(ICON_Meta);
          STARTPOINT.setItemMeta(STARTPOINT_Meta);
          CHECKPOINT.setItemMeta(CHECKPOINT_Meta);
          CREATE.setItemMeta(CREATE_Meta);

          RACE_CREATE.setItem(10, new ItemStack(SET_NAME));
          RACE_CREATE.setItem(12, new ItemStack(RACE_TYPE));
          RACE_CREATE.setItem(14, new ItemStack(AMOUNT));
          RACE_CREATE.setItem(16, new ItemStack(RAP));
          RACE_CREATE.setItem(28, new ItemStack(ICON));
          RACE_CREATE.setItem(30, new ItemStack(STARTPOINT));
          RACE_CREATE.setItem(32, new ItemStack(CHECKPOINT));
          RACE_CREATE.setItem(49, new ItemStack(CREATE));

          return RACE_CREATE;
     }

     public static Inventory getRaceType(Player player) {
          Inventory RACE_CREATE_TYPE = Bukkit.createInventory(player, 9 * 6, "RACE_CREATE_TYPE");
          setBorder(RACE_CREATE_TYPE);

          ItemStack RUN = new ItemStack(Material.TOTEM_OF_UNDYING);
          ItemMeta RUN_Meta = RUN.getItemMeta();
          RUN_Meta.setDisplayName(ChatColor.GOLD + "WALK");
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
          Race_Runner run = waterpunch.atamamozi_d.plugin.race.Race_Core.getRuner(player);
          if (run.getRace().getRace_Type() == Race_Type.WALK) RACE_CREATE_TYPE.setItem(29, new ItemStack(I));
          if (run.getRace().getRace_Type() == Race_Type.BOAT) RACE_CREATE_TYPE.setItem(33, new ItemStack(I));

          return RACE_CREATE_TYPE;
     }

     public static Inventory getRaceRap(Player player) {
          Race_Runner run = waterpunch.atamamozi_d.plugin.race.Race_Core.getRuner(player);
          Inventory RACE_CREATE_RAP = Bukkit.createInventory(player, 9 * 6, "RACE_CREATE_RAP");
          setBorder(RACE_CREATE_RAP);

          ItemStack RAP = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
          ItemMeta RAP_Meta = RAP.getItemMeta();

          RAP_Meta.setDisplayName(ChatColor.GOLD + "Rap : " + ChatColor.RED + String.valueOf(run.getRace().getRap()));
          RAP.setItemMeta(RAP_Meta);

          RACE_CREATE_RAP.setItem(22, new ItemStack(RAP));
          RACE_CREATE_RAP.setItem(20, new ItemStack(getUP()));
          RACE_CREATE_RAP.setItem(24, new ItemStack(getDown()));

          return RACE_CREATE_RAP;
     }

     public static Inventory getRaceAmount(Player player) {
          Race_Runner run = waterpunch.atamamozi_d.plugin.race.Race_Core.getRuner(player);
          Inventory RACE_CREATE_AMOUNT = Bukkit.createInventory(player, 9 * 6, "RACE_CREATE_AMOUNT");
          setBorder(RACE_CREATE_AMOUNT);

          ItemStack AMOUNT = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
          ItemMeta AMOUNT_Meta = AMOUNT.getItemMeta();
          if (run.getRace().getJoin_Amount() == 0) {
               AMOUNT_Meta.setDisplayName(ChatColor.GOLD + "Max Member : " + ChatColor.RED + "No Limit");
          } else {
               AMOUNT_Meta.setDisplayName(ChatColor.GOLD + "Max Member : " + ChatColor.RED + String.valueOf(run.getRace().getJoin_Amount()));
          }
          AMOUNT.setItemMeta(AMOUNT_Meta);

          RACE_CREATE_AMOUNT.setItem(22, new ItemStack(AMOUNT));
          RACE_CREATE_AMOUNT.setItem(20, new ItemStack(getUP()));
          RACE_CREATE_AMOUNT.setItem(24, new ItemStack(getDown()));

          return RACE_CREATE_AMOUNT;
     }

     public static Inventory getRaceIcon(Player player) {
          Race_Runner run = waterpunch.atamamozi_d.plugin.race.Race_Core.getRuner(player);
          Inventory RACE_CREATE_ICON = Bukkit.createInventory(player, 9 * 6, "RACE_CREATE_ICON");
          setBorder(RACE_CREATE_ICON);

          ItemStack ICOM = new ItemStack(run.getRace().getIcon());
          ItemMeta ICON_Meta = ICOM.getItemMeta();
          ICON_Meta.setDisplayName(ChatColor.GREEN + "Click to New Icon item on you Inventory");
          ICOM.setItemMeta(ICON_Meta);

          RACE_CREATE_ICON.setItem(22, new ItemStack(ICOM));
          return RACE_CREATE_ICON;
     }

     public static Inventory getRaceStartPoint(Player player) {
          Race_Runner run = waterpunch.atamamozi_d.plugin.race.Race_Core.getRuner(player);
          Inventory RACE_CREATE_STARTPOINT = Bukkit.createInventory(player, 9 * 6, "RACE_CREATE_STARTPOINT");
          setBorder(RACE_CREATE_STARTPOINT);

          ItemStack STARTPOINT = new ItemStack(Material.TARGET);
          ItemMeta STARTPOINT_Meta = STARTPOINT.getItemMeta();
          STARTPOINT_Meta.setDisplayName(ChatColor.GOLD + "Start Point Editor");
          List<String> lores = new ArrayList<String>();
          lores.add(ChatColor.GOLD + "RightClick  : " + ChatColor.RED + "Updeta CheckPoint");
          lores.add(ChatColor.GOLD + "LeftClick   : " + ChatColor.RED + "View CheckPoint");
          lores.add(ChatColor.GOLD + "WheelClick  : " + ChatColor.RED + "Delete CheckPoint");

          STARTPOINT_Meta.setLore(lores);
          STARTPOINT.setItemMeta(STARTPOINT_Meta);
          RACE_CREATE_STARTPOINT.setItem(4, new ItemStack(STARTPOINT));

          for (int i = 0; i < run.getRace().getStartPointLoc().size(); i++) RACE_CREATE_STARTPOINT.setItem(i + 9, new ItemStack(getRace_StartPint_Item(run.getRace(), i)));

          return RACE_CREATE_STARTPOINT;
     }

     public static Inventory getRaceCheckPoint(Player player) {
          Race_Runner run = waterpunch.atamamozi_d.plugin.race.Race_Core.getRuner(player);
          Inventory RACE_CREATE_CHECKPOINT = Bukkit.createInventory(player, 9 * 6, "RACE_CREATE_CHECKPOINT");
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

          for (int i = 0; i < run.getRace().getCheckPointLoc().size(); i++) RACE_CREATE_CHECKPOINT.setItem(i + 9, new ItemStack(getRace_CheckPint_Item(run.getRace(), i)));

          return RACE_CREATE_CHECKPOINT;
     }
}
