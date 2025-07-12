package pl.mineomi.dscloud.JDA;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.internal.managers.channel.concrete.CategoryManagerImpl;

import java.util.List;

public class StorageManager {
    String categoryName = "storage"; //Must be in lowercase
    private static CategoryManagerImpl categoryManager;
//    private TextChannel console;
//    private TextChannel metacontent;
//    private TextChannel content;


    public void setStorageChannels(SlashCommandInteractionEvent event) {
        //If it doesn't have, create storage category
        if (!checkIfHasStorage(event, categoryName)) {
            event.getGuild().createCategory(categoryName).complete();
        }

        categoryManager = new CategoryManagerImpl(event.getGuild().getCategoriesByName(categoryName, false).get(0));

        if (!checkIfCategoryHasChannel("console"))
            categoryManager.getChannel().createTextChannel("console").queue();
        if (!checkIfCategoryHasChannel("metacontent"))
            categoryManager.getChannel().createTextChannel("metacontent").queue();
        if (!checkIfCategoryHasChannel("content"))
            categoryManager.getChannel().createTextChannel("content").queue();

//        console = event.getGuild().getTextChannelsByName("console", false).get(0);
//        metacontent = event.getGuild().getTextChannelsByName("metacontent", false).get(0);
//        content = event.getGuild().getTextChannelsByName("content", false).get(0);
    }

    private boolean checkIfHasStorage(SlashCommandInteractionEvent event, String categoryName) {
        if (!event.getGuild().getCategories().isEmpty()) {
            for (int i = 0; i < event.getGuild().getCategories().size(); i++) {
                if (event.getGuild().getCategories().get(i).getName().equals(categoryName)) {
                    return true;
                }

            }
        }
        return false;
    }
    private boolean checkIfCategoryHasChannel(String channelName) {
        for (int i = 0; i < categoryManager.getChannel().getTextChannels().size(); i++) {
            if (categoryManager.getChannel().getTextChannels().get(i).getName().equals(channelName)) {
                return true;
            }
        }
        return false;
    }


    public void downloadAttachmetsFromList(List<String> ids) {

    }
}
