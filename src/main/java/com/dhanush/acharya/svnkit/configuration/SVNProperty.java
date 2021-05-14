package com.dhanush.acharya.svnkit.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author DhanushAcharya on 19-04-2021
 * @project svn-kit
 */
@EnableConfigurationProperties
@PropertySource("classpath:application.properties")
@Configuration
public class SVNProperty {

    @Value("${svn.url}")
    public  String SVN_URL;

    @Value("${svn.username}")
    public  String USERNAME;

    @Value("${svn.password}")
    public  String PASSWORD;

    @Value("${svn.start-revision-number}")
    public  long START_VERSION;

    @Value("${svn.end-revision-number}")
    public  long END_VERSION;

}
