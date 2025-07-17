package pl.mineomi.dscloud.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.lingala.zip4j.exception.ZipException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping("/download")
    public ResponseEntity<StreamingResponseBody> getTest3(@RequestBody DscFile dscFile) throws ZipException, ExecutionException, InterruptedException {
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
        DscFile dscFile = new DscFile();
        dscFile.setId("1394621066139402280");
        dscFile.setName("hackToLearn2.mp4");
        dscFile.setMessageIds(List.of("1394620977287004280", "1394621064654360657"));
        dscFile.setSize(37057257);
        dscFile.setUploadDate(new Date());
        dscFile.setGuildId("848921667833167933");


        StorageManager.deleteDscFile(dscFile);

        return "delete test";
    }
}
