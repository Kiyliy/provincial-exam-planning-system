package com.tsj.Service;
import com.tsj.Mapper.MajorInfo2Mapper;
import com.tsj.project.MajorInfo2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MajorInfo2Service {
    @Autowired
    private MajorInfo2Mapper majorInfo2Mapper;

    public List<MajorInfo2> getMajorInfo2() {
        return majorInfo2Mapper.getMajorInfo2();
    }

    public void insertMajorInfo2(MajorInfo2 majorInfo2) {
        majorInfo2Mapper.insertMajorInfo2(majorInfo2);
    }

    public void deleteMajorInfo2(Integer id) {
        majorInfo2Mapper.deleteMajorInfo2(id);
    }

    public void updateMajorInfo2(MajorInfo2 majorInfo2) {
        majorInfo2Mapper.updateMajorInfo2(majorInfo2);
    }

    public MajorInfo2 getMajorInfo2ById(Integer id) {
        return majorInfo2Mapper.getMajorInfo2ById(id);
    }
} 