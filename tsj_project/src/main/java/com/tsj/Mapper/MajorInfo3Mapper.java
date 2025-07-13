package com.tsj.Mapper;
import org.apache.ibatis.annotations.Mapper;
import com.tsj.project.MajorInfo3;
import java.util.List;

@Mapper
public interface MajorInfo3Mapper {
    List<MajorInfo3> getMajorInfo3();
    void insertMajorInfo3(MajorInfo3 majorInfo3);
    void updateMajorInfo3(MajorInfo3 majorInfo3);
    void deleteMajorInfo3(Integer id);
    MajorInfo3 getMajorInfo3ById(Integer id);
} 