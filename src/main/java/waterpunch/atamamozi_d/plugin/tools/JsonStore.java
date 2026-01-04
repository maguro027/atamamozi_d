package waterpunch.atamamozi_d.plugin.tools;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import waterpunch.atamamozi_d.plugin.race.RacePackage;

/**
 * Simple JSON persistence helper for RacePackage objects.
 */
public final class JsonStore {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private JsonStore() {
    }

    public static boolean save(RacePackage rp, File dir) {
        if (rp == null || dir == null)
            return false;
        if (!dir.exists())
            dir.mkdirs();

        String name = rp.getRaceId() != null ? rp.getRaceId().toString() : rp.getRaceName();
        File out = new File(dir, name + ".json");
        try (FileWriter fw = new FileWriter(out)) {
            GSON.toJson(rp, fw);
            return true;
        } catch (Exception e) {
            System.err.println("JsonStore.save failed: " + e.getMessage());
            return false;
        }
    }

    public static List<RacePackage> loadAll(File dir) {
        List<RacePackage> out = new ArrayList<>();
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return out;
        File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".json"));
        if (files == null)
            return out;
        for (File f : files) {
            try (FileReader fr = new FileReader(f)) {
                RacePackage rp = GSON.fromJson(fr, RacePackage.class);
                if (rp != null)
                    out.add(rp);
            } catch (Exception e) {
                System.err.println("JsonStore.loadAll: failed to read " + f.getName() + ": " + e.getMessage());
            }
        }
        return out;
    }
}
