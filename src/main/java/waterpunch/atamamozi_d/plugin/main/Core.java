package waterpunch.atamamozi_d.plugin.main;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import waterpunch.atamamozi_d.plugin.event.Event;
import waterpunch.atamamozi_d.plugin.tool.CountDownTimer;
import waterpunch.atamamozi_d.plugin.tool.LocationViewer;
import waterpunch.atamamozi_d.plugin.tool.Meta;

public class Core extends JavaPlugin {

     @Override
     public void onEnable() {
          System.out.println("ATAMAMOZI-D ENGINE START");
          new Event(this);
          new Meta(this);
          new CountDownTimer(this);
          waterpunch.atamamozi_d.plugin.main.Main.loadconfig();
          waterpunch.atamamozi_d.plugin.race.Race_Core.clear();
     }

     @Override
     public void onDisable() {
          System.out.println("ATAMAMOZI-D ENGINE STOP");
     }

     @Override
     public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
          if (!(cmd.getName().equalsIgnoreCase("atamamozi_d")) || !(sender instanceof Player) || !(sender.isOp())) return false;
          if (args.length == 0) {
               ((Player) sender).openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getTop((Player) sender));
               return false;
          }
          switch (args[0]) {
               case "1":
                    onDebug1((Player) sender);
                    break;
               case "2":
                    onDebug2((Player) sender);
                    break;
               case "help":
                    onhelp((Player) sender);
                    break;
               case "load":
                    onload((Player) sender);
                    break;
               case "stop":
                    onstop((Player) sender);
                    break;
               case "leave":
                    onleave((Player) sender);
                    break;
               case "create":
                    oncreate((Player) sender);
                    break;
               case "setStartPoint":
                    if (args.length <= 1) {
                         ((Player) sender).sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setWarning() + "Please int");
                         return false;
                    }
                    onsetStartpoint((Player) sender, args[1]);
                    break;
               case "addCheckPoint":
                    if (args.length == 1) {
                         ((Player) sender).sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setWarning() + "Please int");
                         return false;
                    }
                    onsetCheckpoint((Player) sender, args[1]);
                    break;
               case "updateCheckPoint":
                    if (args.length == 2) {
                         ((Player) sender).sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setWarning() + "Please int");
                         return false;
                    }
                    onupdatecheckpoint((Player) sender, args[1], args[2]);
                    break;
               default:
                    onhelp((Player) sender);
                    break;
          }
          return false;
     }

     @Override
     public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
          if (args.length == 1 && cmd.getName().equalsIgnoreCase("atamamozi_d")) {
               ArrayList<String> subcmd = new ArrayList<String>();
               subcmd.add("help");
               subcmd.add("load");
               subcmd.add("help");
               subcmd.add("stop");
               subcmd.add("leave");
               subcmd.add("create");
               subcmd.add("setStartPoint");
               subcmd.add("addCheckPoint");
               subcmd.add("updateCheckPoint");

               return subcmd;
          }
          return null;
     }

     void onhelp(Player player) {
          player.sendMessage("---------------------");
          player.sendMessage("[help] this messeage");
          player.sendMessage("[load] /waterpunch.atamamozi_d load 'race name'");
          player.sendMessage("[stop] /waterpunch.atamamozi_d stop 'race name' race stop");
          player.sendMessage("[leave] leave player");
          player.sendMessage("---------------------");
     }

     void onDebug1(Player player) {
          player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
          player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setWarning() + "You ");
     }

     void onDebug2(Player player) {}

     void onload(Player player) {}

     void onstop(Player player) {}

     void onleave(Player player) {}

     void oncreate(Player player) {
          player.openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceCreate(player));
     }

     void onsetStartpoint(Player player, String r) {
          try {
               if (!waterpunch.atamamozi_d.plugin.race.Editer.getCheckPoint_Editr().contains(player)) {
                    player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setWarning() + "You don't Edit Race");
                    return;
               }
               if (Integer.parseInt(r) <= 0) {
                    player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setWarning() + "Please enter Over 0");
                    return;
               }

               player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Start point set completed");
               player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Please set to Check point");
               player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Next Run " + "[" + ChatColor.RED + "/atamamozi_d addCheckPoint [r]" + ChatColor.WHITE + "]" + " at your destination");

               setCheckPoint(player, Integer.parseInt(r), 0);
          } catch (NumberFormatException xr) {
               player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setWarning() + "<" + ChatColor.RED + r + ChatColor.GOLD + "> is Not Number");
          }
     }

     void onsetCheckpoint(Player player, String r) {
          try {
               if (!waterpunch.atamamozi_d.plugin.race.Editer.getCheckPoint_Editr().contains(player)) {
                    player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "You don't Edit Race");
                    return;
               }
               if (Integer.parseInt(r) <= 0) {
                    player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setWarning() + "Please enter Over 0");
                    return;
               }

               addCheckPoint(player, Integer.parseInt(r));

               player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Set Check Point");
          } catch (NumberFormatException xr) {
               player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setWarning() + "<" + ChatColor.RED + r + ChatColor.GOLD + "> is Not Number");
          }
     }

     void onupdatecheckpoint(Player player, String r, String No) {
          try {
               if (!waterpunch.atamamozi_d.plugin.race.Editer.getCheckPoint_Editr().contains(player)) {
                    player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setWarning() + "You don't Edit Race");
                    return;
               }
               if (Integer.parseInt(r) <= 0) {
                    player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setWarning() + "Please enter Over 0");
                    return;
               }

               if (waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).getCheckPoint().size() > Integer.parseInt(No)) {
                    player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Check Point<" + ChatColor.RED + r + ChatColor.GOLD + "> is Up Data");

                    setCheckPoint(player, Integer.parseInt(r), Integer.parseInt(No));
               } else {
                    player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setWarning() + "<" + ChatColor.RED + r + ChatColor.GOLD + "> is Over Number");
               }
          } catch (NumberFormatException xr) {
               player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setWarning() + "<" + ChatColor.RED + r + "or" + No + ChatColor.GOLD + "> is Not Number");
          }
     }

     void setCheckPoint(Player player, int r, int no) {
          if (waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).getCheckPoint().size() == 0) {
               waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).getCheckPoint().add(player.getLocation());
               waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).getCheckPoint_R().add(r);
          } else {
               waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).getCheckPoint().set((no), player.getLocation());
               waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).getCheckPoint_R().set(no, r);
          }
          player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
          LocationViewer locationViewer = new LocationViewer();
          locationViewer.setLoc(player.getLocation());
          locationViewer.setr(r);
          locationViewer.DrawCircle();
          CountDownTimer time = new CountDownTimer(locationViewer, 5);
          time.start();
     }

     void addCheckPoint(Player player, int r) {
          waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).getCheckPoint().add(player.getLocation());
          waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).getCheckPoint_R().add(r);

          player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
          LocationViewer locationViewer = new LocationViewer();
          locationViewer.setLoc(player.getLocation());
          locationViewer.setr(r);
          locationViewer.DrawCircle();
          CountDownTimer time = new CountDownTimer(locationViewer, 5);
          time.start();
     }

     void remCheckPoint(Player player, int no) {
          waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).getCheckPoint().remove(no);
          waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).getCheckPoint_R().remove(no);
     }
}
