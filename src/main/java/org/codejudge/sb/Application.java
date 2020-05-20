package org.codejudge.sb;

import lombok.extern.slf4j.Slf4j;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.FileSystemUtils;
import static org.neo4j.configuration.GraphDatabaseSettings.DEFAULT_DATABASE_NAME;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@ComponentScan({"org.codejudge.sb"})
@Slf4j
public class Application {


	public static void main(String[] args) {
		log.info("Starting Application...");
		SpringApplication.run(Application.class, args);
	}

}
