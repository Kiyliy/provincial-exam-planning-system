package com.tsj.Controller;
import com.tsj.project.Textbook;
import com.tsj.Service.TextbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/textbook")
public class TextbookController {
    @Autowired
    private TextbookService textbookService;
    
    @GetMapping("/getTextbooks")
    public List<Textbook> getTextbooks() {
        return textbookService.getTextbooks();
    }
    
    @PostMapping("/insertTextbook")
    public void insertTextbook(@RequestBody Textbook textbook) {
        textbookService.insertTextbook(textbook);
    }
    
    @GetMapping("/deleteTextbook/{id}")
    public void deleteTextbook(@PathVariable Integer id) {
        textbookService.deleteTextbook(id);
    }
    
    @PostMapping("/updateTextbook")
    public void updateTextbook(@RequestBody Textbook textbook) {
        textbookService.updateTextbook(textbook);
    }
    
    @GetMapping("/getTextbookById/{id}")
    public Textbook getTextbookById(@PathVariable Integer id) {
        return textbookService.getTextbookById(id);
    }
    
    @GetMapping("/searchTextbooks/{keyword}")
    public List<Textbook> searchTextbooks(@PathVariable String keyword) {
        return textbookService.searchTextbooks(keyword);
    }
} 