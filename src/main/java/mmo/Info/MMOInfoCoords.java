/*
 * This file is part of mmoMinecraft (https://github.com/mmoMinecraftDev).
 *
 * mmoMinecraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mmo.Info;

import java.util.BitSet;
import java.util.HashMap;
import mmo.Core.MMOListener;
import mmo.Core.MMOPlugin;
import mmo.Core.events.MMOInfoEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.getspout.spoutapi.gui.ContainerType;
import org.getspout.spoutapi.gui.GenericContainer;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericTexture;
import org.getspout.spoutapi.gui.Label;
import org.getspout.spoutapi.player.SpoutPlayer;

public class MMOInfoCoords extends MMOPlugin {

	private HashMap<Player, Label> widgets = new HashMap<Player, Label>();

	@Override
	public BitSet mmoSupport(BitSet support) {
		support.set(MMO_PLAYER);
		support.set(MMO_NO_CONFIG);
		support.set(MMO_AUTO_EXTRACT);
		return support;
	}

	@Override
	public void onEnable() {
		super.onEnable();
		pm.registerEvent(Type.PLAYER_MOVE,
				  new PlayerListener() {

					  @Override
					  public void onPlayerMove(PlayerMoveEvent event) {
						  Player player = event.getPlayer();
						  Label label = widgets.get(player);
						  if (label != null) {
							  String coords = getCoords(player);
							  if (!coords.equals(label.getText())) {
								  label.setText(coords).setDirty(true);
							  }
						  }
					  }
				  }, Priority.Monitor, this);
		pm.registerEvent(Type.CUSTOM_EVENT,
				  new MMOListener() {

					  @Override
					  public void onMMOInfo(MMOInfoEvent event) {
						  if (event.isToken("coords")) {
							  SpoutPlayer player = event.getPlayer();
							  if (player.hasPermission("mmo.info.coords")) {
								  Label label = (Label) new GenericLabel(getCoords(player)).setResize(true).setFixed(true);
								  widgets.put(player, label);
								  event.setWidget(plugin, label);
								  event.setIcon("map.png");
							  } else {
								  event.setCancelled(true);
							  }
						  }
					  }
				  }, Priority.Normal, this);
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}

	@Override
	public void onPlayerQuit(Player player) {
		widgets.remove(player);
	}

	public String getCoords(Player player) {
		Location loc = player.getLocation();
		return String.format("x:%d, y:%d, z:%d", (int) loc.getX(), (int) loc.getY(), (int) loc.getZ());
	}
}
