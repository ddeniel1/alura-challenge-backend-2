package application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan
@EnableJpaRepositories
public class ApplicationStart {
    public static void main(String[] args) {
        new ApplicationStart().run(args);
    }

    public void run(String[] args){
        SpringApplication.run(ApplicationStart.class, args);
    }
}
