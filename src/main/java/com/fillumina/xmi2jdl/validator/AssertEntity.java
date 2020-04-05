package com.fillumina.xmi2jdl.validator;

import com.fillumina.xmi2jdl.Entity;
import com.fillumina.xmi2jdl.Pagination;
import java.util.Optional;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertEntity extends AbstractAssertor<AssertEntity> {

    private final Entity entity;
    
    AssertEntity(AbstractValidator validator, String entityName) {
        super(validator, "entity " + entityName);
        
        Optional<Entity> entOpt = validator.findEntityByName(entityName);
        this.entity = testValidIfNotNull("entity " + entityName, entOpt.get());
    }
    
    protected AssertEntity assertPagination(Pagination pagination) {
        return assertEquals("pagination", pagination, entity.getPagination());
    }
    
    protected AssertEntity isFilter() {
        return assertFilter(true);
    }
    
    protected AssertEntity isNotFilter() {
        return assertFilter(false);
    }
    
    protected AssertEntity assertFilter(boolean filter) {
        return assertEquals("filter", filter, entity.isFilter());
    }
    
    protected AssertEntity isSkipClient() {
        return assertSkipClient(true);
    }
    
    protected AssertEntity isNotSkipClient() {
        return assertSkipClient(false);
    }
    
    protected AssertEntity assertSkipClient(boolean skipClient) {
        return assertEquals("skipClient", skipClient, entity.isSkipClient());
    }

    protected AssertEntity isSkipServer() {
        return assertSkipServer(true);
    }

    protected AssertEntity isNotSkipServer() {
        return assertSkipServer(false);
    }
    
    protected AssertEntity assertSkipServer(boolean skipServer) {
        return assertEquals("skipServer", skipServer, entity.isSkipServer());
    }
}
