package waterpunch.atamamozi_d.plugin.tool;

import java.util.ArrayList;
import java.util.Collections;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import waterpunch.atamamozi_d.plugin.race.Race_Runner;

public class Scoreboard {

     public static void setScoreboard(Race_Runner runner) {
          ArrayList<Score> Scoreboards = new ArrayList<>();

          org.bukkit.scoreboard.Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
          Objective objective = board.registerNewObjective("Stats", "dummy", "a");
          objective.setDisplayName("Atamamozi_" + ChatColor.RED + "D");
          objective.setDisplaySlot(DisplaySlot.SIDEBAR);

          Scoreboards.add(objective.getScore("Race: " + runner.getRace().getRace_name()));
          Scoreboards.add(objective.getScore("Rap : "));
          Scoreboards.add(objective.getScore(runner.getRap() + " / " + runner.getRace().getRap()));
          Scoreboards.add(objective.getScore("CheckPiont : "));
          Scoreboards.add(objective.getScore(runner.getCheckPoint() + " / " + runner.getRace().getCheckPointLoc().size()));

          Collections.reverse(Scoreboards);
          for (int i = 0; i < Scoreboards.size(); i++) {
               Scoreboards.get(i).setScore(i);
          }

          runner.getPlayer().setScoreboard(board);
     }
}
