package net.simplyrin.annihilation.rp;

import java.time.OffsetDateTime;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.entities.DiscordBuild;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;

import net.simplyrin.annihilation.rp.utils.ThreadPool;

public class Test {

	private static RichPresence.Builder presence;
	private static IPCClient ipcClient;

	public static void main(String[] args) {
		ThreadPool.run(() -> {
			while(true) {
				try {
					Thread.sleep(Integer.MAX_VALUE);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});

		IPCClient ipcClient = new IPCClient(467357544281669642L);
		try {
			ipcClient.connect(new DiscordBuild[0]);
		} catch (NoDiscordClientException e) {
			System.out.println("You don't have Discord Client!");
			System.exit(0);
			return;
		}
		RichPresence.Builder presence = new RichPresence.Builder();
		presence.setDetails("Map: Coastal");
		presence.setStartTimestamp(OffsetDateTime.now());
		presence.setLargeImage("shotbow", "play.shotbow.net");
		ipcClient.sendRichPresence(presence.build());
	}

}
