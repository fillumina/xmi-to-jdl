package com.fillumina.xmi2jdl.parser;

import com.fillumina.xmi2jdl.util.StringHelper;
import com.fillumina.xmi2jdl.DataType;
import com.fillumina.xmi2jdl.Entity;
import com.fillumina.xmi2jdl.EntityDiagram;
import com.fillumina.xmi2jdl.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ReadXMLFileUsingSaxParser 
        extends DefaultHandler 
        implements EntityDiagram {

    private final Map<String, ParsedEntity> parsedEntities = new HashMap<>();

    private final Map<String, DataType> dataTypes = new HashMap<>();
    private final Map<String, Entity> entities = new HashMap<>();
    private final Map<String, Enumeration> enumerations = new HashMap<>();
    private final Map<String, String> substitutions = new HashMap<>();

    private ParsedEntity currentEntity;
    private Enumeration currentEnumeration;
    private ParsedRelationship parsedRelationship;
    
    private final boolean honourPrivateField;

    public ReadXMLFileUsingSaxParser(boolean honourPrivateField) {
        this.honourPrivateField = honourPrivateField;
    }

    public void consolidate() {
        parsedEntities.values().forEach(e -> {
            entities.put(e.getId(), e.createEntity(substitutions));
        });
        parsedEntities.values().forEach(e -> {
            e.fillEntityAttributes(dataTypes, parsedEntities, substitutions);
        });
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
                        String id = attributes.getValue("xmi.id");
                        String name = attributes.getValue("name");
                        String comment = attributes.getValue("comment");
                        String namespace = attributes.getValue("namespace");
                        if ("Datatypes".equals(namespace)) {
                            DataType dataType = new DataType(id, name, null, null);
                            dataTypes.put(id, dataType);
                        } else {
                            currentEnumeration = null;
                            currentEntity = new ParsedEntity(id, name, comment);
                            parsedEntities.put(id, currentEntity);
                        }
                        break;
                    }
                case "UML:DataType":
                    {
                        String name = attributes.getValue("name");
                        String id = attributes.getValue("xmi.id");
                        DataType dataType = new DataType(id, name, null, null);
                        dataTypes.put(id, dataType);
                        break;
                    }
                case "UML:Attribute":
                    {
                        String visibility = attributes.getValue("visibility");
                        
                        if (!honourPrivateField || !"private".equals(visibility)) {
                            String attributeName = attributes.getValue("name");
                            String targetId = attributes.getValue("type");
                            String comment = attributes.getValue("comment");

                            ParsedAttribute parsedAttribute = new ParsedAttribute(
                                attributeName, targetId, comment);

                            currentEntity.addAttribute(parsedAttribute);
                        }
                        
                        break;
                    }

                case "UML:Enumeration":
                    {
                        String name = attributes.getValue("name");
                        String id = attributes.getValue("xmi.id");
                        String comment = attributes.getValue("comment");
                        var cp = new CommentParser(comment);
                        Enumeration enumeration = new Enumeration(
                                name, id, cp.getComment(), cp.getValidation());
                        enumerations.put(id, enumeration);
                        dataTypes.put(id, enumeration);
                        currentEntity = null;
                        currentEnumeration = enumeration;
                        break;
                    }
                case "UML:EnumerationLiteral":
                    {
                        String name = attributes.getValue("name");
                        String comment = attributes.getValue("comment");
                        currentEnumeration.addLiteral(name, comment);
                        break;
                    }
                    
                case "notewidget":
                    {
                        String content = attributes.getValue("text");
                        if (content.contains("{substitutions}")) {
                            String[] lines = content.split("\n");
                            for (String l : lines) {
                                int idx = l.indexOf('=');
                                if (idx != -1) {
                                    String key = l.substring(0, idx);
                                    String value = l.substring(idx + 1);
                                    this.substitutions.put(key, value);
                                }
                            }
                        }
                        break;
                    }
                case "assocwidget":
                    {
                        String fromEntityId = attributes.getValue("widgetaid");
                        String toEntityId = attributes.getValue("widgetbid");
                        parsedRelationship = new ParsedRelationship()
                                .ownerEntityId(fromEntityId)
                                .targetEntityId(toEntityId);
                        break;
                    }
                case "floatingtext":
                    {
                        String role = attributes.getValue("role");
                        String text = attributes.getValue("text");
                        switch (role) {
                            case "701":
                                parsedRelationship.ownerMultiplicity(text);
                                break;
                            case "702":
                                parsedRelationship.targetMultiplicity(text);
                                break;
                            case "710":
                                parsedRelationship.name(text);
                        }
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
                throw new RuntimeException("Exception in enumeration " + 
                        this.currentEnumeration.getName(), e);
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) 
            throws SAXException {
        
        switch (qName) {
            case "assocwidget": 
                ParsedEntity pe = parsedEntities.get(
                        parsedRelationship.getOwnerEntityId());
                
                if (pe != null) {
                    Optional<ParsedAttribute> attribute = 
                            pe.getParsedAttributeByEntityIdAndName(
                                    parsedRelationship.getTargetEntityId(),
                                    parsedRelationship.getName());

                    if (attribute.isPresent() && 
                            parsedRelationship.getOwnerMultiplicity() != null) {
                        ParsedAttribute attr = attribute.get();
                        CommentParser cp = new CommentParser(attr.getComment());
                        String validation = cp.getValidation();
                        String type = Multiplicity.getRelationAsString(
                                parsedRelationship.getOwnerMultiplicity(),
                                parsedRelationship.getTargetMultiplicity());
                        String val = StringHelper.mergeTokens(type, validation);
                        attr.setComment("{" + val + "} " + 
                                StringHelper.toString(cp.getComment()) );
                    }
                }
                break;
        }

    }

    @Override
    public Map<String, DataType> getDataTypes() {
        return dataTypes;
    }

    @Override
    public Map<String, Entity> getEntities() {
        return entities;
    }

    @Override
    public Map<String, Enumeration> getEnumerations() {
        return enumerations;
    }
}
