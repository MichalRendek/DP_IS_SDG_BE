package sk.stuba.sdg.isbe.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sk.stuba.sdg.isbe.domain.model.StoredData;
import sk.stuba.sdg.isbe.services.StoredDataService;

import java.util.List;

@RestController
@RequestMapping("api/datapoint/datapointsave")
public class StoredDataController {
    @Autowired
    private StoredDataService storedDataService;

    @GetMapping
    public List<StoredData> getStoredDatas() {return storedDataService.getStoredDatas();}

    @Operation(summary = "Add new StoredData into the system")
    @PostMapping("/create")
    public StoredData createStoredData(@Valid @RequestBody StoredData storedData) {
        return storedDataService.createStoredData(storedData);
    }

    @Operation(summary = "Get StoredData by uid")
    @GetMapping("/getStoredDataById/{storedDataId}")
    public StoredData getStoredDataById(@PathVariable String storedDataId) {
        return storedDataService.getStoredDataById(storedDataId);
    }

    @Operation(summary = "Update StoredData")
    @PostMapping("/updateStoredData/{storedDataId}")
    public StoredData updateStoredData(@PathVariable String storedDataId, @Valid @RequestBody StoredData changeStoredData) {
        return storedDataService.updateStoredData(storedDataId, changeStoredData);
    }

    @Operation(summary = "Delete StoredData")
    @DeleteMapping("deleteStoredData/{storedDataId}")
    public StoredData deleteStoredData(@PathVariable String storedDataId) {
        return storedDataService.deleteStoredData(storedDataId);
    }
}
