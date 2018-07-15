package net.simplyrin.annihilation.rp;

import java.time.OffsetDateTime;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.entities.DiscordBuild;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.simplyrin.annihilation.rp.commands.CommandAnniRP;

/**
 * Created by SimplyRin on 2018/07/14.
 *
 * Copyright (c) 2018 SimplyRin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
@Mod(modid = "AnnihilationRP", version = "1.0")
public class Main {

	private Minecraft mc;

	private boolean isShotbow;
	private boolean isTeamDetectMode = false;

	private String currentMap;
	private String currentTeam;

	private IPCClient ipcClient;

	@EventHandler
	public void init(FMLInitializationEvent event) {
		this.mc = Minecraft.getMinecraft();

		MinecraftForge.EVENT_BUS.register(this);
		ClientCommandHandler.instance.registerCommand(new CommandAnniRP(this));
	}

	// @SubscribeEvent
	public void onClientConnectedToServerEvent(FMLNetworkEvent.ClientConnectedToServerEvent event) {
		this.isShotbow = this.mc.getCurrentServerData().serverIP.contains("shotbow.net");
	}

	@SubscribeEvent
	public void onChat(ClientChatReceivedEvent event) {
		String message = ChatColor.stripColor(event.getMessage().getFormattedText());

		if(message.contains("Welcome to the Shotbow XP System")) {
			this.currentMap = null;

			this.disconnect();
			this.isShotbow = true;
			this.connect();
			return;
		}

		if(!this.isShotbow) {
			return;
		}

		if(message.contains("You have joined the")) {
			this.isTeamDetectMode = true;
			return;
		}

		if(this.isTeamDetectMode) {
			this.isTeamDetectMode = false;

			if(message.contains("Red team")) {
				this.currentTeam = "Red";
			}

			if(message.contains("Yellow team")) {
				this.currentTeam = "Yellow";
			}

			if(message.contains("Green team")) {
				this.currentTeam = "Green";
			}

			if(message.contains("Blue team")) {
				this.currentTeam = "Blue";
			}

			this.disconnect();
			this.connect();
			return;
		}

		if(message.endsWith(" selected, loading...")) {
			this.disconnect();
			this.currentMap = this.toUpperCase(message.split(" selected, loading...")[0]);
			this.connect();
			return;
		}

		if(message.contains("Welcome to Annihilation") || message.contains("You disconnected mid-game, you have been teleported back to your spawn.")) {
			this.disconnect();
			ScoreObjective scoreObjective = this.mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(1);
			this.currentMap = ChatColor.stripColor(scoreObjective.getDisplayName()).replace("Map: ", "");
			this.connect();
			return;
		}
	}

	public void connect() {
		this.ipcClient = new IPCClient(467357544281669642L);
		try {
			this.ipcClient.connect(new DiscordBuild[0]);
		} catch (NoDiscordClientException e) {
			System.out.println("You don't have Discord Client!");
			System.exit(0);
			return;
		}
		RichPresence.Builder presence = new RichPresence.Builder();
		if(this.currentMap != null) {
			presence.setDetails("Map: " + this.currentMap);
		} else {
			presence.setDetails("Lobby");
		}
		if(this.currentTeam != null) {
			presence.setState("Team: " + this.currentTeam);
		} else {
			presence.setState(null);
		}

		presence.setStartTimestamp(OffsetDateTime.now());
		presence.setLargeImage("shotbow", "play.shotbow.net");
		this.ipcClient.sendRichPresence(presence.build());
	}

	public void disconnect() {
		if(this.ipcClient != null) {
			this.ipcClient.close();
			this.ipcClient = null;
		}
	}

	public String detectTeam() {
		NetworkPlayerInfo networkPlayerInfo = this.mc.getConnection().getPlayerInfo(this.mc.thePlayer.getUniqueID());
		if(networkPlayerInfo == null) {
			return "Unknown";
		}

		ITextComponent displayName = networkPlayerInfo.getDisplayName();
		if(displayName == null) {
			return "Unknown";
		}

		String unformattedText = displayName.getUnformattedText();
		if(unformattedText == null) {
			return "Unknown";
		}

		this.sendMessage(unformattedText + ", " + displayName.toString());
		return "Unknown";
	}

	public String toUpperCase(String message) {
		return message.toUpperCase().substring(0, 1) + message.toLowerCase().substring(1);
	}

	public void sendMessage(String message) {
		this.mc.thePlayer.addChatMessage(new TextComponentString(message));
	}

}
