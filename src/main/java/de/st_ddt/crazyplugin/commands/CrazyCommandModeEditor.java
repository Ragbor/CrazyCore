package de.st_ddt.crazyplugin.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;

import de.st_ddt.crazyplugin.exceptions.CrazyCommandException;
import de.st_ddt.crazyplugin.exceptions.CrazyCommandNoSuchException;
import de.st_ddt.crazyplugin.exceptions.CrazyCommandPermissionException;
import de.st_ddt.crazyplugin.exceptions.CrazyCommandUsageException;
import de.st_ddt.crazyplugin.exceptions.CrazyException;
import de.st_ddt.crazyutil.ChatHeaderProvider;
import de.st_ddt.crazyutil.ChatHelperExtended;
import de.st_ddt.crazyutil.modes.Mode;

public class CrazyCommandModeEditor<S extends ChatHeaderProvider> extends CrazyCommandExecutor<S>
{

	protected final Map<String, Mode<?>> modes = new TreeMap<String, Mode<?>>();

	public CrazyCommandModeEditor(final S plugin)
	{
		super(plugin);
	}

	@Override
	public void command(final CommandSender sender, final String[] args) throws CrazyException
	{
		if (args.length == 0)
			throw new CrazyCommandNoSuchException("Mode", "(none)", modes.keySet());
		final String name = args[0].toLowerCase();
		if (name.contains("*"))
		{
			if (args.length != 1)
				throw new CrazyCommandUsageException("<*>", "<Mode*>", "<Mode> [NewValue]");
			final Pattern pattern = Pattern.compile(StringUtils.replace(name, "*", ".*"));
			for (final Entry<String, Mode<?>> temp : modes.entrySet())
				if (pattern.matcher(temp.getKey()).matches())
					show(sender, temp.getValue());
			return;
		}
		final Mode<?> mode = modes.get(name);
		if (mode == null)
		{
			final TreeSet<String> alternatives = new TreeSet<String>();
			final Pattern pattern = Pattern.compile(".*" + name + ".*");
			for (final Entry<String, Mode<?>> temp : modes.entrySet())
				if (hasAccessPermission(sender, temp.getValue()))
					if (pattern.matcher(temp.getKey()).matches())
						alternatives.add(temp.getKey());
			throw new CrazyCommandNoSuchException("Mode", args[0], alternatives);
		}
		else if (hasAccessPermission(sender, mode))
			if (args.length == 1)
				show(sender, mode);
			else
				try
				{
					update(sender, mode, ChatHelperExtended.shiftArray(args, 1));
				}
				catch (final CrazyCommandException e)
				{
					e.addCommandPrefix(args[0]);
					throw e;
				}
		else
			throw new CrazyCommandPermissionException();
	}

	@Override
	public List<String> tab(final CommandSender sender, final String[] args)
	{
		final List<String> res = new ArrayList<String>();
		if (args.length == 1)
		{
			final String last = args[args.length - 1].toLowerCase();
			for (final Entry<String, Mode<?>> mode : modes.entrySet())
				if (hasAccessPermission(sender, mode.getValue()))
					if (mode.getKey().startsWith(last))
						res.add(mode.getKey());
		}
		else
		{
			final Mode<?> mode = modes.get(args[0].toLowerCase());
			if (mode != null)
				if (hasAccessPermission(sender, mode))
				{
					final List<String> temp = mode.tab(ChatHelperExtended.shiftArray(args, 1));
					if (temp != null)
						res.addAll(temp);
				}
		}
		return res;
	}

	public void addMode(final Mode<?> mode)
	{
		modes.put(mode.getName().toLowerCase(), mode);
	}

	public void show(final CommandSender sender, final Mode<?> mode)
	{
		mode.showValue(sender);
	}

	public void update(final CommandSender sender, final Mode<?> mode, final String[] args) throws CrazyException
	{
		mode.setValue(sender, ChatHelperExtended.shiftArray(args, 1));
	}

	public boolean hasAccessPermission(final CommandSender sender, final Mode<?> mode)
	{
		return true;
	}
}
