package tempoexport.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import javax.xml.transform.Result;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "metadata",
        "results"
})

public class TempoCloudAccountDto {

    @JsonProperty("metadata")
    CloudMetaDataDto metaData;
    String self;

    @JsonProperty("results")
    List<CloudResultsDto> results = null;
    @JsonIgnore
    Map<String, Object> resultsProperties = new HashMap<String, Object>();
}