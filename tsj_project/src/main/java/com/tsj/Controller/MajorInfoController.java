package com.tsj.Controller;
import com.tsj.project.MajorInfo;
import com.tsj.Service.MajorInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/majorInfo")
public class MajorInfoController {
    @Autowired
    private MajorInfoService majorInfoService;

    @GetMapping("/getMajorInfo")
    public List<MajorInfo> getMajorInfo(){
        return majorInfoService.getMajorInfo();
    }

    @PostMapping("/insertMajorInfo")
    public void insertMajorInfo(@RequestBody MajorInfo majorInfo){
        majorInfoService.insertMajorInfo(majorInfo);
    }

    @GetMapping("/deleteMajorInfo/{id}")
    public void deleteMajorInfo(@PathVariable Integer id){
        majorInfoService.deleteMajorInfo(id);
    }

    @PostMapping("/updateMajorInfo")
    public void updateMajorInfo(@RequestBody MajorInfo majorInfo){
        majorInfoService.updateMajorInfo(majorInfo);
    }

    @GetMapping("/getMajorInfoById")
    public MajorInfo getMajorInfoById(@RequestParam Integer id){
        return majorInfoService.getMajorInfoById(id);
    }
} 