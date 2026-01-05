package com.homifybackend.controller.ManagePersonalinfoController;

import org.springframework.web.bind.annotation.*;

import com.homifybackend.service.ManagePersonalinfoService.ManagePersonalInfoService;
import com.homifybackend.dto.ManagePersonalInfoDTO;

@RestController
@RequestMapping("/api/manage-personalinfo")
@CrossOrigin
public class ManagePersonalInfoController {

    private final ManagePersonalInfoService service;

    public ManagePersonalInfoController(ManagePersonalInfoService service) {
        this.service = service;
    }

    @GetMapping
    public ManagePersonalInfoDTO getProfile(@RequestParam Long userId) {
        return service.getProfile(userId);
    }

    @PutMapping
    public ManagePersonalInfoDTO updateProfile(@RequestParam Long userId,
                                               @RequestBody ManagePersonalInfoDTO dto) {
        return service.updateProfile(userId, dto);
    }
}
