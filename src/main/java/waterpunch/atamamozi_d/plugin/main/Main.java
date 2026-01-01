package waterpunch.atamamozi_d.plugin.main;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import waterpunch.atamamozi_d.plugin.race.Race;
import waterpunch.atamamozi_d.plugin.race.Race_Core;
import waterpunch.atamamozi_d.plugin.race.Race_Package;
import waterpunch.atamamozi_d.plugin.score.Player_Score;
import waterpunch.atamamozi_d.plugin.score.Player_Score_Core;
import waterpunch.atamamozi_d.plugin.score.Score_parts;
import waterpunch.atamamozi_d.plugin.tool.CollarMessage;
import waterpunch.atamamozi_d.plugin.tool.CreateJson;

public class Main {

     public static final File FILE_RACE = new File(
               new File("").getAbsolutePath() + "/plugins/Atamamozi_D/Races/");
     public static final File FILE_SCORE = new File(
               new File("").getAbsolutePath() + "/plugins/Atamamozi_D/Player_Scores/");
     public static final File FILE_RACE_MENU = new File(
               new File("").getAbsolutePath() + "/plugins/Atamamozi_D/");

     public static void loadData() {
          FILE_RACE.mkdirs();
          File[] targetFile_dir_list = new File(FILE_RACE.toString()).listFiles();
          if (targetFile_dir_list == null)
               return;
          getRaces();
          getScores();
          getTop_Menu();
     }

     @SuppressWarnings("unchecked")
     private static void getTop_Menu() {
          createfile(FILE_RACE_MENU + "/race_list.json");
          try {
               Reader reader = Files.newBufferedReader(Paths.get(FILE_RACE_MENU + "/race_list.json"));
               Race_Core.TOP_MENU = new Gson().fromJson(reader,
                         new LinkedHashMap<Integer, ArrayList<Inventory>>().getClass());
          } catch (IOException e) {
               Bukkit.getLogger().log(Level.SEVERE, "Failed to load top menu JSON", e);
          }
     }

     public static void getRaces() {
          File[] files = FILE_RACE.listFiles();
          if (files == null)
               return;
          for (File tmpFile : files)
               if (tmpFile.isDirectory()) {
                    getRaces();
               } else {
                    String name = tmpFile.getName();
                    int idx = name.lastIndexOf('.');
                    if (idx < 0)
                         continue;
                    if (!name.substring(idx).equals(".json"))
                         continue;
                    try (FileReader fileReader = new FileReader(tmpFile)) {
                         Gson gson = new Gson();
                         Race r = gson.fromJson(fileReader, Race.class);
                         if (r.getUUID() == null) {
                              r.setUUID();
                              CreateJson.save(r);
                         }
                         Race_Core.addRace(r);
                    } catch (JsonSyntaxException | JsonIOException | IOException e) {
                         String message = CollarMessage.setWarning() + "Race Data Broken..." + tmpFile.getName();
                         Bukkit.getLogger().warning(message);
                         Bukkit.getLogger().log(Level.WARNING, "Failed to parse race JSON: " + tmpFile.getName(), e);
                         break;
                    }
               }
     }

     public static void getScores() {
          File[] files = FILE_SCORE.listFiles();
          if (files == null)
               return;
          for (File tmpFile : files)
               if (!tmpFile.isDirectory()) {
                    String name = tmpFile.getName();
                    int idx = name.lastIndexOf('.');
                    if (idx < 0)
                         continue;
                    if (name.substring(idx).equals(".json")) {
                         try (FileReader fileReader = new FileReader(tmpFile)) {
                              Gson gson = new Gson();
                              Player_Score r = gson.fromJson(fileReader, Player_Score.class);
                              Player_Score_Core.Score.add(r);

                              for (Score_parts parts : r.getScore_parts())
                                   for (Race_Package Package : Race_Core.Race_packages)
                                        if (Package.getRace_ID().equals(parts.getRace_ID()))
                                             Package.addJoinCount(parts.getCount());

                              r.getTOPScores().forEach((k, v) -> Player_Score_Core.addRanking(k, r.getName(), v));
                         } catch (JsonSyntaxException | JsonIOException | IOException e) {
                              Bukkit.getLogger().log(Level.WARNING, "Failed to load score JSON: " + tmpFile.getName(),
                                        e);
                         }
                    }
               }
          for (Race r : Race_Core.Race_list)
               Player_Score_Core.SortRanking(r.getUUID());
     }

     /**
      * ファイルが存在しない場合に新規作成します。
      *
      * @param string 作成するファイルのパス
      */
     public static void createfile(String string) {
          try {
               Path path = Paths.get(string);
               // ファイルが存在しない場合のみ作成する
               if (!Files.exists(path)) {
                    Files.createFile(path);
               }
          } catch (IOException e) {
               Bukkit.getLogger().log(Level.WARNING, "Failed to create file: " + string, e);
          }
     }
}
