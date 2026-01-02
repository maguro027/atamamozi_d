package waterpunch.atamamozi_d.plugin.event;

import java.util.Objects;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
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
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import waterpunch.atamamozi_d.plugin.menus.Menus;
import waterpunch.atamamozi_d.plugin.race.Race;
import waterpunch.atamamozi_d.plugin.race.Race_Core;
import waterpunch.atamamozi_d.plugin.race.Race_Runner;
import waterpunch.atamamozi_d.plugin.race.enums.Race_Mode;
import waterpunch.atamamozi_d.plugin.race.enums.Race_Runner_Mode;
import waterpunch.atamamozi_d.plugin.race.enums.Race_Type;
import waterpunch.atamamozi_d.plugin.race.events.RaceModeChangeEvent;
import waterpunch.atamamozi_d.plugin.race.export.Hachitai;
import waterpunch.atamamozi_d.plugin.tool.CollarMessage;
import waterpunch.atamamozi_d.plugin.tool.CreateJson;
import waterpunch.atamamozi_d.plugin.tool.Location.Loc_parts;

/**
 * レース関連のゲームプレイイベントを処理するイベントハンドラ。
 *
 * <p>
 * 処理内容:
 * <ul>
 * <li>レースメニューのインベントリクリックイベント（作成、参加、設定）</li>
 * <li>チェックポイント検出のためのプレイヤー移動監視</li>
 * <li>レースへの参加/退出用の看板操作</li>
 * <li>プレイヤーの参加/退出による状態初期化</li>
 * <li>ボートレースのための乗り物イベント</li>
 * </ul>
 *
 * @author waterpunch
 */
public class Event implements Listener {

     private final Plugin plugin;

     /**
      * イベントリスナーのインスタンスを作成し、プラグインマネージャに自動登録します。
      * （コンストラクタでリスナー登録されていた既存の挙動を復元）
      *
      * @param plugin プラグインインスタンス
      */
     public Event(Plugin plugin) {
          this.plugin = plugin;
     }

     /**
      * このイベントリスナーをプラグインに登録します。
      * コンストラクタ後に呼び出すとイベント処理が有効になります。
      */
     public void register() {
          plugin.getServer().getPluginManager().registerEvents(this, plugin);
     }

