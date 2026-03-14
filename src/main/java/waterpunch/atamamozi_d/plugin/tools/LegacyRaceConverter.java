package waterpunch.atamamozi_d.plugin.tools;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.logging.Logger;

import org.yaml.snakeyaml.Yaml;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import waterpunch.atamamozi_d.plugin.tool.RaceSystem;

/**
 * Simple converter for legacy race JSON files.
 * - Reads plugin.yml version.
 * - If version is semantic with 3 parts (e.g. 0.2.1) will normalize JSON keys
 * and set `schemaVersion`.
 * - Moves originals to a `Backup` folder and writes converted files into
 * `Races` (or performs in-place conversion when source==output).
 */
public class LegacyRaceConverter {

    private static final Logger FALLBACK = Logger.getLogger("LegacyRaceConverter");

    private static void logInfo(String msg) {
        RaceSystem.logInfo(FALLBACK, msg);
    }

    private static void logWarning(String msg) {
        RaceSystem.logWarn(FALLBACK, msg);
    }

    private static void logWarning(String msg, Throwable t) {
        RaceSystem.logWarn(FALLBACK, msg, t);
    }

    private static void logSevere(String msg) {
        RaceSystem.logError(FALLBACK, msg);
    }

    private static void logSevere(String msg, Throwable t) {
        RaceSystem.logError(FALLBACK, msg, t);
    }

