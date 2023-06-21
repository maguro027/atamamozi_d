package waterpunch.atamamozi_d.plugin.main;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import waterpunch.atamamozi_d.plugin.event.Event;
import waterpunch.atamamozi_d.plugin.race.Race_Runner;
import waterpunch.atamamozi_d.plugin.race.checkpoint.CheckPointLoc;
import waterpunch.atamamozi_d.plugin.tool.CountDownTimer;
import waterpunch.atamamozi_d.plugin.tool.Race_Mode;

public class Core extends JavaPlugin {

     @Override
     public void onEnable() {
          System.out.println("ATAMAMOZI-D ENGINE START");
          new Event(this);
          new CountDownTimer(this);
          waterpunch.atamamozi_d.plugin.main.Main.loadconfig();
          for (Player p : this.getServer().getOnlinePlayers()) if (p.getOpenInventory().getTitle().equals("RACE_CREATE")) p.closeInventory();
     }

     @Override
     public void onDisable() {
          System.out.println("ATAMAMOZI-D ENGINE STOP");
          waterpunch.atamamozi_d.plugin.race.Race_Core.clear();
     }

     @Override
     public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
          if (!(cmd.getName().equalsIgnoreCase("atamamozi_d")) || !(sender instanceof Player)) return false;
          if (args.length == 0) {
               ((Player) sender).openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getTop((Player) sender));
               return false;
          }
          Race_Runner run = null;
          switch (args[0]) {
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
               case "setName":
               case "setname":
                    if (args.length == 1) {
                         ((Player) sender).sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setWarning() + "Need Name");
                         return false;
                    }
                    run = waterpunch.atamamozi_d.plugin.race.Race_Core.getRuner((Player) sender);
                    if (run.getMode() == Race_Mode.EDIT) {
                         if (args[1].equals("[ER]")) {
                              run.getPlayer().sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setWarning() + "NG Word");
                              return false;
                         }
                         waterpunch.atamamozi_d.plugin.race.Race_Core.getRace(run.getRaceID()).setRace_name(args[1]);
                         run.getPlayer().openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceCreate(run.getPlayer()));
                         run.UpdateScoreboard();
                    }
                    break;
               case "addStartPoint":
                    onaddStartpoint((Player) sender);
                    break;
               case "addCheckPoint":
                    if (args.length == 1) {
                         ((Player) sender).sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setWarning() + "Please int");
                         return false;
                    }
                    onaddCheckpoint((Player) sender, args[1]);
                    break;
               // case "updateCheckPoint":
               //      if (!(args.length == 1)) {
               //           ((Player) sender).sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setWarning() + "Please int");
               //           return false;
               //      }
               //      onupdatecheckpoint((Player) sender, args[1], args[2]);
               //      break;
               case "start":
                    run = waterpunch.atamamozi_d.plugin.race.Race_Core.getRuner((Player) sender);
                    if (run == null) return false;
                    switch (run.getMode()) {
                         case EDIT:
                              return false;
                         case GOAL:
                              run.getPlayer().sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Not join the race");
                              return false;
                         case RUN:
                              return false;
                         case WAIT:
                              waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Start(waterpunch.atamamozi_d.plugin.race.Race_Core.getRace(run.getRaceID()));
                              return false;
                         default:
                    }
                    break;
               case "re":
               case "respawn":
                    onrespawn((Player) sender);
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
               subcmd.add("start");
               subcmd.add("stop");
               subcmd.add("leave");
               subcmd.add("create");
               subcmd.add("addStartPoint");
               subcmd.add("addCheckPoint");
               // subcmd.add("updateCheckPoint");
               subcmd.add("setName");
               subcmd.add("respawn");

               return subcmd;
          }
          return null;
     }

     void onhelp(Player player) {
          player.sendMessage("---------------------");
          player.sendMessage("[help] this messeage");
          player.sendMessage("[load] /atamamozi_d load 'race name'");
          player.sendMessage("[stop] /atamamozi_d stop 'race name' race stop");
          player.sendMessage("[leave] leave player");
          player.sendMessage("---------------------");
     }

     void onload(Player player) {}

     void onstop(Player player) {}

     void onleave(Player player) {
          waterpunch.atamamozi_d.plugin.race.Race_Core.removeRunner(player);
     }

     void oncreate(Player player) {
          player.openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceCreate(player));
     }

     void onaddStartpoint(Player player) {
          Race_Runner run = waterpunch.atamamozi_d.plugin.race.Race_Core.getRuner(player);
          if (run == null || !(run.getMode() == Race_Mode.EDIT)) return;

          waterpunch.atamamozi_d.plugin.race.Race_Core.getRace(run.getRaceID()).addStartPointLoc(player.getLocation());
          player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Set Start Point");
          waterpunch.atamamozi_d.plugin.race.export.Hachitai.setCircle(run, player.getLocation(), 1);
          run.UpdateScoreboard();
     }

     void onaddCheckpoint(Player player, String r) {
          Race_Runner run = waterpunch.atamamozi_d.plugin.race.Race_Core.getRuner(player);
          if (run == null || !(run.getMode() == Race_Mode.EDIT)) return;
          try {
               if (Integer.parseInt(r) <= 0) {
                    player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setWarning() + "Please enter Over 0");
                    return;
               }
               waterpunch.atamamozi_d.plugin.race.Race_Core.getRace(run.getRaceID()).addCheckPointLoc(player.getLocation(), Integer.parseInt(r));

               player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
               player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Set Check Point");
          } catch (NumberFormatException xr) {
               player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setWarning() + "<" + ChatColor.RED + r + ChatColor.GOLD + "> is Not Number");
          }
          run.UpdateScoreboard();
     }

     void onsetCheckPoint(Player player, int r, int no) {
          Race_Runner run = waterpunch.atamamozi_d.plugin.race.Race_Core.getRuner(player);
          if (run == null || !(run.getMode() == Race_Mode.EDIT)) return;
          if (waterpunch.atamamozi_d.plugin.race.Race_Core.getRace(run.getRaceID()).getCheckPointLoc().size() == 0) {
               waterpunch.atamamozi_d.plugin.race.Race_Core.getRace(run.getRaceID()).addCheckPointLoc(player.getLocation(), r);
          } else {
               waterpunch.atamamozi_d.plugin.race.Race_Core.getRace(run.getRaceID()).getCheckPointLoc().set(no, new CheckPointLoc(player.getLocation(), r));
          }
          run.UpdateScoreboard();
     }

     void onrespawn(Player player) {
          Race_Runner run = waterpunch.atamamozi_d.plugin.race.Race_Core.getRuner(player);
          if (run == null || run.getMode() == Race_Mode.EDIT) {
               player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "You not join race");
               return;
          }
          run.ReSpawn();
          return;
     }

     void remCheckPoint(Player player, int no) {
          Race_Runner run = waterpunch.atamamozi_d.plugin.race.Race_Core.getRuner(player);
          if (run == null || !(run.getMode() == Race_Mode.EDIT)) return;
          waterpunch.atamamozi_d.plugin.race.Race_Core.getRace(run.getRaceID()).getCheckPointLoc().remove(no);
          run.UpdateScoreboard();
     }
}
