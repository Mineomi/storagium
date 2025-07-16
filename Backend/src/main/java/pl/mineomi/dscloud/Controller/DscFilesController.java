package pl.mineomi.dscloud.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.mineomi.dscloud.JDA.DscFile;
import pl.mineomi.dscloud.JDA.StorageManager;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
public class DscFilesController {
    @GetMapping("/files/{guildId}")
    public CompletableFuture<List<DscFile>> getFilesInGuild(@PathVariable String guildId){
        return StorageManager.getFilesInGuild(guildId);
    }
}
