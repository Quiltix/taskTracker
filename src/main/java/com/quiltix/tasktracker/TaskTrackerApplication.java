package com.quiltix.tasktracker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.TimeZone;


@EnableScheduling
@SpringBootApplication
public class TaskTrackerApplication implements CommandLineRunner {

	@Value("${app.avatar-dir}")
	private String avatarDir;

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Europe/Moscow"));
		SpringApplication.run(TaskTrackerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Files.createDirectories(Paths.get(avatarDir));
	}
}
