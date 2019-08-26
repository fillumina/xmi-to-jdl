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

    public void print(Appendable buf) throws IOException {
        buf.append("// ENUMERATIONS ")
                .append(System.lineSeparator())
                .append(System.lineSeparator());

        for (Enumeration e : sort(enumerations.values())) {
            e.appendEnumeration(buf);
        }

        buf.append("// ENTITIES ")
                .append(System.lineSeparator())
                .append(System.lineSeparator());

        List<Entity> entitySortedList = sort(entities.values());

        for (Entity e : entitySortedList) {
            e.appendEntity(buf);
        }

        buf.append("// RELATIONSHIPS")
                .append(System.lineSeparator())
                .append(System.lineSeparator());

        buf.append("relationship ManyToOne {")
                .append(System.lineSeparator())
                .append(System.lineSeparator());

        for (Entity e : entitySortedList) {
            e.appendRelationship(buf);
        }

        buf.append("}").append(System.lineSeparator());


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
                        currentEntity = new Entity(name, id, comment);
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
                    DataType dataType = dataTypes.get(type);
                    if (dataType != null) {
                        if ("undef".equals(dataType.getName())) {
                            errors.add("for entity " + currentEntity.getName() +
                                    " attribute " + attributeName +
                                    " is of undefined type!");
                        }
                        DataTypeRef dataTypeRef =
                                new DataTypeRef(dataType, attributeName, comment);
                        currentEntity.addReference(dataTypeRef);
                    } else {
                        Entity entity = entities.get(type);
                        if (entity != null) {
                            EntityRef entityRef =
                                    new EntityRef(currentEntity, entity,
                                            attributeName, comment);
                            currentEntity.addReference(entityRef);
                        }
                    }
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
    }

    private <T extends Comparable<T>> List<T> sort(Collection<T> coll) {
        List<T> list = new ArrayList<>(coll);
        Collections.sort(list);
        return list;
    }
}
