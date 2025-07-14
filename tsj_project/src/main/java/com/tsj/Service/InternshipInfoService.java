package com.tsj.Service;
import com.tsj.Mapper.InternshipInfoMapper;
import com.tsj.project.InternshipInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class InternshipInfoService {
    @Autowired
    private InternshipInfoMapper internshipInfoMapper;
    
    public List<InternshipInfo> getInternshipInfo(){
        return internshipInfoMapper.getInternshipInfo();
    }
    
    public void insertInternshipInfo(InternshipInfo internshipInfo){
        internshipInfoMapper.insertInternshipInfo(internshipInfo);
    }
    
    public void deleteInternshipInfo(Integer id){
        internshipInfoMapper.deleteInternshipInfo(id);
    }
    
    public void updateInternshipInfo(InternshipInfo internshipInfo){
        internshipInfoMapper.updateInternshipInfo(internshipInfo);
    }
    
    public InternshipInfo getInternshipInfoById(Integer id){
        return internshipInfoMapper.getInternshipInfoById(id);
    }
    
    public List<InternshipInfo> getInternshipInfoByStatus(String status){
        return internshipInfoMapper.getInternshipInfoByStatus(status);
    }
    
    // 根据用户ID查询实习报名状态
    public List<InternshipInfo> getInternshipInfoByUserId(Integer userId, String state){
        return internshipInfoMapper.getInternshipInfoByUserId(userId, state);
    }
    
    // 更新用户的实习报名状态
    public void updateInternshipInfoState(Integer internshipId, Integer userId, String state){
        internshipInfoMapper.updateInternshipInfoState(internshipId, userId, state);
    }
} 