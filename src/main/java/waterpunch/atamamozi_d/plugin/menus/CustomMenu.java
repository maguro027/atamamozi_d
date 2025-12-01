package waterpunch.atamamozi_d.plugin.menus;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * カスタムメニュークラス
 * カスタマイズ可能なインベントリメニューを作成する
 * 
 * Custom menu class
 * Creates customizable inventory menus
 */
public class CustomMenu {

     /** メニューインベントリ / Menu inventory */
     private Inventory RACE_RANKING;
     
     /** アイテムリスト / Item list */
     private ArrayList<ItemStack> Items = new ArrayList<>();

     /**
      * カスタムメニューを作成する
      * 
      * Creates a custom menu
      * 
      * @param InventoryName インベントリ名 / Inventory name
      */
     public CustomMenu(String InventoryName) {
          RACE_RANKING = Bukkit.createInventory(null, 9 * 6, InventoryName);
     }

     /**
      * アイテムを追加する
      * 
      * Adds an item
      * 
      * @param itemStack アイテム / Item
      */
     public void addItem(ItemStack itemStack) {
          Items.add(itemStack);
     }

     /**
      * 指定位置にアイテムを追加する
      * 
      * Adds an item at specified position
      * 
      * @param i 位置 / Position
      * @param itemStack アイテム / Item
      */
     public void addItem(int i, ItemStack itemStack) {
          Items.add(i, itemStack);
     }

     /**
      * アイテムを削除する
      * 
      * Removes an item
      * 
      * @param i 位置 / Position
      */
     public void removeItem(int i) {
          Items.remove(i);
     }
}
