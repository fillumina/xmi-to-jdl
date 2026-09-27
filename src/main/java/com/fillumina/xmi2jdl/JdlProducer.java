package com.fillumina.xmi2jdl;

import com.fillumina.xmi2jdl.util.AppendableWrapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
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
        collectMapIdErrors(entitySortedList, errors);

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

        errors.forEach(e -> buf.writeln("// ", e));
    }
        
    /**
     * Records the relationships whose JHipster 6 and 7 derived identifier
     * option could not be translated: JHipster 9 does not accept it, so the
     * relationship is written as a plain one to one and the user is told
     * about it in the ERRORS section of the JDL.
     */
    private void collectMapIdErrors(List<Entity> entities, List<String> errors) {
        var messages = new LinkedHashSet<String>();
        entities.forEach(e -> e.getAllRelationships().stream()
                .filter(Relationship::isMapId)
                .forEach(r -> messages.add(
                        r.getOwner().getName() + "{" + r.getAttributeName() + "} to "
                                + r.getTarget().getName()
                                + ": 'with jpaDerivedIdentifier' is not supported by"
                                + " JHipster 9 and has been dropped")));
        errors.addAll(messages);
    }

    private <T extends Comparable<T>> List<T> sort(Collection<T> coll) {
        List<T> list = new ArrayList<>(coll);
        Collections.sort(list);
        return list;
    }

}
