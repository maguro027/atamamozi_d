package waterpunch.atamamozi_d.plugin.menus;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class CustomMenu {

     private ArrayList<ItemStack> Items = new ArrayList<>();

     public CustomMenu(String InventoryName) {
          Bukkit.createInventory(null, 9 * 6, InventoryName);
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
}
