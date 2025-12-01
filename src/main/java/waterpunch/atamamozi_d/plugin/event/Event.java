package waterpunch.atamamozi_d.plugin.event;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.plugin.Plugin;
import waterpunch.atamamozi_d.plugin.menus.Menus;
import waterpunch.atamamozi_d.plugin.race.Race;
import waterpunch.atamamozi_d.plugin.race.Race_Core;
import waterpunch.atamamozi_d.plugin.race.Race_Runner;
import waterpunch.atamamozi_d.plugin.race.enums.Race_Runner_Mode;
import waterpunch.atamamozi_d.plugin.race.enums.Race_Type;
import waterpunch.atamamozi_d.plugin.race.export.Hachitai;
import waterpunch.atamamozi_d.plugin.tool.CollarMessage;
import waterpunch.atamamozi_d.plugin.tool.CreateJson;
import waterpunch.atamamozi_d.plugin.tool.Location.Loc_parts;

/**
 * Event handler for all race-related gameplay events.
 * 
 * <p>Handles:
 * <ul>
 * <li>Inventory click events for race menus (create, join, configure)</li>
 * <li>Player movement for checkpoint detection</li>
 * <li>Sign interaction for race join/leave</li>
 * <li>Player join/quit for state initialization</li>
 * <li>Vehicle events for boat races</li>
 * </ul>
 * 
 * @author waterpunch
 */
public class Event implements Listener {

     /** Reference to plugin instance */
     Plugin plugin_data = null;

     /**
      * Register this event listener with the plugin.
      * 
      * @param plugin Plugin instance
      */
     public Event(Plugin plugin) {
          plugin.getServer().getPluginManager().registerEvents(this, plugin);
          plugin_data = plugin;
     }

