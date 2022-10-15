package waterpunch.atamamozi_d.plugin.tool;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.bukkit.entity.Player;

public class CreateJson {

     public static final File file_ = new File(new File("").getAbsolutePath().toString() + "/plugins/atamamozi_D/");

     public static void RaceToJson(Player player) {
          Gson gson = new Gson();
          System.out.println("test8");
          System.out.println("test: " + gson.toJson(waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player)));
     }

     public static void createfile(String string) {
          try {
               Files.createFile(Paths.get(string));
          } catch (IOException e) {}
     }

     public static void saveconfig(Player player, String string) {
          try {
               createfile(file_ + waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).getRace_name() + ".json");
               FileWriter writer = new FileWriter(file_ + waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(player).getRace_name() + ".json");

               writer.write(string);
               writer.close();
          } catch (IOException e) {
               e.printStackTrace();
          }
     }
}
