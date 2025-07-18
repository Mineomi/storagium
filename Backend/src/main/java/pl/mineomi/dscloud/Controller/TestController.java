package pl.mineomi.dscloud.Controller;

import org.springframework.web.bind.annotation.*;
import pl.mineomi.dscloud.JDA.DscFile;
import pl.mineomi.dscloud.JDA.StorageManager;
import pl.mineomi.dscloud.utils.ZipHelper;


import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
public class TestController {

    @GetMapping("/")
    public String getTest(){
        ZipHelper.sendFiles(Paths.get("test2/essenn.mp4"), "848921667833167933", UUID.randomUUID().toString());

        return "essen";
    }

    @GetMapping("/hack")
    public String getTest2(){
        ZipHelper.sendFiles(Paths.get("test2/hackToLearn2.mp4"), "848921667833167933", UUID.randomUUID().toString());

        return "hackToLearnMp4";
    }

    @GetMapping("/test")
    public String getTest5(){
        ZipHelper.sendFiles(Paths.get("test2/test.jpg"), "848921667833167933", UUID.randomUUID().toString());

        return "small test file";
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
