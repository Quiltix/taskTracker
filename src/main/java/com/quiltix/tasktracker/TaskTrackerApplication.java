package com.quiltix.tasktracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication
public class TaskTrackerApplication {


	public static void main(String[] args) {
		SpringApplication.run(TaskTrackerApplication.class, args);
	}

}
