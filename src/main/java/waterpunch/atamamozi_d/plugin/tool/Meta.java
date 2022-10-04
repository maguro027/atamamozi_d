package waterpunch.atamamozi_d.plugin.tool;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

public class Meta {
	static Plugin plugin;

	public Meta(Plugin plugindata) {
		plugin = plugindata;
	}

	private Player player;
	private int Race_ID;
	private int Check_point;

	private final static String DATA_KEY = "waterpunch.";
	private final static String MEMO = "ATAMAMOAZI_D.";
	private final static String CHECK_POINT = "CHeCK_POINT.";

	public Meta(Player player) {
		this.player = player;
	}

	public void setPlayerMeta() {

		if (!(player == null)) {

			player.setMetadata(DATA_KEY, // key
					new FixedMetadataValue(plugin, // �v���O�C��
							"MSYS.Player" // �ݒ肵�����l
					));
			player.setMetadata(MEMO, // key
					new FixedMetadataValue(plugin, // �v���O�C��
							Race_ID // �ݒ肵�����l
					));
			player.setMetadata(CHECK_POINT, // key
					new FixedMetadataValue(plugin, // �v���O�C��
							Check_point // �ݒ肵�����l
					));
		}
	}
}
