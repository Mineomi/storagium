package pl.mineomi.dscloud.JDA;


import lombok.Builder;
import lombok.Data;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;

@Data
@Builder
public class DscFile {
    private String id;
    private String name;
    private List<String> messageIds;
    private long size;
    private String uploadDate;
    private String guildId;
}
