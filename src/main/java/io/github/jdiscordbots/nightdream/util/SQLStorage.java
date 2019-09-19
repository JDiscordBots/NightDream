/*
 * Copyright (c) JDiscordBots 2019
 * File: SQLStorage.java
 * Project: NightDream
 * Licensed under Boost Software License 1.0
 */

package io.github.jdiscordbots.nightdream.util;

import io.github.jdiscordbots.nightdream.logging.*;
import net.dv8tion.jda.api.entities.Guild;

import java.sql.*;

/**
 * Implementation of {@link Storage} but with sql databases
 */
public class SQLStorage implements Storage {

	private Connection connection;
	private static final NDLogger LOG = NDLogger.getLogger("storage");

	public SQLStorage() throws SQLException {
		try (Connection con = DriverManager.getConnection(BotData.getDatabaseUrl())) {
			this.connection = con;
			LOG.log(LogType.DONE, "Successfully connected to database");
		}
	}

	private Connection getConnection() {
		return connection;
	}

	@Override
	public String read(String unit, String key, String defaultValue) {
		try (PreparedStatement ps = getConnection().prepareStatement("SELECT ? FROM ?;")) {
			ps.setString(1, key);
			ps.setString(2, unit);

			try (ResultSet set = ps.executeQuery()) {
				if (set.next()) {
					return set.getString(1);
				} else if (defaultValue != null){
					try (PreparedStatement ps1 = getConnection().prepareStatement("INSERT INTO ? (key, value) VALUES (?, ?);")) {
						ps1.setString(1, unit);
						ps1.setString(2, key);
						ps1.setString(3, defaultValue);
						ps1.execute();
						return defaultValue;
					}
				} else return null;
			}
		} catch (SQLException e) {
			LOG.log(LogType.WARN, "Failed to read sql database", e);
		}
		return null;
	}

	@Override
	public void write(String unit, String key, String value) {
		if (read(unit, key, null) == null) {
			try (PreparedStatement ps = getConnection().prepareStatement("INSERT INTO ? (key, value) VALUES (?, ?);")) {
				ps.setString(1, unit);
				ps.setString(2, key);
				ps.setString(3, value);
				ps.execute();
			} catch (SQLException e) {
				LOG.log(LogType.WARN, "Failed to write sql database", e);
			}
		} else {
			try (PreparedStatement ps = getConnection().prepareStatement("UPDATE ? SET value = ? WHERE key = ?;")) {
				ps.setString(1, unit);
				ps.setString(2, value);
				ps.setString(3, key);
				ps.execute();
			} catch (SQLException e) {
				LOG.log(LogType.WARN, "Failed to write sql database", e);
			}
		}
	}

	@Override
	public void remove(String unit, String key) {
		if (read(unit, key, null) == null) return;
		try (PreparedStatement ps = getConnection().prepareStatement("DELETE FROM ? WHERE key = ?;")) {
			ps.setString(1, unit);
			ps.setString(2, key);
			ps.execute();
		} catch (SQLException e) {
			LOG.log(LogType.WARN, "Failed to delete sql data", e);
		}
	}

	@Override
	public String getGuildDefault(String key) {
		return read("guild_default", key, null);
	}

	@Override
	public void setGuildDefault(String key, String value) {
		write("guild_default", key, value);
	}

	@Override
	public String getForGuild(Guild guild, String key) {
		return read("guild_" + guild.getId(), key, null);
	}

	@Override
	public void setForGuild(Guild guild, String key, String value) {
		write("guild_" + guild.getId(), key, value);
	}
}
