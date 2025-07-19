package pl.mineomi.dscloud.Controller;

import net.lingala.zip4j.exception.ZipException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import pl.mineomi.dscloud.JDA.DscFile;
import pl.mineomi.dscloud.JDA.StorageManager;
import pl.mineomi.dscloud.utils.ZipHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
public class DscFilesController {
    @GetMapping("/files/{guildId}")
    public CompletableFuture<List<DscFile>> getFilesInGuild(@PathVariable String guildId){
        return StorageManager.getFilesInGuild(guildId);
    }

    @PostMapping("/download")
    public ResponseEntity<StreamingResponseBody> downloadDscFile(@RequestBody DscFile dscFile) throws ZipException, ExecutionException, InterruptedException {
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

    @PostMapping("/upload/{guildId}")
    public ResponseEntity<String> handleFileUpload(@PathVariable String guildId, @RequestParam("file")MultipartFile file) {
        if(file.isEmpty())
            return ResponseEntity.badRequest().body("Empty file");

        //Id to create unique temporary folder names
        String uploadId = UUID.randomUUID().toString();

        try{
            Path path = Paths.get("test2/tempFiles/" + guildId + "/" + uploadId + "/" + file.getOriginalFilename());

            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());

            ZipHelper.sendFiles(path, guildId, uploadId);

            return ResponseEntity.ok("File saved:" + file.getOriginalFilename());
        }catch (IOException e){
            return ResponseEntity.status(500).body("Error while saving file");
        }
    }

    @DeleteMapping("/file")
    public ResponseEntity<String> deleteDscFile(@RequestBody DscFile dscFile) {

        StorageManager.deleteDscFile(dscFile);

        return ResponseEntity.ok("File deleted: " + dscFile.getName());
    }

    @PutMapping("file/{newName}")
    public ResponseEntity<String> renameDscFile(@RequestBody DscFile dscFile, @PathVariable String newName) throws IOException, ExecutionException, InterruptedException {

        StorageManager.renameDscFile(dscFile, newName);

        return ResponseEntity.ok("File " + dscFile.getName() + " renamed to: " + newName);
    }
}
