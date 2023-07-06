package waterpunch.atamamozi_d.plugin.race;

import java.util.ArrayList;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import waterpunch.atamamozi_d.plugin.race.checkpoint.CheckPointLoc;
import waterpunch.atamamozi_d.plugin.tool.Loc_parts;
import waterpunch.atamamozi_d.plugin.tool.Race_Mode;
import waterpunch.atamamozi_d.plugin.tool.Race_Type;

public class Race {

     private String creator, race_name;
     private UUID race_ID;
     private Race_Type race_type;
     private Material icon;
     private int join_amount;
     private int rap;
     private Race_Mode race_Mode;
     private ArrayList<Loc_parts> StartPoint = new ArrayList<>();
     private ArrayList<CheckPointLoc> CheckPoint_Loc = new ArrayList<>();

     private ArrayList<String> Score = new ArrayList<>();

     private int Error_Count;

     public Race(Player creator) {
          this.creator = creator.getName();
          this.race_ID = UUID.randomUUID();
          this.race_name = "DEFAULT";
          this.race_type = Race_Type.WALK;
          this.icon = Material.MAP;
          this.rap = 1;
          this.race_Mode = Race_Mode.WAIT;
          this.join_amount = 1;
     }

     public void addStartPointLoc(Location loc) {
          StartPoint.add(new Loc_parts(loc));
     }

     public ArrayList<Loc_parts> getStartPointLoc() {
          return this.StartPoint;
     }

     public void addCheckPointLoc(Location loc, int r) {
          CheckPoint_Loc.add(new CheckPointLoc(loc, r));
     }

     public ArrayList<CheckPointLoc> getCheckPointLoc() {
          return this.CheckPoint_Loc;
     }

     public String getCreator() {
          return this.creator;
     }

     public void setRace_name(String race_name) {
          this.race_name = race_name;
     }

     public Race_Type getRace_Type() {
          return this.race_type;
     }

     public void setRace_Type(Race_Type race_type) {
          this.race_type = race_type;
     }

     public String getRace_name() {
          return this.race_name;
     }

     public void setIcon(Material icon) {
          this.icon = icon;
     }

     public Material getIcon() {
          return this.icon;
     }

     public void setJoin_Amount(int join_amount) {
          this.join_amount = join_amount;
     }

     public int getJoin_Amount() {
          return this.join_amount;
     }

     public void setRap(int Rap) {
          this.rap = Rap;
     }

     public int getRap() {
          return this.rap;
     }

     public void setErrorCount(int i) {
          this.Error_Count = i;
     }

     public void addErrorCount() {
          this.Error_Count++;
     }

     public int getErrorCount() {
          return this.Error_Count;
     }

     public void setMode(Race_Mode mode) {
          this.race_Mode = mode;
     }

     public Race_Mode getMode() {
          return this.race_Mode;
     }

     public void setScore(ArrayList<String> Score) {
          this.Score = Score;
     }

     public ArrayList<String> getScore() {
          return this.Score;
     }

     public void setID() {
          this.race_ID = UUID.randomUUID();
     }

     public UUID getID() {
          return this.race_ID;
     }
     // public void Count() {
     //      BukkitTask task = null;
     //      System.out.println(waterpunch.atamamozi_d.plugin.main.Core.getthis().getConfig().getInt("Setting.CountDown"));
     //      // System.out.println(waterpunch.atamamozi_d.plugin.main.Core.getthis().getConfig().getInt("Setting.CountDown"));
     //      task = waterpunch.atamamozi_d.plugin.main.Core.getthis().getServer().getScheduler().runTaskTimer(waterpunch.atamamozi_d.plugin.main.Core.getthis(), new Timer(waterpunch.atamamozi_d.plugin.main.Core.getthis(), 5), 0L, 20L);
     //      Bukkit
     //           .getScheduler()
     //           .runTaskTimer(
     //                waterpunch.atamamozi_d.plugin.main.Core.getthis(),
     //                new Runnable() {
     //                     private int time = waterpunch.atamamozi_d.plugin.main.Core.getthis().getConfig().getInt("Setting.CountDown");

     //                     @Override
     //                     public void run() {
     //                          if (waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Run.isEmpty()) return;
     //                          if (waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Run.get(waterpunch.atamamozi_d.plugin.race.Race_Core.getRace(getID())).size() == 0) return;

     //                          if (time == 0) {
     //                               waterpunch.atamamozi_d.plugin.main.Core.getthis().getServer().getScheduler().cancelTask(task.cancel());
     //                               waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Start(waterpunch.atamamozi_d.plugin.race.Race_Core.getRace(getID()));
     //                          }

     //                          for (Race_Runner run : waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Run.get(waterpunch.atamamozi_d.plugin.race.Race_Core.getRace(getID()))) run.setCountDown(this.time);
     //                          this.time--;
     //                     }
     //                },
     //                0L,
     //                20L
     //           );
     // }

     // private class Timer extends BukkitRunnable {

     //      int time; //秒数
     //      JavaPlugin plugin; //BukkitのAPIにアクセスするためのJavaPlugin

     //      public Timer(Plugin plugin2, int i) {
     //           this.time = i;
     //           this.plugin = (JavaPlugin) plugin2;
     //      }

     //      @Override
     //      public void run() {
     //           if (time <= 0) {
     //                //タイムアップなら
     //                plugin.getServer().broadcastMessage("Start!"); //Start!と全員に表示
     //                plugin.getServer().getScheduler().cancelTask(task.getTaskId()); //自分自身を止める
     //           } else {
     //                plugin.getServer().broadcastMessage("" + time); //残り秒数を全員に表示
     //           }
     //           time--; //1秒減算
     //      }
     // }
}
