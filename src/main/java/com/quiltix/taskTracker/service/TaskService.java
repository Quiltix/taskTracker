package com.quiltix.taskTracker.service;


import com.quiltix.taskTracker.DTO.Task.CreateTaskDTO;
import com.quiltix.taskTracker.model.Task;
import com.quiltix.taskTracker.model.TaskRepository;
import com.quiltix.taskTracker.model.User;
import com.quiltix.taskTracker.model.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public List<Task> getAllTasks(Authentication authentication){
        String username = authentication.getName();
        User user = userRepository.findUserByUsername(username).orElseThrow(()-> new UsernameNotFoundException("User not found"));

        return taskRepository.findByOwner(user);
    }

    public Task createTakes(Authentication authentication, CreateTaskDTO taskDTO) throws Exception{
        String username = authentication.getName();
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Task task = new Task().builder()
                .title(taskDTO.getTitle())
                .description(taskDTO.getDescription())
                .important(taskDTO.getImportant())
                .timeToComplete(taskDTO.getTimeToComplete())
                .startTime(LocalDateTime.now())
                .owner(user)
                .complete(false)
                .build();


        return taskRepository.save(task);


    }


}
