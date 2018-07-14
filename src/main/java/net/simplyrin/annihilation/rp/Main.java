package net.simplyrin.annihilation.rp;

import java.time.OffsetDateTime;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.entities.DiscordBuild;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.simplyrin.annihilation.rp.utils.ChatColor;

/**
 * Created by SimplyRin on 2018/07/14.
 *
 * Copyright (C) 2018 SimplyRin
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
@Mod(modid = "AnnihilationRP", version = "1.0")
public class Main {

	private Minecraft mc;

	private boolean isShotbow;
	private String currentMap;

	private IPCClient ipcClient;

	@EventHandler
	public void init(FMLInitializationEvent event) {
		this.mc = Minecraft.getMinecraft();

		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onChat(ClientChatReceivedEvent event) {
		String message = ChatColor.stripColor(event.message.getFormattedText());

		if(message.contains("Welcome to the Shotbow XP System")) {
			this.isShotbow = true;
			this.connect();
			return;
		}

		if(!this.isShotbow) {
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
			ScoreObjective scoreObjective = this.mc.theWorld.getScoreboard().func_96539_a(1);
			this.currentMap = ChatColor.stripColor(scoreObjective.getDisplayName()).replace("Map: ", "");
			this.connect();
			return;
		}
	}

	@SubscribeEvent
    public void onLoin(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
		this.isShotbow = false;
		this.disconnect();
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

	public String toUpperCase(String message) {
		return message.toUpperCase().substring(0, 1) + message.toLowerCase().substring(1);
	}

	public void sendMessage(String message) {
		this.mc.thePlayer.addChatMessage(new ChatComponentText(message));
	}

}
