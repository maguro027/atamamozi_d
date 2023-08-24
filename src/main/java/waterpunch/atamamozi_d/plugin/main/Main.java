package waterpunch.atamamozi_d.plugin.main;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import waterpunch.atamamozi_d.plugin.race.Race;
import waterpunch.atamamozi_d.plugin.score.Player_Score;

public class Main {

     public static final File file_Race = new File(new File("").getAbsolutePath().toString() + "/plugins/Atamamozi_D/Races/");
     public static final File file_SCORE = new File(new File("").getAbsolutePath().toString() + "/plugins/Atamamozi_D/Player_Scores/");

     public static void loadconfig() {
          file_Race.mkdirs();
          File[] targetFile_dir_list = new File(file_Race.toString()).listFiles();
          if (targetFile_dir_list == null) return;
          getRaces();
          getScores();
     }

     public static void getRaces() {
          File[] files = waterpunch.atamamozi_d.plugin.tool.CreateJson.file_Race.listFiles();
          if (files == null) return;
          for (File tmpFile : files) if (tmpFile.isDirectory()) {
               getRaces();
          } else {
               if (tmpFile.getName().substring(tmpFile.getName().lastIndexOf(".")).equals(".json")) {
                    try (FileReader fileReader = new FileReader(tmpFile)) {
                         Gson gson = new Gson();
                         Race r = gson.fromJson(fileReader, Race.class);
                         if (r.getUUID() == null) waterpunch.atamamozi_d.plugin.tool.CreateJson.save(r);
                         waterpunch.atamamozi_d.plugin.race.Race_Core.Race_list.add(r);
                    } catch (JsonSyntaxException | JsonIOException | IOException e) {
                         e.printStackTrace();
                    }
               }
          }
     }

     public static void getScores() {
          File[] files = waterpunch.atamamozi_d.plugin.tool.CreateJson.file_SCORE.listFiles();
          if (files == null) return;
          for (File tmpFile : files) if (!tmpFile.isDirectory()) {
               if (tmpFile.getName().substring(tmpFile.getName().lastIndexOf(".")).equals(".json")) {
                    try (FileReader fileReader = new FileReader(tmpFile)) {
                         Gson gson = new Gson();
                         Player_Score r = gson.fromJson(fileReader, Player_Score.class);
                         waterpunch.atamamozi_d.plugin.score.Player_Score_Core.Score.add(r);
                         r.getTOPScores().forEach((k, v) -> waterpunch.atamamozi_d.plugin.score.Player_Score_Core.addRanking(k, r.getName(), v));
                    } catch (JsonSyntaxException | JsonIOException | IOException e) {
                         e.printStackTrace();
                    }
               }
          }
          for (Race r : waterpunch.atamamozi_d.plugin.race.Race_Core.Race_list) waterpunch.atamamozi_d.plugin.score.Player_Score_Core.SortRanking(r.getUUID());
     }

     public static void createfile(String string) {
          try {
               Files.createFile(Paths.get(string));
          } catch (IOException e) {}
     }

     public static void saveconfig(String string) {
          try {
               FileWriter writer = new FileWriter(file_Race + "/race_list.json");

               writer.write(string);
               writer.close();
          } catch (IOException e) {
               e.printStackTrace();
          }
     }
}
