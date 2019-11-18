package com.fillumina.xmi2jdl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * @author fra
 */
public class JdlProducer implements EntityDiagramConsumer {

    private final Appendable buf;

    public JdlProducer(Appendable buf) {
        this.buf = buf;
    }
    
    @Override
    public void consume(EntityDiagram diagram) {
        final Map<String, DataType> dataTypes = diagram.getDataTypes();
        final Map<String, Entity> entities = diagram.getEntities();
        final Map<String, Enumeration> enumerations = diagram.getEnumerations();
        
        List<String> errors = new ArrayList<>();
        
        try {
            buf.append("// " + enumerations.size() + " ENUMERATIONS ")
                    .append(System.lineSeparator())
                    .append(System.lineSeparator());

            for (Enumeration e : sort(enumerations.values())) {
                e.appendEnumeration(buf);
            }

            buf.append("// " + entities.size() + " ENTITIES ")
                    .append(System.lineSeparator())
                    .append(System.lineSeparator());

            List<Entity> entitySortedList = sort(entities.values());

            for (Entity e : entitySortedList) {
                e.appendEntity(buf);
            }

            buf.append("// RELATIONSHIPS")
                    .append(System.lineSeparator())
                    .append(System.lineSeparator());

            for (Relationship relationship : Relationship.values()) {
                boolean relationshipPresent = false;
                for (Entity e : entitySortedList) {
                    if (e.hasRelationships(relationship)) {
                        relationshipPresent = true;
                        break;
                    }
                }

                if (relationshipPresent) {
                    buf.append("relationship ").append(relationship.name())
                            .append(" {")
                            .append(System.lineSeparator())
                            .append(System.lineSeparator());

                    for (Entity e : entitySortedList) {
                        e.appendRelationship(relationship, buf);
                    }

                    buf.append("}")
                            .append(System.lineSeparator())
                            .append(System.lineSeparator());
                }
            }


            buf.append(System.lineSeparator()).append("// ERRORS")
                    .append(System.lineSeparator())
                    .append(System.lineSeparator());

            for (String e : errors) {
                buf.append(e).append(System.lineSeparator());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
        
    private <T extends Comparable<T>> List<T> sort(Collection<T> coll) {
        List<T> list = new ArrayList<>(coll);
        Collections.sort(list);
        return list;
    }

}
