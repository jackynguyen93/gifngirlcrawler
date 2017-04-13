package com.vozer;

import com.vozer.service.CrawlerImages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GifngirlcrawlerApplication  {

	@Autowired
	CrawlerImages crawlerImages;


	public static void main(String[] args) {
		SpringApplication.run(GifngirlcrawlerApplication.class, args);

	}

}
