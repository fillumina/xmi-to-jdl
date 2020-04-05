package com.fillumina.xmi2jdl.validator;

import com.fillumina.xmi2jdl.DataTypeRef;
import com.fillumina.xmi2jdl.Entity;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertAttribute extends AbstractAssertor<AssertAttribute> {

    private DataTypeRef attribute;
    
    public AssertAttribute(AbstractValidator validator,
            String entityName, String attributeName) {
        super(validator, "enitity " + entityName + 
                " attribute " + attributeName + ": ");
        
        Entity entity = testValidIfNotNull("entity", 
                validator.findEntityByName(entityName).get());
        if (entity != null) {
            attribute = testValidIfNotNull("attribute", 
                    entity.getFieldByName(attributeName).get());
        }
    }
    
    public AssertAttribute assertValidation(String validation) {
        return assertEqualTokens("validation", validation, 
                () -> attribute.getValidation());
    }

    public AssertAttribute assertType(String type) {
        return assertEquals("type", type, () -> attribute.getDataType().getName());
    }
    
    public AssertAttribute isDisplay() {
        return assertDisplay(true);
    }

    public AssertAttribute isNotDisplay() {
        return assertDisplay(false);
    }
    
    public AssertAttribute assertDisplay(boolean display) {
        return assertEquals("display", display, () -> attribute.isDisplay());
    }
}
