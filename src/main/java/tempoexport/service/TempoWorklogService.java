package tempoexport.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tempoexport.connector.TempoCloudConnector;
import tempoexport.connector.TempoServerConnector;
import tempoexport.dto.cloud.worklog.CloudWorklogDto;
import tempoexport.dto.cloud.worklog.CloudWorklogsListDto;
import tempoexport.dto.server.worklog.*;

import javax.xml.stream.events.EndDocument;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Slf4j
@Service
public class TempoWorklogService {
    @Autowired
    private TempoCloudConnector tempoCloudConnector;
    @Autowired
    private TempoServerConnector tempoServerConnector;
    @Autowired
    private TempoServiceUtil tempoServiceUtil;

    @Value("${jira.cloud.get.worklogs.count}")
    private Integer cloudWorklogsCount;

    public void migrateTempoWorklogs() {
    }

    public void deleteTempoServerWorklogs() {
        Integer worklogCount = 0;
        Integer deletedWorklogs = 0;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);

        TempoServerWorklogRequestDto worklogRequestDates = new TempoServerWorklogRequestDto();
        log.info("From: {}", worklogRequestDates.getFrom());
        log.info("To: {}", worklogRequestDates.getTo());

        while (LocalDate.parse(worklogRequestDates.getFrom(), formatter).isBefore(LocalDate.now())) {
            TempoServerReturnWorklogDto[] serverWorklogs = tempoServerConnector.getTempoServerWorklogs(worklogRequestDates);
            log.info("Period worklog count: {}", serverWorklogs.length);
            worklogCount = worklogCount + serverWorklogs.length;
            for (TempoServerReturnWorklogDto serverWorklog : serverWorklogs) {
                log.info("Worklog id: {}", serverWorklog.getTempoWorklogId().toString());
                boolean deletedWorklog = tempoServerConnector.deleteTempoServerWorklog(serverWorklog.getTempoWorklogId());
                if (deletedWorklog == true) {
                    log.info("Worklog {} deleted", serverWorklog.getTempoWorklogId());
                    worklogCount--;
                    deletedWorklogs++;
                }
            }

            LocalDate currentDateTo = LocalDate.parse(worklogRequestDates.getTo(), formatter);
            String nextDateFrom = currentDateTo.toString();
            String nextDateTo = currentDateTo.plusMonths(1).toString();

            worklogRequestDates.setFrom(nextDateFrom);
            worklogRequestDates.setTo(nextDateTo);

            log.info("From: {}", worklogRequestDates.getFrom());
            log.info("To: {}", worklogRequestDates.getTo());
        }
        log.info("Worklogs deleted: {}", deletedWorklogs);
        log.info("Total worklogs left in server: {}", worklogCount);
        log.info("Total 403 errors: {}", tempoServerConnector.serverWorklogDeletionErrorCounter403);
        log.info("Total 500 errors: {}", tempoServerConnector.serverWorklogDeletionErrorCounter500);

        tempoServerConnector.serverWorklogDeletionErrorCounter500 = 0;
        tempoServerConnector.serverWorklogDeletionErrorCounter403 = 0;
    }

    public void migrateWorklogs() {
        LocalTime timeStart = LocalTime.now();
        Integer worklogCountFromCloud = 0;
        CloudWorklogsListDto cloudWorklogsListDto = tempoCloudConnector.getTempoCloudWorklogs();

        while (cloudWorklogsListDto.getCloudWorklogsMetaDataDto().getNext() != null) {
            worklogCountFromCloud = worklogCountFromCloud + cloudWorklogsListDto.getCloudWorklogsMetaDataDto().getCount();

            for (CloudWorklogDto cloudWorklogDto : cloudWorklogsListDto.getResults()) {
                ServerWorklogDto serverWorklogDto = new ServerWorklogDto();

                serverWorklogDto.setBillableSeconds(cloudWorklogDto.getBillableSeconds());
                serverWorklogDto.setComment(cloudWorklogDto.getDescription());
                if (serverWorklogDto.getComment() == null || serverWorklogDto.getComment().isEmpty()) {
                    serverWorklogDto.setComment(".");
                }
                serverWorklogDto.setStarted(cloudWorklogDto.getStartDate());
                serverWorklogDto.setTimeSpentSeconds(cloudWorklogDto.getTimeSpentSeconds());
                serverWorklogDto.setOriginTaskId(cloudWorklogDto.getCloudWorklogIssueDto().getKey());
                serverWorklogDto.setWorker(tempoServiceUtil.getJiraServerUserKey(cloudWorklogDto.getCloudWorklogAuthorDto().getDisplayName()));
                log.info(serverWorklogDto.toString());

                if (serverWorklogDto.getWorker() != null) {
                    boolean tempoServerWorklog = tempoServerConnector.insertTempoServerWorklog(serverWorklogDto);
                    if (tempoServerWorklog == true) {
                        log.info("Worklog for task {} created", serverWorklogDto.getOriginTaskId());
                    }
                } else {
                    tempoServerConnector.serverWorklogUsersWithoutName++;
                    log.info("Worker not existing: unable to create worklog for issue {}", cloudWorklogDto.getCloudWorklogIssueDto().getKey());
                }
            }
            cloudWorklogsListDto = tempoCloudConnector.getNextTempoCloudWorklogs(cloudWorklogsListDto.getCloudWorklogsMetaDataDto().getNext());
            log.info("Worklogs from cloud: {}", worklogCountFromCloud);

        }
        LocalTime timeEnd = LocalTime.now();

        log.info("Start time: {}", timeStart);
        log.info("End time: {}", timeEnd);
        log.info("Total server worklogs created: {}", tempoServerConnector.worklogsCreated);
        log.info("Total invalid users: {}", tempoServerConnector.serverWorklogUsersWithoutName);
        log.info("Total 400 errors: {}", tempoServerConnector.serverWorklogInsertionErrorCounter400);
        log.info("Total 403 errors: {}", tempoServerConnector.serverWorklogInsertionErrorCounter403);
        log.info("Total 500 errors: {}", tempoServerConnector.serverWorklogInsertionErrorCounter500);
    }
}