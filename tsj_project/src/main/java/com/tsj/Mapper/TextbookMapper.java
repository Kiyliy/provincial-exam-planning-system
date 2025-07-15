package com.tsj.Mapper;
import org.apache.ibatis.annotations.Mapper;
import com.tsj.project.Textbook;
import java.util.List;

@Mapper
public interface TextbookMapper {
    List<Textbook> getTextbooks();
    void insertTextbook(Textbook textbook);
    void updateTextbook(Textbook textbook);
    void deleteTextbook(Integer id);
    Textbook getTextbookById(Integer id);
    List<Textbook> searchTextbooks(String keyword);
} 