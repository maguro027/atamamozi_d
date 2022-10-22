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
          Score Rap = objective.getScore("Rap : " + runner.getNowRap() + " / " + runner.getRap());
          Name.setScore(1);
          Rap.setScore(2);
          runner.getPlayer().setScoreboard(board);
     }
}
