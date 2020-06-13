package edu.berkeley.nlp.assignments.parsing.ling.tokensregex;

import edu.berkeley.nlp.assignments.parsing.ling.tokensregex.types.Expression;
import edu.berkeley.nlp.assignments.parsing.ling.tokensregex.types.Expressions;
import edu.berkeley.nlp.assignments.parsing.ling.tokensregex.types.Value;
import edu.berkeley.nlp.assignments.parsing.util.CoreMap;

/**
 * Pattern for matching a CoreMap using a generic expression
 *
 * @author Angel Chang
 */
public class CoreMapExpressionNodePattern extends NodePattern<CoreMap> {

  Env env;
  Expression expression;

  public CoreMapExpressionNodePattern() {}

  public CoreMapExpressionNodePattern(Env env, Expression expression) {
    this.env = env;
    this.expression = expression;
  }

  public static CoreMapExpressionNodePattern valueOf(Expression expression) {
    return valueOf(null, expression);
  }

  public static CoreMapExpressionNodePattern valueOf(Env env, Expression expression) {
    CoreMapExpressionNodePattern p = new CoreMapExpressionNodePattern(env, expression);
    return p;
  }

  @Override
  public boolean match(CoreMap token) {
    Value v = expression.evaluate(env, token);
    Boolean matched = Expressions.convertValueToBoolean(v, false);
    return (matched != null)? matched:false;
  }

  public String toString() {
    return expression.toString();
  }

}
