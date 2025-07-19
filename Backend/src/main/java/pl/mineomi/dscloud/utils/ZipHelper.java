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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class ZipHelper {
    @SneakyThrows
    public static void sendFiles(Path path, String guildId, String uploadId) {

        List<File> fileToAdd = List.of(
                new File(path.toString())
        );

        String fileName = path.getFileName().toString();
        long fileSize = Files.size(path);

        String uploadToDscDir = "test2/uploads/" + guildId + "/" + uploadId;

        File guildDirectory = new File(uploadToDscDir);
        if(!guildDirectory.exists())
            guildDirectory.mkdirs();


        ZipFile zipFile = new ZipFile(uploadToDscDir + "/" + fileName + ".zip");
        zipFile.createSplitZipFile(fileToAdd, new ZipParameters(), true, 10 * 1024 * 1024); //Spliting zip file in 10MB parts

        //Delete temp files
        Files.delete(path);
        Files.delete(path.getParent());

        StorageManager.saveFilesInChannel(fileName, guildId, fileSize, uploadId);
    }


    public static Path downloadFile(DscFile dscFile) throws ZipException, ExecutionException, InterruptedException {
        //Downloading parts of zip file
        int filesNumber = StorageManager.downloadAttachmentsFromList(dscFile);

        //Extracting file from part zip files
        String usedDirectory = "test2/" + dscFile.getGuildId() + "/" + dscFile.getName() + "/";
        String partFileName = usedDirectory + "/" + dscFile.getName() + ".zip";

        if(filesNumber > 1){

            String mergedZipFileName = usedDirectory + "/" + dscFile.getName() + "0.zip";

            new ZipFile(partFileName).mergeSplitFiles(new File(mergedZipFileName));
            new ZipFile(new File(mergedZipFileName)).extractAll(usedDirectory + "/" + "unzippedFile");
        }
        else
            new ZipFile(new File(partFileName)).extractAll(usedDirectory + "/" + "unzippedFile");

        String unzippedFileName = Arrays.stream(new File(usedDirectory  + "/" + "unzippedFile").listFiles()).toList().get(0).getName();

        return Paths.get(usedDirectory +"/unzippedFile/" +unzippedFileName );



    }

}
