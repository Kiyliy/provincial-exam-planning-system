package com.tsj.Service;
import com.tsj.Mapper.MajorInfo3Mapper;
import com.tsj.project.MajorInfo3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MajorInfo3Service {
    @Autowired
    private MajorInfo3Mapper majorInfo3Mapper;

    public List<MajorInfo3> getMajorInfo3() {
        return majorInfo3Mapper.getMajorInfo3();
    }

    public void insertMajorInfo3(MajorInfo3 majorInfo3) {
        majorInfo3Mapper.insertMajorInfo3(majorInfo3);
    }

    public void deleteMajorInfo3(Integer id) {
        majorInfo3Mapper.deleteMajorInfo3(id);
    }

    public void updateMajorInfo3(MajorInfo3 majorInfo3) {
        majorInfo3Mapper.updateMajorInfo3(majorInfo3);
    }

    public MajorInfo3 getMajorInfo3ById(Integer id) {
        return majorInfo3Mapper.getMajorInfo3ById(id);
    }
} 