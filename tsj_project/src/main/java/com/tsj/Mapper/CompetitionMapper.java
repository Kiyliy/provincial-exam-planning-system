package com.tsj.Mapper;
import org.apache.ibatis.annotations.Mapper;
import com.tsj.project.Competition;
import java.util.List;

@Mapper
public interface CompetitionMapper {
    List<Competition> getCompetitions();
    void insertCompetition(Competition competition);
    void updateCompetition(Competition competition);
    void deleteCompetition(Integer id);
    Competition getCompetitionById(Integer id);
} 