package com.dhanush.acharya.svnkit;

import com.dhanush.acharya.svnkit.configuration.SVNProperty;
import com.dhanush.acharya.svnkit.service.SvnKitService;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@AllArgsConstructor
public class SvnKitApplication implements CommandLineRunner {

    private final SvnKitService service;
    private final SVNProperty svnProperty;

    public static void main(String[] args) {
        SpringApplication.run(SvnKitApplication.class, args);

    }

    @Override
    public void run(String... args) {
        service.getCommittedFiles(svnProperty.SVN_URL,
                svnProperty.USERNAME,
                svnProperty.PASSWORD,
                svnProperty.START_VERSION,
                svnProperty.END_VERSION);

    }
}
