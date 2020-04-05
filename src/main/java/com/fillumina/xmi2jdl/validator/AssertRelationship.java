package com.fillumina.xmi2jdl.validator;

import com.fillumina.xmi2jdl.Relationship;
import com.fillumina.xmi2jdl.RelationshipType;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertRelationship extends AbstractAssertor<AssertRelationship> {
    private Relationship relationship;
    
    public AssertRelationship(AbstractValidator validator,
            String entityName, String relationshipName) {
        super(validator, "enitity " + entityName + 
                " relation " + relationshipName + ": ");
        this.relationship = testValidIfNotNull("relationship", 
                validator.findRelationship(entityName, relationshipName));
    }

    public AssertRelationship assertType(RelationshipType type) {
        return assertEquals("type", type, relationship.getRelationshipType());
    }
    
    public AssertRelationship isUnidirectional() {
        return assertUnidirectional(true);
    }
    
    public AssertRelationship isNotUnidirectional() {
        return assertUnidirectional(false);
    }
    
    public AssertRelationship assertUnidirectional(boolean unidirectional) {
        return assertEquals("unidirectional", unidirectional, 
                relationship.isUnidirectional());
    }
    
    public AssertRelationship assertTarget(String target) {
        return assertEquals("target", target, relationship.getTarget().getName());
    }

    public AssertRelationship isRequired() {
        return assertRequired(true);
    }
    
    public AssertRelationship isNotRequired() {
        return assertRequired(false);
    }
    
    private AssertRelationship assertRequired(boolean required) {
        return assertEquals("required", required, relationship.isRequired());
    }
    
}
