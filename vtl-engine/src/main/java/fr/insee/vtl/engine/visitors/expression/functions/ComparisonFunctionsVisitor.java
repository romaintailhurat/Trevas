package fr.insee.vtl.engine.visitors.expression.functions;

import fr.insee.vtl.engine.exceptions.ConflictingTypesException;
import fr.insee.vtl.engine.exceptions.InvalidTypeException;
import fr.insee.vtl.engine.exceptions.VtlRuntimeException;
import fr.insee.vtl.engine.utils.TypeChecking;
import fr.insee.vtl.engine.visitors.expression.ExpressionVisitor;
import fr.insee.vtl.model.ResolvableExpression;
import fr.insee.vtl.parser.VtlBaseVisitor;
import fr.insee.vtl.parser.VtlParser;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <code>ComparisonFunctionsVisitor</code> is the base visitor for expressions involving comparison functions.
 */
public class ComparisonFunctionsVisitor extends VtlBaseVisitor<ResolvableExpression> {

    private final ExpressionVisitor exprVisitor;

    /**
     * Constructor taking a scripting context.
     *
     * @param context The expression visitor.
     */
    public ComparisonFunctionsVisitor(ExpressionVisitor expressionVisitor) {
        exprVisitor = Objects.requireNonNull(expressionVisitor);
    }


    /**
     * Visits a 'between' expression with scalar operand and delimiters.
     *
     * @param ctx The scripting context for the expression.
     * @return A <code>ResolvableExpression</code> resolving to a boolean (<code>true</code> if the operand is between the delimiters).
     */
    @Override
    public ResolvableExpression visitBetweenAtom(VtlParser.BetweenAtomContext ctx) {
        ResolvableExpression operandExpression = exprVisitor.visit(ctx.op);
        ResolvableExpression fromExpression = exprVisitor.visit(ctx.from_);
        ResolvableExpression toExpression = exprVisitor.visit(ctx.to_);

        if (operandExpression.getType() != fromExpression.getType() &&
                operandExpression.getType() != toExpression.getType())
            throw new VtlRuntimeException(new ConflictingTypesException(
                            List.of(operandExpression.getType(), fromExpression.getType(), toExpression.getType()),
                            ctx
                    ));
        // TODO: handle other types (dates?)
        if (TypeChecking.isLong(operandExpression))
            return ResolvableExpression.withType(Boolean.class, context -> {
                Long operandValue = (Long) operandExpression.resolve(context);
                Long fromValue = (Long) fromExpression.resolve(context);
                Long toValue = (Long) toExpression.resolve(context);
                return operandValue >= fromValue && operandValue <= toValue;
            });
        return ResolvableExpression.withType(Boolean.class, context -> {
            Double operandValue = (Double) operandExpression.resolve(context);
            Double fromValue = (Double) fromExpression.resolve(context);
            Double toValue = (Double) toExpression.resolve(context);
            return operandValue >= fromValue && operandValue <= toValue;
        });
    }

    /**
     * Visits a pattern matching expression with string operand and regular expression.
     *
     * @param ctx The scripting context for the expression.
     * @return A <code>ResolvableExpression</code> resolving to a boolean (<code>true</code> if the operand matches the pattern).
     */
    @Override
    public ResolvableExpression visitCharsetMatchAtom(VtlParser.CharsetMatchAtomContext ctx) {
        ResolvableExpression operandExpression = exprVisitor.visit(ctx.op);
        ResolvableExpression patternExpression = exprVisitor.visit(ctx.pattern);

        if (!operandExpression.getType().equals(String.class)) {
            throw new VtlRuntimeException(
                    new InvalidTypeException(String.class, operandExpression.getType(), ctx.op)
            );
        }
        if (!patternExpression.getType().equals(String.class)) {
            throw new VtlRuntimeException(
                    new InvalidTypeException(String.class, patternExpression.getType(), ctx.pattern)
            );
        }

        return ResolvableExpression.withType(Boolean.class, context -> {
            String operandValue = (String) operandExpression.resolve(context);
            String patternValue = (String) patternExpression.resolve(context);
            Pattern pattern = Pattern.compile(patternValue);
            Matcher matcher = pattern.matcher(operandValue);
            return matcher.matches();
        });
    }

    /**
     * Visits a null testing expression with scalar operand.
     *
     * @param ctx The scripting context for the expression.
     * @return A <code>ResolvableExpression</code> resolving to a boolean (<code>true</code> if the operand is null).
     */
    @Override
    public ResolvableExpression visitIsNullAtom(VtlParser.IsNullAtomContext ctx) {
        ResolvableExpression operandExpression = exprVisitor.visit(ctx.expr());
        return ResolvableExpression.withType(Boolean.class, context ->
                operandExpression.resolve(context) == null
        );
    }
    
}
