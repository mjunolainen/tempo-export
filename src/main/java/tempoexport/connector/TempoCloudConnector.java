package tempoexport.connector;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import tempoexport.dto.WorkLogDto;

@Slf4j
@Component
public class TempoCloudConnector {


  @Autowired
  private RestTemplate restTemplate;

  @Value("${tempo.cloud.url}")
  private String tempoCloudUrl;

  @Value("${tempo.token}")
  private String token;

  public WorkLogDto getWorklogs() {
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBearerAuth(token);
      HttpEntity httpEntity = new HttpEntity<>(null, headers);
      ResponseEntity<WorkLogDto> usage = restTemplate.exchange(tempoCloudUrl + "/worklogs", HttpMethod.GET, httpEntity, WorkLogDto.class);
      return usage.getBody();
    }
    catch (HttpStatusCodeException sce) {
      log.error("Status Code exception {}", sce);
      throw new RuntimeException("Status code exception ", sce);
    }
  }



}
