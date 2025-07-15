package com.tsj.Controller;
import com.tsj.project.User;
import com.tsj.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    
    @GetMapping("/getUserByUsername")
    public User getUserByUsername(@RequestParam String username){
        return userService.getUserByUsername(username);
    }
    
    @PostMapping("/updateOnlineStatus")
    public void updateOnlineStatus(@RequestParam Long id, @RequestParam Integer status){
        userService.updateOnlineStatus(id, status);
    }
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginInfo){
        String username = loginInfo.get("username");
        String password = loginInfo.get("password");
        
        User user = userService.login(username, password);
        Map<String, Object> response = new HashMap<>();
        
        if (user != null) {
            response.put("success", true);
            response.put("message", "登录成功");
            response.put("user", user);
        } else {
            response.put("success", false);
            response.put("message", "用户名或密码错误");
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@RequestParam Long userId){
        userService.logout(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "已成功登出");
        
        return ResponseEntity.ok(response);
    }
}