package fr.insee.vtl.engine.exceptions;

import java.util.Collection;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.tree.ParseTree;

/**
 * The <code>ConflictingTypesException</code> indicates a conflict between the types of elements used in an expression.
 */
public class ConflictingTypesException extends VtlScriptException {

    /**
     * Constructor taking the parsing context and the conflicting types.
     *
     * @param tree The parsing context where the exception is thrown.
     * @param types The conflicting types.
     */
    public ConflictingTypesException(ParseTree tree, Collection<Class<?>> types) {

        // TODO Wouldn't it be more logical to invert the parameters of ConflictingTypesException in order to match parent logic?
        super(String.format("conflicting types: %s", types.stream().map(Class::getSimpleName)
                .collect(Collectors.toList())), tree);
    }
}
