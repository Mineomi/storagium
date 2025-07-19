package pl.mineomi.dscloud.JDA;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.internal.managers.channel.concrete.CategoryManagerImpl;
import pl.mineomi.dscloud.DscloudApplication;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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

    public static void renameDscFile(DscFile dscFile, String newName) throws ExecutionException, InterruptedException, IOException {
        StorageManager storageManager = getStorageManagerByGuildId(dscFile.getGuildId());

        Message message = storageManager.metaContent.retrieveMessageById(dscFile.getId()).complete();

        String renameId = UUID.randomUUID().toString();


        File file = new File("test2/" + dscFile.getGuildId() + "/" + renameId + "/" + message.getAttachments().get(0));
        file.getParentFile().mkdirs();
        message.getAttachments().get(0).getProxy().downloadToFile(file).get();

        ObjectMapper mapper = new ObjectMapper();
        DscFile newDscFile = mapper.readValue(file, DscFile.class);
        newDscFile.setName(newName);

        message.delete().queue();
        file.delete();

        File newJsonFile = new File("test2/" + dscFile.getGuildId() + "/" + renameId + "/" + newName + ".json");
        mapper.writeValue(newJsonFile, newDscFile);



        FileUpload metaFile = FileUpload.fromData(newJsonFile);
        storageManager.metaContent.sendMessage("")
                .addFiles(metaFile)
                .complete();

        deleteFolder(newJsonFile.getParentFile());
    }


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




    public static int downloadAttachmentsFromList(DscFile dscFile) throws ExecutionException, InterruptedException {
        StorageManager storageManager = getStorageManagerByGuildId(dscFile.getGuildId());
        int filesNumber = 0;

        File dir = new File("test2/" + dscFile.getGuildId() + "/" + dscFile.getName());
        dir.mkdirs();

        for(String id : dscFile.getMessageIds()){
            Message message = storageManager.content.retrieveMessageById(id).complete();
            for (Message.Attachment attachment : message.getAttachments()){
                File file = new File("test2/" + dscFile.getGuildId() + "/" + dscFile.getName() + "/" + dscFile.getName() + "." + getFileExtension(attachment.getFileName()));
                attachment.getProxy().downloadToFile(file).get();
                filesNumber++;
            }
        }

        return filesNumber;
    }

    private static StorageManager getStorageManagerByGuildId(String guildId){
        if(!storageManagerMap.containsKey(guildId))
            new StorageManager().setupStorageChannels(guildId);

        return storageManagerMap.get(guildId);
    }

    public static void saveFilesInChannel(String fileName, String guildId, long fileSize, String uploadId) throws IOException {
        StorageManager storageManager = getStorageManagerByGuildId(guildId);

        File dir = new File("test2/uploads/" + guildId + "/" + uploadId);

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
            messageIds.add(storageManager.content.sendMessage("")
                    .addFiles(list)
                    .complete().getId());
            i++;
        }

        //Create DscFile
        DscFile dscFile = new DscFile();
        dscFile.setName(fileName);
        dscFile.setSize(fileSize);
        dscFile.setMessageIds(messageIds);
        dscFile.setUploadDate(new Date());
        dscFile.setGuildId(guildId);



        //Map DscFile to json file
        File jsonFile = new File(dir + "/" + fileName + ".json");
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(jsonFile, dscFile);


        //Send json DscFile to suitable discord channel
        FileUpload metaFile = FileUpload.fromData(jsonFile);
        storageManager.metaContent.sendMessage("")
                .addFiles(metaFile)
                        .complete();

        //Delete temp directory
        deleteFolder(dir);
    }

    private static void deleteFolder(File folder) {
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteFolder(file);
                }
            }
        }
        folder.delete();
    }

    public static void deleteDownloadTemporaryFiles(DscFile dscFile){
        deleteFolder(new File("test2/" + dscFile.getGuildId() + "/" + dscFile.getName()));
    }

    public static void deleteDscFile(DscFile dscFile) {
        StorageManager storageManager = getStorageManagerByGuildId(dscFile.getGuildId());

        if(dscFile.getMessageIds().size() < 2)
            storageManager.content.deleteMessageById(dscFile.getMessageIds().get(0)).queue();
        else
            storageManager.content.deleteMessagesByIds(dscFile.getMessageIds()).queue();

        storageManager.metaContent.deleteMessageById(dscFile.getId()).queue();
    }


    public static CompletableFuture<List<DscFile>> getFilesInGuild(String guildId) {
        StorageManager storageManager = getStorageManagerByGuildId(guildId);
        ObjectMapper mapper = new ObjectMapper();

        List<CompletableFuture<DscFile>> futures = new ArrayList<>();
        CompletableFuture<List<DscFile>> resultFuture = new CompletableFuture<>();

        storageManager.metaContent.getIterableHistory().forEachAsync(message -> {
            CompletableFuture<DscFile> fileFuture = message.getAttachments().get(0).getProxy().download().thenApply(inputStream -> {
                try {
                    DscFile dscFile = mapper.readValue(inputStream, DscFile.class);
                    dscFile.setId(message.getId());
                    return dscFile;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            futures.add(fileFuture);
            return true;
        }).thenRun(() ->{
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenApply(v -> futures.stream().map(future -> future.join()).toList())
                    .thenAccept(list -> resultFuture.complete(list))
                    .exceptionally(ex -> {
                        resultFuture.completeExceptionally(ex);
                        return null;
                    });
        });

        return resultFuture;
    }

    public static String getFileExtension(String name) {
        int lastDot = name.lastIndexOf('.');
        return (lastDot == -1) ? "" : name.substring(lastDot + 1);
    }
}
