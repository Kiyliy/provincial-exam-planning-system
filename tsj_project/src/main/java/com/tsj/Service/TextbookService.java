package com.tsj.Service;
import com.tsj.Mapper.TextbookMapper;
import com.tsj.project.Textbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TextbookService {
    @Autowired
    private TextbookMapper textbookMapper;
    
    public List<Textbook> getTextbooks() {
        return textbookMapper.getTextbooks();
    }
    
    public void insertTextbook(Textbook textbook) {
        textbookMapper.insertTextbook(textbook);
    }
    
    public void deleteTextbook(Integer id) {
        textbookMapper.deleteTextbook(id);
    }
    
    public void updateTextbook(Textbook textbook) {
        textbookMapper.updateTextbook(textbook);
    }
    
    public Textbook getTextbookById(Integer id) {
        return textbookMapper.getTextbookById(id);
    }
    
    public List<Textbook> searchTextbooks(String keyword) {
        return textbookMapper.searchTextbooks(keyword);
    }
} 