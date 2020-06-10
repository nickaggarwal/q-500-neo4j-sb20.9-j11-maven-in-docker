package org.codejudge.sb.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class GraphResult {

    List<GraphNode> nodes;
    Map<String, String> rows;
}
