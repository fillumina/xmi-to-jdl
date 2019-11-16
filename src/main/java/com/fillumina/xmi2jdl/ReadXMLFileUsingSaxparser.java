package com.fillumina.xmi2jdl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ReadXMLFileUsingSaxparser extends DefaultHandler {

    private final Map<String, DataType> dataTypes = new HashMap<>();
    private final Map<String, Entity> entities = new HashMap<>();
    private final Map<String, Enumeration> enumerations = new HashMap<>();
    private final List<String> errors = new ArrayList<>();

    private Entity currentEntity;
    private Enumeration currentEnumeration;

    public void consolidate() {
        entities.values().forEach(e -> {
            e.resolveReferences(dataTypes, entities, errors);
        });
    }
    
    public void print(Appendable buf) throws IOException {
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
    }

    @Override
    public void startElement(String uri,
            String localName,
            String qName,
            Attributes attributes) throws SAXException {

        try {
            switch (qName) {
                case "UML:Class":
                    {
                        String name = attributes.getValue("name");
                        String id = attributes.getValue("xmi.id");
                        String comment = attributes.getValue("comment");
                        String namespace = attributes.getValue("namespace");
                        if ("Datatypes".equals(namespace)) {
                            DataType dataType = new DataType(id, name, null);
                            dataTypes.put(id, dataType);
                        } else {
                            currentEnumeration = null;
                            currentEntity = new Entity(id, name, comment);
                            entities.put(id, currentEntity);
                        }
                        break;
                    }
                case "UML:DataType":
                    {
                        String name = attributes.getValue("name");
                        String id = attributes.getValue("xmi.id");
                        DataType dataType = new DataType(id, name, null);
                        dataTypes.put(id, dataType);
                        break;
                    }
                case "UML:Attribute":
                    {
                        String attributeName = attributes.getValue("name");
                        String type = attributes.getValue("type");
                        String comment = attributes.getValue("comment");

                        currentEntity.addAttribute(new Attribute(
                                attributeName, type, comment));
                        
                        break;
                    }
                case "UML:Enumeration":
                    {
                        String name = attributes.getValue("name");
                        String id = attributes.getValue("xmi.id");
                        String comment = attributes.getValue("comment");
                        Enumeration enumeration = new Enumeration(name, id, comment);
                        enumerations.put(id, enumeration);
                        dataTypes.put(id, enumeration);
                        currentEntity = null;
                        currentEnumeration = enumeration;
                        break;
                    }
                case "UML:EnumerationLiteral":
                    {
                        String name = attributes.getValue("name");
                        currentEnumeration.addLiteral(name);
                        break;
                    }
                default:
                    break;
            }
        } catch (Exception e) {
            if (currentEntity != null) {
                throw new RuntimeException(
                    "Exception in entity " + this.currentEntity.getName(), e);
            } else {
                throw new RuntimeException(
                    "Exception in enumeration " + this.currentEnumeration.getName(), 
                        e);
            }
        }
    }

    private <T extends Comparable<T>> List<T> sort(Collection<T> coll) {
        List<T> list = new ArrayList<>(coll);
        Collections.sort(list);
        return list;
    }
}
