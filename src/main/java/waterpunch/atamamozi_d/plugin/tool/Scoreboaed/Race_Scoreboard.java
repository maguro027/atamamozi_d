package waterpunch.atamamozi_d.plugin.tool.Scoreboaed;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import waterpunch.atamamozi_d.plugin.race.Race;
import waterpunch.atamamozi_d.plugin.race.Race_Core;
import waterpunch.atamamozi_d.plugin.race.Race_Runner;
import waterpunch.atamamozi_d.plugin.race.enums.Race_Runner_Mode;

public class Race_Scoreboard {

     private ArrayList<Score> Scoreboards; // kept for backwards compatibility if needed
     private Scoreboard board;
     private Objective objective;

     /**
      * Build the actual Scoreboard object from a list of lines. Use buildLines() to
      * get the content first and avoid rebuilding when content didn't change.
      */
     public Scoreboard buildBoardFromLines(List<String> lines) {
          board = Bukkit.getScoreboardManager().getNewScoreboard();
          objective = board.registerNewObjective("Stats", "dummy", "a");
          objective.setDisplayName("Atamamozi_" + ChatColor.RED + "D");
          objective.setDisplaySlot(DisplaySlot.SIDEBAR);

          // The original logic reversed items then set scores from 0..n-1
          List<String> tmp = new ArrayList<>(lines);
          Collections.reverse(tmp);
          ArrayList<Score> scores = new ArrayList<>();
          for (String s : tmp)
               scores.add(objective.getScore(s));
          for (int i = 0; i < scores.size(); i++)
               scores.get(i).setScore(i);
          return board;
     }

     public List<String> buildLines(Race_Runner runner) {
          Race RACE = Race_Core.getRace(runner.getRaceID());
          if (RACE == null)
               return null;

          ArrayList<String> lines = new ArrayList<>();
          lines.add("[" + ChatColor.AQUA + "RACE" + ChatColor.WHITE + "]");
          lines.add(RACE.getRace_name());
          switch (runner.getMode()) {
               case NO_ENTRY:
                    return null;
               case WAIT:
                    lines.add(ChatColor.YELLOW + "WAITING");

                    if (Race_Core.getRace(runner.getRaceID()).getCountDown() <= 5) {
                         lines.add(ChatColor.RED + "" + Race_Core.getRace(runner.getRaceID()).getCountDown()
                                   + ChatColor.WHITE + " s");
                    } else {
                         lines.add("" + Race_Core.getRace(runner.getRaceID()).getCountDown() + ChatColor.WHITE + " s");
                    }
                    lines.add("/atd " + ChatColor.AQUA + "start");
                    lines.add("[" + ChatColor.AQUA + "ENTRY" + ChatColor.WHITE + "]");
                    if (Race_Core.Race_Run.get(RACE.getUUID()) != null)
                         for (Race_Runner val : Race_Core.Race_Run.get(RACE.getUUID()))
                              lines.add("-" + ChatColor.AQUA + val.getPlayer().getName());
                    break;
               case RUN:
                    lines.add("Time : ");
                    lines.add(runner.getNOWTimest());
                    lines.add("Rap  : ");
                    lines.add(runner.getRap() + " / " + RACE.getRap());
                    lines.add("CheckPoint : ");
                    lines.add(runner.getCheckPoint() + " / " + RACE.getCheckPointLoc().size());
                    // speed is fetched from runner cached value (updated at a 100ms interval)
                    lines.add("SPEED : " + runner.getCachedSpeed());
                    break;
               case ALL_GOAL_WAIT:
                    if (Race_Core.Race_Run.get(RACE.getUUID()) == null)
                         break;
                    lines.add("[" + ChatColor.AQUA + "SCORE" + ChatColor.WHITE + "]");
                    for (Race_Runner val : Race_Core.Race_Run.get(RACE.getUUID())) {
                         lines.add(ChatColor.AQUA + val.getPlayer().getName());
                         if (val.getMode() == Race_Runner_Mode.RUN)
                              lines.add("-Runnig...");
                         else
                              lines.add(val.getTimest());
                    }
                    break;
               case EDIT:
                    lines.add("[" + ChatColor.AQUA + "TYPE" + ChatColor.WHITE + "]");
                    lines.add(RACE.getRace_Type().toString());
                    lines.add("[" + ChatColor.AQUA + "RAP" + ChatColor.WHITE + "]");
                    lines.add(RACE.getRap() + "");
                    lines.add("[" + ChatColor.AQUA + "STARTPOINT" + ChatColor.WHITE + "]");
                    lines.add(RACE.getStartPointLoc().size() + "");
                    lines.add("[" + ChatColor.AQUA + "CHECKPOINT" + ChatColor.WHITE + "]");
                    lines.add(RACE.getCheckPointLoc().size() + "");
                    break;
               default:
                    break;
          }

          // return the list of scoreboard lines (buildBoardFromLines handles ordering)
          return lines;
     }
}
