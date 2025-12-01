package waterpunch.atamamozi_d.plugin.menus;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CustomMenu {

     private final Inventory inventory;
     private final ArrayList<ItemStack> Items = new ArrayList<>();

     public CustomMenu(String InventoryName) {
          inventory = Bukkit.createInventory(null, 9 * 6, InventoryName);
     }

     public Inventory getInventory() {
          return inventory;
     }

     public void addItem(ItemStack itemStack) {
          Items.add(itemStack);
     }

     public void addItem(int i, ItemStack itemStack) {
          Items.add(i, itemStack);
     }

     public void removeItem(int i) {
          Items.remove(i);
     }

     public ArrayList<ItemStack> getItems() {
          return Items;
     }

     public void build() {
          inventory.clear();
          for (int i = 0; i < Items.size() && i < inventory.getSize(); i++) {
               inventory.setItem(i, Items.get(i));
          }
     }
}
