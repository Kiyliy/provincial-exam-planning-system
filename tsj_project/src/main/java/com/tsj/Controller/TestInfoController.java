package com.tsj.Controller;
import com.tsj.project.TestInfo;
import com.tsj.Service.TestInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/testInfo")
public class TestInfoController {
    @Autowired
    private TestInfoService testInfoService;
    
    @GetMapping("/getTestInfo")
    public List<TestInfo> getTestInfo(){
        return testInfoService.getTestInfo();
    }
    
    @GetMapping("/getTestInfoWithMajors")
    public List<TestInfo> getTestInfoWithMajors(){
        return testInfoService.getTestInfoWithMajors();
    }
    
    @PostMapping("/insertTestInfo")
    public void insertTestInfo(@RequestBody TestInfo testInfo){
        testInfoService.insertTestInfo(testInfo);
    }
    
    @GetMapping("/deleteTestInfo/{id}")
    public void deleteTestInfo(@PathVariable Integer id){
        testInfoService.deleteTestInfo(id);
    }
    
    @PostMapping("/updateTestInfo")
    public void updateTestInfo(@RequestBody TestInfo testInfo){
        testInfoService.updateTestInfo(testInfo);
    }
    
    @GetMapping("/getTestInfoById")
    public TestInfo getTestInfoById(@RequestParam Integer id){
        return testInfoService.getTestInfoById(id);
    }
    
    @GetMapping("/getTestInfoByMajorId")
    public TestInfo getTestInfoByMajorId(@RequestParam Integer majorId){
        return testInfoService.getTestInfoByMajorId(majorId);
    }
} 