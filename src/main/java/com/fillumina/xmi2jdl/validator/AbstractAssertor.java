package com.fillumina.xmi2jdl.validator;

import com.fillumina.xmi2jdl.util.StringHelper;
import java.util.Objects;
import java.util.function.Supplier;

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
    
    public <V> T assertEquals(String what, V shouldBe, Supplier<V> is) {
        test(msg + " " + what, () -> {
            V value = is.get();
            if (!Objects.equals(shouldBe, value)) {
                validator.error(msg + " expected " + what + " be " + shouldBe + 
                        " was " + value);
            }
        });
        return (T) this;
    }
    
    public T assertEqualTokens(String what, String shouldBe, 
            Supplier<String> is) {
        test(msg + " " + what, () -> {
            String value = is.get();
            if (!StringHelper.equalTokens(shouldBe, value)) {
                validator.error(msg + " expected " + what + " be " + shouldBe + 
                        " was " + value);
            }
        });
        return (T) this;
    }
}
