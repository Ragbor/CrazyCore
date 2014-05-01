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

public class CommandLanguageExtract extends CommandExecutor
{

	public CommandLanguageExtract(final CrazyCore plugin)
	{
		super(plugin);
	}

	@Override
	@Localized({ "CRAZYCORE.COMMAND.LANGUAGE.EXTRACTED {Language}", "CRAZYPLUGIN.COMMAND.LANGUAGE.EXTRACTED.PLUGIN {Language} {Plugin}" })
	public void command(final CommandSender sender, final String[] args) throws CrazyException
	{
		if (args.length != 1)
			throw new CrazyCommandUsageException("<Language>", "<Plugin>", "*");
		final String name = args[0];
		// Extract all
		if (name.equals("*"))
		{
			for (final String loaded : CrazyLocale.getLoadedLanguages())
			{
				for (final CrazyPlugin plugin : CrazyPlugin.getCrazyPlugins())
					plugin.unpackLanguage(loaded, sender, true);
				owner.sendLocaleMessage("COMMAND.LANGUAGE.EXTRACTED", sender, loaded, sender);
			}
			return;
		}
		// Extract language
		final String language = CrazyLocale.fixLanguage(name);
		if (language != null)
		{
			for (final CrazyPlugin plugin : CrazyPlugin.getCrazyPlugins())
				plugin.unpackLanguage(language, sender, true);
			owner.sendLocaleMessage("COMMAND.LANGUAGE.EXTRACTED", sender, language);
			return;
		}
		// Extract plugin languages
		final CrazyPlugin plugin = CrazyPlugin.getPlugin(name);
		if (plugin != null)
		{
			for (final String loaded : CrazyLocale.getLoadedLanguages())
			{
				plugin.unpackLanguage(loaded, sender, true);
				plugin.sendLocaleMessage("COMMAND.LANGUAGE.EXTRACTED.PLUGIN", sender, loaded, plugin.getName());
			}
			return;
		}
		// Nothing found
		final LinkedHashSet<String> alternatives = new LinkedHashSet<String>();
		alternatives.addAll(CrazyLocale.getLoadedLanguages());
		for (final CrazyPlugin temp : CrazyPlugin.getCrazyPlugins())
			alternatives.add(temp.getName());
		throw new CrazyCommandNoSuchException("Languages/Plugins", name, alternatives);
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
