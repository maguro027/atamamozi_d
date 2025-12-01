package waterpunch.atamamozi_d.plugin.menus;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CustomMenu {

     private Inventory inventory;
     private ArrayList<ItemStack> items = new ArrayList<>();

     public CustomMenu(String InventoryName) {
          inventory = Bukkit.createInventory(null, 9 * 6, InventoryName);
     }

     public Inventory getInventory() {
          return inventory;
     }

     public void addItem(ItemStack itemStack) {
          items.add(itemStack);
     }

     public void addItem(int i, ItemStack itemStack) {
          items.add(i, itemStack);
     }

     public void removeItem(int i) {
          items.remove(i);
     }

     public ArrayList<ItemStack> getItems() {
          return items;
     }
}
