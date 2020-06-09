package org.codejudge.sb.service;

import org.codejudge.sb.model.GraphNode;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.graphdb.*;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.neo4j.configuration.GraphDatabaseSettings.DEFAULT_DATABASE_NAME;


public class Neo4jGraphImpl implements Neo4jGraph{

    private GraphDatabaseService graphDb;
    private static final File databaseDirectory = new File( "CodeGraph" );
    private Transaction tx;

    public Neo4jGraphImpl(){
        //clearDbPath();
        DatabaseManagementService managementService = new DatabaseManagementServiceBuilder( databaseDirectory ).build();
        this.graphDb = managementService.database( "neo4j" );
        //populateData();
    }

    @Override
    public GraphNode createNode(String name, Map<String, String> properties) {
        tx = this.graphDb.beginTx() ;
        Node n1 = tx.createNode(Label.label(name));
        for(Map.Entry<String,String> entry : properties.entrySet()) {
            n1.setProperty(entry.getKey(), entry.getValue());
        }
        GraphNode graphNode = new GraphNode();
        graphNode.setLabel(name);
        graphNode.setNodeId(n1.getId());
        tx.commit();
        return graphNode;
    }

    @Override
    public void createRelationship(Long nodeId1, Long nodeId2, String type) {
        tx = this.graphDb.beginTx();
        Node n1 = tx.getNodeById(nodeId1);
        Node n2 = tx.getNodeById(nodeId2);
        n1.createRelationshipTo(n2, RelationshipType.withName(type));
        tx.commit();
    }

    @Override
    public List<GraphNode> getResult(String cyperQuery) {
        tx = this.graphDb.beginTx();
        List<GraphNode> resultNodes = new ArrayList<>();
        Result result = tx.execute(cyperQuery);
        List< Map< String, String> > keyvals = new ArrayList<>();
        while (result.hasNext()){
            Map<String, Object> row = result.next();
            Map<String, String> hashmap = new HashMap<>();
            for ( Map.Entry<String,Object> column : row.entrySet() ){

                if(column.getValue() instanceof Node) {
                    Node node = (Node) column.getValue();
                    GraphNode graphNode = new GraphNode();
                    Iterable<Label> labels = node.getLabels();
                    for (Label label : labels) {
                        graphNode.setLabel(label.name());
                    }
                    resultNodes.add(graphNode);
                }else if (column.getValue() instanceof String) {
                    hashmap.put(column.getKey(), (String) column.getValue());
                }
            }
        }
        tx.commit();
        return resultNodes;
    }

    private void clearDbPath(){
        try{
            FileSystemUtils.deleteRecursively( databaseDirectory ) ;
        } catch ( Exception e ){
            throw new RuntimeException( e );
        }
    }

    private void populateData(){

        GraphNode n1 = createNode("CLASS", Map.of("name", "Foo"));
        GraphNode n2 = createNode("CLASS", Map.of("name", "Bar"));
        createRelationship(n1.getNodeId(),n2.getNodeId(), "IMPORTS");

    }
}