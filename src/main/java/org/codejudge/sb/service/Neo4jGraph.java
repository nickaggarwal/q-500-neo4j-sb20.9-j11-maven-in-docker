package org.codejudge.sb.service;

import org.codejudge.sb.model.GraphNode;
import org.codejudge.sb.model.GraphResult;
import org.neo4j.graphdb.Node;
import java.util.List;
import java.util.Map;

public interface Neo4jGraph {

    public abstract GraphNode createNode(String label, Map<String, String> properties);
    public abstract void createRelationship(Long n1, Long n2, String type);
    public abstract List<GraphResult> getResult(String cyperQuery);

}
