package fr.insee.vtl.engine;

import fr.insee.vtl.engine.exceptions.UndefinedVariableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class VtlScriptEngineTest {

    private ScriptEngine engine;

    @BeforeEach
    public void setUp() {
        engine = new ScriptEngineManager().getEngineByName("vtl");
    }

    @Test
    public void testExceptions() {
        assertThatThrownBy(() -> {
            engine.eval("var := undefinedVariable + 42");
        }).isInstanceOf(UndefinedVariableException.class)
                .hasMessage("undefined variable undefinedVariable");
    }
}