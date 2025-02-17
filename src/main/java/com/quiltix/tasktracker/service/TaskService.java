package com.quiltix.tasktracker.service;


import com.quiltix.tasktracker.DTO.CreateTaskDTO;
import com.quiltix.tasktracker.model.Task;
import com.quiltix.tasktracker.model.TaskRepository;
import com.quiltix.tasktracker.model.User;
import com.quiltix.tasktracker.model.UserRepository;
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

    public Task createTaks(Authentication authentication, CreateTaskDTO taskDTO) throws Exception{
        String username = authentication.getName();
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Task task = Task.builder()
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
