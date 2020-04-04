package com.fillumina.xmi2jdl.validator;

import com.fillumina.xmi2jdl.Relationship;
import com.fillumina.xmi2jdl.RelationshipType;
import java.util.Objects;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertRelationship {
    private final AbstractValidator validator;
    private final String msg;
    private Relationship relationship;
    
    public AssertRelationship(AbstractValidator validator,
            String entityName, String relationshipName) {
        this.validator = validator;
        this.msg = "enitity " + entityName + 
                " relation " + relationshipName + ": ";
        this.relationship = 
                validator.findRelationship(entityName, relationshipName);
        this.validator.test("find relationship", () -> {
            if (this.relationship == null) {
                this.validator.error(msg + " not found");
            }
        });
    }

    private void test(String name, Runnable test) {
        if (relationship != null) {
            this.validator.test( msg + " assert " + name, test);
        }
    }
    
    public AssertRelationship assertType(RelationshipType type) {
        test("type as " + type, () -> {
            if (!Objects.equals(type, relationship.getRelationshipType()) ) {
                this.validator.error(msg + " expected type " + type +
                        " was " + relationship.getRelationshipType());
            }
        });
        return this;
    }
    
    public AssertRelationship isUnidirectional() {
        return assertUnidirectional(true);
    }
    
    public AssertRelationship isNotUnidirectional() {
        return assertUnidirectional(false);
    }
    
    public AssertRelationship assertUnidirectional(boolean unidirectional) {
        test("is unidirectional " + unidirectional, () -> {
            if (!Objects.equals(unidirectional, relationship.isUnidirectional()) ) {
                this.validator.error(msg + " expected unidirectional " + 
                        unidirectional + " was " + relationship.isUnidirectional());
            }
        });
        return this;
    }
    
    public AssertRelationship assertTarget(String target) {
        test("target is " + target, () -> {
            if (!Objects.equals(target, relationship.getTarget().getName()) ) {
                this.validator.error(msg + " expected target " + target +
                        " was " + relationship.getTarget().getName());
            }
        });
        return this;
    }

    public AssertRelationship isRequired() {
        return assertRequired(true);
    }
    
    public AssertRelationship isNotRequired() {
        return assertRequired(false);
    }
    
    private AssertRelationship assertRequired(boolean required) {
        test("is " + (required ? "" : "not ") + "required", () -> {
            if (required != relationship.isRequired()) {
                this.validator.error(msg + " expected required " + 
                        required + " was " + relationship.isRequired());
            }
        });
        return this;
    }

    public AbstractValidator end() {
        validator.endTest();
        return validator;
    }
    
}
