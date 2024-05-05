package sk.stuba.sdg.isbe.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sk.stuba.sdg.isbe.domain.model.JobStatus;
import sk.stuba.sdg.isbe.services.JobStatusService;

@RestController
@RequestMapping("api/jobs/status")
public class JobStatusController {

    @Autowired
    private JobStatusService jobStatusService;

    @Operation(summary = "Create JobStatus")
    @PostMapping("/create")
    public JobStatus createJobStatus(@Valid @RequestBody JobStatus jobStatus) {
        return jobStatusService.createJobStatus(jobStatus);
    }

    @Operation(summary = "Get JobStatus by id")
    @GetMapping("/getJobStatus/{jobStatusId}")
    public JobStatus getJobStatus(@PathVariable String jobStatusId) {
        return jobStatusService.getJobStatus(jobStatusId);
    }

    @Operation(summary = "Update JobStatus")
    @PostMapping("/updateJobStatus/{jobStatusId}")
    public JobStatus updateJobStatus(@PathVariable String jobStatusId, @Valid @RequestBody JobStatus changeJobStatus) {
        return jobStatusService.updateJobStatus(jobStatusId, changeJobStatus, null, null);
    }

}
