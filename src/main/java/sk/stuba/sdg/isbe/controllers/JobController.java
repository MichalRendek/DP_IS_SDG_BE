package sk.stuba.sdg.isbe.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sk.stuba.sdg.isbe.domain.model.Job;
import sk.stuba.sdg.isbe.services.JobService;

import java.util.List;

@RestController
@RequestMapping("api/jobs/job")
public class JobController {

    @Autowired
    private JobService jobService;

    @Operation(summary = "Run a job created from a recipe with n repetitions")
    @PostMapping("runJobFromRecipe/{recipeId}/{deviceId}/{repetitions}/{scheduledDays}/{scheduledHour}/{scheduledMinute}")
    public Job createJobFromRecipe(@PathVariable String recipeId, @PathVariable String deviceId, @PathVariable int repetitions,
                                   @PathVariable List<Integer> scheduledDays, @PathVariable Integer scheduledHour, @PathVariable Integer scheduledMinute) {
        return jobService.runJobFromRecipe(recipeId, deviceId, repetitions, scheduledDays, scheduledHour, scheduledMinute);
    }

    @Operation(summary = "Run a job on given device with n repetitions")
    @PostMapping("runJob/{deviceId}/{repetitions}/{scheduledDays}/{scheduledHour}/{scheduledMinute}")
    public Job runJob(@Valid @RequestBody Job job, @PathVariable String deviceId, @PathVariable int repetitions,
                      @PathVariable List<Integer> scheduledDays, @PathVariable Integer scheduledHour, @PathVariable Integer scheduledMinute) {
        return jobService.runJob(job, deviceId, repetitions, scheduledDays, scheduledHour, scheduledMinute);
    }

    @Operation(summary = "Reset a job's status")
    @PostMapping("resetJob/{jobId}")
    public Job resetJob(@PathVariable String jobId) {
        return jobService.resetJob(jobId);
    }

    @Operation(summary = "Get job by its ID")
    @GetMapping("getJobById/{jobId}")
    public Job getJobById(@PathVariable String jobId) {
        return jobService.getJobById(jobId);
    }

    @Operation(summary = "Get jobs by name")
    @GetMapping("getJobsByName/{name}")
    public List<Job> getJobByName(@PathVariable String name) {
        return jobService.getJobsByName(name);
    }

    @Operation(summary = "Skip cycle in a job")
    @PutMapping("skipCycle/{jobId}")
    public Job skipCycle(@PathVariable String jobId) {
        return jobService.skipCycle(jobId);
    }

    @Operation(summary = "Skip step in a job")
    @PutMapping("skipStep/{jobId}")
    public Job skipStep(@PathVariable String jobId) {
        return jobService.skipStep(jobId);
    }

    @Operation(summary = "Pause a job")
    @PutMapping("pauseJob/{jobId}")
    public Job pauseJob(@PathVariable String jobId) {
        return jobService.pauseJob(jobId);
    }

    @Operation(summary = "Cancel a job")
    @PutMapping("cancelJob/{jobId}")
    public Job cancelJob(@PathVariable String jobId) {
        return jobService.cancelJob(jobId);
    }

    @Operation(summary = "Continue job when its paused")
    @PutMapping("continueJob/{jobId}")
    public Job continueJob(@PathVariable String jobId) {
        return jobService.continueJob(jobId);
    }

    @Operation(summary = "Get job's current status")
    @GetMapping("getJobStatus/{jobId}")
    public String getJobStatus(@PathVariable String jobId) {
        return jobService.getJobStatus(jobId);
    }

    @Operation(summary = "Get all jobs")
    @GetMapping("getAllJobs")
    public List<Job> getAllJobs() {
        return jobService.getAllJobs();
    }

    @Operation(summary = "Get all jobs on a device optionally with sorting")
    @GetMapping("getAllJobsOnDevice/{deviceId}/{sortBy}/{sortDirection}")
    public List<Job> getAllJobsOnDevice(@PathVariable String deviceId,
                                        @PathVariable @Parameter(description = "Unsorted -> NONE or NULL") String sortBy,
                                        @PathVariable @Parameter(description = "Unsorted -> NONE or NULL") String sortDirection) {
        return jobService.getAllJobsOnDevice(deviceId, sortBy, sortDirection);
    }

    @Operation(summary = "Get all jobs on a device with pagination optionally with sorting")
    @GetMapping("getAllJobsOnDevicePageable/{deviceId}/{page}/{pageSize}/{sortBy}/{sortDirection}")
    public List<Job> getAllJobsOnDevicePageable(@PathVariable String deviceId, @PathVariable int page, @PathVariable int pageSize,
                                                @PathVariable @Parameter(description = "Unsorted -> NONE or NULL") String sortBy,
                                                @PathVariable @Parameter(description = "Unsorted -> NONE or NULL") String sortDirection) {
        return jobService.getAllJobsOnDevicePageable(deviceId, page, pageSize, sortBy, sortDirection);
    }

    @Operation(summary = "Get finished jobs on a device optionally with sorting")
    @GetMapping("getFinishedJobs/{deviceId}/{sortBy}/{sortDirection}")
    public List<Job> getFinishedJobsByStatus(@PathVariable String deviceId,
                                             @PathVariable @Parameter(description = "Unsorted -> NONE or NULL") String sortBy,
                                             @PathVariable @Parameter(description = "Unsorted -> NONE or NULL") String sortDirection) {
        return jobService.getFinishedJobsByStatus(deviceId, sortBy, sortDirection);
    }

    @Operation(summary = "Get running jobs on a device optionally with sorting")
    @GetMapping("getRunningJobs/{deviceId}/{sortBy}/{sortDirection}")
    public List<Job> getRunningJobsByStatus(@PathVariable String deviceId,
                                            @PathVariable @Parameter(description = "Unsorted -> NONE or NULL") String sortBy,
                                            @PathVariable @Parameter(description = "Unsorted -> NONE or NULL") String sortDirection) {
        return jobService.getRunningJobsByStatus(deviceId, sortBy, sortDirection);
    }

    @Operation(summary = "Get all jobs by a given status optionally with sorting")
    @GetMapping("getJobsByStatus/{deviceId}/{status}/{sortBy}/{sortDirection}")
    public List<Job> getJobsByStatus(@PathVariable String deviceId, @PathVariable String status,
                                     @PathVariable @Parameter(description = "Unsorted -> NONE or NULL") String sortBy,
                                     @PathVariable @Parameter(description = "Unsorted -> NONE or NULL") String sortDirection) {
        return jobService.getAllJobsByStatus(deviceId, status, sortBy, sortDirection);
    }

    @Operation(summary = "Get all jobs by a given status with pagination optionally with sorting")
    @GetMapping("getAllJobsByStatusPageable/{deviceId}/{status}/{page}/{pageSize}/{sortBy}/{sortDirection}")
    public List<Job> getAllJobsByStatusPageable(@PathVariable String deviceId, @PathVariable String status, @PathVariable int page, @PathVariable int pageSize,
                                                @PathVariable @Parameter(description = "Unsorted -> NONE or NULL") String sortBy,
                                                @PathVariable @Parameter(description = "Unsorted -> NONE or NULL") String sortDirection) {
        return jobService.getAllJobsByStatusPageable(deviceId, status, page, pageSize, sortBy, sortDirection);
    }
}
