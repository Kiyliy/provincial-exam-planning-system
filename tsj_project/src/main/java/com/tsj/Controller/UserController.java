package com.tsj.Controller;
import com.tsj.project.User;
import com.tsj.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;
    
    @GetMapping("/getUser")
    public List<User> getUser(){
        return userService.getUser();
    }
    
    @PostMapping("/insertUser")
    public void insertUser(@RequestBody User user){
        userService.insertUser(user);
    }
    
    @GetMapping("/deleteUser/{id}")
    public void deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
    }
    
    @PostMapping("/updateUser")
    public void updateUser(@RequestBody User user){
        userService.updateUser(user);
    }
    
    @GetMapping("/getUser2")
    public User getUser2(@RequestParam Long id){
        return userService.getUser2(id);
    }
}