package pl.mineomi.dscloud.JDA;


import lombok.Data;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;

@Data
public class DscFile {
    private String name;
    private Message.Attachment icon;
    private List<String> ids;
    private String size;
    private String uploadDate;
}
