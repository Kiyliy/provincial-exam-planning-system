package com.tsj.Mapper;
import org.apache.ibatis.annotations.Mapper;
import com.tsj.project.MajorInfo2;
import java.util.List;

@Mapper
public interface MajorInfo2Mapper {
    List<MajorInfo2> getMajorInfo2();
    void insertMajorInfo2(MajorInfo2 majorInfo2);
    void updateMajorInfo2(MajorInfo2 majorInfo2);
    void deleteMajorInfo2(Integer id);
    MajorInfo2 getMajorInfo2ById(Integer id);
} 