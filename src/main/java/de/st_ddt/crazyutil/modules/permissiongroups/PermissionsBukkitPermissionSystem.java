package de.st_ddt.crazyutil.modules.permissiongroups;

import java.util.LinkedHashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.platymuus.bukkit.permissions.Group;
import com.platymuus.bukkit.permissions.PermissionsPlugin;

class PermissionsBukkitPermissionSystem extends NoPermissionSystem
{

	private final PermissionsPlugin plugin;

	public PermissionsBukkitPermissionSystem()
	{
		super();
		plugin = (PermissionsPlugin) Bukkit.getServer().getPluginManager().getPlugin("PermissionsBukkit");
		if (plugin == null)
			throw new IllegalArgumentException("PermissionsBukkit plugin cannot be null!");
	}

	@Override
	public String getName()
	{
		return "PermissionsBukkit";
	}

	@Override
	public boolean hasGroup(final Player player, final String name)
	{
		return super.hasGroup(player, name) || getGroups(player).contains(name);
	}

	@Override
	public String getGroup(final Player player)
	{
		for (final Group group : plugin.getGroups(player.getName()))
			return group.getName();
		return null;
	}

	@Override
	public Set<String> getGroups(final Player player)
	{
		final Set<String> res = new LinkedHashSet<String>();
		for (final Group group : plugin.getGroups(player.getName()))
			res.add(group.getName());
		return res;
	}
}
