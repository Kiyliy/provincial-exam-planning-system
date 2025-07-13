package com.tsj.Controller;
import com.tsj.project.MajorInfo3;
import com.tsj.Service.MajorInfo3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/majorInfo3")
public class MajorInfo3Controller {
    @Autowired
    private MajorInfo3Service majorInfo3Service;

    @GetMapping("/getMajorInfo3")
    public List<MajorInfo3> getMajorInfo3(){
        return majorInfo3Service.getMajorInfo3();
    }

    @PostMapping("/insertMajorInfo3")
    public void insertMajorInfo3(@RequestBody MajorInfo3 majorInfo3){
        majorInfo3Service.insertMajorInfo3(majorInfo3);
    }

    @GetMapping("/deleteMajorInfo3/{id}")
    public void deleteMajorInfo3(@PathVariable Integer id){
        majorInfo3Service.deleteMajorInfo3(id);
    }

    @PostMapping("/updateMajorInfo3")
    public void updateMajorInfo3(@RequestBody MajorInfo3 majorInfo3){
        majorInfo3Service.updateMajorInfo3(majorInfo3);
    }

    @GetMapping("/getMajorInfo3ById")
    public MajorInfo3 getMajorInfo3ById(@RequestParam Integer id){
        return majorInfo3Service.getMajorInfo3ById(id);
    }
} 