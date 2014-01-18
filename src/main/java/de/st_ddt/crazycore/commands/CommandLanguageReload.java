package de.st_ddt.crazycore.commands;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.command.CommandSender;

import de.st_ddt.crazycore.CrazyCore;
import de.st_ddt.crazyplugin.CrazyPlugin;
import de.st_ddt.crazyplugin.exceptions.CrazyCommandNoSuchException;
import de.st_ddt.crazyplugin.exceptions.CrazyCommandUsageException;
import de.st_ddt.crazyplugin.exceptions.CrazyException;
import de.st_ddt.crazyutil.locales.CrazyLocale;
import de.st_ddt.crazyutil.source.Localized;
import de.st_ddt.crazyutil.source.Permission;

public class CommandLanguageReload extends CommandExecutor
{

	public CommandLanguageReload(final CrazyCore plugin)
	{
		super(plugin);
	}

	@Override
	@Localized({ "CRAZYCORE.COMMAND.LANGUAGE.RELOADED {Language}", "CRAZYPLUGIN.COMMAND.LANGUAGE.RELOADED.PLUGIN {Language} {Plugin}" })
	public void command(final CommandSender sender, final String[] args) throws CrazyException
	{
		if (args.length != 1)
			throw new CrazyCommandUsageException("<Plugin/Language/*>");
		final String name = args[0].toLowerCase();
		if (name.equals("*"))
		{
			for (final String language : CrazyLocale.getLoadedLanguages())
			{
				for (final CrazyPlugin plugin : CrazyPlugin.getCrazyPlugins())
					plugin.loadLanguage(language, sender);
				owner.sendLocaleMessage("COMMAND.LANGUAGE.RELOADED", sender, language);
			}
			return;
		}
		if (CrazyLocale.PATTERN_LANGUAGE.matcher(name).matches())
		{
			for (final CrazyPlugin plugin : CrazyPlugin.getCrazyPlugins())
				plugin.loadLanguage(name, sender);
			owner.sendLocaleMessage("COMMAND.LANGUAGE.RELOADED", sender, name);
			return;
		}
		final CrazyPlugin plugin = CrazyPlugin.getPlugin(name);
		if (plugin == null)
		{
			final LinkedHashSet<String> alternatives = new LinkedHashSet<String>();
			alternatives.addAll(CrazyLocale.getLoadedLanguages());
			for (final CrazyPlugin temp : CrazyPlugin.getCrazyPlugins())
				alternatives.add(temp.getName());
			throw new CrazyCommandNoSuchException("Languages/Plugins", name, alternatives);
		}
		else
			for (final String language : CrazyLocale.getLoadedLanguages())
			{
				plugin.loadLanguage(language, sender);
				plugin.sendLocaleMessage("COMMAND.LANGUAGE.RELOADED.PLUGIN", sender, language, plugin.getName());
			}
	}

	@Override
	public List<String> tab(final CommandSender sender, final String[] args)
	{
		if (args.length != 1)
			return null;
		final List<String> res = new ArrayList<String>();
		final String arg = args[0];
		final Pattern pattern = Pattern.compile(arg, Pattern.CASE_INSENSITIVE);
		for (final String language : CrazyLocale.getActiveLanguages())
			if (pattern.matcher(language).find() || pattern.matcher(CrazyLocale.getSaveLanguageName(language)).find())
				res.add(language);
		for (final CrazyPlugin plugin : CrazyPlugin.getCrazyPlugins())
			if (pattern.matcher(plugin.getName()).find())
				res.add(plugin.getName());
		return res;
	}

	@Override
	@Permission("crazylanguage.advanced")
	public boolean hasAccessPermission(final CommandSender sender)
	{
		return sender.hasPermission("crazylanguage.advanced");
	}
}
