package com.fillumina.xmi2jdl;

import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface EntityDiagram {

    Map<String, DataType> getDataTypes();

    Map<String, Entity> getEntities();

    Map<String, Enumeration> getEnumerations();
    
    default EntityDiagram exec(EntityDiagramConsumer consumer) {
        consumer.consume(this);
        return this;
    }
}
