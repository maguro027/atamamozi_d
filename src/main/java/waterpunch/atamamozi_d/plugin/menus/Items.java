package waterpunch.atamamozi_d.plugin.menus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import waterpunch.atamamozi_d.plugin.race.Race;
import waterpunch.atamamozi_d.plugin.race.Race_Core;
import waterpunch.atamamozi_d.plugin.race.Race_Runner;
import waterpunch.atamamozi_d.plugin.score.Ranking_parts;

public class Items {

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
          List<String> lores = new ArrayList<String>();
          lores.add(ChatColor.AQUA + "- Click to View-");
          race_LIST.setItemMeta(race_LIST_Meta);
          return race_LIST;
     }

     static ItemStack getRanking() {
          ItemStack Ranking = new ItemStack(Material.TOTEM_OF_UNDYING);
          ItemMeta Ranking_Meta = Ranking.getItemMeta();
          Ranking_Meta.setDisplayName(ChatColor.GOLD + "Ranking");
          List<String> lores = new ArrayList<String>();
          lores.add(ChatColor.AQUA + "- Click to View-");
          Ranking_Meta.setLore(lores);
          Ranking.setItemMeta(Ranking_Meta);
          return Ranking;
     }

     static ItemStack getCreater(String Name) {
          ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
          SkullMeta skull = (SkullMeta) item.getItemMeta();
          skull.setOwner(Name);
          skull.setDisplayName(ChatColor.GOLD + "Creater Search");
          List<String> lores = new ArrayList<String>();
          lores.add(ChatColor.AQUA + "- " + "Coming Soon" + " -");
          // lores.add(ChatColor.AQUA + "- " + Name + " -");
          skull.setLore(lores);
          item.setItemMeta(skull);
          return item;
     }

     static ItemStack getThisClick() {
          ItemStack ThisClick = new ItemStack(Material.DIAMOND);
          ItemMeta ThisClick_Meta = ThisClick.getItemMeta();
          ThisClick_Meta.setDisplayName(ChatColor.GOLD + " ");
          ThisClick.setItemMeta(ThisClick_Meta);
          return ThisClick;
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

     static ItemStack getRace(Race race, Player player) {
          ItemStack race_item = new ItemStack(race.getIcon());
          ItemMeta race_item_Meta = race_item.getItemMeta();
          race_item_Meta.setDisplayName(race.getRace_name());
          List<String> lores = new ArrayList<String>();
          switch (race.getMode()) {
               case EDIT:
                    lores.add(ChatColor.RED + "EDIT NOW ");
                    break;
               case RUN:
                    lores.add(ChatColor.RED + "Raceing NOW ");
                    break;
               default:
                    break;
          }
          lores.add(ChatColor.GOLD + "Creator : " + ChatColor.RED + race.getCreator());
          lores.add(ChatColor.GOLD + "Race Type : " + ChatColor.RED + race.getRace_Type());
          if (race.getJoin_Amount() == 0) {
               lores.add(ChatColor.GOLD + "Max Join : " + ChatColor.RED + "No Limit");
          } else {
               if ((Race_Core.Race_Run.get(race.getUUID()) == null)) {
                    lores.add(ChatColor.GOLD + "Join " + ChatColor.RED + ": 0 / " + race.getJoin_Amount());
               } else {
                    lores.add(ChatColor.GOLD + "Join : " + ChatColor.RED + Race_Core.Race_Run.get(race.getUUID()).size() + " / " + race.getJoin_Amount());
                    for (Race_Runner val : Race_Core.Race_Run.get(race.getUUID())) lores.add(ChatColor.AQUA + "- " + val.getPlayer().getName());
               }
          }
          lores.add(ChatColor.GOLD + "RAP : " + ChatColor.RED + race.getRap());
          lores.add(ChatColor.GOLD + "CheckPoint : " + ChatColor.RED + race.getCheckPointLoc().size());
          if (waterpunch.atamamozi_d.plugin.score.Player_Score_Core.getPlayer_TOP_Score(player, race.getUUID()) != null) {
               Calendar result = Calendar.getInstance();
               result.setTimeInMillis(waterpunch.atamamozi_d.plugin.score.Player_Score_Core.getPlayer_TOP_Score(player, race.getUUID()));
               SimpleDateFormat sdf = new SimpleDateFormat("mm:ss:SSS");
               lores.add(ChatColor.GOLD + "Score : " + ChatColor.RED + sdf.format(result.getTime()));
               int Rank = waterpunch.atamamozi_d.plugin.score.Player_Score_Core.getRank(race.getUUID(), player.getName());
               if (Rank != -1) lores.add(ChatColor.GOLD + "Ranking : " + ChatColor.AQUA + Rank + ChatColor.RED + " th");
          }

          race_item_Meta.setLore(lores);
          race_item.setItemMeta(race_item_Meta);
          return race_item;
     }

     static ItemStack getRaceRank(Race race, Player player) {
          ItemStack Rank = new ItemStack(race.getIcon());
          ItemMeta Rank_Meta = Rank.getItemMeta();
          Rank_Meta.setDisplayName(ChatColor.GREEN + race.getRace_name());
          List<String> lores = new ArrayList<String>();
          int i = 1;

          if (waterpunch.atamamozi_d.plugin.score.Player_Score_Core.getRanking(race.getUUID()) != null) {
               SimpleDateFormat sdf = new SimpleDateFormat("mm:ss:SSS");
               String space = "";
               for (Ranking_parts parts : waterpunch.atamamozi_d.plugin.score.Player_Score_Core.getRanking(race.getUUID())) {
                    if (10 > i) space = "  "; else space = "";

                    if (player.getName().equals(parts.getNAME())) {
                         lores.add(ChatColor.AQUA + space + i + ChatColor.GOLD + " th :  " + ChatColor.RED + sdf.format(parts.getTIME()) + ChatColor.GOLD + "　" + ChatColor.AQUA + parts.getNAME());
                    } else {
                         lores.add(ChatColor.AQUA + space + i + ChatColor.GOLD + " th :  " + ChatColor.RED + sdf.format(parts.getTIME()) + ChatColor.GOLD + "　" + ChatColor.GRAY + parts.getNAME());
                    }
                    if (i == waterpunch.atamamozi_d.plugin.main.Core.MENU_RANK_VIEW) break;
                    i++;
               }
               Rank_Meta.setLore(lores);
          }
          Rank.setItemMeta(Rank_Meta);
          return Rank;
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
          int itemamount = i + 1;
          ItemStack CHECK = new ItemStack(Material.MAP, itemamount);
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
}
