package pl.mineomi.dscloud.JDA;


import lombok.Data;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;

@Data
public class File {
    private String type;
    private String title;
    private Message.Attachment icon;
    private List<Long> ids;
    private String size;
    private String date;
}
