package pl.mineomi.dscloud.utils;

import lombok.SneakyThrows;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;

import java.io.File;
import java.util.List;


public class ZipHelper {
    @SneakyThrows
    public void sendFiles(String filePath) {

        List<File> filesToAdd = List.of(
                new File(filePath)
        );

        ZipFile zipFile = new ZipFile("test2/file.zip");
        zipFile.createSplitZipFile(filesToAdd, new ZipParameters(), true, 10485764);


    }




}
