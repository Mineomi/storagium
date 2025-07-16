package pl.mineomi.dscloud.Controller;

import net.lingala.zip4j.exception.ZipException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import pl.mineomi.dscloud.JDA.DscFile;
import pl.mineomi.dscloud.JDA.StorageManager;
import pl.mineomi.dscloud.utils.ZipHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.Date;
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

    @GetMapping("/test")
    public String getTest5(){
        ZipHelper.sendFiles("test2/test.jpg", "848921667833167933");

        return "small test file";
    }

    @GetMapping("/download")
    public ResponseEntity<StreamingResponseBody> getTest3() throws ZipException, MalformedURLException, ExecutionException, InterruptedException {
        DscFile dscFile = DscFile.builder()
                .name("hackToLearn2.mp4")
                .messageIds(List.of("1394620977287004280", "1394621064654360657"))
                .size(37057257)
                .uploadDate(new Date())
                .guildId("848921667833167933").build();

        Path filePath = ZipHelper.downloadFile(dscFile);

        File file = filePath.toFile();

        if(!file.exists())
            return ResponseEntity.notFound().build();

        StreamingResponseBody stream = outputStream -> {
            try(InputStream inputStream = new FileInputStream(file)){
                inputStream.transferTo(outputStream);
                outputStream.flush();
            }finally {
                StorageManager.deleteDownloadTemporaryFiles(dscFile);
            }
        };

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .body(stream);
    }

    @GetMapping("/delete")
    public String getTest4() {
        DscFile dscFile = DscFile.builder()
                .id("1394621066139402280")
                .name("hackToLearn2.mp4")
                .messageIds(List.of("1394620977287004280", "1394621064654360657"))
                .size(37057257)
                .uploadDate(new Date())
                .guildId("848921667833167933").build();

        StorageManager.deleteDscFile(dscFile);

        return "delete test";
    }
}
