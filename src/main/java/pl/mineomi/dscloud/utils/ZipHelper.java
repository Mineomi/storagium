package pl.mineomi.dscloud.utils;

import lombok.SneakyThrows;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import pl.mineomi.dscloud.JDA.DscFile;
import pl.mineomi.dscloud.JDA.StorageManager;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;


public class ZipHelper {
    @SneakyThrows
    public void sendFiles(String filePath, String guildId) {

        List<File> filesToAdd = List.of(
                new File(filePath)
        );

        String fileName = Paths.get(filePath).getFileName().toString();


        File guildDirectory = new File("test2/" + guildId + "/" + fileName);
        if(!guildDirectory.exists())
            guildDirectory.mkdirs();

        ZipFile zipFile = new ZipFile("test2/" + guildId + "/" + fileName + "/" + fileName + ".zip");
        zipFile.createSplitZipFile(filesToAdd, new ZipParameters(), true, 10 * 1024 * 1024); //Spliting zip file in 10MB parts



        StorageManager.saveFilesInChannel(fileName, guildId);
    }


    public File downloadFile(DscFile dscFile) {
        //Namierzenie plików na kanale

//        new StorageManager().downloadAttachmentsFromList(dscFile.getIds());


        //Pobranie ich
        //Połączenie w jeden plik
        //Zwrócenie pliku do pobrania

        throw new UnsupportedOperationException("To be implemented");
    }

}
