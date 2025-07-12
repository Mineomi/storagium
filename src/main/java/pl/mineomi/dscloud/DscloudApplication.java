package pl.mineomi.dscloud;

import net.dv8tion.jda.api.entities.Guild;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import pl.mineomi.dscloud.JDA.BotCommands;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class DscloudApplication {

	private static final GatewayIntent[] gatewayIntents = {GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES};

	public static JDA jda;

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(DscloudApplication.class, args);

		JDABuilder builder = JDABuilder.create(args[0], Arrays.asList(gatewayIntents))
				.addEventListeners(new BotCommands());
		builder.setStatus(OnlineStatus.ONLINE);

		builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE, CacheFlag.ACTIVITY, CacheFlag.EMOJI, CacheFlag.CLIENT_STATUS, CacheFlag.ONLINE_STATUS, CacheFlag.SCHEDULED_EVENTS, CacheFlag.STICKER);

		jda = builder.build().awaitReady();

		List<Guild> guilds = jda.getGuilds();

		for(Guild guild : guilds){
			guild.upsertCommand("test", "test").queue();
			guild.upsertCommand("showguilds", "prints guilds that his in").queue();
			guild.upsertCommand("createstorage", "Creates storage category with its channels").queue();
		}
	}

}
