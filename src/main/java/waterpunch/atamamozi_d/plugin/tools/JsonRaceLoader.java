package waterpunch.atamamozi_d.plugin.tools;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import waterpunch.atamamozi_d.plugin.tool.RaceSystem;

public class JsonRaceLoader {

    public static void main(String[] args) throws Exception {
        String defaultPath = "testdata/Races";
        String target = args.length > 0 ? args[0] : defaultPath;

        File f = new File(target);
        if (f.isDirectory()) {
            File[] files = f.listFiles((d, name) -> name.toLowerCase().endsWith(".json"));
            if (files == null || files.length == 0) {
                RaceSystem.logInfo("No json files found in: " + target);
                return;
            }
            for (File file : files) {
                process(file);
            }
        } else if (f.isFile()) {
            process(f);
        } else {
            RaceSystem.logError("Path not found: " + target);
        }
    }

    private static void process(File file) {
        try (Reader r = new FileReader(file)) {
            JsonElement el = JsonParser.parseReader(r);
            if (!el.isJsonObject()) {
                RaceSystem.logInfo(file.getName() + " -> not an object");
                return;
            }
            JsonObject obj = el.getAsJsonObject();

            String id = findId(obj);
            String name = findName(obj);
            int checkpoints = findCheckpoints(obj);

            RaceSystem.logInfo(
                    String.format("%s -> id=%s name=%s checkpoints=%d", file.getName(), id, name, checkpoints));

            // If there is a checkpoint array, print its contents
            JsonElement cpEl = findCheckpointElement(obj);
            if (cpEl != null && cpEl.isJsonArray()) {
                int idx = 0;
                for (JsonElement e : cpEl.getAsJsonArray()) {
                    idx++;
                    if (!e.isJsonObject())
                        continue;
                    JsonObject ce = e.getAsJsonObject();
                    JsonObject loc = ce.has("locParts") ? ce.getAsJsonObject("locParts")
                            : (ce.has("loc_parts") ? ce.getAsJsonObject("loc_parts")
                                    : (ce.has("LocParts") ? ce.getAsJsonObject("LocParts") : null));
                    String locStr = "";
                    if (loc != null) {
                        locStr = String.format("x=%s y=%s z=%s yaw=%s pitch=%s world=%s",
                                safeGet(loc, "x"), safeGet(loc, "y"), safeGet(loc, "z"), safeGet(loc, "yaw"),
                                safeGet(loc, "pitch"), safeGet(loc, "world_name"));
                    }
                    String rVal = ce.has("r") ? ce.get("r").getAsString() : "";
                    String abcd = "";
                    if (ce.has("abcd") && ce.get("abcd").isJsonArray()) {
                        abcd = ce.get("abcd").getAsJsonArray().toString();
                    }
                    RaceSystem
                            .logInfo(String.format("  checkpoint[%d]: r=%s abcd=%s loc={%s}", idx, rVal, abcd, locStr));
                }
            }
        } catch (Exception e) {
            RaceSystem.logError(String.format("Failed to parse %s: %s", file.getName(), e.getMessage()));
        }
    }

    private static JsonElement findCheckpointElement(JsonObject obj) {
        String[] keys = { "checkPointLoc", "check_point_loc", "CheckPoint_Loc", "CheckPointLoc", "checkpoint_loc",
                "checkpointLoc" };
        for (String k : keys) {
            if (obj.has(k))
                return obj.get(k);
        }
        // fallback: search for key containing 'check' and 'point'
        for (Map.Entry<String, JsonElement> e : obj.entrySet()) {
            String k = e.getKey().toLowerCase();
            if (k.contains("check") && k.contains("point"))
                return e.getValue();
        }
        return null;
    }

    private static String safeGet(JsonObject o, String key) {
        return o.has(key) ? o.get(key).getAsString() : "";
    }

    private static String findId(JsonObject obj) {
        String[] keys = { "raceId", "race_id", "race_ID", "id" };
        for (String k : keys) {
            if (obj.has(k))
                return obj.get(k).getAsString();
        }
        // fallback: try any uuid-like string value
        for (Map.Entry<String, JsonElement> e : obj.entrySet()) {
            if (e.getValue().isJsonPrimitive() && e.getValue().getAsString().matches("[0-9a-fA-F\\-]{36}")) {
                return e.getValue().getAsString();
            }
        }
        return null;
    }

    private static String findName(JsonObject obj) {
        String[] keys = { "raceName", "race_name", "name" };
        for (String k : keys) {
            if (obj.has(k))
                return obj.get(k).getAsString();
        }
        return null;
    }

    private static int findCheckpoints(JsonObject obj) {
        for (Map.Entry<String, JsonElement> e : obj.entrySet()) {
            String k = e.getKey().toLowerCase();
            if (k.contains("check") && k.contains("point") && k.contains("loc")) {
                if (e.getValue().isJsonArray())
                    return e.getValue().getAsJsonArray().size();
            }
            // also accept other variants
            if (k.contains("checkpoint") && e.getValue().isJsonArray())
                return e.getValue().getAsJsonArray().size();
        }
        return 0;
    }
}
