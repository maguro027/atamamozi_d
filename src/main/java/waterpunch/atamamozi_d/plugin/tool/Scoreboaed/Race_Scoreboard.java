package waterpunch.atamamozi_d.plugin.tool.Scoreboaed;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import waterpunch.atamamozi_d.plugin.race.Race;
import waterpunch.atamamozi_d.plugin.race.Race_Mode;
import waterpunch.atamamozi_d.plugin.race.Race_Runner;

public class Race_Scoreboard {

     private ArrayList<Score> Scoreboards;
     private Scoreboard board;
     private Objective objective;

     public Scoreboard updateScoreboard(Race_Runner runner) {
          Race RACE = waterpunch.atamamozi_d.plugin.race.Race_Core.getRace(runner.getRaceID());
          Scoreboards = null;
          Scoreboards = new ArrayList<>();
          board = Bukkit.getScoreboardManager().getNewScoreboard();
          objective = board.registerNewObjective("Stats", "dummy", "a");
          objective.setDisplayName("Atamamozi_" + ChatColor.RED + "D");
          objective.setDisplaySlot(DisplaySlot.SIDEBAR);
          Scoreboards.add(objective.getScore("[" + ChatColor.AQUA + "RACE" + ChatColor.WHITE + "]"));
          Scoreboards.add(objective.getScore(RACE.getRace_name()));
          switch (runner.getMode()) {
               case WAIT:
                    Scoreboards.add(objective.getScore(ChatColor.YELLOW + "WAITING"));

                    if (waterpunch.atamamozi_d.plugin.race.Race_Core.getRace(runner.getRaceID()).getCountDown() <= 5) {
                         Scoreboards.add(objective.getScore(ChatColor.RED + "" + waterpunch.atamamozi_d.plugin.race.Race_Core.getRace(runner.getRaceID()).getCountDown() + ChatColor.WHITE + " s"));
                    } else {
                         Scoreboards.add(objective.getScore("" + waterpunch.atamamozi_d.plugin.race.Race_Core.getRace(runner.getRaceID()).getCountDown() + ChatColor.WHITE + " s"));
                    }
                    Scoreboards.add(objective.getScore("/atd " + ChatColor.AQUA + "start"));
                    Scoreboards.add(objective.getScore("[" + ChatColor.AQUA + "ENTRY" + ChatColor.WHITE + "]"));
                    for (Race_Runner val : waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Run.get(RACE.getUUID())) Scoreboards.add(objective.getScore("-" + ChatColor.AQUA + val.getPlayer().getName()));
                    break;
               case RUN:
                    Scoreboards.add(objective.getScore("Time : "));
                    Scoreboards.add(objective.getScore(runner.getNOWTimest()));
                    Scoreboards.add(objective.getScore("Rap  : "));
                    Scoreboards.add(objective.getScore(runner.getRap() + " / " + RACE.getRap()));
                    Scoreboards.add(objective.getScore("CheckPoint : "));
                    Scoreboards.add(objective.getScore(runner.getCheckPoint() + " / " + RACE.getCheckPointLoc().size()));
                    Scoreboards.add(objective.getScore("SPEED : " + new BigDecimal((Math.sqrt(Math.pow(runner.getnewLoc().getX() - runner.getoldLoc().getX(), 2) + Math.pow(runner.getnewLoc().getZ() - runner.getoldLoc().getZ(), 2)) * 20 * 60 * 60) / 1000).setScale(1, RoundingMode.HALF_UP).intValue()));
                    break;
               case GOAL:
                    if (waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Run.get(RACE.getUUID()) == null) break;
                    Scoreboards.add(objective.getScore("[" + ChatColor.AQUA + "SCORE" + ChatColor.WHITE + "]"));
                    for (Race_Runner val : waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Run.get(RACE.getUUID())) {
                         Scoreboards.add(objective.getScore(ChatColor.AQUA + val.getPlayer().getName()));
                         if (val.getMode() == Race_Mode.RUN) Scoreboards.add(objective.getScore("-Runnig...")); else if (val.getMode() == Race_Mode.GOAL) Scoreboards.add(objective.getScore(val.getTimest()));
                    }
                    break;
               case EDIT:
                    Scoreboards.add(objective.getScore("[" + ChatColor.AQUA + "TYPE" + ChatColor.WHITE + "]"));
                    Scoreboards.add(objective.getScore(RACE.getRace_Type().toString()));
                    Scoreboards.add(objective.getScore("[" + ChatColor.AQUA + "RAP" + ChatColor.WHITE + "]"));
                    Scoreboards.add(objective.getScore(RACE.getRap() + ""));
                    Scoreboards.add(objective.getScore("[" + ChatColor.AQUA + "STARTPOINT" + ChatColor.WHITE + "]"));
                    Scoreboards.add(objective.getScore(RACE.getStartPointLoc().size() + ""));
                    Scoreboards.add(objective.getScore("[" + ChatColor.AQUA + "CHECKPOINT" + ChatColor.WHITE + "]"));
                    Scoreboards.add(objective.getScore(RACE.getCheckPointLoc().size() + ""));
                    break;
               default:
                    break;
          }

          Collections.reverse(Scoreboards);
          for (int i = 0; i < Scoreboards.size(); i++) Scoreboards.get(i).setScore(i);

          return board;
     }
}
