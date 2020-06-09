package org.codejudge.sb.controller;

import io.swagger.annotations.ApiOperation;
import org.codejudge.sb.model.GraphNode;
import org.codejudge.sb.service.Neo4jGraphImpl;
import org.neo4j.graphdb.Node;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
public class AppController {

    private Neo4jGraphImpl neo4jGraph;


    @ApiOperation("This is the hello world api")
    @GetMapping("/")
    public String hello() {
        return "Hello World!!";
    }

    @GetMapping("/get-all-nodes")
    public List<String> getAllNodes() {
        this.neo4jGraph = new Neo4jGraphImpl();
        List<String> names = new ArrayList<>();
        List<GraphNode> res = this.neo4jGraph.getResult("MATCH (method:MethodDeclaration)-[r]->(someChild) RETURN method, properties(method), labels(method), type(r), id(someChild), labels(someChild) LIMIT 10;");
        for(GraphNode node : res){
            String name = node.getLabel();
            names.add(name);
        }
        return names;
    }

    @GetMapping("/get-all-foo-classes")
    public List<String> getFooNodes() {
        return List.of("SampleClass");
    }

}
