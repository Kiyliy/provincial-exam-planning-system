package com.tsj.Service;
import com.tsj.Mapper.UserMapper;
import com.tsj.project.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    
    public List<User> getUser(){
        return userMapper.getUser();
    }
    
    public void insertUser(User user){
        userMapper.insertUser(user);
    }
    
    public void deleteUser(Long id){
        userMapper.deleteUser(id);
    }
    
    public void updateUser(User user){
        userMapper.updateUser(user);
    }
    
    public User getUser2(Long id){
        return userMapper.getUser2(id);
    }
}