     /**
      * Handle inventory click events for race menus.
      * 
      * <p>Processes clicks in:
      * <ul>
      * <li>RACE_TOP_MENU - Main race menu</li>
      * <li>RACE_LIST - Browse available races</li>
      * <li>RACE_CREATE - Configure new race</li>
      * <li>RACE_CREATE_TYPE/RAP/AMOUNT/ICON - Race parameter selection</li>
      * <li>RACE_RANKING - View leaderboards</li>
      * <li>RACE_EDIT - Modify existing race</li>
      * </ul>
      * 
      * @param event Inventory click event
      */
     @EventHandler
     public void onInventoryClickEvent(InventoryClickEvent event) {
          // try {
          // switch (getStatus()) {
          // case OFFLINE:
          // return "JOB or SLEEP";
          // case JOIN_ME:
          // return "BOREDOM or LONELY";
          // case ONLINE:
          // return "PLZ INVITE";
          // }
          // } catch (Exception e) {
          // // Unknown status in handler (debug output removed)
          // }
          // quick validations and use locals to avoid repeated casts and expensive string
          // matching
          if (event.getInventory() == null || event.getInventory().getType() != InventoryType.CHEST)
               return;
          if (!(event.getWhoClicked() instanceof Player))
               return;
          Player p = (Player) event.getWhoClicked();
          String title = p.getOpenInventory() == null ? "" : p.getOpenInventory().getTitle();
          if (title == null || !title.startsWith("RACE"))
               return;

          Race_Runner run = Race_Core.getRunner(p);
          // for create/edit menus we require a runner to exist
          if (run == null
                    && (title.startsWith("RACE_CREATE") || title.equals("RACE_EDIT") || title.equals("RACE_CREATE_TYPE")
                              || title.equals("RACE_CREATE_RAP") || title.equals("RACE_CREATE_AMOUNT"))) {
               p.sendMessage(CollarMessage.setWarning() + "Internal error: runner not found for menu");
               return;
          }
          switch (title) {
               case "RACE_TOP_MENU":
                    event.setCancelled(true);
                    if (event.getRawSlot() == 1)
                         p.openInventory(Menus.getRaceList(p));
                    if (event.getRawSlot() == 7)
                         p.openInventory(Menus.getRaceCreate(p));
                    break;
               case "RACE_LIST":
                    event.setCancelled(true);
                    if (event.getCurrentItem() == null)
                         return;
                    if (event.getRawSlot() == 45)
                         p.openInventory(Menus.getTop(p));
                    if (event.getRawSlot() == 46)
                         p.openInventory(Menus.getRaceList(p));
                    if (event.getRawSlot() == 47)
                         p.openInventory(Menus.getRaceRanking(p));
                    if (event.getRawSlot() >= 9 && event.getRawSlot() < 45) {
                         Race r = Race_Core.getRace(event.getCurrentItem().getItemMeta().getDisplayName());
                         if (r == null)
                              break;
                         Race_Core.joinRace(r, p);
                         p.closeInventory();
                    }
                    break;
               case "RACE_RANKING":
                    event.setCancelled(true);
                    if (event.getRawSlot() == 45)
                         p.openInventory(Menus.getTop(p));
                    if (event.getRawSlot() == 46)
                         p.openInventory(Menus.getRaceList(p));
                    if (event.getRawSlot() == 47)
                         p.openInventory(Menus.getRaceRanking(p));
                    return;
               case "RACE_EDIT":
                    event.setCancelled(true);
                    if (event.getRawSlot() == 45)
                         p.openInventory(Menus.getTop(p));
                    break;
               case "RACE_CREATE":
                    event.setCancelled(true);
                    if (event.getRawSlot() == 45)
                         p.openInventory(Menus.getTop(p));
                    if (event.getRawSlot() == 12)
                         p.openInventory(Menus.getRaceType(p));
                    if (event.getRawSlot() == 14)
                         p.openInventory(Menus.getRaceAmount(p));
                    if (event.getRawSlot() == 16)
                         p.openInventory(Menus.getRaceRap(p));
                    if (event.getRawSlot() == 28)
                         p.openInventory(Menus.getRaceIcon(p));
                    if (event.getRawSlot() == 30)
                         p.openInventory(Menus.getRaceStartPoint(p));
                    if (event.getRawSlot() == 32)
                         p.openInventory(Menus.getRaceCheckPoint(p));
                    if (event.getRawSlot() == 49)
                         CreateJson.saveRace(p);
                    break;
               case "RACE_CREATE_TYPE":
                    event.setCancelled(true);
                    if (event.getRawSlot() == 45)
                         p.openInventory(Menus.getRaceCreate(p));
                    if (event.getRawSlot() == 20 || event.getRawSlot() == 29) {
                         Race_Core.getRace(run.getRaceID()).setRace_Type(Race_Type.WALK);
                         p.openInventory(Menus.getRaceType(p));
                         if (run != null)
                              run.UpdateScoreboard();
                    } else if (event.getRawSlot() == 24 || event.getRawSlot() == 33) {
                         Race_Core.getRace(run.getRaceID()).setRace_Type(Race_Type.BOAT);
                         p.openInventory(Menus.getRaceType(p));
                         if (run != null)
                              run.UpdateScoreboard();
                    }
                    break;
               case "RACE_CREATE_RAP":
                    event.setCancelled(true);
                    if (event.getRawSlot() == 45)
                         p.openInventory(Menus.getRaceCreate(p));
                    if (event.getRawSlot() == 20) {
                         Race_Core.getRace(run.getRaceID()).setRap(Race_Core.getRace(run.getRaceID()).getRap() + 1);
                         p.openInventory(Menus.getRaceRap(p));
                         if (run != null)
                              run.UpdateScoreboard();
                    }
                    if (event.getRawSlot() == 24) {
                         if (Race_Core.getRace(run.getRaceID()).getRap() == 1)
                              return;
                         Race_Core.getRace(run.getRaceID()).setRap(Race_Core.getRace(run.getRaceID()).getRap() - 1);
                         p.openInventory(Menus.getRaceRap(p));
                         if (run != null)
                              run.UpdateScoreboard();
                    }
                    break;
               case "RACE_CREATE_AMOUNT":
                    event.setCancelled(true);
                    if (event.getRawSlot() == 45)
                         p.openInventory(Menus.getRaceCreate(p));
                    if (event.getRawSlot() == 20) {
                         Race_Core.getRace(run.getRaceID())
                                   .setJoin_Amount(Race_Core.getRace(run.getRaceID()).getJoin_Amount() + 1);
                         p.openInventory(Menus.getRaceAmount(p));
                         if (run != null)
                              run.UpdateScoreboard();
                    }
                    if (event.getRawSlot() == 24) {
                         if (Race_Core.getRace(run.getRaceID()).getJoin_Amount() == 1)
                              return;
                         Race_Core.getRace(run.getRaceID())
                                   .setJoin_Amount(Race_Core.getRace(run.getRaceID()).getJoin_Amount() - 1);
                         p.openInventory(Menus.getRaceAmount(p));
                         if (run != null)
                              run.UpdateScoreboard();
                    }
                    break;
               case "RACE_CREATE_ICON":
                    event.setCancelled(true);
                    if (event.getRawSlot() == 45) {
                         p.openInventory(Menus.getRaceCreate(p));
                    } else {
                         if (event.getCurrentItem() == null)
                              return;
                         Race_Core.getRace(run.getRaceID()).setIcon(event.getCurrentItem().getType());
                         p.openInventory(Menus.getRaceIcon(p));
                    }
                    break;
               case "RACE_CREATE_CHECKPOINT":
                    if (event.getRawSlot() == 45) {
                         event.setCancelled(true);
                         p.openInventory(Menus.getRaceCreate(p));
                    } else {
                         if (event.getCurrentItem() == null || event.getCurrentItem().getType() != Material.MAP) {
                              event.setCancelled(true);
                              return;
                         }
                         switch (event.getAction()) {
                              case CLONE_STACK: // remove
                                   Race_Core.getRace(run.getRaceID()).getCheckPointLoc().remove(event.getRawSlot() - 9);
                                   p.openInventory(Menus.getRaceCheckPoint(p));
                                   event.setCancelled(true);
                                   break;
                              case PICKUP_HALF: // Update
                                   Race_Core.getRace(run.getRaceID()).getCheckPointLoc().set(event.getRawSlot() - 9,
                                             Race_Core.getRace(run.getRaceID()).getCheckPointLoc()
                                                       .get(event.getRawSlot() - 9));
                                   event.setCancelled(true);
                                   break;
                              case PICKUP_ALL:
                                   Hachitai.setCircle(run, Race_Core.getRace(run.getRaceID()).getStartPointLoc()
                                             .get(event.getRawSlot() - 9).getLocation(), 1);

                                   p.openInventory(Menus.getRaceCheckPoint(p));
                                   event.setCancelled(true);
                                   break;
                              default:
                                   break;
                         }
                    }
                    break;
               case "RACE_CREATE_STARTPOINT":
                    if (event.getRawSlot() == 45) {
                         event.setCancelled(true);
                         p.openInventory(Menus.getRaceCreate(p));
                    } else {
                         if (event.getCurrentItem() == null
                                   || event.getCurrentItem().getType() != Material.EMERALD_BLOCK) {
                              event.setCancelled(true);
                              return;
                         }
                         switch (event.getAction()) {
                              case CLONE_STACK: // remove
                                   Race_Core.getRace(run.getRaceID()).getStartPointLoc().remove(event.getRawSlot() - 9);
                                   p.openInventory(Menus.getRaceStartPoint(p));
                                   event.setCancelled(true);
                                   break;
                              case PICKUP_HALF: // Update
                                   Race_Core.getRace(run.getRaceID()).getStartPointLoc().set(event.getRawSlot() - 9,
                                             new Loc_parts(p.getLocation()));
                                   event.setCancelled(true);
                                   break;
                              case PICKUP_ALL:
                                   Hachitai.setCircle(run, Race_Core.getRace(run.getRaceID()).getStartPointLoc()
                                             .get(event.getRawSlot() - 9).getLocation(), 1);
                                   p.openInventory(Menus.getRaceStartPoint(p));
                                   event.setCancelled(true);
                                   break;
                              default:
                                   break;
                         }
                    }
          }
     }

