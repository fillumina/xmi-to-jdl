package com.fillumina.xmi2jdl.validator;

import com.fillumina.xmi2jdl.DataTypeRef;
import com.fillumina.xmi2jdl.Entity;
import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertAttribute {

    private final AbstractValidator validator;
    private final String msg;
    private DataTypeRef attribute;
    
    public AssertAttribute(AbstractValidator validator,
            String entityName, String attributeName) {
        this.validator = validator;
        this.msg = "enitity " + entityName + 
                " attribute " + attributeName + ": ";
        Optional<Entity> entityOpt = validator.findEntityByName(entityName);
        if (entityOpt.isEmpty()) {
            validator.test(msg, () -> {
                validator.error("entity " + entityName + " doesn't exist");
            });
        } else {
            Entity entity = entityOpt.get();
            Optional<DataTypeRef> attrOpt = entity.getFieldByName(attributeName);
            if (attrOpt.isEmpty()) {
                validator.test(msg, () -> {
                    validator.error("entity " + entityName + 
                            " attribute " + attributeName + " doesn't exist");
                });
            } else {
                attribute = attrOpt.get();
            }
        }
    }

    private void test(String name, Runnable test) {
        if (attribute != null) {
            this.validator.test( msg + " assert " + name, test);
        }
    }
    
    public AssertAttribute assertType(String type) {
        test(msg + " type is " + type, () -> {
            if (!Objects.equals(
                    type,
                    attribute.getDataType().getName())) { 
                validator.error(msg + " expected type is " + type + " was " +
                        attribute.getDataType().getName());
            }
        });
        return this;
    }

    public AssertAttribute isDisplay() {
        return assertDisplay(true);
    }

    public AssertAttribute isNotDisplay() {
        return assertDisplay(false);
    }
    
    public AssertAttribute assertDisplay(boolean display) {
        test(msg + " display is " + display, () -> {
            if (!Objects.equals(
                    display,
                    attribute.isDisplay())) { 
                validator.error(msg + " expected display is " + display + " was " +
                        attribute.isDisplay());
            }
        });
        return this;
    }
}
