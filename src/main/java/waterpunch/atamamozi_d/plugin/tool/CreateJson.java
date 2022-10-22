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

public class CreateJson {

     public static final File file_ = new File(new File("").getAbsolutePath().toString() + "/plugins/Atamamozi_D/Race/");

     public static void createfile(String string) {
          try {
               Files.createFile(Paths.get(string));
          } catch (IOException e) {}
     }

     public static void saveRace(Player player) {
          if (!(file_.exists())) file_.mkdir();
          waterpunch.atamamozi_d.plugin.main.Main.createfile("/plugins/atamamozi_D/Race/" + waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).getRace_name() + ".json");
          String relativePath = "";
          String URL = new File(relativePath).getAbsolutePath().toString() + "/plugins/atamamozi_D/Race/" + waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).getRace_name() + ".json";
          try (Writer writer = new FileWriter(URL)) {
               Gson gson = new Gson();
               gson.toJson(waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player), writer);

               waterpunch.atamamozi_d.plugin.race.Race_Core.Race_list.add(waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player));

               player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
               player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Race Create Complete!!");
               System.out.println(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + player.getName() + "is Race Create");
               System.out.println(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "NAME :" + waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).getRace_name());
               waterpunch.atamamozi_d.plugin.race.Editer.getRace().remove(player);
               player.closeInventory();
          } catch (IOException e) {
               e.printStackTrace();
          }
     }
}
