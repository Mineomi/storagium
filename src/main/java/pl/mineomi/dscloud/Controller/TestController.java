package pl.mineomi.dscloud.Controller;

import jakarta.annotation.Resource;
import net.lingala.zip4j.exception.ZipException;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.mineomi.dscloud.JDA.BotCommands;
import pl.mineomi.dscloud.JDA.DscFile;
import pl.mineomi.dscloud.utils.ZipHelper;

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
    public ResponseEntity<UrlResource> getTest3() throws ZipException, MalformedURLException, ExecutionException, InterruptedException {
        Path filePath = ZipHelper.downloadFile(DscFile.builder()
                .name("hackToLearn2.mp4")
                .messageIds(new ArrayList<>(List.of("1394422640923508909", "1394422701698842818")))
                .size(37057257)
                .uploadDate("Mon Jul 14 22:57:59 CEST 2025").build(),
                "848921667833167933");

        UrlResource resource = new UrlResource(filePath.toUri());

        if(!resource.exists())
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
