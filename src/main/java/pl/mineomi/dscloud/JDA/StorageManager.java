package pl.mineomi.dscloud.JDA;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.internal.managers.channel.concrete.CategoryManagerImpl;
import pl.mineomi.dscloud.DscloudApplication;

import java.io.File;
import java.util.*;

public class StorageManager {
    final static int MAX_FILES_IN_MESSAGE = 2;
    public static Map<String, StorageManager> storageManagerMap = new HashMap<>();


    String categoryName = "storage"; //Must be in lowercase
    private static CategoryManagerImpl categoryManager;
    private TextChannel console;
    private TextChannel metaContent;
    private TextChannel content;
    private int uploadStatus = 0;
    private int downloadStatus = 0;



    public void setupStorageChannels(String guildId) {
        //If it doesn't have, create storage category
        Guild guild = DscloudApplication.jda.getGuildById(guildId);

        if (!checkIfHasStorage(guild, categoryName)) {
            guild.createCategory(categoryName).complete();
        }

        categoryManager = new CategoryManagerImpl(guild.getCategoriesByName(categoryName, false).get(0));

        if (!checkIfCategoryHasChannel("console"))
            categoryManager.getChannel().createTextChannel("console").queue();
        if (!checkIfCategoryHasChannel("metacontent"))
            categoryManager.getChannel().createTextChannel("metacontent").queue();
        if (!checkIfCategoryHasChannel("content"))
            categoryManager.getChannel().createTextChannel("content").queue();

        console = guild.getTextChannelsByName("console", false).get(0);
        metaContent = guild.getTextChannelsByName("metacontent", false).get(0);
        content = guild.getTextChannelsByName("content", false).get(0);

        storageManagerMap.put(guildId, this);
    }

    private boolean checkIfHasStorage(Guild guild, String categoryName) {
        if (!guild.getCategories().isEmpty()) {
            for (int i = 0; i < guild.getCategories().size(); i++) {
                if (guild.getCategories().get(i).getName().equals(categoryName)) {
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




    public void downloadAttachmentsFromList(List<String> ids) {

    }

    private static StorageManager getStorageManagerByGuildId(String guildId){
        if(!storageManagerMap.containsKey(guildId))
            new StorageManager().setupStorageChannels(guildId);

        return storageManagerMap.get(guildId);
    }

    public static void saveFilesInChannel(String fileName, String guildId, long fileSize) {
        StorageManager storageManager = getStorageManagerByGuildId(guildId);

        File dir = new File("test2/" + guildId + "/" + fileName);

        //Get files in directory
        List<File> files = Arrays.stream(dir.listFiles()).toList();
        List<List<FileUpload>> groupedFiles = new ArrayList<>();

        //Convert File list to FileUpload list
        List<FileUpload> fileUploads = new ArrayList<>();
        for(File file : files){
            fileUploads.add(FileUpload.fromData(file));
        }



        //Split files into value of maxFilesInMessage size lists
        int filesInMessage = 0;
        int groupedFilesIndex = 0;
        groupedFiles.add(new ArrayList<>());
        for(FileUpload file : fileUploads){
            if(filesInMessage >= StorageManager.MAX_FILES_IN_MESSAGE){
                groupedFiles.add(new ArrayList<>());
                groupedFilesIndex++;
                filesInMessage = 0;
            }

            groupedFiles.get(groupedFilesIndex).add(file);
            filesInMessage++;
                
            
        }

        //Send messages with files attached
        int i = 0;
        List<String> messageIds = new ArrayList<>();
        for(List<FileUpload> list : groupedFiles){
            storageManager.uploadStatus = i/groupedFiles.size();
            messageIds.add(storageManager.content.sendMessage("files")
                    .addFiles(list)
                    .complete().getId());
            i++;
        }

        //Create DscFile
        DscFile dscFile = DscFile.builder()
                .name(fileName)
                .size(fileSize)
                .messageIds(messageIds)
                .uploadDate(new Date().toString())
                .build();


        //Send DscFile to suitable discord channel
        storageManager.metaContent.sendMessage(dscFile.getName() +"\n" + dscFile.getMessageIds() + "\n" + dscFile.getSize() + "\n" + dscFile.getUploadDate()).queue();

        //Delete temp directory
        dir.delete();

        storageManager.console.sendMessage("Files transfer compeleted").queue();

    }
}
