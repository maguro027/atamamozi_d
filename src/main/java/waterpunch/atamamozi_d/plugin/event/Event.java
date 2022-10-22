package waterpunch.atamamozi_d.plugin.event;

import java.util.HashMap;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import waterpunch.atamamozi_d.plugin.tool.CountDownTimer;
import waterpunch.atamamozi_d.plugin.tool.LocationViewer;
import waterpunch.atamamozi_d.plugin.tool.Race_Type;

public class Event implements Listener {

     Plugin plugin_data = null;
     HashMap<Player, Location> player_loc = new HashMap<>();
     HashMap<LocationViewer, Integer> circle = new HashMap<>();

     public Event(Plugin plugin) {
          plugin.getServer().getPluginManager().registerEvents(this, plugin);
          plugin_data = plugin;
     }

     @EventHandler
     public void onInventoryClickEvent(InventoryClickEvent event) {
          if (event.getInventory().toString().matches(".*" + "Custom" + ".*") && event.getInventory().getType() == InventoryType.CHEST) {
               switch (((Player) event.getWhoClicked()).getOpenInventory().getTitle().toString()) {
                    case "RACE_TOP_MENU":
                         event.setCancelled(true);
                         if (event.getRawSlot() == 1) ((Player) event.getWhoClicked()).openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceList((Player) event.getWhoClicked()));
                         if (event.getRawSlot() == 4) ((Player) event.getWhoClicked()).openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceEdit((Player) event.getWhoClicked()));
                         if (event.getRawSlot() == 7) ((Player) event.getWhoClicked()).openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceCreate(((Player) event.getWhoClicked())));
                         break;
                    case "RACE_LIST":
                         event.setCancelled(true);
                         if (event.getRawSlot() == 45) ((Player) event.getWhoClicked()).openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getTop((Player) event.getWhoClicked()));
                         if (event.getCurrentItem() == null) return;
                         if (waterpunch.atamamozi_d.plugin.race.Race_Core.isJoin((Player) event.getWhoClicked())) return;

                         for (int i = 0; i < waterpunch.atamamozi_d.plugin.race.Race_Core.Race_list.size(); i++) {
                              if (waterpunch.atamamozi_d.plugin.race.Race_Core.Race_list.get(i).getRace_name().equals(event.getCurrentItem().getItemMeta().getDisplayName())) {
                                   waterpunch.atamamozi_d.plugin.race.Race_Core.joinRace(waterpunch.atamamozi_d.plugin.race.Race_Core.Race_list.get(i), (Player) event.getWhoClicked());
                                   ((Player) event.getWhoClicked()).sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Join Race : " + waterpunch.atamamozi_d.plugin.race.Race_Core.Race_list.get(i).getRace_name());
                              }
                         }
                         break;
                    case "RACE_EDIT":
                         event.setCancelled(true);
                         if (event.getRawSlot() == 45) ((Player) event.getWhoClicked()).openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getTop((Player) event.getWhoClicked()));
                         break;
                    case "RACE_CREATE":
                         event.setCancelled(true);
                         if (event.getRawSlot() == 45) ((Player) event.getWhoClicked()).openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getTop((Player) event.getWhoClicked()));

                         if (event.getRawSlot() == 10) waterpunch.atamamozi_d.plugin.race.Editer.setName_editor((Player) event.getWhoClicked());
                         if (event.getRawSlot() == 12) ((Player) event.getWhoClicked()).openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceType(((Player) event.getWhoClicked())));
                         if (event.getRawSlot() == 14) ((Player) event.getWhoClicked()).openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceAmount(((Player) event.getWhoClicked())));
                         if (event.getRawSlot() == 16) ((Player) event.getWhoClicked()).openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceRap(((Player) event.getWhoClicked())));
                         if (event.getRawSlot() == 28) ((Player) event.getWhoClicked()).openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceIcon(((Player) event.getWhoClicked())));
                         if (event.getRawSlot() == 30) if (waterpunch.atamamozi_d.plugin.race.Editer.getCheckPoint_Editr().contains((Player) event.getWhoClicked())) ((Player) event.getWhoClicked()).openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceCheckPoint(((Player) event.getWhoClicked()))); else waterpunch.atamamozi_d.plugin.race.Editer.setCheckPoint_Editr((Player) event.getWhoClicked());

                         if (event.getRawSlot() == 49) waterpunch.atamamozi_d.plugin.tool.CreateJson.saveRace((Player) event.getWhoClicked());
                         break;
                    case "RACE_CREATE_TYPE":
                         event.setCancelled(true);
                         if (event.getRawSlot() == 45) ((Player) event.getWhoClicked()).openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceCreate((Player) event.getWhoClicked()));
                         if (event.getRawSlot() == 20 || event.getRawSlot() == 29) {
                              waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(((Player) event.getWhoClicked())).setRace_Type(Race_Type.RUN);
                              ((Player) event.getWhoClicked()).openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceType(((Player) event.getWhoClicked())));
                         }
                         if (event.getRawSlot() == 24 || event.getRawSlot() == 33) {
                              waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(((Player) event.getWhoClicked())).setRace_Type(Race_Type.BOAT);
                              ((Player) event.getWhoClicked()).openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceType(((Player) event.getWhoClicked())));
                         }
                         break;
                    case "RACE_CREATE_RAP":
                         event.setCancelled(true);
                         if (event.getRawSlot() == 45) ((Player) event.getWhoClicked()).openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceCreate((Player) event.getWhoClicked()));
                         if (event.getRawSlot() == 20) {
                              waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(((Player) event.getWhoClicked())).setRap(waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(((Player) event.getWhoClicked())).getRap() + 1);
                              ((Player) event.getWhoClicked()).openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceRap(((Player) event.getWhoClicked())));
                         }
                         if (event.getRawSlot() == 24) {
                              if (waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(((Player) event.getWhoClicked())).getRap() == 1) return;
                              waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(((Player) event.getWhoClicked())).setRap(waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(((Player) event.getWhoClicked())).getRap() - 1);
                              ((Player) event.getWhoClicked()).openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceRap(((Player) event.getWhoClicked())));
                         }
                         break;
                    case "RACE_CREATE_AMOUNT":
                         event.setCancelled(true);
                         if (event.getRawSlot() == 45) ((Player) event.getWhoClicked()).openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceCreate((Player) event.getWhoClicked()));
                         if (event.getRawSlot() == 20) {
                              waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(((Player) event.getWhoClicked())).setJoin_Amount(waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(((Player) event.getWhoClicked())).getJoin_Amount() + 1);
                              ((Player) event.getWhoClicked()).openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceAmount(((Player) event.getWhoClicked())));
                         }
                         if (event.getRawSlot() == 24) {
                              if (waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(((Player) event.getWhoClicked())).getJoin_Amount() == 0) return;
                              waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(((Player) event.getWhoClicked())).setJoin_Amount(waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(((Player) event.getWhoClicked())).getJoin_Amount() - 1);
                              ((Player) event.getWhoClicked()).openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceAmount(((Player) event.getWhoClicked())));
                         }
                         break;
                    case "RACE_CREATE_ICON":
                         event.setCancelled(true);
                         if (event.getRawSlot() == 45) {
                              ((Player) event.getWhoClicked()).openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceCreate((Player) event.getWhoClicked()));
                         } else {
                              if (event.getCurrentItem() == null) return;
                              waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(((Player) event.getWhoClicked())).setIcon(event.getCurrentItem().getType());
                              ((Player) event.getWhoClicked()).openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceIcon(((Player) event.getWhoClicked())));
                         }
                         break;
                    case "RACE_CREATE_POINT":
                         if (event.getRawSlot() == 45) {
                              event.setCancelled(true);
                              ((Player) event.getWhoClicked()).openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceCreate((Player) event.getWhoClicked()));
                         } else {
                              if (event.getCurrentItem() == null) return;

                              ((Player) event.getWhoClicked()).sendMessage(event.getAction().toString());
                              if (event.getAction() == InventoryAction.CLONE_STACK) {
                                   //remove
                                   waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(((Player) event.getWhoClicked())).getCheckPointLoc().remove(event.getRawSlot() - 9);
                                   event.setCancelled(true);
                              }
                              if (event.getAction() == InventoryAction.PICKUP_HALF) {
                                   //Update
                                   waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(((Player) event.getWhoClicked())).getCheckPointLoc().set(event.getRawSlot() - 9, waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(((Player) event.getWhoClicked())).getCheckPointLoc().get(event.getRawSlot() - 9));
                                   event.setCancelled(true);
                              }
                              if (event.getAction() == InventoryAction.PICKUP_ALL) {
                                   LocationViewer locationViewer = new LocationViewer();
                                   locationViewer.setLoc(waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(((Player) event.getWhoClicked())).getCheckPointLoc().get(event.getRawSlot() - 9).getLocation());
                                   locationViewer.setr(waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(((Player) event.getWhoClicked())).getCheckPointLoc().get(event.getRawSlot() - 9).getr());
                                   locationViewer.DrawCircle();

                                   CountDownTimer time = new CountDownTimer(locationViewer, 5);
                                   time.start();

                                   ((Player) event.getWhoClicked()).openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceCheckPoint((Player) event.getWhoClicked()));
                                   event.setCancelled(true);
                              }
                              break;
                         }
               }
          }
     }

     @EventHandler
     public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
          if (waterpunch.atamamozi_d.plugin.race.Editer.getName_Edit().contains(event.getPlayer())) {
               if (event.getMessage().equals("[ER]")) {
                    event.getPlayer().sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setWarning() + "NG Word");
                    event.setCancelled(true);
                    return;
               }
               waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(event.getPlayer()).setRace_name(event.getMessage());
               waterpunch.atamamozi_d.plugin.race.Editer.getName_Edit().remove(event.getPlayer());
               Bukkit
                    .getScheduler()
                    .runTask(
                         plugin_data,
                         () -> {
                              event.getPlayer().openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceCreate(event.getPlayer()));
                         }
                    );
               event.setCancelled(true);
          }
     }

     @EventHandler
     public void SignChangeEvent(SignChangeEvent e) {
          if (e.getLine(0).equals("[race]")) e.setLine(0, "[Race]");
          if (!(e.getLine(0).equals("[Race]"))) return;
          String cash = e.getLine(1);
          e.setLine(1, "Loaging...");
          for (int i = 0; i < waterpunch.atamamozi_d.plugin.race.Race_Core.Race_list.size(); i++) if (waterpunch.atamamozi_d.plugin.race.Race_Core.Race_list.get(i).getRace_name().equals(cash)) {
               e.setLine(1, waterpunch.atamamozi_d.plugin.race.Race_Core.Race_list.get(i).getRace_name());
               if (StringUtils.isNumeric(e.getLine(2))) e.setLine(2, e.getLine(2) + " : Rap"); else e.setLine(2, waterpunch.atamamozi_d.plugin.race.Race_Core.Race_list.get(i).getRap() + " : Rap");
               e.setLine(3, waterpunch.atamamozi_d.plugin.race.Race_Core.Race_list.get(i).getCreator());
          }
          if (!(e.getLine(1).equals(cash))) {
               e.setLine(1, ChatColor.RED + "Error");
               return;
          }
     }

     @EventHandler(ignoreCancelled = true)
     public void onEnSignClick(PlayerInteractEvent e) {
          if (e.getPlayer().isSneaking() || !(e.getClickedBlock().getState() instanceof Sign) || e.getAction() != Action.RIGHT_CLICK_BLOCK || !e.hasBlock()) return;
          Sign signboard = (Sign) e.getClickedBlock().getState();
          if (!(signboard.getLine(0).equals("[Race]"))) return;
          waterpunch.atamamozi_d.plugin.race.Race_Core.joinRace(signboard.getLine(1).toString(), e.getPlayer());
     }

     @EventHandler
     public void onPlayerMove(final PlayerMoveEvent event) {
          // if (waterpunch.atamamozi_d.plugin.race.Race_Core.race_join_players.containsKey(event.getPlayer())) {
          //           if (waterpunch.atamamozi_d.plugin.race.export.Hachitai.CheckPlanePassed(event.getPlayer(), player_loc.get(event.getPlayer()))) {
          //                player_loc.put(event.getPlayer(), event.getPlayer().getLocation());
          //                Bukkit
          //                     .getServer()
          //                     .getScheduler()
          //                     .scheduleSyncDelayedTask(
          //                          plugin_data,
          //                          new Runnable() {
          //                               @Override
          //                               public void run() {
          //                                    waterpunch.atamamozi_d.plugin.race.export.Hachitai.CheckPlanePassed(event.getPlayer(), player_loc.get(event.getPlayer()));
          //                                    player_loc.remove(event.getPlayer());
          //                               }
          //                          },
          //                          1L
          //                     );
          //           }
          //      }
     }
}
