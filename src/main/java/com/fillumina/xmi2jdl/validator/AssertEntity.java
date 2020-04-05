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
        this.entity = testValidIfNotNull("", entOpt.get());
    }

    public AssertEntity isPagination() {
        return assertPagination(Pagination.pagination);
    }

    public AssertEntity isInfiniteScroll() {
        return assertPagination(Pagination.infinite);
    }

    public AssertEntity assertPagination(Pagination pagination) {
        return assertEquals("pagination", pagination, entity.getPagination());
    }
    
    public AssertEntity isFilter() {
        return assertFilter(true);
    }
    
    public AssertEntity isNotFilter() {
        return assertFilter(false);
    }
    
    public AssertEntity assertFilter(boolean filter) {
        return assertEquals("filter", filter, entity.isFilter());
    }
    
    public AssertEntity isSkipClient() {
        return assertSkipClient(true);
    }
    
    public AssertEntity isNotSkipClient() {
        return assertSkipClient(false);
    }
    
    public AssertEntity assertSkipClient(boolean skipClient) {
        return assertEquals("skipClient", skipClient, entity.isSkipClient());
    }

    public AssertEntity isSkipServer() {
        return assertSkipServer(true);
    }

    public AssertEntity isNotSkipServer() {
        return assertSkipServer(false);
    }
    
    public AssertEntity assertSkipServer(boolean skipServer) {
        return assertEquals("skipServer", skipServer, entity.isSkipServer());
    }
}
