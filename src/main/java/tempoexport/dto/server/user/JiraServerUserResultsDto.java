package tempoexport.dto.server.user;

import lombok.Data;

@Data
public class JiraServerUserResultsDto {
    String self;
    String key;
    String name;
    String emailAddress;
    String displayName;
    boolean active;
    boolean deleted;
}