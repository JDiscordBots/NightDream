/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: LicenseTest.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream.commands;

import static io.github.jdiscordbots.jdatesting.TestUtils.getMessage;
import static io.github.jdiscordbots.jdatesting.TestUtils.hasEmbed;
import static io.github.jdiscordbots.jdatesting.TestUtils.hasEmbedField;
import static io.github.jdiscordbots.jdatesting.TestUtils.sendCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.jdiscordbots.nightdream.commands.Command.CommandType;
import net.dv8tion.jda.api.entities.Message;

public class LicenseTest {
	@Test
	public void testWithoutCommands() {
		sendCommand("license");
		Message resp=getMessage("I need a License.");
		assertNotNull(resp);
		resp.delete().queue();
	}
	@Test
	public void testAdvice() {
		sendCommand("license advice");
		Message resp=getMessage("<https://developer.github.com/v3/licenses/> states:\n>>> GitHub is a lot of things, but it’s not a law firm. As such, GitHub does not provide legal advice. Using the Licenses API or sending us an email about it does not constitute legal advice nor does it create an attorney-client relationship. If you have any questions about what you can and can't do with a particular license, you should consult with your own legal counsel before moving forward. In fact, you should always consult with your own lawyer before making any decisions that might have legal ramifications or that may impact your legal rights.\n\nGitHub created the License API to help users get information about open source licenses and the projects that use them. We hope it helps, but please keep in mind that we’re not lawyers (at least not most of us aren't) and that we make mistakes like everyone else. For that reason, GitHub provides the API on an \"as-is\" basis and makes no warranties regarding any information or licenses provided on or through it, and disclaims liability for damages resulting from using the API.");
		assertNotNull(resp);
		resp.delete().queue();
	}
	@Test
	public void testInvalidLicense() {
		sendCommand("license invalid");
		Message resp=getMessage("No such license! Use the SPDX ID.");
		assertNotNull(resp);
		resp.delete().queue();
	}
	@Test
	public void testCommonLicenseWithFields() {
		sendCommand("license GPL-3.0");
		Message resp=getMessage(msg->hasEmbed(msg, "GNU General Public License v3.0", "Permissions of this strong copyleft license are conditioned on making available complete source code of licensed works and modifications, which include larger works using a licensed work, under the same license. Copyright and license notices must be preserved. Contributors provide an express grant of patent rights."));
		assertNotNull(resp);
		assertTrue(hasEmbedField(resp, "Permissions", "commercial-use\nmodifications\ndistribution\npatent-use\nprivate-use"));
		assertTrue(hasEmbedField(resp, "Conditions", "include-copyright\ndocument-changes\ndisclose-source\nsame-license"));
		assertTrue(hasEmbedField(resp, "Limitations", "liability\nwarranty"));
		assertTrue(hasEmbedField(resp, "Common?", "Yes"));
		resp.delete().queue();
	}
	@Test
	public void testUncmmonLicenseWithEmptyFields() {
		sendCommand("license WTFPL");
		Message resp=getMessage(msg->hasEmbed(msg, "Do What The F*ck You Want To Public License", "The easiest license out there. It gives the user permissions to do whatever they want with your code."));
		assertNotNull(resp);
		assertTrue(hasEmbedField(resp, "Permissions", "commercial-use\nmodifications\ndistribution\nprivate-use"));
		assertTrue(hasEmbedField(resp, "Conditions", "<nothing>"));
		assertTrue(hasEmbedField(resp, "Limitations", "<nothing>"));
		assertTrue(hasEmbedField(resp, "Common?", "No"));
		resp.delete().queue();
	}
	@Test
	public void testHelp() {
		assertEquals("Get info about a license", new License().help());
	}
	@Test
	public void testCommandType() {
		assertSame(CommandType.UTIL, new License().getType());
	}
}
