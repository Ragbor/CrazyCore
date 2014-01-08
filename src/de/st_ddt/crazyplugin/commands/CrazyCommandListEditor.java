package de.st_ddt.crazyplugin.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import de.st_ddt.crazyplugin.exceptions.CrazyCommandException;
import de.st_ddt.crazyplugin.exceptions.CrazyCommandParameterException;
import de.st_ddt.crazyplugin.exceptions.CrazyException;
import de.st_ddt.crazyutil.ChatHeaderProvider;
import de.st_ddt.crazyutil.ChatHelperExtended;
import de.st_ddt.crazyutil.locales.CrazyLocale;

public abstract class CrazyCommandListEditor<S extends ChatHeaderProvider, T> extends CrazyCommandCollectionEditor<S, T>
{

	public CrazyCommandListEditor(final S chatHeaderProvider)
	{
		this(chatHeaderProvider, true, true, true);
	}

	public CrazyCommandListEditor(final S owner, final boolean add, final boolean insert, final boolean remove)
	{
		super(owner, add, remove);
		if (insert)
			addSubCommand(new CrazyCommandListInsert(owner), "insert");
	}

	@Override
	protected abstract List<T> getCollection();

	// @ // Localized("PATH {Element} {Index}")
	protected abstract String addViaIndexLocale();

	// @ // Localized("PATH {Element} {Index}")
	protected abstract String removeViaIndexLocale();

	private class CrazyCommandListInsert extends CrazyCommandExecutor<S>
	{

		protected CrazyCommandListInsert(final S plugin)
		{
			super(plugin);
		}

		@Override
		public void command(final CommandSender sender, final String[] args) throws CrazyException
		{
			try
			{
				final int index = Math.min(Integer.parseInt(args[0]), getCollection().size());
				if (index < 0)
					throw new CrazyCommandParameterException(0, "positive Number (Integer)");
				try
				{
					final T elem = getEntry(sender, ChatHelperExtended.shiftArray(args, 1));
					getCollection().add(index, elem);
					saveChanges();
					CrazyLocale.getLocaleHead().getLanguageEntry(addViaIndexLocale()).sendMessage(sender, elem, index);
				}
				catch (final CrazyCommandException e)
				{
					e.addCommandPrefix(args[0]);
					throw e;
				}
			}
			catch (final NumberFormatException e)
			{
				throw new CrazyCommandParameterException(0, "Number (Integer)");
			}
		}
	}

	@Override
	protected void commandRemove(final CommandSender sender, final String[] args) throws CrazyException
	{
		if (args.length == 1)
			try
			{
				final int index = Integer.parseInt(args[0]);
				final T elem = getCollection().remove(index);
				saveChanges();
				CrazyLocale.getLocaleHead().getLanguageEntry(removeViaIndexLocale()).sendMessage(sender, elem, index);
				return;
			}
			catch (final Exception e)
			{}
		final T elem = getEntry(sender, args);
		getCollection().remove(elem);
		saveChanges();
		CrazyLocale.getLocaleHead().getLanguageEntry(removeLocale()).sendMessage(sender, elem);
	}
}
