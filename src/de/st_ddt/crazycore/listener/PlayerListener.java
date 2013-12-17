package de.st_ddt.crazycore.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import de.st_ddt.crazycore.CrazyCore;
import de.st_ddt.crazyutil.source.Permission;

public class PlayerListener implements Listener
{

	private final CrazyCore plugin;

	public PlayerListener(final CrazyCore plugin)
	{
		super();
		this.plugin = plugin;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	@Permission("crazycore.protectedplayer")
	public void PlayerJoin(final PlayerJoinEvent event)
	{
		final Player player = event.getPlayer();
		if (player.hasPermission("crazycore.protectedplayer"))
			if (plugin.getProtectedPlayers().add(player.getName().toLowerCase()))
				plugin.saveConfiguration();
	}
}
