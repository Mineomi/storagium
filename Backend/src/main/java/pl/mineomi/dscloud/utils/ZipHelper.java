package pl.mineomi.dscloud.utils;

import lombok.SneakyThrows;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import pl.mineomi.dscloud.JDA.DscFile;
import pl.mineomi.dscloud.JDA.StorageManager;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class ZipHelper {
    @SneakyThrows
    public static void sendFiles(String filePath, String guildId) {

        List<File> filesToAdd = List.of(
                new File(filePath)
        );

        Path path = Paths.get(filePath);
        String fileName = path.getFileName().toString();
        long fileSize = Files.size(path);

        File guildDirectory = new File("test2/" + guildId + "/" + fileName);
        if(!guildDirectory.exists())
            guildDirectory.mkdirs();

        ZipFile zipFile = new ZipFile("test2/" + guildId + "/" + fileName + "/" + fileName + ".zip");
        zipFile.createSplitZipFile(filesToAdd, new ZipParameters(), true, 10 * 1024 * 1024); //Spliting zip file in 10MB parts



        StorageManager.saveFilesInChannel(fileName, guildId, fileSize);
    }


    public static Path downloadFile(DscFile dscFile) throws ZipException, ExecutionException, InterruptedException {
        //Downloading parts of zip file
        StorageManager.downloadAttachmentsFromList(dscFile);
            //Extracting file from part zip files
            String usedDirectory = "test2/" + dscFile.getGuildId() + "/" + dscFile.getName() + "/";
            String partFileName = usedDirectory + "/" + dscFile.getName() + ".zip";
            String mergedZipFileName = usedDirectory + "/" + dscFile.getName() + "0.zip";

            new ZipFile(partFileName).mergeSplitFiles(new File(mergedZipFileName));
            new ZipFile(new File(mergedZipFileName)).extractFile(dscFile.getName(), usedDirectory);


            return Paths.get(usedDirectory +"/"+dscFile.getName() );



    }

}
