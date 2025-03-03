package com.quiltix.tasktracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;


@EnableScheduling
@SpringBootApplication
public class TaskTrackerApplication {
	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Europe/Moscow"));
		SpringApplication.run(TaskTrackerApplication.class, args);
	}
}
