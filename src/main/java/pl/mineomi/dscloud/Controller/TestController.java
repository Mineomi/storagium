package pl.mineomi.dscloud.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.mineomi.dscloud.JDA.BotCommands;
import pl.mineomi.dscloud.utils.ZipHelper;

@RestController
public class TestController {

    @GetMapping("/")
    public String getTest(){
        new ZipHelper().sendFiles("test2/essenn.mp4");

//        BotCommands.testConnection("848921667833167933"); //guild id
        return "test123";
    }
}
