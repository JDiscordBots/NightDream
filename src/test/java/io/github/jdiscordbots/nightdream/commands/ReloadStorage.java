/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: ReloadStorage.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream.commands;

import java.util.ArrayList;
import java.util.List;

import io.github.jdiscordbots.nightdream.storage.Storage;
import net.dv8tion.jda.api.entities.Guild;

public class ReloadStorage implements Storage{
	
	private Storage forward;
	private int fullReloadCount;
	private List<Guild> guildReloads;
	
	
	
	public ReloadStorage(Storage forward) {
		this.forward=forward;
		resetReloadStats();
	}
	
	public void resetReloadStats() {
		fullReloadCount=0;
		guildReloads=new ArrayList<>();
	}
	
	public Storage getForward() {
		return forward;
	}

	@Override
	public void reload() {
		fullReloadCount++;
		forward.reload();
	}
	
	@Override
	public void reload(Guild guild) {
		guildReloads.add(guild);
		forward.reload(guild);
	}
	
	public int getFullReloadCount() {
		return fullReloadCount;
	}
	
	public List<Guild> getGuildReloads() {
		return guildReloads;
	}

	@Override
	public String read(String unit, String subUnit, String key, String defaultValue, String... defaultSubUnits) {
		return forward.read(unit, subUnit, key, defaultValue, defaultSubUnits);
	}

	@Override
	public String read(String unit, String key, String defaultValue) {
		return forward.read(unit, key, defaultValue);
	}

	@Override
	public void write(String unit, String subUnit, String key, String value, String... defaultSubUnits) {
		forward.write(unit, subUnit, key, value, defaultSubUnits);
	}

	@Override
	public void write(String unit, String key, String value) {
		forward.write(unit, key, value);
	}

	@Override
	public void remove(String unit, String subUnit, String key) {
		forward.remove(unit, subUnit, key);
	}

	@Override
	public void remove(String unit, String key) {
		forward.remove(unit, key);
	}

	@Override
	public String getForGuild(Guild guild, String key) {
		return forward.getForGuild(guild, key);
	}
	@Override
	public String getGuildDefault(String key) {
		return forward.getGuildDefault(key);
	}
	@Override
	public void setForGuild(Guild guild, String key, String value) {
		forward.setForGuild(guild, key, value);
	}
	@Override
	public void setGuildDefault(String key, String value) {
		forward.setGuildDefault(key, value);
	}
}
