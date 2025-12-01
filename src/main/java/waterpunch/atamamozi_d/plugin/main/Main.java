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
import waterpunch.atamamozi_d.plugin.race.Race;
import waterpunch.atamamozi_d.plugin.race.Race_Core;
import waterpunch.atamamozi_d.plugin.tool.CollarMessage;
import waterpunch.atamamozi_d.plugin.tool.CreateJson;

public class Main {

     public static final File file_Race = new File(new File("").getAbsolutePath().toString() + "/plugins/Atamamozi_D/Races/");

     public static int fil_count = 0;

     public static void loadconfig() {
          file_Race.mkdirs();
          File[] targetFile_dir_list = new File(file_Race.toString()).listFiles();
          if (targetFile_dir_list == null) return;
          getRaces();
     }

     public static void getRaces() {
          File[] files = CreateJson.file_Race.listFiles();
          if (files == null) return;
          for (File tmpFile : files) {
               if (tmpFile.isDirectory()) continue;
               if (!tmpFile.getName().substring(tmpFile.getName().lastIndexOf(".")).equals(".json")) continue;
               try (FileReader fileReader = new FileReader(tmpFile)) {
                    Gson gson = new Gson();
                    Race_Core.getRaces().add(gson.fromJson(fileReader, Race.class));
                    System.out.println(tmpFile.getName());
               } catch (JsonSyntaxException | JsonIOException | IOException e) {
                    e.printStackTrace();
                    continue;
               }
          }
          System.out.println(CollarMessage.setInfo() + "Load Complete " + Race_Core.getRaces().size() + " Races");
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
