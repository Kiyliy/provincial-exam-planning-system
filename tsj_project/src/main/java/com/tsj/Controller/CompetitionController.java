package com.tsj.Controller;
import com.tsj.project.Competition;
import com.tsj.Service.CompetitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/competition")
public class CompetitionController {
    @Autowired
    private CompetitionService competitionService;
    
    @GetMapping("/getCompetitions")
    public List<Competition> getCompetitions() {
        return competitionService.getCompetitions();
    }
    @PostMapping("/insertCompetition")
    public void insertCompetition(@RequestBody Competition competition) {
        competitionService.insertCompetition(competition);
    }
    @GetMapping("/deleteCompetition/{id}")
    public void deleteCompetition(@PathVariable Integer id) {
        competitionService.deleteCompetition(id);
    }
    @PostMapping("/updateCompetition")
    public void updateCompetition(@RequestBody Competition competition) {
        competitionService.updateCompetition(competition);
    }
    @GetMapping("/getCompetitionById/{id}")
    public Competition getCompetitionById(@PathVariable Integer id) {
        return competitionService.getCompetitionById(id);
    }
} 