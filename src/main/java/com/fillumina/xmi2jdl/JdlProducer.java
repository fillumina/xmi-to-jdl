package com.fillumina.xmi2jdl;

import com.fillumina.xmi2jdl.util.AppendableWrapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class JdlProducer implements EntityDiagramConsumer {

    private final AppendableWrapper buf;

    public JdlProducer(Appendable appendable) {
        this.buf = new AppendableWrapper(appendable);
    }
    
    @Override
    public void consume(EntityDiagram diagram) {
        //final Map<String, DataType> dataTypes = diagram.getDataTypes();
        final Map<String, Entity> entities = diagram.getEntities();
        final Map<String, Enumeration> enumerations = diagram.getEnumerations();
        
        List<String> errors = new ArrayList<>();
        
        buf.writeln("// ", enumerations.size(), " ENUMERATIONS ").writeln();
        sort(enumerations.values())
                .forEach(e -> e.appendEnumeration(buf.getAppendable()) );

        buf.writeln("// ", entities.size(), " ENTITIES ").writeln();
        List<Entity> entitySortedList = sort(entities.values());
        entitySortedList.forEach(e -> e.appendEntity(buf.getAppendable()) );

        buf.writeln("// RELATIONSHIPS").writeln();
        for (RelationshipType relationship : RelationshipType.values()) {
            boolean relationshipPresent = false;
            for (Entity e : entitySortedList) {
                if (e.hasRelationships(relationship)) {
                    relationshipPresent = true;
                    break;
                }
            }

            if (relationshipPresent) {
                buf.writeln("relationship ", relationship.name(), " {")
                        .writeln();

                entitySortedList.forEach(e ->
                    e.appendRelationship(relationship, buf.getAppendable()) );

                buf.writeln("}").writeln();
            }
        }


        buf.writeln().writeln("// ERRORS").writeln();

        errors.forEach(e -> buf.writeln(e));
    }
        
    private <T extends Comparable<T>> List<T> sort(Collection<T> coll) {
        List<T> list = new ArrayList<>(coll);
        Collections.sort(list);
        return list;
    }

}
