package com.tsj.Controller;
import com.tsj.project.InternshipInfo;
import com.tsj.Service.InternshipInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/internshipInfo")
public class InternshipInfoController {
    @Autowired
    private InternshipInfoService internshipInfoService;
    
    @GetMapping("/getInternshipInfo")
    public List<InternshipInfo> getInternshipInfo(){
        return internshipInfoService.getInternshipInfo();
    }
    
    @PostMapping("/insertInternshipInfo")
    public void insertInternshipInfo(@RequestBody InternshipInfo internshipInfo){
        internshipInfoService.insertInternshipInfo(internshipInfo);
    }
    
    @GetMapping("/deleteInternshipInfo/{id}")
    public void deleteInternshipInfo(@PathVariable Integer id){
        internshipInfoService.deleteInternshipInfo(id);
    }
    
    @PostMapping("/updateInternshipInfo")
    public void updateInternshipInfo(@RequestBody InternshipInfo internshipInfo){
        internshipInfoService.updateInternshipInfo(internshipInfo);
    }
    
    @GetMapping("/getInternshipInfoById/{id}")
    public InternshipInfo getInternshipInfoById(@PathVariable Integer id){
        return internshipInfoService.getInternshipInfoById(id);
    }
    
    @GetMapping("/getInternshipInfoByStatus/{status}")
    public List<InternshipInfo> getInternshipInfoByStatus(@PathVariable String status){
        return internshipInfoService.getInternshipInfoByStatus(status);
    }
    
    @GetMapping("/getInternshipInfoByUserId/{userId}/{state}")
    public List<InternshipInfo> getInternshipInfoByUserId(@PathVariable Integer userId, @PathVariable String state){
        return internshipInfoService.getInternshipInfoByUserId(userId, state);
    }
    
    @GetMapping("/updateInternshipInfoState/{internshipId}/{userId}/{state}")
    public void updateInternshipInfoState(@PathVariable Integer internshipId, @PathVariable Integer userId, @PathVariable String state){
        internshipInfoService.updateInternshipInfoState(internshipId, userId, state);
    }
} 