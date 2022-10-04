package waterpunch.atamamozi_d.plugin.event;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import waterpunch.atamamozi_d.plugin.tool.CountDownTimer;
import waterpunch.atamamozi_d.plugin.tool.LocationViewer;
import waterpunch.atamamozi_d.plugin.tool.Race_Type;

public class Event implements Listener {
  Plugin plugin_data = null;
  HashMap<Player, Location> player_loc = new HashMap<>();
  HashMap<LocationViewer, Integer> circle = new HashMap<>();

  public Event(Plugin plugin) {
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
    plugin_data = plugin;
  }

  @EventHandler
  public void onInventoryClickEvent(InventoryClickEvent event) {
    if (event.getInventory().toString().matches(".*" + "Custom" + ".*")
        && event.getInventory().getType() == InventoryType.CHEST) {
      switch (((Player) event.getWhoClicked()).getOpenInventory().getTitle().toString()) {
        case "RACE_TOP_MENU":
          event.setCancelled(true);
          if (event.getRawSlot() == 1)
            ((Player) event.getWhoClicked())
                .openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceList((Player) event.getWhoClicked()));
          if (event.getRawSlot() == 4)
            ((Player) event.getWhoClicked())
                .openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceEdit((Player) event.getWhoClicked()));
          if (event.getRawSlot() == 7)
            ((Player) event.getWhoClicked()).openInventory(
                waterpunch.atamamozi_d.plugin.menus.Menus.getRaceCreate(((Player) event.getWhoClicked())));
          break;
        case "RACE_LIST":
          event.setCancelled(true);
          if (event.getRawSlot() == 45)
            ((Player) event.getWhoClicked())
                .openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getTop((Player) event.getWhoClicked()));
          break;
        case "RACE_EDIT":
          event.setCancelled(true);
          if (event.getRawSlot() == 45)
            ((Player) event.getWhoClicked())
                .openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getTop((Player) event.getWhoClicked()));
          break;
        case "RACE_CREATE":
          event.setCancelled(true);
          if (event.getRawSlot() == 45)
            ((Player) event.getWhoClicked())
                .openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getTop((Player) event.getWhoClicked()));
          if (event.getRawSlot() == 19)
            waterpunch.atamamozi_d.plugin.race.Editer.setName_editor((Player) event.getWhoClicked());
          if (event.getRawSlot() == 21)
            ((Player) event.getWhoClicked())
                .openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceType(((Player) event.getWhoClicked())));
          if (event.getRawSlot() == 23)
            ((Player) event.getWhoClicked()).openInventory(
                waterpunch.atamamozi_d.plugin.menus.Menus.getRaceAmount(((Player) event.getWhoClicked())));
          if (event.getRawSlot() == 25)
            ((Player) event.getWhoClicked())
                .openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceIcon(((Player) event.getWhoClicked())));

          if (event.getRawSlot() == 40)
            if (waterpunch.atamamozi_d.plugin.race.Editer.getCheckPoint_Editr()
                .contains((Player) event.getWhoClicked())) {
              ((Player) event.getWhoClicked()).openInventory(
                  waterpunch.atamamozi_d.plugin.menus.Menus.getRaceCheckPoint(((Player) event.getWhoClicked())));
            } else {
              waterpunch.atamamozi_d.plugin.race.Editer.setCheckPoint_Editr((Player) event.getWhoClicked());
            }
          // SAVE if (event.getRawSlot() == 45) ((Player)
          // event.getWhoClicked()).openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceType(((Player)
          // event.getWhoClicked())));
          break;
        case "RACE_CREATE_TYPE":
          event.setCancelled(true);
          if (event.getRawSlot() == 45)
            ((Player) event.getWhoClicked())
                .openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceCreate((Player) event.getWhoClicked()));
          if (event.getRawSlot() == 20 || event.getRawSlot() == 29) {
            waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(((Player) event.getWhoClicked()))
                .setRace_Type(Race_Type.RUN);
            ((Player) event.getWhoClicked())
                .openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceType(((Player) event.getWhoClicked())));
          }
          if (event.getRawSlot() == 24 || event.getRawSlot() == 33) {
            waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(((Player) event.getWhoClicked()))
                .setRace_Type(Race_Type.BOAT);
            ((Player) event.getWhoClicked())
                .openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceType(((Player) event.getWhoClicked())));
          }

          break;
        case "RACE_CREATE_AMOUNT":
          event.setCancelled(true);
          if (event.getRawSlot() == 45)
            ((Player) event.getWhoClicked())
                .openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceCreate((Player) event.getWhoClicked()));
          if (event.getRawSlot() == 20) {
            waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(((Player) event.getWhoClicked()))
                .setJoin_Amount(waterpunch.atamamozi_d.plugin.race.Editer.getRace()
                    .get(((Player) event.getWhoClicked())).getJoin_Amount() + 1);
            ((Player) event.getWhoClicked()).openInventory(
                waterpunch.atamamozi_d.plugin.menus.Menus.getRaceAmount(((Player) event.getWhoClicked())));
          }
          if (event.getRawSlot() == 24) {
            if (waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(((Player) event.getWhoClicked()))
                .getJoin_Amount() == 0)
              return;
            waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(((Player) event.getWhoClicked()))
                .setJoin_Amount(waterpunch.atamamozi_d.plugin.race.Editer.getRace()
                    .get(((Player) event.getWhoClicked())).getJoin_Amount() - 1);
            ((Player) event.getWhoClicked()).openInventory(
                waterpunch.atamamozi_d.plugin.menus.Menus.getRaceAmount(((Player) event.getWhoClicked())));
          }
          break;
        case "RACE_CREATE_ICON":
          event.setCancelled(true);
          if (event.getRawSlot() == 45) {
            ((Player) event.getWhoClicked())
                .openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceCreate((Player) event.getWhoClicked()));
          } else {
            if (event.getCurrentItem() == null)
              return;
            waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(((Player) event.getWhoClicked()))
                .setIcon(event.getCurrentItem().getType());
            ((Player) event.getWhoClicked())
                .openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceIcon(((Player) event.getWhoClicked())));
          }
          break;
        case "RACE_CREATE_POINT":
          event.setCancelled(true);
          if (event.getRawSlot() == 45) {
            ((Player) event.getWhoClicked())
                .openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceCreate((Player) event.getWhoClicked()));
          } else {
            if (event.getCurrentItem() == null)
              return;

            // ((Player) event.getWhoClicked()).sendMessage(event.getAction().toString());
            if (event.getAction() == InventoryAction.CLONE_STACK) {
              waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(((Player) event.getWhoClicked())).getCheckPoint()
                  .remove(event.getRawSlot() - 9);
              waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(((Player) event.getWhoClicked()))
                  .getCheckPoint_R().remove(event.getRawSlot() - 9);
            }
            if (event.getAction() == InventoryAction.PICKUP_HALF) {
              waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(((Player) event.getWhoClicked())).getCheckPoint()
                  .set(event.getRawSlot() - 9, ((Player) event.getWhoClicked()).getLocation());
              waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(((Player) event.getWhoClicked()))
                  .getCheckPoint_R().set(event.getRawSlot() - 9, waterpunch.atamamozi_d.plugin.race.Editer.getRace()
                      .get(((Player) event.getWhoClicked())).getCheckPoint_R().get(event.getRawSlot() - 9));
            }
            if (event.getAction() == InventoryAction.PICKUP_ALL) {
              LocationViewer locationViewer = new LocationViewer();
              locationViewer.setLoc(waterpunch.atamamozi_d.plugin.race.Editer.getRace()
                  .get(((Player) event.getWhoClicked())).getCheckPoint().get(event.getRawSlot() - 9));
              locationViewer.setr(waterpunch.atamamozi_d.plugin.race.Editer.getRace()
                  .get(((Player) event.getWhoClicked())).getCheckPoint_R().get(event.getRawSlot() - 9));
              locationViewer.DrawCircle();

              CountDownTimer time = new CountDownTimer(locationViewer, 5);
              time.start();

              ((Player) event.getWhoClicked()).openInventory(
                  waterpunch.atamamozi_d.plugin.menus.Menus.getRaceCheckPoint((Player) event.getWhoClicked()));
            }
            break;
          }
      }
    }
  }

  @EventHandler
  public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
    if (waterpunch.atamamozi_d.plugin.race.Editer.getName_Edit().contains(event.getPlayer())) {
      waterpunch.atamamozi_d.plugin.race.Editer.getRace().get(event.getPlayer()).setRace_name(event.getMessage());
      waterpunch.atamamozi_d.plugin.race.Editer.getName_Edit().remove(event.getPlayer());
      Bukkit
          .getScheduler()
          .runTask(
              plugin_data,
              () -> {
                event.getPlayer()
                    .openInventory(waterpunch.atamamozi_d.plugin.menus.Menus.getRaceCreate(event.getPlayer()));
              });
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onPlayerMove(final PlayerMoveEvent event) {
    if (waterpunch.atamamozi_d.plugin.race.Race_Core.race_join_Players().containsKey(event.getPlayer())) {
      if (waterpunch.atamamozi_d.plugin.race.export.Hachitai.CheckPlanePassed(event.getPlayer(),
          player_loc.get(event.getPlayer()))) {
        player_loc.put(event.getPlayer(), event.getPlayer().getLocation());
        Bukkit
            .getServer()
            .getScheduler()
            .scheduleSyncDelayedTask(
                plugin_data,
                new Runnable() {

                  @Override
                  public void run() {
                    waterpunch.atamamozi_d.plugin.race.export.Hachitai.CheckPlanePassed(event.getPlayer(),
                        player_loc.get(event.getPlayer()));
                    player_loc.remove(event.getPlayer());
                  }
                },
                1L);
      }
    }
  }
}
