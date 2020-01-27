/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.vaulty.builder;

import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;

import java.util.Arrays;
import java.util.List;

public class ItemBuilder {

	private ItemStack is;
	private ItemMeta meta;

	public ItemBuilder(Material material) {
		is = new ItemStack(material);
		meta();
	}

	@SuppressWarnings("deprecation")
	public ItemBuilder(MaterialData materialData) {
		is = new ItemStack(materialData.getItemType(), 1, materialData.getData());
		meta();
	}

	public ItemBuilder(SkullType skullType) {
		is = new ItemStack(Material.SKULL_ITEM);
		is.setDurability((short) skullType.ordinal());
		meta();
	}

	public ItemBuilder(ItemStack from) {
		this.is = from.clone();
		meta();
	}

	public ItemBuilder(Enchantment bookEnchantment, int level) {
		is = new ItemStack(Material.ENCHANTED_BOOK);
		meta();
		addBookEnchant(bookEnchantment, level);
	}

	public ItemBuilder addBookEnchant(Enchantment bookEnchantment, int level) {
		EnchantmentStorageMeta enchantMeta = (EnchantmentStorageMeta) meta;
		enchantMeta.addStoredEnchant(bookEnchantment, level, true);
		return this;
	}

	private void meta() {
		this.meta = is.getItemMeta();
	}

	public ItemBuilder amount(int amount) {
		is.setAmount(amount);
		return this;
	}

	public ItemBuilder damage(short damage) {
		is.setDurability(damage);
		return this;
	}

	@SuppressWarnings("deprecation")
	public ItemBuilder dyeColor(DyeColor color) {
		return damage(color.getDyeData());
	}

	public ItemBuilder name(String displayName) {
		return name(displayName, true);
	}

	public ItemBuilder name(String displayName, boolean colorTransform) {
		if (colorTransform)
			displayName = ChatColor.translateAlternateColorCodes('&', displayName);
		meta.setDisplayName(displayName);
		return this;
	}

	public ItemBuilder appendLore(String... lore) {
		return appendLore(Arrays.asList(lore));
	}

	public ItemBuilder appendLore(List<String> lore) {
		List<String> lines = Lists.newArrayList();
		if (meta.hasLore()) {
			lines.addAll(meta.getLore());
		}
		for (String line : lore) {
			lines.addAll(Arrays.asList(line.split("\\n")));
		}
		meta.setLore(lines);
		return this;
	}

	public ItemBuilder appendInlineLore(String lore) {
		List<String> lines = Lists.newArrayList();
		if (meta.hasLore()) {
			lines.addAll(meta.getLore());
		}
		if (lines.isEmpty())
			lines.add(lore);
		else {
			int lastIndex = lines.size() - 1;
			lines.set(lastIndex, lines.get(lastIndex) + lore);
		}
		meta.setLore(lines);
		return this;
	}

	public ItemBuilder removeLoreLine(int line) {
		if (checkLore(line)) {
			meta.getLore().remove(line);
		}
		return this;
	}

	public ItemBuilder editLoreLine(int line, String value) {
		if (checkLore(line)) {
			List<String> lore = meta.getLore();
			lore.set(line, value);
			meta.setLore(lore);
		}
		else {
			int lines2add = meta.hasLore() ? (line + 1) - meta.getLore().size() : line + 1;
			List<String> lore = Lists.newArrayList();
			if (meta.hasLore())
				lore.addAll(meta.getLore());
			for (int i = 0; i < lines2add; i++) {
				lore.add(" ");
			}
			lore.set(line, value);
			meta.setLore(lore);
		}
		return this;
	}

	private boolean checkLore(int line) {
		return meta.hasLore() && line < meta.getLore().size();
	}

	public ItemBuilder addEnchant(Enchantment enchantment, int level, boolean unsafe) {
		if (unsafe)
			is.addUnsafeEnchantment(enchantment, level);
		else
			meta.addEnchant(enchantment, level, true);
		return this;
	}

	public ItemBuilder addFlags(ItemFlag... itemFlags) {
		meta.addItemFlags(itemFlags);
		return this;
	}

	public ItemBuilder quickEnchant() {
		return addEnchant(Enchantment.DAMAGE_ARTHROPODS, 1, false).addFlags(ItemFlag.HIDE_ENCHANTS);
	}

	public ItemBuilder makeUnbreakable() {
		return setUnbreakable(true, false);
	}

	public ItemBuilder setUnbreakable(boolean unbreakable, boolean hidden) {
		meta.spigot().setUnbreakable(unbreakable);
		if (hidden && !meta.hasItemFlag(ItemFlag.HIDE_UNBREAKABLE)) {
			meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		}
		return this;
	}

	public ItemBuilder skullOwner(String owner) {
		if (meta instanceof SkullMeta) {
			((SkullMeta) meta).setOwner(owner);
		}
		return this;
	}

	public ItemBuilder copy() {
		return new ItemBuilder(toItemStack());
	}

	public ItemStack toItemStack() {
		is.setItemMeta(meta);
		return is;
	}

}
