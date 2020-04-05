package com.fillumina.xmi2jdl.validator;

import java.util.Objects;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AbstractAssertor<T extends AbstractAssertor<?>> {

    private final AbstractValidator validator;
    private final String msg;
    private boolean valid;
    
    public AbstractAssertor(AbstractValidator validator, String message) {
        this.validator = validator;
        this.msg = message;
    }

    protected <T> T testValidIfNotNull(String name, T value) {
        this.validator.test( msg + " assert not null " + name, () -> {
            if (value == null) {
                this.validator.error(msg + " " + name + " is null");
            }
        });
        this.valid = value != null;
        return value;
    }
    
    protected void test(String name, Runnable test) {
        if (valid) {
            this.validator.test( msg + " assert " + name, test);
        }
    }
    
    public T assertEquals(String what, Object shouldBe, Object is) {
        test(msg + " " + what, () -> {
            if (!Objects.equals(shouldBe, is)) {
                validator.error(msg + " expected " + what + " be " + shouldBe + 
                        " was " + is);
            }
        });
        return (T) this;
    }
}
