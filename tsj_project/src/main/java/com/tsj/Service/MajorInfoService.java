package com.tsj.Service;
import com.tsj.Mapper.MajorInfoMapper;
import com.tsj.project.MajorInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MajorInfoService {
    @Autowired
    private MajorInfoMapper majorInfoMapper;

    public List<MajorInfo> getMajorInfo() {
        return majorInfoMapper.getMajorInfo();
    }

    public void insertMajorInfo(MajorInfo majorInfo) {
        majorInfoMapper.insertMajorInfo(majorInfo);
    }

    public void deleteMajorInfo(Integer id) {
        majorInfoMapper.deleteMajorInfo(id);
    }

    public void updateMajorInfo(MajorInfo majorInfo) {
        majorInfoMapper.updateMajorInfo(majorInfo);
    }

    public MajorInfo getMajorInfoById(Integer id) {
        return majorInfoMapper.getMajorInfoById(id);
    }
} 