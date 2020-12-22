package tempoexport.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tempoexport.service.TempoExportService;

@RestController
public class TempoExportController {
    @Autowired
    private TempoExportService tempoExportService;

    @GetMapping("tempoData")
    public void getTempoData() {
        tempoExportService.tempoData();
    }

    @GetMapping("tempoCloudAccounts")
    public void getTempoCloudAccounts() {
        tempoExportService.tempoCloudAccounts();
    }

}