package com.tsj.Mapper;
import org.apache.ibatis.annotations.Mapper;
import com.tsj.project.MajorInfo;
import java.util.List;

@Mapper
public interface MajorInfoMapper {
    List<MajorInfo> getMajorInfo();
    void insertMajorInfo(MajorInfo majorInfo);
    void updateMajorInfo(MajorInfo majorInfo);
    void deleteMajorInfo(Integer id);
    MajorInfo getMajorInfoById(Integer id);
} 