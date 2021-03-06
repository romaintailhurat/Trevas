package fr.insee.vtl.engine.visitors.expression;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.insee.vtl.engine.exceptions.InvalidTypeException;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BooleanExprTest {

    private ScriptEngine engine;

    @BeforeEach
    public void setUp() {
        engine = new ScriptEngineManager().getEngineByName("vtl");
    }

    @Test
    public void testBooleans() throws ScriptException {
        ScriptContext context = engine.getContext();
        List<Boolean> a = List.of(false, false, true, true);
        List<Boolean> b = List.of(false, true, false, true);

        List<Boolean> and = List.of(false, false, false, true);
        List<Boolean> or = List.of(false, true, true, true);
        List<Boolean> xor = List.of(false, true, true, false);

        for (int i = 0; i < 4; i++) {
            context.setAttribute("a", a.get(i), ScriptContext.ENGINE_SCOPE);
            context.setAttribute("b", b.get(i), ScriptContext.ENGINE_SCOPE);

            engine.eval("" +
                    "andRes := a and b;" +
                    "orRes := a or b;" +
                    "xorRes := a xor b;"
            );
            assertThat(context.getAttribute("andRes")).isEqualTo(and.get(i));
            assertThat(context.getAttribute("orRes")).isEqualTo(or.get(i));
            assertThat(context.getAttribute("xorRes")).isEqualTo(xor.get(i));
        }
    }

    @Test
    public void testBooleanTypeExceptions() {
        assertThatThrownBy(() -> {
            engine.eval("s := 1 and 2;");
        }).isInstanceOf(InvalidTypeException.class)
                .hasMessage("invalid type Long, expected 1 to be Boolean");

        assertThatThrownBy(() -> {
            engine.eval("s := true or 2;");
        }).isInstanceOf(InvalidTypeException.class)
                .hasMessage("invalid type Long, expected 2 to be Boolean");
    }

    @Test
    public void testUnaryNot() throws ScriptException {
        ScriptContext context = engine.getContext();

        engine.eval("t := not false;");
        assertThat((Boolean) context.getAttribute("t")).isTrue();
        engine.eval("f := not true;");
        assertThat((Boolean) context.getAttribute("f")).isFalse();
//        engine.eval("n := not null;");
//        assertThat(context.getAttribute("n")).isNull();

        assertThatThrownBy(() -> {
            engine.eval("s := not 888;");
        }).isInstanceOf(InvalidTypeException.class)
                .hasMessage("invalid type Long, expected 888 to be Boolean");
    }

}
