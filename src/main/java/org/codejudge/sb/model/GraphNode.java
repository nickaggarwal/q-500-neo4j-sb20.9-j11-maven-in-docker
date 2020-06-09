package org.codejudge.sb.model;

import lombok.Data;

@Data
public class GraphNode {

    Long nodeId;
    String shortname;
    String label;
    String astkind;
    String longname;
    String file;
}
