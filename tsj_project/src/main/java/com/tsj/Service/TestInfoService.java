package com.tsj.Service;
import com.tsj.Mapper.TestInfoMapper;
import com.tsj.project.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TestInfoService {
    @Autowired
    private TestInfoMapper testInfoMapper;
    
    public List<TestInfo> getTestInfo(){
        return testInfoMapper.getTestInfo();
    }
    
    public List<TestInfo> getTestInfoWithMajors(){
        return testInfoMapper.getTestInfoWithMajors();
    }
    
    public void insertTestInfo(TestInfo testInfo){
        testInfoMapper.insertTestInfo(testInfo);
    }
    
    public void deleteTestInfo(Integer id){
        testInfoMapper.deleteTestInfo(id);
    }
    
    public void updateTestInfo(TestInfo testInfo){
        testInfoMapper.updateTestInfo(testInfo);
    }
    
    public TestInfo getTestInfoById(Integer id){
        return testInfoMapper.getTestInfoById(id);
    }
    
    public TestInfo getTestInfoByMajorId(Integer majorId){
        return testInfoMapper.getTestInfoByMajorId(majorId);
    }

    public void deleteTestInfoByMajorId(Integer majorId){
        testInfoMapper.deleteTestInfoByMajorId(majorId);
    }
} 