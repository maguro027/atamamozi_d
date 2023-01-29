package waterpunch.atamamozi_d.plugin.event;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
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
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.plugin.Plugin;
import waterpunch.atamamozi_d.plugin.race.Race;
import waterpunch.atamamozi_d.plugin.race.Race_Runner;
import waterpunch.atamamozi_d.plugin.tool.Loc_parts;
import waterpunch.atamamozi_d.plugin.tool.LocationViewer;
import waterpunch.atamamozi_d.plugin.tool.Race_Type;

public class Event implements Listener {

     Plugin plugin_data = null;

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
                         if (!waterpunch.atamamozi_d.plugin.race.Race_Core.isJoin(((Player) event.getWhoClicked()))) {
                              ((Player) event.getWhoClicked()).sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Already join race");
                              return;
                         }

                         waterpunch.atamamozi_d.plugin.race.Race_Core.joinRace(waterpunch.atamamozi_d.plugin.race.Race_Core.getRace(event.getCurrentItem().getItemMeta().getDisplayName()), waterpunch.atamamozi_d.plugin.race.Race_Core.getRace(event.getCurrentItem().getItemMeta().getDisplayName()).getRap(), (Player) event.getWhoClicked());
                         ((Player) event.getWhoClicked()).closeInventory();

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
                         if (event.getRawSlot() == 30) if (waterpunch.atamamozi_d.plugin.race.Editer.getCheckPoint_Editr().contains((Player) event.getWhoClicked())) ((Player) event.getWhoClicked()).openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceStartPoint(((Player) event.getWhoClicked()))); else waterpunch.atamamozi_d.plugin.race.Editer.setCheckPoint_Editr((Player) event.getWhoClicked());
                         if (event.getRawSlot() == 32) if (waterpunch.atamamozi_d.plugin.race.Editer.getCheckPoint_Editr().contains((Player) event.getWhoClicked())) ((Player) event.getWhoClicked()).openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceCheckPoint(((Player) event.getWhoClicked()))); else waterpunch.atamamozi_d.plugin.race.Editer.setCheckPoint_Editr((Player) event.getWhoClicked());

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
                    case "RACE_CREATE_CHECKPOINT":
                         if (event.getRawSlot() == 45) {
                              event.setCancelled(true);
                              ((Player) event.getWhoClicked()).openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceCreate((Player) event.getWhoClicked()));
                         } else {
                              if (event.getCurrentItem() == null) return;

                              ((Player) event.getWhoClicked()).sendMessage(event.getAction().toString());
                              if (event.getAction() == InventoryAction.CLONE_STACK) {
                                   //remove
                                   waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(((Player) event.getWhoClicked())).getCheckPointLoc().remove(event.getRawSlot() - 9);
                                   ((Player) event.getWhoClicked()).openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceCheckPoint((Player) event.getWhoClicked()));
                                   event.setCancelled(true);
                              }
                              if (event.getAction() == InventoryAction.PICKUP_HALF) {
                                   //Update
                                   waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(((Player) event.getWhoClicked())).getCheckPointLoc().set(event.getRawSlot() - 9, waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(((Player) event.getWhoClicked())).getCheckPointLoc().get(event.getRawSlot() - 9));
                                   event.setCancelled(true);
                              }
                              if (event.getAction() == InventoryAction.PICKUP_ALL) {
                                   waterpunch.atamamozi_d.plugin.race.export.Hachitai.setCircle(waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(((Player) event.getWhoClicked())).getStartPointLoc().get(event.getRawSlot() - 9).getLocation(), 1);

                                   ((Player) event.getWhoClicked()).openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceCheckPoint((Player) event.getWhoClicked()));
                                   event.setCancelled(true);
                              }
                         }
                         break;
                    case "RACE_CREATE_STARTPOINT":
                         if (event.getRawSlot() == 45) {
                              event.setCancelled(true);
                              ((Player) event.getWhoClicked()).openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceCreate((Player) event.getWhoClicked()));
                         } else {
                              if (event.getCurrentItem() == null) return;
                              ((Player) event.getWhoClicked()).sendMessage(event.getAction().toString());
                              if (event.getAction() == InventoryAction.CLONE_STACK) {
                                   //remove
                                   waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(((Player) event.getWhoClicked())).getStartPointLoc().remove(event.getRawSlot() - 9);
                                   ((Player) event.getWhoClicked()).openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceCheckPoint((Player) event.getWhoClicked()));
                                   event.setCancelled(true);
                              }
                              if (event.getAction() == InventoryAction.PICKUP_HALF) {
                                   //Update
                                   waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(((Player) event.getWhoClicked())).getStartPointLoc().set(event.getRawSlot() - 9, new Loc_parts(((Player) event.getWhoClicked()).getLocation()));
                                   event.setCancelled(true);
                              }
                              if (event.getAction() == InventoryAction.PICKUP_ALL) {
                                   waterpunch.atamamozi_d.plugin.race.export.Hachitai.setCircle(waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(((Player) event.getWhoClicked())).getStartPointLoc().get(event.getRawSlot() - 9).getLocation(), 1);

                                   ((Player) event.getWhoClicked()).openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceStartPoint((Player) event.getWhoClicked()));
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
          String name_cash = e.getLine(1);
          String rap_cash = e.getLine(2);
          e.setLine(1, "Loaging...");
          Race Race = waterpunch.atamamozi_d.plugin.race.Race_Core.getRace(name_cash);
          if (Race == null) {
               e.setLine(1, ChatColor.RED + "Error");
               return;
          } else {
               e.setLine(1, Race.getRace_name());
               if (NumberUtils.isDigits(rap_cash)) e.setLine(2, rap_cash + " : Rap"); else e.setLine(2, Race.getRap() + " : Rap");
               e.setLine(3, Race.getCreator());
          }
     }

     @EventHandler(ignoreCancelled = true)
     public void onEnSignClick(PlayerInteractEvent e) {
          if (e.getPlayer().isSneaking() || !(e.getClickedBlock().getState() instanceof Sign) || e.getAction() != Action.RIGHT_CLICK_BLOCK || !e.hasBlock()) return;
          Sign signboard = (Sign) e.getClickedBlock().getState();
          if (!(signboard.getLine(0).equals("[Race]"))) return;
          if (!waterpunch.atamamozi_d.plugin.race.Race_Core.isJoin(e.getPlayer())) {
               e.getPlayer().sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Already join race");
               return;
          }
          if (waterpunch.atamamozi_d.plugin.race.Race_Core.getRace(signboard.getLine(1)) == null) {
               e.getPlayer().sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setWarning() + "Unknown Race");
               signboard.setLine(1, ChatColor.RED + "Error");
               signboard.setLine(2, "");
               signboard.setLine(3, "");
               return;
          }
          waterpunch.atamamozi_d.plugin.race.Race_Core.joinRace(waterpunch.atamamozi_d.plugin.race.Race_Core.getRace(signboard.getLine(1)), Integer.parseInt(signboard.getLine(2).replace(" : Rap", "")), e.getPlayer());
     }

     @EventHandler
     public void onPlayerMove(final PlayerMoveEvent event) {
          if (waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Runner_List.isEmpty()) return;
          for (Race_Runner run : waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Runner_List) {
               if (run.getPlayer() == event.getPlayer()) {
                    Location chackpoint = run.getRace().getCheckPointLoc().get(run.getCheckPoint()).getLocation();
                    int r = run.getRace().getCheckPointLoc().get(run.getCheckPoint()).getr();
                    LocationViewer locationViewer = new LocationViewer(event.getPlayer(), run.getRace(), run.getCheckPoint());
                    locationViewer.DrawCircle();
                    if (waterpunch.atamamozi_d.plugin.race.export.Hachitai.CheckPlanePassed(run, event.getTo(), event.getFrom())) {
                         double[] rtn = waterpunch.atamamozi_d.plugin.race.export.Hachitai.GetIntersection(run, chackpoint, event.getTo(), event.getFrom());
                         if (((rtn[0] - chackpoint.getX()) * (rtn[0] - chackpoint.getX()) + (rtn[1] - chackpoint.getY()) * (rtn[1] - chackpoint.getY()) + (rtn[2] - chackpoint.getZ()) * (rtn[2] - chackpoint.getZ())) < r * r) run.addCheckPoint();
                    }
                    break;
               }
          }
     }

     @EventHandler
     public void leave(PlayerQuitEvent event) {
          waterpunch.atamamozi_d.plugin.race.Race_Core.removeRunner(event.getPlayer());
     }

     @EventHandler
     public void AnitBoat_Damage(VehicleDestroyEvent event) {
          if (!(event.getVehicle().getPassenger() instanceof Player) || !(event.getVehicle().getType() == EntityType.BOAT)) return;
          if (!waterpunch.atamamozi_d.plugin.race.Race_Core.isJoin((Player) event.getVehicle().getPassenger())) {
               event.setCancelled(true);
               return;
          }
     }

     @EventHandler
     public void AnitBoat_Leave(VehicleExitEvent event) {
          if (!(event.getExited() instanceof Player) || !(event.getVehicle().getType() == EntityType.BOAT)) return;
          if (waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Runner_Onetime.equals((Player) event.getExited())) {
               waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Runner_Onetime.remove((Player) event.getExited());
               event.setCancelled(false);
               return;
          }

          if (!waterpunch.atamamozi_d.plugin.race.Race_Core.isJoin((Player) event.getExited())) {
               event.setCancelled(true);
               return;
          }
     }
}