     @EventHandler
     public void SignChangeEvent(SignChangeEvent e) {
          if (e.getLine(0).equals("[race]") || e.getLine(0).equals("[Race]"))
               e.setLine(0, ChatColor.AQUA + "[Race]");
          if (!(e.getLine(0).equals(ChatColor.AQUA + "[Race]")))
               return;
          String name_cash = e.getLine(1);

          e.setLine(1, "Loaging...");
          Race Race = Race_Core.getRace(name_cash);
          if (Race == null) {
               e.setLine(1, ChatColor.RED + "Error");
               return;
          } else {
               e.setLine(1, Race.getRace_name());
               e.setLine(2, Race.getRap() + " : Rap");
               e.setLine(3, Race.getCreator());
          }
     }

     @Deprecated
     @EventHandler(ignoreCancelled = true)
     public void onEnSignClick(PlayerInteractEvent e) {
          if (e.getPlayer().isSneaking() || !(e.getClickedBlock().getState() instanceof Sign)
                    || e.getAction() != Action.RIGHT_CLICK_BLOCK || !e.hasBlock())
               return;
          if (!(((Sign) e.getClickedBlock().getState()).getLine(0).equals(ChatColor.AQUA + "[Race]")))
               return;
          if (Race_Core.getRace(((Sign) e.getClickedBlock().getState()).getLine(1)) == null) {
               e.getPlayer().sendMessage(CollarMessage.setWarning() + "Unknown Race");
               ((Sign) e.getClickedBlock().getState()).setLine(1, ChatColor.RED + "Error");
               ((Sign) e.getClickedBlock().getState()).setLine(2, "");
               ((Sign) e.getClickedBlock().getState()).setLine(3, "");
               return;
          }
          Race_Core.joinRace(Race_Core.getRace((((Sign) e.getClickedBlock().getState()).getLine(1))), e.getPlayer());
     }

