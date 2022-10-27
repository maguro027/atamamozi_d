package waterpunch.atamamozi_d.plugin.tool;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import waterpunch.atamamozi_d.plugin.race.Race_Runner;

public class Scoreboard {

     public static void setScoreboard(Race_Runner runner) {
          org.bukkit.scoreboard.Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
          Objective objective = board.registerNewObjective("Stats", "dummy");
          objective.setDisplayName("Atamamozi_" + ChatColor.RED + "D");
          objective.setDisplaySlot(DisplaySlot.SIDEBAR);
          Score Name = objective.getScore("Race: " + runner.getRace().getRace_name());
          Score Rap1 = objective.getScore("Rap : ");
          Score Rap2 = objective.getScore(runner.getRap() + " / " + runner.getRace().getRap());
          Score Check1 = objective.getScore("CheckPiont : ");
          Score Check2 = objective.getScore(runner.getCheckPoint() + " / " + runner.getRace().getCheckPointLoc().size());
          Name.setScore(9);
          Rap1.setScore(8);
          Rap2.setScore(7);
          Check1.setScore(6);
          Check2.setScore(5);
          runner.getPlayer().setScoreboard(board);
     }
}
