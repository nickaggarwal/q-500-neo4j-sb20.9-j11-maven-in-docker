package org.codejudge.sb.service;

import org.codejudge.sb.model.GraphNode;
import org.codejudge.sb.model.GraphResult;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.graphdb.*;
import org.neo4j.kernel.impl.core.NodeEntity;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public List<GraphResult> getResult(String cyperQuery) {
        tx = this.graphDb.beginTx();
        List<GraphResult> resultNodes = new ArrayList<>();
        Result result = tx.execute(cyperQuery);
        while (result.hasNext()){
            Map<String, Object> row = result.next();
            GraphResult rowResult = new GraphResult();

            Map<String, String> hashmap = new HashMap<>();
            List<GraphNode> nodes = new ArrayList<>();

            for ( Map.Entry<String,Object> column : row.entrySet() ){

                if(column.getValue() instanceof NodeEntity) {
                    Node node = (Node) column.getValue();
                    GraphNode graphNode = new GraphNode();
                    Iterable<Label> labels = node.getLabels();
                    for (Label label : labels) {
                        graphNode.setLabel(label.name());

                    }
                    graphNode.setFile((String)node.getProperty("file"));
                    graphNode.setNodeId(node.getId());
                    nodes.add(graphNode);
                }else {
                    if(column.getValue() == null){
                        hashmap.put(column.getKey(),"null");
                    }else{
                        hashmap.put(column.getKey().replace(".", "_"), column.getValue().toString());
                    }

                }
            }

            rowResult.setNodes(nodes);
            rowResult.setRows(hashmap);
            resultNodes.add(rowResult);
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

    public List<Map<String, String>> serialze(List<GraphResult> results){
        List<Map<String, String> > resultRows = new ArrayList<>();
        for(GraphResult resultRow : results){
            Map<String, String> hashmap = new HashMap<>();
            List<String> nodeIds = new ArrayList();
            for( GraphNode node : resultRow.getNodes() )
            {
                nodeIds.add(node.getNodeId().toString());
            }
            String nodeCommaSeperated = nodeIds.stream().collect(Collectors.joining(","));

            hashmap.put("node_ids", nodeCommaSeperated);
            hashmap.putAll(resultRow.getRows());
            resultRows.add(hashmap);
        }
        return resultRows;
    }
}