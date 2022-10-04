package waterpunch.atamamozi_d.plugin.tool;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class CountDownTimer extends BukkitRunnable {
  static Plugin plugin;

  public CountDownTimer(Plugin plugindata) {
    plugin = plugindata;
  }

  private int seconds;
  private LocationViewer view;

  public CountDownTimer(LocationViewer view, int seconds) {
    this.seconds = seconds;
    this.view = view;
  }

  public void start() {
    this.runTaskTimer(plugin, 0, 20);
  }

  public void stop() {
    this.cancel();
  }

  @Override
  public void run() {
    if (seconds <= 0) {
      this.cancel();
      return;
    }
    if (seconds % 1 == 0) view.DrawCircle();

    seconds--;
  }
}