    @SuppressWarnings("BusyWait")
    private static boolean moveToBackupWithRetry(Path source, Path target) {
        int attempts = 5;
        for (int i = 0; i < attempts; i++) {
            try {
                Files.move(source, target);
                return true;
            } catch (IOException mvEx) {
                try {
                    Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                    Files.deleteIfExists(source);
                    return true;
                } catch (IOException copyEx) {
                    if (i == attempts - 1) {
                        logWarning(" - failed to move or copy backup: " + copyEx.getMessage(), copyEx);
                        return false;
                    }
                    if (i < attempts - 1) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        String pluginYml = args.length > 0 ? args[0] : "src/main/resources/plugin.yml";
        String sourceDir = args.length > 1 ? args[1] : "testdata/Races";
        boolean force = false;
        boolean cleanup = false;
        for (int i = 2; i < args.length; i++) {
            if ("--force".equals(args[i]))
                force = true;
            if ("--cleanup".equals(args[i]) || "--remove-backup".equals(args[i]))
                cleanup = true;
        }

        String version = readPluginVersion(pluginYml);
        convert(version, sourceDir, force, cleanup);
    }

    public static void convert(String version, String sourceDir, boolean force, boolean removeBackupOnSuccess) {
        if (version == null) {
            logSevere("plugin.yml version not found; aborting");
            return;
        }

        String[] parts = version.split("\\.");
        if (parts.length < 3) {
            logInfo("plugin version '" + version + "' has <3 segments — skipping conversion by policy");
            return;
        }

        File src = new File(sourceDir);
        if (!src.exists() || !src.isDirectory()) {
            logSevere("Source dir not found: " + sourceDir);
            return;
        }

        // Backup should be directly under Atamamozi_D folder, not Races folder
        File pluginFolder = src.getParentFile(); // Atamamozi_D
        File backupDir = new File(pluginFolder, "Backup");
        File outDir = new File(pluginFolder, "Races");

        boolean inPlace = false;
        try {
            if (src.getCanonicalPath().equals(outDir.getCanonicalPath())) {
                inPlace = true;
                logInfo("Convert Start");
            }
        } catch (IOException ex) {
            // ignore canonical path failures
        }

        if (!backupDir.exists())
            backupDir.mkdirs();
        if (!outDir.exists())
            outDir.mkdirs();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        File[] files = src.listFiles((d, name) -> name.toLowerCase().endsWith(".json"));
        if (files == null) {
            logInfo("No json files to process in " + sourceDir);
            return;
        }

        for (File f : files) {
            logInfo("Processing: " + f.getName());
            if (inPlace) {
                boolean ok = convertFile(f, version, force, removeBackupOnSuccess);
                if (!ok) {
                    logWarning(" - in-place conversion failed for: " + f.getName());
                }
                continue;
            }

            try (Reader r = new FileReader(f)) {
                JsonElement el = JsonParser.parseReader(r);
                if (!el.isJsonObject()) {
                    logInfo(" - skipping (not object)");
                    continue;
                }
                JsonObject obj = el.getAsJsonObject();

                String schema = obj.has("schemaVersion") ? obj.get("schemaVersion").getAsString() : null;
                if (!force && version.equals(schema)) {
                    logInfo(" - already up-to-date (schemaVersion matches)");
                    continue;
                }

                JsonObject normalized = normalize(obj);
                normalized.addProperty("schemaVersion", version);

                // write normalized to outDir with same filename
                File outFile = new File(outDir, f.getName());
                try (FileWriter fw = new FileWriter(outFile)) {
                    gson.toJson(normalized, fw);
                }

                // move original to backup (use copy+delete fallback for Windows locked files)
                Path targetBackup = backupDir.toPath().resolve(f.getName());
                boolean moved = moveToBackupWithRetry(f.toPath(), targetBackup);
                if (!moved) {
                    logWarning(" - failed to move or copy backup: " + f.getName());
                }

                // If requested, remove the backup after successful conversion
                if (removeBackupOnSuccess) {
                    try {
                        Files.deleteIfExists(targetBackup);
                    } catch (IOException delEx) {
                        logWarning(" - failed to delete backup after conversion: " + delEx.getMessage(), delEx);
                    }
                }

                logInfo(" - converted -> " + outFile.getPath());

            } catch (IOException | com.google.gson.JsonIOException | com.google.gson.JsonSyntaxException e) {
                logSevere("Failed to convert " + f.getName() + ": " + e.getMessage(), e);
            }
        }
    }

    /**
     * Convert a single JSON file in-place (original moved to Backup, converted
     * written to same location).
     */
    public static boolean convertFile(File file, String version, boolean force, boolean removeBackupOnSuccess) {
        if (version == null) {
            logSevere("plugin.yml version not found; aborting");
            return false;
        }
        if (file == null || !file.exists() || !file.isFile()) {
            logSevere("File not found: " + file);
            return false;
        }

        File parent = file.getParentFile();
        // Backup should live under the plugin root (Atamamozi_D), not inside Races
        File pluginFolder = parent != null ? parent.getParentFile() : null;
        File backupDir = pluginFolder != null ? new File(pluginFolder, "Backup") : new File(parent, "Backup");
        if (!backupDir.exists())
            backupDir.mkdirs();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // Step 1: Read and parse the file (close reader before any file operations)
        JsonObject normalized;
        File tmpOut = null;
        try {
            JsonObject obj;
            try (Reader r = new FileReader(file)) {
                JsonElement el = JsonParser.parseReader(r);
                if (!el.isJsonObject()) {
                    logInfo(" - skipping (not object): " + file.getName());
                    return false;
                }
                obj = el.getAsJsonObject();
            } // FileReader is now closed

            String schema = obj.has("schemaVersion") ? obj.get("schemaVersion").getAsString() : null;
            if (!force && version.equals(schema)) {
                logInfo(" - already up-to-date (schemaVersion matches): " + file.getName());
                return true;
            }

            normalized = normalize(obj);
            normalized.addProperty("schemaVersion", version);

            // Step 2: Write normalized to a temp file
            tmpOut = new File(parent, file.getName() + ".tmp");
            try (FileWriter fw = new FileWriter(tmpOut)) {
                gson.toJson(normalized, fw);
            } // FileWriter is now closed

            // Step 3: Move original to backup (all file handles are now closed)
            Path targetBackup = backupDir.toPath().resolve(file.getName());
            boolean moved = moveToBackupWithRetry(file.toPath(), targetBackup);
            if (!moved) {
                logWarning(" - failed to move or copy backup: " + file.getName());
            }

            // Step 4: Move tmpOut into place
            try {
                Files.move(tmpOut.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException m2) {
                // fallback to copy with replace
                try {
                    Files.copy(tmpOut.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    Files.deleteIfExists(tmpOut.toPath());
                } catch (IOException ex) {
                    logSevere(" - failed to write converted file: " + ex.getMessage(), ex);
                    return false;
                }
            }

            // Step 5: Remove backup if requested
            if (removeBackupOnSuccess) {
                try {
                    Files.deleteIfExists(targetBackup);
                } catch (IOException delEx) {
                    logWarning(" - failed to delete backup after conversion: " + delEx.getMessage(), delEx);
                }
            }

            logInfo(" - converted single -> " + file.getPath());
            return true;
        } catch (IOException | com.google.gson.JsonIOException | com.google.gson.JsonSyntaxException e) {
            logSevere("Failed to convert " + file.getName() + ": " + e.getMessage(), e);
            return false;
        } finally {
            try {
                if (tmpOut != null && tmpOut.exists()) {
                    Files.deleteIfExists(tmpOut.toPath());
                }
            } catch (IOException ex) {
                logWarning(" - failed to cleanup tmp file: " + ex.getMessage(), ex);
            }
        }
    }

    private static JsonObject normalize(JsonObject src) {
        JsonObject dst = new JsonObject();

        for (Map.Entry<String, JsonElement> e : src.entrySet()) {
            String key = e.getKey();
            JsonElement val = e.getValue();

            String nk = normalizeKey(key);

            // Special handling for checkpoint array: normalize to "checkPoint"
            // and convert each element's "loc_parts" -> "locParts"
            if (isCheckpointKey(key) && val.isJsonArray()) {
                com.google.gson.JsonArray arr = new com.google.gson.JsonArray();
                for (JsonElement item : val.getAsJsonArray()) {
                    if (!item.isJsonObject()) {
                        arr.add(item);
                        continue;
                    }
                    JsonObject obj = item.getAsJsonObject();
                    JsonObject outItem = new JsonObject();
                    // move loc_parts to locParts
                    if (obj.has("loc_parts")) {
                        outItem.add("locParts", obj.get("loc_parts"));
                    } else if (obj.has("LocParts")) {
                        outItem.add("locParts", obj.get("LocParts"));
                    } else if (obj.has("locParts")) {
                        outItem.add("locParts", obj.get("locParts"));
                    }
                    // copy r and abcd
                    if (obj.has("r"))
                        outItem.add("r", obj.get("r"));
                    if (obj.has("abcd"))
                        outItem.add("abcd", obj.get("abcd"));
                    arr.add(outItem);
                }
                dst.add("checkPoint", arr);
                continue;
            }

            // start_point -> startPoint (camelCase)
            if (isStartPointKey(key) && val.isJsonArray()) {
                dst.add("startPoint", val);
                continue;
            }

            // default: copy using normalized key (camelCase)
            dst.add(nk, val);
        }

        return dst;
    }

    private static boolean isCheckpointKey(String k) {
        String lower = k.toLowerCase();
        return lower.contains("check") && lower.contains("point") && lower.contains("loc");
    }

    private static boolean isStartPointKey(String k) {
        String lower = k.toLowerCase();
        return lower.contains("start") && lower.contains("point");
    }

    private static String normalizeKey(String k) {
        // common legacy-to-canonical mappings: convert to camelCase
        switch (k) {
            case "StartPoint":
            case "start_point":
                return "startPoint";
            case "CheckPoint_Loc":
            case "CheckPointLoc":
            case "check_point_loc":
                return "checkPoint";
            case "race_ID":
            case "race_id":
                return "raceId";
            case "race_name":
                return "raceName";
            case "race_type":
                return "raceType";
            case "race_Mode":
            case "race_mode":
                return "raceMode";
            case "Error_Count":
            case "error_count":
                return "errorCount";
            case "join_amount":
                return "joinAmount";
            case "TIME":
            case "time":
                return "time";
            default:
                // fallback: convert snake_case/PascalCase to camelCase
                return toCamel(k);
        }
    }

    private static String toCamel(String s) {
        if (s == null || s.isEmpty())
            return s;

        // Handle snake_case: convert to camelCase
        if (s.contains("_")) {
            StringBuilder sb = new StringBuilder();
            boolean capitalizeNext = false;
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (c == '_') {
                    capitalizeNext = true;
                } else if (capitalizeNext) {
                    sb.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                } else {
                    sb.append(Character.toLowerCase(c));
                }
            }
            return sb.toString();
        }

        // Handle PascalCase: convert first char to lowercase
        if (Character.isUpperCase(s.charAt(0))) {
            return Character.toLowerCase(s.charAt(0)) + s.substring(1);
        }

        // Already camelCase or lowercase
        return s;
    }

    private static String readPluginVersion(String pluginYml) {
        try (Reader r = new FileReader(pluginYml)) {
            Yaml y = new Yaml();
            Object o = y.load(r);
            if (o instanceof Map) {
                Map<?, ?> m = (Map<?, ?>) o;
                Object ver = m.get("version");
                return ver == null ? null : ver.toString();
            }
        } catch (Exception e) {
            logSevere("Failed to read plugin.yml: " + e.getMessage(), e);
        }
        return null;
    }
}
