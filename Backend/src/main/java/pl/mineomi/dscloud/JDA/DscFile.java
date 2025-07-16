package pl.mineomi.dscloud.JDA;


import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;

import java.util.Date;
import java.util.List;

@Data
@RequiredArgsConstructor
public class DscFile {
    private String id;
    private String name;
    private List<String> messageIds;
    private long size;
    private Date uploadDate;
    private String guildId;
}
