package com.tsj.Service;
import com.tsj.Mapper.CompetitionMapper;
import com.tsj.project.Competition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CompetitionService {
    @Autowired
    private CompetitionMapper competitionMapper;
    
    public List<Competition> getCompetitions() {
        return competitionMapper.getCompetitions();
    }
    public void insertCompetition(Competition competition) {
        competitionMapper.insertCompetition(competition);
    }
    public void deleteCompetition(Integer id) {
        competitionMapper.deleteCompetition(id);
    }
    public void updateCompetition(Competition competition) {
        competitionMapper.updateCompetition(competition);
    }
    public Competition getCompetitionById(Integer id) {
        return competitionMapper.getCompetitionById(id);
    }
} 