package pl.mineomi.dscloud.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.mineomi.dscloud.JDA.BotCommands;

@RestController
public class TestController {

    @GetMapping("/")
    public String getTest(){
        BotCommands.testConnection("848921667833167933"); //guild id
        return "test123";
    }
}
