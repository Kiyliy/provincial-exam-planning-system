package com.tsj.Controller;
import com.tsj.project.MajorInfo2;
import com.tsj.Service.MajorInfo2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/majorInfo2")
public class MajorInfo2Controller {
    @Autowired
    private MajorInfo2Service majorInfo2Service;

    @GetMapping("/getMajorInfo2")
    public List<MajorInfo2> getMajorInfo2(){
        return majorInfo2Service.getMajorInfo2();
    }

    @PostMapping("/insertMajorInfo2")
    public void insertMajorInfo2(@RequestBody MajorInfo2 majorInfo2){
        majorInfo2Service.insertMajorInfo2(majorInfo2);
    }

    @GetMapping("/deleteMajorInfo2/{id}")
    public void deleteMajorInfo2(@PathVariable Integer id){
        majorInfo2Service.deleteMajorInfo2(id);
    }

    @PostMapping("/updateMajorInfo2")
    public void updateMajorInfo2(@RequestBody MajorInfo2 majorInfo2){
        majorInfo2Service.updateMajorInfo2(majorInfo2);
    }

    @GetMapping("/getMajorInfo2ById")
    public MajorInfo2 getMajorInfo2ById(@RequestParam Integer id){
        return majorInfo2Service.getMajorInfo2ById(id);
    }
} 