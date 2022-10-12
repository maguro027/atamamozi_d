package waterpunch.atamamozi_d.plugin.main;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {

     public static final File file_ = new File(new File("").getAbsolutePath().toString() + "/plugins/waterpunch.atamamozi_d/");
     public static List<String> items = new ArrayList<String>();

     public static int fil_count = 0;

     public static void loadconfig() {
          if (!(file_.exists())) file_.mkdir();

          try {
               createfile(file_ + "/race_list.json");
               //
               File[] targetFile_dir_list = new File(file_.toString()).listFiles();

               if (targetFile_dir_list == null) return;
               for (File targetFile : targetFile_dir_list) if (targetFile.getName().substring(targetFile.getName().lastIndexOf(".")).equals(".json")) fil_count++;

               System.out.println("load " + fil_count + " File");
               //

               Reader reader = Files.newBufferedReader(Paths.get(file_ + "/race_list.json"));
               items = new Gson().fromJson(reader, new TypeToken<List<String>>() {}.getType());

               reader.close();
          } catch (IOException e) {
               e.printStackTrace();
          }
     }

     public static void createfile(String string) {
          try {
               Files.createFile(Paths.get(string));
          } catch (IOException e) {}
     }

     public static void saveconfig(String string) {
          try {
               FileWriter writer = new FileWriter(file_ + "/race_list.json");

               writer.write(string);
               writer.close();
          } catch (IOException e) {
               e.printStackTrace();
          }
     }
}
