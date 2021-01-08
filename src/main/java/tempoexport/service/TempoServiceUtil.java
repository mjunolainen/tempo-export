package tempoexport.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tempoexport.connector.TempoServerConnector;
import tempoexport.dto.server.user.JiraServerUserResultsDto;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class TempoServiceUtil {
    @Autowired
    private TempoServerConnector tempoServerConnector;
    Map<String, JiraServerUserResultsDto> jiraUserServerMap = null;

    public String jiraServerUserKey(String cloudDisplayName) {
        String serverUserKey = null;
        if (jiraServerUserMap().containsKey(cloudDisplayName)) {
            serverUserKey = jiraServerUserMap().get(cloudDisplayName).getKey();
        }
        return serverUserKey;
    }

    public String jiraUserName(String cloudDisplayName) {
        String serverUserName = null;
        if (jiraServerUserMap().containsKey(cloudDisplayName)) {
            serverUserName = jiraServerUserMap().get(cloudDisplayName).getName();
        }
        return serverUserName;
    }

    public Map<String, JiraServerUserResultsDto> jiraServerUserMap() {
        if (jiraUserServerMap == null) {
            JiraServerUserResultsDto[] dto = tempoServerConnector.getJiraServerUsers();
            Map<String, JiraServerUserResultsDto> paramMap = new HashMap<>();

            if (dto.length > 0) {
                for (JiraServerUserResultsDto userKeyDto : dto) {
                    paramMap.put(userKeyDto.getDisplayName(), userKeyDto);
                }
            }
            jiraUserServerMap = paramMap;
            return jiraUserServerMap;
        } else {
            return jiraUserServerMap;
        }
    }

    public String jiraServerUserEmail(String cloudDisplayName) {
        String serverUserEmail = null;
        if (jiraServerUserMap().containsKey(cloudDisplayName)) {
            serverUserEmail = jiraServerUserMap().get(cloudDisplayName).getEmailAddress();
        }
        return serverUserEmail;
    }
}
