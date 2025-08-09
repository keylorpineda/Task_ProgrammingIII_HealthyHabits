package task.healthyhabits.services;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import task.healthyhabits.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    
}