     /**
      * レースメニューのインベントリクリックイベントを処理します。
      *
      * <p>
      * 以下のメニューでのクリックを処理します:
      * <ul>
      * <li>RACE_TOP_MENU - メインレースメニュー</li>
      * <li>RACE_LIST - 利用可能なレース一覧</li>
      * <li>RACE_CREATE - 新しいレースの設定</li>
      * <li>RACE_CREATE_TYPE/RAP/AMOUNT/ICON - レースパラメータ選択</li>
      * <li>RACE_RANKING - リーダーボード表示</li>
      * <li>RACE_EDIT - 既存レースの編集</li>
      * </ul>
      *
      * @param event インベントリクリックイベント
      */
     @EventHandler
     public void onInventoryClickEvent(InventoryClickEvent event) {

          if (event.getInventory().getType() != InventoryType.CHEST)
               return;

          if (!(event.getWhoClicked() instanceof Player))
               return;

          Player p = (Player) event.getWhoClicked();
          InventoryView view = p.getOpenInventory();
          String title = view.getTitle();
          if (!title.startsWith("RACE"))
               return;

          // デフォルトでRACEメニューからアイテムを持ち出せないようにします。
          // 個別のメニューアクションは下で処理します。
          event.setCancelled(true);

          Race_Runner run = Race_Core.getRunner(p);
          // 作成/編集メニューではランナーオブジェクトが存在する必要がある
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
                         ItemStack currentItem = event.getCurrentItem();
                         if (currentItem != null) {
                              ItemMeta meta = currentItem.getItemMeta();
                              if (meta != null) {
                                   String displayName = meta.getDisplayName();
                                   Race r = Race_Core.getRace(displayName);
                                   if (r == null)
                                        break;
                                   Race_Core.joinRace(r, p);
                                   p.closeInventory();
                              }
                         }
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
                    if (event.getRawSlot() == 49) {
                         Race_Runner runner = Race_Core.getRunner(p);
                         if (runner != null && runner.getMode() == Race_Runner_Mode.EDIT) {
                              Race race = Race_Core.getRace(runner.getRaceID());
                              if (race != null) {
                                   race.setMode(Race_Mode.WAIT);
                                   Race_Core.addRace(race);

                                   // プレイヤーへの通知
                                   p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                                   p.sendMessage(CollarMessage.setInfo() + "Race Create Complete!!");

                                   Race_Core.removeRunner(p);
                                   p.closeInventory();
                              } else {
                                   p.openInventory(Menus.getRaceCreate(p));
                              }
                         }
                    }
                    break;
               case "RACE_CREATE_TYPE":
                    event.setCancelled(true);
                    if (event.getRawSlot() == 45)
                         p.openInventory(Menus.getRaceCreate(p));
                    if (run == null)
                         break;
                    UUID raceID1 = run.getRaceID();
                    if (raceID1 == null)
                         break;
                    Race race1 = Race_Core.getRace(raceID1);
                    if (race1 == null)
                         break;
                    if (event.getRawSlot() == 20 || event.getRawSlot() == 29) {
                         race1.setRace_Type(Race_Type.WALK);
                         p.openInventory(Menus.getRaceType(p));
                         run.UpdateScoreboard();
                    } else if (event.getRawSlot() == 24 || event.getRawSlot() == 33) {
                         race1.setRace_Type(Race_Type.BOAT);
                         p.openInventory(Menus.getRaceType(p));
                         run.UpdateScoreboard();
                    }
                    break;
               case "RACE_CREATE_RAP":
                    event.setCancelled(true);
                    if (event.getRawSlot() == 45)
                         p.openInventory(Menus.getRaceCreate(p));
                    if (run == null)
                         break;
                    UUID raceID2 = run.getRaceID();
                    if (raceID2 == null)
                         break;
                    Race race2 = Race_Core.getRace(raceID2);
                    if (race2 == null)
                         break;
                    if (event.getRawSlot() == 20) {
                         race2.setRap(race2.getRap() + 1);
                         p.openInventory(Menus.getRaceRap(p));
                         run.UpdateScoreboard();
                    }
                    if (event.getRawSlot() == 24) {
                         if (race2.getRap() == 1)
                              return;
                         race2.setRap(race2.getRap() - 1);
                         p.openInventory(Menus.getRaceRap(p));
                         run.UpdateScoreboard();
                    }
                    break;
               case "RACE_CREATE_AMOUNT":
                    event.setCancelled(true);
                    if (event.getRawSlot() == 45)
                         p.openInventory(Menus.getRaceCreate(p));
                    if (run == null)
                         break;
                    UUID raceID3 = run.getRaceID();
                    if (raceID3 == null)
                         break;
                    Race race3 = Race_Core.getRace(raceID3);
                    if (race3 == null)
                         break;
                    if (event.getRawSlot() == 20) {
                         race3.setJoin_Amount(race3.getJoin_Amount() + 1);
                         p.openInventory(Menus.getRaceAmount(p));
                         run.UpdateScoreboard();
                    }
                    if (event.getRawSlot() == 24) {
                         if (race3.getJoin_Amount() == 1)
                              return;
                         race3.setJoin_Amount(race3.getJoin_Amount() - 1);
                         p.openInventory(Menus.getRaceAmount(p));
                         run.UpdateScoreboard();
                    }
                    break;
               case "RACE_CREATE_ICON":
                    event.setCancelled(true);
                    if (event.getRawSlot() == 45) {
                         p.openInventory(Menus.getRaceCreate(p));
                    } else {
                         ItemStack iconItem = event.getCurrentItem();
                         if (iconItem == null)
                              return;
                         if (run == null)
                              return;
                         UUID raceID4 = run.getRaceID();
                         if (raceID4 == null)
                              return;
                         Race race4 = Race_Core.getRace(raceID4);
                         if (race4 == null)
                              return;
                         race4.setIcon(iconItem.getType());
                         p.openInventory(Menus.getRaceIcon(p));
                    }
                    break;
               case "RACE_CREATE_CHECKPOINT":
                    if (event.getRawSlot() == 45) {
                         event.setCancelled(true);
                         p.openInventory(Menus.getRaceCreate(p));
                    } else {
                         ItemStack checkItem = event.getCurrentItem();
                         if (checkItem == null || checkItem.getType() != Material.MAP) {
                              event.setCancelled(true);
                              return;
                         }
                         if (run == null) {
                              event.setCancelled(true);
                              return;
                         }
                         UUID raceID5 = run.getRaceID();
                         if (raceID5 == null) {
                              event.setCancelled(true);
                              return;
                         }
                         Race race5 = Race_Core.getRace(raceID5);
                         if (race5 == null) {
                              event.setCancelled(true);
                              return;
                         }
                         int checkIndex = event.getRawSlot() - 9;
                         if (checkIndex < 0 || checkIndex >= race5.getCheckPointLoc().size()) {
                              event.setCancelled(true);
                              return;
                         }

                         switch (event.getAction()) {
                              case CLONE_STACK: // 削除
                                   race5.getCheckPointLoc().remove(checkIndex);
                                   p.openInventory(Menus.getRaceCheckPoint(p));
                                   event.setCancelled(true);
                                   break;
                              case PICKUP_HALF: // 更新
                                   race5.getCheckPointLoc().set(checkIndex,
                                             race5.getCheckPointLoc().get(checkIndex));
                                   event.setCancelled(true);
                                   break;
                              case PICKUP_ALL:
                                   Hachitai.setCircle(run, race5.getStartPointLoc()
                                             .get(checkIndex).getLocation(), 1);

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
                         ItemStack startItem = event.getCurrentItem();
                         if (startItem == null || startItem.getType() != Material.EMERALD_BLOCK) {
                              event.setCancelled(true);
                              return;
                         }
                         if (run == null) {
                              event.setCancelled(true);
                              return;
                         }
                         UUID raceID6 = run.getRaceID();
                         if (raceID6 == null) {
                              event.setCancelled(true);
                              return;
                         }
                         Race race6 = Race_Core.getRace(raceID6);
                         if (race6 == null) {
                              event.setCancelled(true);
                              return;
                         }
                         int startIndex = event.getRawSlot() - 9;
                         if (startIndex < 0 || startIndex >= race6.getStartPointLoc().size()) {
                              event.setCancelled(true);
                              return;
                         }

                         switch (event.getAction()) {
                              case CLONE_STACK: // 削除
                                   race6.getStartPointLoc().remove(startIndex);
                                   p.openInventory(Menus.getRaceStartPoint(p));
                                   event.setCancelled(true);
                                   break;
                              case PICKUP_HALF: // 更新
                                   race6.getStartPointLoc().set(startIndex,
                                             new Loc_parts(p.getLocation()));
                                   event.setCancelled(true);
                                   break;
                              case PICKUP_ALL:
                                   Hachitai.setCircle(run, race6.getStartPointLoc()
                                             .get(startIndex).getLocation(), 1);
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
          String line0 = e.getLine(0);
          if (line0 == null)
               return;
          if (line0.equals("[race]") || line0.equals("[Race]"))
               e.setLine(0, ChatColor.AQUA + "[Race]");
          if (!(line0.equals(ChatColor.AQUA + "[Race]")))
               return;
          String name_cash = e.getLine(1);

          e.setLine(1, "Loaging...");
          Race Race = Race_Core.getRace(name_cash);
          if (Race == null) {
               e.setLine(1, ChatColor.RED + "Error");
          } else {
               e.setLine(1, Race.getRace_name());
               e.setLine(2, Race.getRap() + " : Rap");
               e.setLine(3, Race.getCreator());
          }
     }

     @Deprecated
     @EventHandler(ignoreCancelled = true)
     public void onEnSignClick(PlayerInteractEvent e) {
          if (e.getPlayer().isSneaking() || !e.hasBlock() || e.getAction() != Action.RIGHT_CLICK_BLOCK)
               return;

          org.bukkit.block.Block clickedBlock = e.getClickedBlock();
          if (clickedBlock == null || !(clickedBlock.getState() instanceof Sign))
               return;

          Sign sign = (Sign) clickedBlock.getState();
          if (!(sign.getLine(0).equals(ChatColor.AQUA + "[Race]")))
               return;

          String raceName = sign.getLine(1);
          if (Race_Core.getRace(raceName) == null) {
               e.getPlayer().sendMessage(CollarMessage.setWarning() + "Unknown Race");
               sign.setLine(1, ChatColor.RED + "Error");
               sign.setLine(2, "");
               sign.setLine(3, "");
               sign.update();
               return;
          }
          Race_Core.joinRace(Race_Core.getRace(raceName), e.getPlayer());
     }

     @EventHandler
     public void onPlayerMove(final PlayerMoveEvent event) {
          // Avoid handling movement every tiny delta — only run logic when player changes
          // block
          Location fromLocation = event.getFrom();
          Location toLocation = event.getTo();
          // Assert non-null to satisfy static analysis that follows
          Objects.requireNonNull(fromLocation, "event.getFrom() returned null");
          Objects.requireNonNull(toLocation, "event.getTo() returned null");
          if (fromLocation.getBlockX() == toLocation.getBlockX()
                    && fromLocation.getBlockY() == toLocation.getBlockY()
                    && fromLocation.getBlockZ() == toLocation.getBlockZ())
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
                              && !raceEdit.getCheckPointLoc().isEmpty()) {
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
          @SuppressWarnings("unused")
          Race_Runner runner = new Race_Runner(event.getPlayer());
     }

     @Deprecated
     @EventHandler
     public void AnitBoat_Damage(VehicleDestroyEvent event) {
          Entity vehiclePassenger = event.getVehicle().getPassenger();
          if (!(vehiclePassenger instanceof Player)
                    || !(event.getVehicle().getType() == EntityType.BOAT))
               return;
          Player passenger = (Player) vehiclePassenger;
          if (Race_Core.isJoin(passenger)) {
               for (Race_Runner val : Race_Core.Race_Runner_List)
                    if (val.getPlayer().getUniqueId().equals(passenger.getUniqueId())
                              && val.getMode() == Race_Runner_Mode.RUN) {
                         event.setCancelled(true);
                         return;
                    }
          }
     }

     @Deprecated
     @EventHandler
     public void AnitBoat_Leave(VehicleExitEvent event) {
          // Only care about players exiting boats
          if (!(event.getExited() instanceof Player))
               return;
          if (event.getVehicle().getType() != EntityType.BOAT)
               return;

          Player player = (Player) event.getExited();
          Race_Runner runner = Race_Core.getRunner(player);
          if (runner == null) {

               return;
          }

          // Debug: log runner state to help diagnose why exits occur

          // Allow a single "enter" grace (used when spawning/respawning the boat)
          if (runner.getEnter()) {

               runner.setEnter(false);
               return; // do not cancel — this exit is intentional
          }

          // During RUN mode, prevent leaving the boat
          if (runner.getMode() == Race_Runner_Mode.RUN) {

               event.setCancelled(true);
          }
     }

     @EventHandler
     public void AnitEnter(VehicleEnterEvent event) {
          if (!(event.getEntered() instanceof Player))
               return;
          if (event.getVehicle().getType() != EntityType.BOAT)
               return;

          Player player = (Player) event.getEntered();
          Race_Runner runner = Race_Core.getRunner(player);
          if (runner == null)
               return;

          // If runner is in RUN mode, disallow entering boats other than the assigned
          // one.
          java.util.UUID currentCar = runner.getCar();
          java.util.UUID vehicleId = event.getVehicle().getUniqueId();

          // Check whether the vehicle already contains other passengers (other than
          // this player)
          boolean hasOtherPassengers = false;
          for (Entity ent : event.getVehicle().getPassengers()) {
               if (!ent.getUniqueId().equals(player.getUniqueId())) {
                    hasOtherPassengers = true;
                    break;
               }
          }

          if (runner.getMode() == Race_Runner_Mode.RUN) {
               if (currentCar == null) {
                    // Allow first-time boarding only if there are no other existing
                    // passengers (prevents hopping into someone else's boat)
                    if (hasOtherPassengers) {
                         event.setCancelled(true);
                         player.sendMessage(CollarMessage.setWarning() + "Cannot enter other boats during race");
                         return;
                    }
               } else {
                    // If already assigned a car, prevent entering a different one
                    if (!currentCar.equals(vehicleId)) {
                         event.setCancelled(true);
                         player.sendMessage(CollarMessage.setWarning() + "You cannot change boats during the race");
                         return;
                    }
               }
          }

          // Player actually entered the boat — clear the enter-grace and (re)record
          // vehicle id
          runner.setEnter(false);
          runner.setCar(vehicleId);
     }

     /**
      * レースの状態変更イベントを処理します。
      * 
      * <p>
      * このハンドラは、レースがWAIT→RUN、RUN→GOALなどの状態遷移を行ったときに
      * 必要な処理（ログ記録、通知など）を実行します。
      * </p>
      * 
      * @param event レース状態変更イベント
      */
     @EventHandler
     public void onRaceModeChange(RaceModeChangeEvent event) {
          Race race = event.getRace();
          Race_Mode oldMode = event.getOldMode();
          Race_Mode newMode = event.getNewMode();
          
          // 状態変更をログに記録
          plugin.getLogger().info(
               "Race mode changed: " + race.getRace_name() + 
               " [" + oldMode + " -> " + newMode + "]"
          );
          
          // 必要に応じて、特定の状態遷移に対する追加処理を実装できます
          // 例: WAIT -> RUN の場合、全参加者に音を鳴らす
          if (oldMode == Race_Mode.WAIT && newMode == Race_Mode.RUN) {
               // レース開始時の処理（必要に応じて実装）
          }
     }
}
