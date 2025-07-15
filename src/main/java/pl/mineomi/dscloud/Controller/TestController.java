package pl.mineomi.dscloud.Controller;

import jakarta.annotation.Resource;
import net.lingala.zip4j.exception.ZipException;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import pl.mineomi.dscloud.JDA.BotCommands;
import pl.mineomi.dscloud.JDA.DscFile;
import pl.mineomi.dscloud.JDA.StorageManager;
import pl.mineomi.dscloud.utils.ZipHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class TestController {

    @GetMapping("/")
    public String getTest(){
        ZipHelper.sendFiles("test2/essenn.mp4", "848921667833167933");

        return "essen";
    }

    @GetMapping("/hack")
    public String getTest2(){
        ZipHelper.sendFiles("test2/hackToLearn2.mp4", "848921667833167933");

        return "hackToLearnMp4";
    }

    @GetMapping("/download")
    public ResponseEntity<StreamingResponseBody> getTest3() throws ZipException, MalformedURLException, ExecutionException, InterruptedException {
        DscFile dscFile = DscFile.builder()
                .name("hackToLearn2.mp4")
                .messageIds(new ArrayList<>(List.of("1394422640923508909", "1394422701698842818")))
                .size(37057257)
                .uploadDate("Mon Jul 14 22:57:59 CEST 2025").build();
        String guildId = "848921667833167933";

        Path filePath = ZipHelper.downloadFile(dscFile, guildId);

        File file = filePath.toFile();

        if(!file.exists())
            return ResponseEntity.notFound().build();

        StreamingResponseBody stream = outputStream -> {
            try(InputStream inputStream = new FileInputStream(file)){
                inputStream.transferTo(outputStream);
                outputStream.flush();
            }finally {
                StorageManager.deleteDownloadTemporaryFiles(guildId, dscFile.getName());
            }
        };

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .body(stream);
    }
}
