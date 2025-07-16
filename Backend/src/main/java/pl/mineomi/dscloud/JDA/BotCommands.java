package pl.mineomi.dscloud.JDA;


import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import static pl.mineomi.dscloud.DscloudApplication.jda;

@Component
public class BotCommands extends ListenerAdapter {


    @SneakyThrows
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) throws IndexOutOfBoundsException {


        switch (event.getName()) {
            case "test" -> event.reply("test123").queue();
            case "showguilds" -> System.out.println(event.getJDA().getGuilds());
            case "createstorage" -> new StorageManager().setupStorageChannels(event.getGuild().getId());
        }



    }


    public static void testConnection(String id){
        Guild guild = jda.getGuildById(id);


        Category category = guild.getCategoriesByName("storage", false).get(0);

        category.getTextChannels().forEach(textChannel -> {
            if(textChannel.getName().equals("console"))
                textChannel.sendMessage("test1234").queue();
        });

    }




}