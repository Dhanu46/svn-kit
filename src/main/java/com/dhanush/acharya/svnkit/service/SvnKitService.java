package com.dhanush.acharya.svnkit.service;

import com.dhanush.acharya.svnkit.utility.ApplicationConstants;
import org.springframework.stereotype.Service;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author DhanushAcharya on 19-04-2021
 * @project svn-kit
 */
@Service
public class SvnKitService {

    private Set<String> uniqueNames = Collections.emptySet();
    private final StringBuilder stringBuilder = new StringBuilder();

    public void getCommittedFiles(String url,
            String name ,
            String password,
            long startRevision,
            long endRevision){

        setupLibrary();
        SVNRepository repository = null;
        try {
            repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
        } catch (SVNException ex) {

            System.err
                    .println("error while creating an SVNRepository for the location '"
                            + url + "': " + ex.getMessage());
            System.exit(1);
        }
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(name,password.toCharArray());
        repository.setAuthenticationManager(authManager);
        try {
            endRevision = repository.getLatestRevision();
        } catch (SVNException ex) {
            System.err.println("error while fetching the latest repository revision: " + ex.getMessage());
            System.exit(1);
        }

        Collection logEntries = null;
        try{
            logEntries = repository.log(new String[] {""}, null,
                    startRevision, endRevision, true, true);

        } catch (SVNException exception) {
            System.out.println("error while collecting log information for '"
                    + url + "': " + exception.getMessage());
            System.exit(1);
        }
        for (Object entry : logEntries) {
            /*
             * gets a next SVNLogEntry
             */
            SVNLogEntry logEntry = (SVNLogEntry) entry;
            if (logEntry.getChangedPaths().size() > 0) {
                Set<String> changedPathsSet = logEntry.getChangedPaths().keySet();
                String current = changedPathsSet
                        .parallelStream()
                        .filter(x->logEntry.getAuthor().equalsIgnoreCase(name))
                        .map(x -> logEntry.getChangedPaths().get(x))
                        .filter(x -> x.getType() == 'M' || x.getType() == 'A')
                        .map(SVNLogEntryPath::getPath)
                        .collect(Collectors.joining(","));
                stringBuilder.append(",").append(current);
            }
        }
        uniqueNames =  Arrays.asList(stringBuilder.toString().split(",")).parallelStream().collect(Collectors.toSet());
        ApplicationConstants.DIRECTORIES.stream().map(this::getFilesListBasedOnDirectoryName).forEach(this::printForEach);
    }
    private static void setupLibrary() {
        SVNRepositoryFactoryImpl.setup();
    }
    private CompletableFuture<List<String>> getFilesListBasedOnDirectoryName(String directoryName){
        return CompletableFuture.supplyAsync(() ->
                uniqueNames.stream().filter(x -> x.contains(directoryName)).collect(Collectors.toList()));
    }
    private void printForEach(CompletableFuture<List<String>> completableFuture){
        try {
            AtomicBoolean dashedLines= new AtomicBoolean(false);
            completableFuture.get(2000, TimeUnit.MILLISECONDS).stream().peek(x-> dashedLines.set(x.length() > 0)).forEach(System.out::println);
            if(dashedLines.get())
                System.out.println("------------------------------------------------------------------------------------------------------");
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
