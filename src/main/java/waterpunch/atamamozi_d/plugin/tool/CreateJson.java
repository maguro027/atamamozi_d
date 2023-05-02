package waterpunch.atamamozi_d.plugin.tool;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import waterpunch.atamamozi_d.plugin.race.Race_Runner;

public class CreateJson {

     public static final File file_ = new File(new File("").getAbsolutePath().toString() + "/plugins/Atamamozi_D/Races/");

     public static void createfile(String string) {
          try {
               Files.createFile(Paths.get(string));
          } catch (IOException e) {
               e.printStackTrace();
          }
     }

     public static void saveRace(Player player) {
          Race_Runner run = waterpunch.atamamozi_d.plugin.race.Race_Core.getRuner(player);
          if (run == null || !(run.getMode() == Race_Mode.EDIT)) return;
          if (!(run.getRace().getErrorCount() == 0)) {
               player.openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceCreate(player));
               return;
          }
          run.getRace().setMode(Race_Mode.WAIT);
          if (!(file_.exists())) file_.mkdir();
          String URL = file_ + "/" + run.getRace().getRace_name() + ".json";
          waterpunch.atamamozi_d.plugin.main.Main.createfile(URL);
          waterpunch.atamamozi_d.plugin.race.Race_Core.Race_list.add(run.getRace());
          try (Writer writer = new FileWriter(URL)) {
               Gson gson = new Gson();
               gson.toJson(run.getRace(), writer);

               waterpunch.atamamozi_d.plugin.race.Race_Core.Race_list.add(run.getRace());

               player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
               player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Race Create Complete!!");
               System.out.println(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + player.getName() + "is Race Create");
               System.out.println(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "NAME :" + run.getRace().getRace_name());
               waterpunch.atamamozi_d.plugin.race.Race_Core.removeRunner(player);
               player.closeInventory();
          } catch (IOException e) {
               e.printStackTrace();
          }
     }
}
