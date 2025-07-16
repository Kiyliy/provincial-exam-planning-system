package com.tsj.Mapper;
import org.apache.ibatis.annotations.Mapper;
import com.tsj.project.TestInfo;
import java.util.List;

@Mapper
public interface TestInfoMapper {
    List<TestInfo> getTestInfo();
    List<TestInfo> getTestInfoWithMajors();
    void insertTestInfo(TestInfo testInfo);
    void updateTestInfo(TestInfo testInfo);
    void deleteTestInfo(Integer id);
    void deleteTestInfoByMajorId(Integer majorId);
    TestInfo getTestInfoById(Integer id);
    TestInfo getTestInfoByMajorId(Integer majorId);
} 