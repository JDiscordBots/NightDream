/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: GitHub.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream.commands;

import java.awt.Color;
import java.io.IOException;

import org.kohsuke.github.GHFileNotFoundException;
import org.kohsuke.github.GHLicense;
import org.kohsuke.github.GHPerson;
import org.kohsuke.github.GHRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.jdiscordbots.nightdream.util.BotData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@BotCommand("github")
public class GitHub implements Command {
	
	private static final Logger LOG=LoggerFactory.getLogger(GitHub.class);
	
	private org.kohsuke.github.GitHub client;

	public GitHub() throws IOException {
		String gitHubToken = BotData.getGitHubToken();
		if (gitHubToken == null || "".equals(gitHubToken)) {
			client=org.kohsuke.github.GitHub.connectAnonymously();
		} else {
			client=org.kohsuke.github.GitHub.connectUsingOAuth(gitHubToken);
		}
	}
	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		EmbedBuilder eb=new EmbedBuilder();
		eb.setColor(Color.GREEN);
		eb.setFooter("Powered by github.com (logically)","https://avatars1.githubusercontent.com/u/59704711?s=60&v=4");
		try {
			switch (args.length) {
			case 0:
				showRepository("JDiscordBots", "NightDream",eb);
				break;
			case 1:
				showPerson(args[0],eb);
				break;
			case 2:
				showRepository(args[0], args[1], eb);
				break;
			default:
				eb.setTitle("Too much of arguments.");
				eb.setDescription("Please use up to two arguments (username, reponame).");
				eb.setColor(Color.RED);
			}
		}catch(GHFileNotFoundException e) {
			eb.setTitle("This user/repository was not found.");
			eb.setDescription("You may want to try again with different arguments.");
			eb.setColor(Color.GRAY);
		}catch(IOException e) {
			LOG.error("An exception occured while executing the GitHub command.",e);
			eb.clear();
			eb.setTitle("An error occured.");
			eb.setDescription("Please try again later.");
			eb.setColor(Color.RED);
		}
		event.getChannel().sendMessage(eb.build()).queue();
	}

	private void showPerson(String userName,EmbedBuilder eb) throws IOException {
		GHPerson person = client.getUser(userName);
		if("Organization".equals(person.getType())) {
			person=client.getOrganization(userName);
		}
		showPerson(person,eb);
	}
	private void showPerson(GHPerson person,EmbedBuilder eb) throws IOException {
		eb.setTitle(person.getType()+" `"+person.getLogin()+"`");
		eb.setAuthor(person.getName(),person.getHtmlUrl().toString(),person.getAvatarUrl());
		addFieldIfNotNull(eb, "Blog", person.getBlog());
		addFieldIfNotNull(eb, "Company", person.getCompany());
		addFieldIfNotNull(eb, "E-Mail", person.getEmail());
		addFieldIfNotNull(eb, "Location", person.getLocation());
		addFieldIfNotNull(eb, "Created account", person.getCreatedAt().toString());
		eb.addField("Followers", String.valueOf(person.getFollowersCount()), true);
		eb.addField("Following",String.valueOf(person.getFollowingCount()),true);
		eb.addField("Public Repositories",String.valueOf(person.getPublicRepoCount()),true);
		eb.addField("Public Gists",String.valueOf(person.getPublicGistCount()),true);
	}
	private void showRepository(String userName,String repoName,EmbedBuilder eb) throws IOException {
		showRepository(client.getRepository(userName+"/"+repoName), eb);
	}
	private void showRepository(GHRepository repo,EmbedBuilder eb) throws IOException {
		if(repo.isPrivate()) {
			throw new GHFileNotFoundException();
		}
		eb.setAuthor(repo.getName(), repo.getHtmlUrl().toString());
		eb.setTitle(repo.getFullName());
		eb.setDescription(repo.getDescription());
		addFieldIfNotNull(eb, "Homepage", repo.getHomepage());
		addFieldIfNotNull(eb, "Language", repo.getLanguage());
		addFieldIfNotNull(eb, "Mirrored from", repo.getMirrorUrl());
		addFieldIfNotNull(eb, "Default branch", repo.getDefaultBranch());
		addFieldIfNotNull(eb, "Owner", repo.getOwnerName());
		GHLicense license = repo.getLicense();
		if(license!=null) {
			eb.addField("License", license.getName(), true);
		}
		eb.addField("Size",repo.getSize()+"KB",true);
	}
	private void addFieldIfNotNull(EmbedBuilder eb,String name,String value) {
		if(value!=null&&!"".equals(value)) {
			eb.addField(name, value, true);
		}
	}
	@Override
	public String help() {
		return "Query GitHub without actually visiting it";
	}

	@Override
	public CommandType getType() {
		return CommandType.UTIL;
	}

}
