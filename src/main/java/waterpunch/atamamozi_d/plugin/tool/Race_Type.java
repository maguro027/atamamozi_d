package waterpunch.atamamozi_d.plugin.tool;

import org.bukkit.Material;

public enum Race_Type {

	RUN(Material.TOTEM_OF_UNDYING), BOAT(Material.OAK_BOAT); // ELYTRA(2)

	@SuppressWarnings("unused")
	private Material id;

	private Race_Type(Material id) {
		this.id = id;
	}
};
