package com.hortina.api.web;

import com.hortina.api.service.TaskSchedulerService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/_admin/scheduler")
public class SchedulerController {

    private final TaskSchedulerService scheduler;

    public SchedulerController(TaskSchedulerService scheduler) {
        this.scheduler = scheduler;
    }

    @PostMapping("/run")
    public String runNow() {
        scheduler.runOnceNowForTesting();
        return "ok";
    }
}
