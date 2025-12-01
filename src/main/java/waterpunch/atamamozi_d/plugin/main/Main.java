package waterpunch.atamamozi_d.plugin.main;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import org.bukkit.inventory.Inventory;
import org.bukkit.Bukkit;
import java.util.logging.Level;
import waterpunch.atamamozi_d.plugin.race.Race;
import waterpunch.atamamozi_d.plugin.race.Race_Core;
import waterpunch.atamamozi_d.plugin.race.Race_Package;
import waterpunch.atamamozi_d.plugin.score.Player_Score;
import waterpunch.atamamozi_d.plugin.score.Player_Score_Core;
import waterpunch.atamamozi_d.plugin.score.Score_parts;
import waterpunch.atamamozi_d.plugin.tool.CollarMessage;
import waterpunch.atamamozi_d.plugin.tool.CreateJson;

/**
 * メインデータローダークラス
 * プラグインの初期化時にレースデータ、スコアデータ、メニューデータを読み込む
 * 
 * Main data loader class
 * Loads race data, score data, and menu data during plugin initialization
 */
public class Main {

     /** レースデータ保存ディレクトリ / Race data storage directory */
     public static final File file_Race = new File(
               new File("").getAbsolutePath().toString() + "/plugins/Atamamozi_D/Races/");
     
     /** プレイヤースコアデータ保存ディレクトリ / Player score data storage directory */
     public static final File file_SCORE = new File(
               new File("").getAbsolutePath().toString() + "/plugins/Atamamozi_D/Player_Scores/");
     
     /** レースメニュー設定ディレクトリ / Race menu configuration directory */
     public static final File file_Rase_Menu = new File(
               new File("").getAbsolutePath().toString() + "/plugins/Atamamozi_D/");

     /**
      * プラグインデータを読み込む
      * レースディレクトリを作成し、全てのデータファイルを読み込む
      * 
      * Loads plugin data
      * Creates race directory and loads all data files
      */
     public static void loadData() {
          file_Race.mkdirs();
          File[] targetFile_dir_list = new File(file_Race.toString()).listFiles();
          if (targetFile_dir_list == null)
               return;
          getRaces();
          getScores();
          getTop_Menu();
     }

     /**
      * トップメニュー設定をJSONファイルから読み込む
      * 
      * Loads top menu configuration from JSON file
      */
     @SuppressWarnings("unchecked")
     private static void getTop_Menu() {
          try {
               createfile(file_Rase_Menu + "/race_list.json");
               Reader reader = Files.newBufferedReader(Paths.get(file_Rase_Menu + "/race_list.json"));
               Race_Core.TOP_MENU = new Gson().fromJson(reader,
                         new LinkedHashMap<Integer, ArrayList<Inventory>>().getClass());
          } catch (IOException e) {
               Bukkit.getLogger().log(Level.SEVERE, "Failed to load top menu JSON", e);
          }
     }

     /**
      * レースディレクトリ内の全JSONファイルからレースデータを読み込む
      * 各レースをRace_Coreに登録する
      * 
      * Loads race data from all JSON files in the race directory
      * Registers each race with Race_Core
      */
     public static void getRaces() {
          File[] files = file_Race.listFiles();
          if (files == null)
               return;
          for (File tmpFile : files) {
               // サブディレクトリはスキップ（再帰処理を行わない）
               // Skip subdirectories (no recursive processing)
               if (tmpFile.isDirectory()) {
                    continue;
               }
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
                    Bukkit.getLogger().warning(CollarMessage.setWarning() + "Race Data Broken...");
                    Bukkit.getLogger().warning(tmpFile.getName());
                    Bukkit.getLogger().log(Level.WARNING, "Failed to parse race JSON: " + tmpFile.getName(), e);
                    break;
               }
          }
     }

     /**
      * プレイヤースコアディレクトリ内の全JSONファイルからスコアデータを読み込む
      * 各プレイヤーのスコアをPlayer_Score_Coreに登録し、ランキングをソートする
      * 
      * Loads score data from all JSON files in the player scores directory
      * Registers each player's score with Player_Score_Core and sorts rankings
      */
     public static void getScores() {
          File[] files = file_SCORE.listFiles();
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

                              // 各レースパッケージに参加回数を加算
                              // Add participation count to each race package
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
          // 全レースのランキングをソート
          // Sort rankings for all races
          for (Race r : Race_Core.Race_list)
               Player_Score_Core.SortRanking(r.getUUID());
     }

     /**
      * 指定されたパスにファイルを作成する（既に存在する場合は何もしない）
      * 
      * Creates a file at the specified path (does nothing if already exists)
      * 
      * @param string 作成するファイルのパス / Path of the file to create
      */
     public static void createfile(String string) {
          try {
               Files.createFile(Paths.get(string));
          } catch (IOException e) {
               Bukkit.getLogger().log(Level.WARNING, "Failed to create file: " + string, e);
          }
     }
}
