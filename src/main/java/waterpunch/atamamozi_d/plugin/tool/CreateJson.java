package waterpunch.atamamozi_d.plugin.tool;

import org.bukkit.Bukkit;
import java.util.logging.Level;

import com.google.gson.Gson;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import waterpunch.atamamozi_d.plugin.main.Main;
import waterpunch.atamamozi_d.plugin.menus.Menus;
import waterpunch.atamamozi_d.plugin.race.Race;
import waterpunch.atamamozi_d.plugin.race.Race_Core;
import waterpunch.atamamozi_d.plugin.race.Race_Runner;
import waterpunch.atamamozi_d.plugin.race.enums.Race_Mode;
import waterpunch.atamamozi_d.plugin.race.enums.Race_Runner_Mode;
import waterpunch.atamamozi_d.plugin.score.Player_Score;

public class CreateJson {

     public static void createfile(String string) {
          try {
               Files.createFile(Paths.get(string));
          } catch (IOException e) {
               e.printStackTrace();
          }
     }

     public static void saveRace(Player player) {
          Race_Runner run = Race_Core.getRunner(player);
          Race race = Race_Core.getRace(run.getRaceID());
          if (run == null || !(run.getMode() == Race_Runner_Mode.EDIT)) return;
          if (!(race.getErrorCount() == 0)) {
               player.openInventory(Menus.getRaceCreate(player));
               return;
          }
          race.setMode(Race_Mode.WAIT);
          if (!(Main.file_Race.exists())) Main.file_Race.mkdir();
          String URL = Main.file_Race + "/" + race.getRace_name() + ".json";
          Main.createfile(URL);
          Race_Core.addRace(race);
          try (Writer writer = new FileWriter(URL)) {
               Gson gson = new Gson();
               gson.toJson(race, writer);

               race.setMode(Race_Mode.WAIT);

               player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
               player.sendMessage(CollarMessage.setInfo() + "Race Create Complete!!");
               Bukkit.getLogger().info(CollarMessage.setInfo() + player.getName() + " is Race Create");
               Bukkit.getLogger().info(CollarMessage.setInfo() + "NAME :" + race.getRace_name());
               Race_Core.removeRunner(player);
               player.closeInventory();
          } catch (IOException e) {
               Bukkit.getLogger().log(Level.SEVERE, "Failed to save race JSON: " + URL, e);
          }
     }

     public static void save(Race race) {
          if (!(Main.file_Race.exists())) Main.file_Race.mkdir();
          String URL = Main.file_Race + "/" + race.getRace_name() + ".json";
          Main.createfile(URL);
          try (Writer writer = new FileWriter(URL)) {
               Gson gson = new Gson();
               gson.toJson(race, writer);
               race.setMode(Race_Mode.WAIT);
          } catch (IOException e) {
               Bukkit.getLogger().log(Level.SEVERE, "Failed to save race JSON: " + URL, e);
          }
     }

     public static void Scoresave(Player_Score Score) {
          if (!(Main.file_SCORE.exists())) Main.file_SCORE.mkdir();
          String URL = Main.file_SCORE + "/" + Score.getUUID() + ".json";
          Main.createfile(URL);
          try (Writer writer = new FileWriter(URL)) {
               Gson gson = new Gson();
               gson.toJson(Score, writer);
          } catch (IOException e) {
               Bukkit.getLogger().log(Level.SEVERE, "Failed to save scores JSON: " + URL, e);
          }
     }

     public static void saveTop_Menu(LinkedHashMap<Integer, ArrayList<Inventory>> Data) {
          if (!(Main.file_SCORE.exists())) Main.file_SCORE.mkdir();
          String URL = Main.file_Rase_Menu + "/race_list.json";
          Main.createfile(URL);
          try (Writer writer = new FileWriter(URL)) {
               Gson gson = new Gson();
               gson.toJson(Data, writer);
          } catch (IOException e) {
               Bukkit.getLogger().log(Level.SEVERE, "Failed to save menu JSON: " + URL, e);
          }
     }
}
