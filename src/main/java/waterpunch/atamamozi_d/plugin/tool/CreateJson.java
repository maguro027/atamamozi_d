package waterpunch.atamamozi_d.plugin.tool;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import com.google.gson.Gson;

import waterpunch.atamamozi_d.plugin.main.Main;
import waterpunch.atamamozi_d.plugin.race.Race;
import waterpunch.atamamozi_d.plugin.race.Race_Core;
import waterpunch.atamamozi_d.plugin.race.Race_Package;
import waterpunch.atamamozi_d.plugin.race.enums.Race_Mode;
import waterpunch.atamamozi_d.plugin.score.Player_Score;

public class CreateJson {

     /**
      * ファイルを作成します。既に存在する場合は何もしません。
      * 
      * @param filePath 作成するファイルのパス
      */
     public static void createfile(String filePath) {
          try {
               Files.createFile(Paths.get(filePath));
          } catch (IOException e) {
               Bukkit.getLogger().log(Level.SEVERE, String.format("Failed to create file: %s", filePath), e);
          }
     }

     /**
      * Race_PackageをJSON形式で保存します。
      * 
      * @param raceId 保存するレースのUUID
      */
     public static void saveRacePackage(UUID raceId) {
          if (raceId == null) {
               Bukkit.getLogger().warning("Cannot save race with null UUID");
               return;
          }

          Race race = Race_Core.getRace(raceId);
          if (race == null) {
               Bukkit.getLogger().warning(String.format("Race not found for UUID: %s", raceId));
               return;
          }

          // Race_Package作成
          Race_Package pkg = new Race_Package(
                    race.getRace_ID(),
                    race.getCreator(),
                    race.getRace_name(),
                    race.getJoin_Amount(),
                    race.getRap(),
                    race.getRace_Type(),
                    race.getIcon());

          // ディレクトリ作成
          if (!Main.FILE_RACE.exists()) {
               Main.FILE_RACE.mkdir();
          }

          String filePath = String.format("%s/%s.json", Main.FILE_RACE, race.getRace_name());
          Main.createfile(filePath);

          try (Writer writer = new FileWriter(filePath)) {
               new Gson().toJson(pkg, writer);
               Bukkit.getLogger().info(
                         String.format("Race package saved: %s (ID: %s)", race.getRace_name(), raceId));
          } catch (IOException e) {
               Bukkit.getLogger().log(Level.SEVERE,
                         String.format("Failed to save race package JSON: %s", filePath), e);
          }
     }

     /**
      * レースをJSON形式で保存します。
      * 
      * @param race 保存するレース
      */
     public static void save(Race race) {
          if (race == null) {
               Bukkit.getLogger().warning("Cannot save null race");
               return;
          }

          if (!Main.FILE_RACE.exists()) {
               Main.FILE_RACE.mkdir();
          }

          String filePath = String.format("%s/%s.json", Main.FILE_RACE, race.getRace_name());
          Main.createfile(filePath);

          try (Writer writer = new FileWriter(filePath)) {
               new Gson().toJson(race, writer);
               race.setMode(Race_Mode.WAIT);
          } catch (IOException e) {
               Bukkit.getLogger().log(Level.SEVERE,
                         String.format("Failed to save race JSON: %s", filePath), e);
          }
     }

     /**
      * プレイヤースコアをJSON形式で保存します。
      * 
      * @param score 保存するスコア
      */
     public static void scoreSave(Player_Score score) {
          if (score == null) {
               Bukkit.getLogger().warning("Cannot save null score");
               return;
          }

          if (!Main.FILE_SCORE.exists()) {
               Main.FILE_SCORE.mkdir();
          }

          String filePath = String.format("%s/%s.json", Main.FILE_SCORE, score.getUUID());
          Main.createfile(filePath);

          try (Writer writer = new FileWriter(filePath)) {
               new Gson().toJson(score, writer);
          } catch (IOException e) {
               Bukkit.getLogger().log(Level.SEVERE,
                         String.format("Failed to save scores JSON: %s", filePath), e);
          }
     }

     /**
      * レースメニューのデータをJSON形式で保存します。
      * 
      * @param menuData 保存するメニューデータ
      */
     public static void saveTopMenu(LinkedHashMap<Integer, ArrayList<Inventory>> menuData) {
          if (menuData == null) {
               Bukkit.getLogger().warning("Cannot save null menu data");
               return;
          }

          if (!Main.FILE_RACE_MENU.exists()) {
               Main.FILE_RACE_MENU.mkdir();
          }

          String filePath = String.format("%s/race_list.json", Main.FILE_RACE_MENU);
          Main.createfile(filePath);

          try (Writer writer = new FileWriter(filePath)) {
               new Gson().toJson(menuData, writer);
          } catch (IOException e) {
               Bukkit.getLogger().log(Level.SEVERE,
                         String.format("Failed to save menu JSON: %s", filePath), e);
          }
     }
}