     @EventHandler
     public void onPlayerMove(final PlayerMoveEvent event) {
          // Avoid handling movement every tiny delta — only run logic when player changes
          // block
          if (event.getFrom().getBlockX() == event.getTo().getBlockX()
                    && event.getFrom().getBlockY() == event.getTo().getBlockY()
                    && event.getFrom().getBlockZ() == event.getTo().getBlockZ())
               return;
          if (Race_Core.Race_Runner_List.isEmpty())
               return;
          Race_Runner run = Race_Core.getRunner(event.getPlayer());
          if (run == null)
               return;
          switch (run.getMode()) {
               case NO_ENTRY:
                    break;
               case EDIT:
                    // Drawing particles for every checkpoint on every small movement is expensive.
                    // Draw only checkpoints that are near the player (or at most a small number) to
                    // keep things lightweight.
                    Race raceEdit = Race_Core.getRace(run.getRaceID());
                    if (raceEdit != null && raceEdit.getCheckPointLoc() != null
                              && raceEdit.getCheckPointLoc().size() != 0) {
                         int drawn = 0;
                         for (int i = 0; i < raceEdit.getCheckPointLoc().size(); i++) {
                              Location cpLoc = raceEdit.getCheckPointLoc().get(i).getLocation();
                              double r = raceEdit.getCheckPointLoc().get(i).getr();
                              // draw if within reasonable range (r + 10) or until we draw up to 8 checkpoints
                              double distSq = cpLoc.distanceSquared(run.getPlayer().getLocation());
                              if (distSq <= (r + 10) * (r + 10) || drawn < 8) {
                                   run.getLocationViewer().DrawCircle(i);
                                   drawn++;
                              }
                              if (drawn >= 8)
                                   break;
                         }
                    }
                    break;
               case RUN:
                    Race raceRun = Race_Core.getRace(run.getRaceID());
                    if (raceRun == null || raceRun.getCheckPointLoc() == null || raceRun.getCheckPointLoc().isEmpty())
                         return;
                    int cpIndex = run.getCheckPoint();
                    if (cpIndex < 0 || cpIndex >= raceRun.getCheckPointLoc().size())
                         return;
                    Location chackpoint = raceRun.getCheckPointLoc().get(cpIndex).getLocation();
                    run.setnewLoc(event.getTo());
                    run.setoldLoc(event.getFrom());
                    run.getLocationViewer().DrawCircle(run.getCheckPoint());
                    run.UpdateScoreboard();
                    if (Hachitai.CheckPlanePassed(run, event.getTo(), event.getFrom())) {
                         double[] rtn = Hachitai.GetIntersection(run, chackpoint, event.getTo(), event.getFrom());
                         if (((rtn[0] - chackpoint.getX()) * (rtn[0] - chackpoint.getX())
                                   + (rtn[1] - chackpoint.getY()) * (rtn[1] - chackpoint.getY())
                                   + (rtn[2] - chackpoint.getZ()) * (rtn[2] - chackpoint.getZ())) < Race_Core
                                             .getRace(run.getRaceID()).getCheckPointLoc().get(run.getCheckPoint())
                                             .getr()
                                             * Race_Core.getRace(run.getRaceID()).getCheckPointLoc()
                                                       .get(run.getCheckPoint()).getr())
                              run.addCheckPoint();
                    }
                    break;
               case WAIT:
                    break;
               default:
                    break;
          }
     }

     @EventHandler
     public void Quit(PlayerQuitEvent event) {
          // removeRunner already handles cleaning up the runner and scoreboard — don't
          // double-remove
          Race_Core.removeRunner(event.getPlayer());
     }

     @EventHandler
     public void join(PlayerJoinEvent event) {
          new Race_Runner(event.getPlayer());
     }

     @Deprecated
     @EventHandler
     public void AnitBoat_Damage(VehicleDestroyEvent event) {
          if (!(event.getVehicle().getPassenger() instanceof Player)
                    || !(event.getVehicle().getType() == EntityType.BOAT))
               return;
          if (Race_Core.isJoin((Player) event.getVehicle().getPassenger()))
               for (Race_Runner val : Race_Core.Race_Runner_List)
                    if (val.getPlayer().getUniqueId().equals(event.getVehicle().getPassenger().getUniqueId())
                              && val.getMode() == Race_Runner_Mode.RUN) {
                         event.setCancelled(true);
                         return;
                    }
     }

     @Deprecated
     @EventHandler
     public void AnitBoat_Leave(VehicleExitEvent event) {
          if (!(event.getExited() instanceof Player) || !(event.getVehicle().getType() == EntityType.BOAT))
               return;
          if (!Race_Core.isJoin((Player) event.getExited()))
               return;
          Race_Runner runner = Race_Core.getRunner((Player) event.getExited());
          if (runner == null)
               return;
          if (runner.getEnter()) {
               runner.setEnter(false);
               event.setCancelled(false);
               return;
          }

          if (Race_Core.isJoin((Player) event.getExited()))
               for (Race_Runner val : Race_Core.Race_Runner_List)
                    if (val.getPlayer() == (Player) event.getExited() && val.getMode() == Race_Runner_Mode.RUN) {
                         event.setCancelled(true);
                         return;
                    }
     }
}
