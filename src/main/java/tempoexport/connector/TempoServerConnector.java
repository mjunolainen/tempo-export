package tempoexport.connector;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import tempoexport.dto.server.account.ServerAccountDto;
import tempoexport.dto.server.account.ServerAccountInsertResponseDto;
import tempoexport.dto.server.account.ServerAccountLinksDto;
import tempoexport.dto.server.account.TempoServerAccountDto;
import tempoexport.dto.server.team.TempoServerTeamDto;
import tempoexport.dto.server.user.JiraServerUserResultsDto;

@Slf4j
@Component
public class TempoServerConnector {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${tempo.server.url}")
    private String tempoServerUrl;

    @Value("${tempo.usernameServer}")
    private String usernameServer;

    @Value("${tempo.passwordServer}")
    private String passwordServer;

    @Value("${jira.server.max.users}")
    private Integer jiraServerMaxUsers;


    public TempoServerTeamDto[] getTempoServerTeams() {
        try {
            ResponseEntity<TempoServerTeamDto[]> usage = restTemplate.exchange(tempoServerUrl + "/rest/tempo-teams/2/team", HttpMethod.GET, getEntity(), TempoServerTeamDto[].class);
            return usage.getBody();
        } catch (HttpStatusCodeException sce) {
            log.error("Status Code exception {}", sce);
            throw new RuntimeException("Status code exception ", sce);
        }
    }

    public TempoServerAccountDto[] getTempoServerAccounts() {
        try {
            ResponseEntity<TempoServerAccountDto[]> usage = restTemplate.exchange(tempoServerUrl + "/rest/tempo-accounts/1/account", HttpMethod.GET, getEntity(), TempoServerAccountDto[].class);
            return usage.getBody();
        } catch (HttpStatusCodeException sce) {
            log.error("Status Code exception {}", sce);
            throw new RuntimeException("Status code exception ", sce);
        }
    }

    public void deleteTempoServerAccounts(Integer accountId) {
        try {
            restTemplate.exchange(tempoServerUrl + "/rest/tempo-accounts/1/account/{id}", HttpMethod.DELETE, getEntity(), void.class, accountId);
        } catch (HttpStatusCodeException sce) {
            throw new RuntimeException("Status code exception ", sce);
        }
    }

    public ServerAccountInsertResponseDto insertAccount(ServerAccountDto insertAccount) {
        try {
            ResponseEntity<ServerAccountInsertResponseDto> usage = restTemplate.exchange(tempoServerUrl + "/rest/tempo-accounts/1/account", HttpMethod.POST, getEntityAccount(insertAccount), ServerAccountInsertResponseDto.class);
            return usage.getBody();
        } catch (HttpStatusCodeException sce) {
            log.error("Status Code exception {}", sce);
            throw new RuntimeException("Status code exception ", sce);
        }
    }

    public JiraServerUserResultsDto[] getJiraServerUsers() {
        try {
            ResponseEntity<JiraServerUserResultsDto[]> usage = restTemplate.exchange(tempoServerUrl + "/rest/api/2/user/search?username=.&amp;startAt=0&maxResults=" + jiraServerMaxUsers, HttpMethod.GET, getEntity(), JiraServerUserResultsDto[].class);
            return usage.getBody();
        } catch (HttpStatusCodeException sce) {
            log.error("Status Code exception {}", sce);
            throw new RuntimeException("Status code exception ", sce);
        }
    }

    private HttpEntity getEntityAccount(ServerAccountDto account) {
        HttpHeaders headers = getHeaders();
        HttpEntity httpEntity = new HttpEntity(account, headers);
        return httpEntity;
    }

    private HttpEntity getEntity() {
        HttpHeaders headers = getHeaders();
        HttpEntity httpEntity = new HttpEntity(null, headers);
        return httpEntity;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(usernameServer, passwordServer);
        return headers;
    }

    public ServerAccountLinksDto insertLinks(ServerAccountLinksDto insertLinksDto) {
        try {
            ResponseEntity<ServerAccountLinksDto> usage = restTemplate.exchange(tempoServerUrl + "/rest/tempo-accounts/1/link", HttpMethod.POST, getEntityLinks(insertLinksDto), ServerAccountLinksDto.class);
            return usage.getBody();
        } catch (HttpStatusCodeException sce) {
            log.error("Status Code exception {}", sce);
            throw new RuntimeException("Status code exception ", sce);
        }
    }

    private HttpEntity getEntityLinks(ServerAccountLinksDto insertLinksDto) {
        HttpHeaders headers = getHeaders();
        HttpEntity httpEntity = new HttpEntity(insertLinksDto, headers);
        return httpEntity;
    }
}