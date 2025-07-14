package com.tsj.Mapper;
import org.apache.ibatis.annotations.Mapper;
import com.tsj.project.InternshipInfo;
import java.util.List;

@Mapper
public interface InternshipInfoMapper {
    List<InternshipInfo> getInternshipInfo();
    void insertInternshipInfo(InternshipInfo internshipInfo);
    void updateInternshipInfo(InternshipInfo internshipInfo);
    void deleteInternshipInfo(Integer id);
    InternshipInfo getInternshipInfoById(Integer id);
    List<InternshipInfo> getInternshipInfoByStatus(String status);
    
    // 根据用户ID查询实习报名状态
    List<InternshipInfo> getInternshipInfoByUserId(Integer userId, String state);
    
    // 更新用户的实习报名状态
    void updateInternshipInfoState(Integer internshipId, Integer userId, String state);
} 