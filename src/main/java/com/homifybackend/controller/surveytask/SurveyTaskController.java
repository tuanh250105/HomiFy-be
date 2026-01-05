package com.homifybackend.controller.surveytask;

import com.homifybackend.dto.survey.ClaimAllSurveyRequest;
import com.homifybackend.dto.survey.ClaimSurveyRequest;
import com.homifybackend.dto.survey.MySurveyTaskDto;
import com.homifybackend.dto.survey.OpportunityPoolDto;
import com.homifybackend.dto.survey.ScheduleSurveyRequest;
import com.homifybackend.dto.survey.UpdateSurveyNoteRequest;
import com.homifybackend.service.agentSurveyTask.SurveyTaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class SurveyTaskController {

    private final SurveyTaskService surveyTaskService;

    public SurveyTaskController(SurveyTaskService surveyTaskService) {
        this.surveyTaskService = surveyTaskService;
    }

    // ✅ BE filter district (FE chỉ gửi param)
    @GetMapping("/opportunity-pool")
    public List<OpportunityPoolDto> getOpportunityPool(
            @RequestParam(required = false) String district
    ) {
        return surveyTaskService.getOpportunityPool(district);
    }

    @GetMapping("/survey-tasks")
    public List<MySurveyTaskDto> getMyTasks(@RequestParam Long agentId) {
        return surveyTaskService.getMyTasks(agentId);
    }

    @PostMapping("/survey-tasks/claim")
    public void claimSurvey(@RequestBody ClaimSurveyRequest req) {
        surveyTaskService.claimSurvey(req.sellRequestId(), req.agentId());
    }

    @PostMapping("/survey-tasks/claim-all")
    public int claimAll(@RequestBody ClaimAllSurveyRequest req) {
        return surveyTaskService.claimAll(req.agentId());
    }

    @PostMapping("/survey-tasks/schedule")
    public void schedule(@RequestBody ScheduleSurveyRequest req) {
        surveyTaskService.schedule(req.taskId(), req.scheduledAt());
    }

    @PatchMapping("/survey-tasks/{taskId}/note")
    public void updateNote(
            @PathVariable Long taskId,
            @RequestBody UpdateSurveyNoteRequest req
    ) {
        surveyTaskService.updateNote(taskId, req.note());
    }
}
