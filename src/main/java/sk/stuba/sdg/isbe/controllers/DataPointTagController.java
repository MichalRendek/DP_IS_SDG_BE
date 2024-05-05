package sk.stuba.sdg.isbe.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sk.stuba.sdg.isbe.domain.model.DataPointTag;
import sk.stuba.sdg.isbe.domain.model.StoredData;
import sk.stuba.sdg.isbe.services.DataPointTagService;
import sk.stuba.sdg.isbe.utilities.StoredDataMergeResponse;

import java.util.List;

@RestController
@RequestMapping("api/datapoint/datapointtag")
public class DataPointTagController {

    @Autowired
    private DataPointTagService dataPointTagService;

    @GetMapping
    public List<DataPointTag> getDataPointTags() {return dataPointTagService.getDataPointTags();}

    @Operation(summary = "Add new Data point tag into the system")
    @PostMapping("/create")
    public DataPointTag createDataPointTag(@Valid @RequestBody DataPointTag dataPointTag) {
        return dataPointTagService.createDataPointTag(dataPointTag);
    }

    @Operation(summary = "Get Data point tag by uid")
    @GetMapping("/getDataPointTagById/{dataPointTagId}")
    public DataPointTag getDataPointTagById(@PathVariable String dataPointTagId) {
        return dataPointTagService.getDataPointTagById(dataPointTagId);
    }

    @Operation(summary = "Get all stored data from data point")
    @GetMapping("/getStoredData/{dataPointTagId}")
    public List<StoredData> getStoredData(@PathVariable String dataPointTagId) {
        return dataPointTagService.getStoredData(dataPointTagId);
    }

    @GetMapping("/getStoredDataFromTags/{startTime}/{endTime}/{cadence}/{method}")
    public List<StoredDataMergeResponse> getStoredDataFromTags(@RequestParam List<String> dataPointTagIds, @PathVariable Long startTime, @PathVariable Long endTime, @PathVariable Long cadence, @PathVariable int method) {
        return dataPointTagService.getStoredDataForTags(dataPointTagIds, startTime, endTime, cadence, method);
    }

    @GetMapping("/downloadStoredDataAsCsv/{dataPointTagId}")
    public ResponseEntity<byte[]> downloadStoredDataAsCsv(@PathVariable String dataPointTagId) {
        List<StoredData> storedDataList = dataPointTagService.getStoredData(dataPointTagId);

        // Assuming you have a utility method to convert your data list to CSV format
        String csvData = convertToCsv(storedDataList);
        byte[] csvBytes = csvData.getBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", "storedData.csv");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return new ResponseEntity<>(csvBytes, headers, HttpStatus.OK);
    }

    // Utility method to convert data list to CSV format
    // You need to implement this method based on your data structure
    private String convertToCsv(List<StoredData> storedDataList) {
        StringBuilder csvBuilder = new StringBuilder();

        // Add data rows
        for (StoredData data : storedDataList) {
            csvBuilder.append(escapeCsvField(data.getUid())).append(",");
            csvBuilder.append(escapeCsvField(data.getDataPointTagId())).append(",");
            csvBuilder.append(data.getValue()).append(",");
            csvBuilder.append(data.getMeasureAt()).append(",");
            csvBuilder.append(data.getMeasureAtDevice()).append(",");
            csvBuilder.append(data.isDeactivated()).append(",");
            csvBuilder.append(escapeCsvField(data.getDeviceId())).append(",");
            csvBuilder.append(escapeCsvField(data.getTag())).append("\n");
        }
        return csvBuilder.toString();
    }

    // Utility method to escape CSV field if necessary
    private static String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }
        // If the field contains a comma, quote it
        if (field.contains(",")) {
            return "\"" + field + "\"";
        }
        return field;
    }

    @Operation(summary = "Get all stored data from data point in time range and cadence")
    @GetMapping("/getStoredDataByTime/{dataPointTagId}/{startTime}/{endTime}/{cadence}/{method}")
    public List<StoredData> getStoredDataByTime(@PathVariable String dataPointTagId,@PathVariable Long startTime, @PathVariable Long endTime, @PathVariable Long cadence, @PathVariable int method) {
        return dataPointTagService.getStoredDataByTime(dataPointTagId, startTime, endTime, cadence, method);
    }

    @Operation(summary = "Update Data point tag")
    @PostMapping("/updateDataPointTag/{dataPointTagId}")
    public DataPointTag updateDataPointTag(@PathVariable String dataPointTagId, @Valid @RequestBody DataPointTag changeDataPointTag) {
        return dataPointTagService.updateDataPointTag(dataPointTagId, changeDataPointTag);
    }

    @Operation(summary = "Delete Data point tag")
    @DeleteMapping("deleteDataPointTag/{dataPointTagId}")
    public DataPointTag deleteDataPointTag(@PathVariable String dataPointTagId) {
        return dataPointTagService.deleteDataPointTag(dataPointTagId);
    }
}
