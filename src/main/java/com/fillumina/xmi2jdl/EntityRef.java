package com.fillumina.xmi2jdl;

import java.io.IOException;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class EntityRef extends Reference {

    private final Entity owner;
    private final Entity target;
    private final String attributeName;

    public EntityRef(Entity owner,
            Entity target,
            String attributeName,
            String comment) {
        super(attributeName, comment);
        this.owner = owner;
        this.target = target;
        this.attributeName = attributeName;
    }

    @Override
    public void append(Appendable buf) throws IOException {
        buf
                .append('\t')
                .append(owner.getName())
                .append("{")
                .append(attributeName)
                .append("} to ")
                .append(target.getName())
                .append(System.lineSeparator());
    }
}
