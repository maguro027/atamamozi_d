package waterpunch.atamamozi_d.plugin.tool;

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
import waterpunch.atamamozi_d.plugin.race.Race_Runner;

public class Race_Scoreboard {

     private ArrayList<Score> Scoreboards;
     private Scoreboard board;
     private Objective objective;

     public Scoreboard updateScoreboard(Race_Runner runner) {
          Scoreboards = null;
          Scoreboards = new ArrayList<>();
          board = Bukkit.getScoreboardManager().getNewScoreboard();
          objective = board.registerNewObjective("Stats", "dummy", "a");
          objective.setDisplayName("Atamamozi_" + ChatColor.RED + "D");
          objective.setDisplaySlot(DisplaySlot.SIDEBAR);

          Scoreboards.add(objective.getScore("[" + ChatColor.AQUA + "RACE" + ChatColor.WHITE + "]"));
          Scoreboards.add(objective.getScore(runner.getRace().getRace_name()));
          switch (runner.getMode()) {
               case WAIT:
                    Scoreboards.add(objective.getScore(""));
                    Scoreboards.add(objective.getScore("Start Waiting..."));
                    Scoreboards.add(objective.getScore("/atamamozi_d " + ChatColor.AQUA + "start"));
                    Scoreboards.add(objective.getScore("[" + ChatColor.AQUA + "ENTRY" + ChatColor.WHITE + "]"));
                    for (Race_Runner val : waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Run.get(runner.getRace())) Scoreboards.add(objective.getScore("-" + ChatColor.AQUA + val.getPlayer().getName()));

                    break;
               case RUN:
                    Scoreboards.add(objective.getScore("Rap : "));
                    Scoreboards.add(objective.getScore(runner.getRap() + " / " + runner.getRace().getRap()));
                    Scoreboards.add(objective.getScore("CheckPiont : "));
                    Scoreboards.add(objective.getScore(runner.getCheckPoint() + " / " + runner.getRace().getCheckPointLoc().size()));

                    double index = 2;

                    Scoreboards.add(objective.getScore("SPEED : " + new BigDecimal((Math.sqrt(Math.pow(runner.getnewLoc().getX() - runner.getoldLoc().getX(), index) + Math.pow(runner.getnewLoc().getZ() - runner.getoldLoc().getZ(), index)) * 20 * 60 * 60) / 1000).setScale(1, RoundingMode.HALF_UP).intValue()));
                    break;
               case GOAL:
                    Scoreboards.add(objective.getScore("[" + ChatColor.AQUA + "SCORE" + ChatColor.WHITE + "]"));
                    for (Race_Runner val : waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Run.get(runner.getRace())) {
                         Scoreboards.add(objective.getScore(ChatColor.AQUA + val.getPlayer().getName()));
                         if (val.getMode() == Race_Mode.RUN) {
                              Scoreboards.add(objective.getScore("-Runnig..."));
                         } else if (val.getMode() == Race_Mode.GOAL) {
                              Scoreboards.add(objective.getScore(val.getTimest()));
                         }
                    }
                    break;
          }

          Collections.reverse(Scoreboards);
          for (int i = 0; i < Scoreboards.size(); i++) Scoreboards.get(i).setScore(i);
          return board;
     }
}
