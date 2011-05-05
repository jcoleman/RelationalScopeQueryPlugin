package com.radiadesign.hibernate.criterion;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.Criteria;
import org.hibernate.type.TypeFactory;
import org.hibernate.type.Type;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.engine.TypedValue;
import org.hibernate.util.StringHelper;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.io.StringWriter;
import com.radiadesign.relationalscope.expression.ExpressionBase;
import com.radiadesign.relationalscope.expression.ArithmeticExpression;
import com.radiadesign.relationalscope.expression.ValueExpression;
import com.radiadesign.relationalscope.expression.ListExpression;
import com.radiadesign.relationalscope.expression.AbstractPropertyExpression;


public class ArbitraryExpressionCriterion implements Criterion {
  
  private final ExpressionBase lhs;
  private final ExpressionBase rhs;
  private final String operator;
  private final Map options;
  private final boolean treatRhsAsBinaryTuple;
  
  
  protected ArbitraryExpressionCriterion(ExpressionBase _lhs, ExpressionBase _rhs, String _operator, Map _options, boolean _treatRhsAsBinaryTuple) {
    lhs = _lhs;
    rhs = _rhs;
    operator = _operator;
    options = _options;
    treatRhsAsBinaryTuple = _treatRhsAsBinaryTuple;
  }
  
  protected ArbitraryExpressionCriterion(ExpressionBase _lhs, ExpressionBase _rhs, String _operator, Map _options) {
    this(_lhs, _rhs, _operator, _options, false);
  }
  
  public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
  throws HibernateException {
    StringWriter sqlWriter = new StringWriter();
    sqlWriter.append('(');
    if (lhs != null) {
      if (rhs == null) { sqlWriter.append('('); }
      appendSqlStringForExpression(lhs, sqlWriter, criteria, criteriaQuery);
      if (rhs == null) { sqlWriter.append(')'); }
      sqlWriter.append(' ');
    }
    sqlWriter.append(operator);
    if (rhs != null) {
      sqlWriter.append(' ');
      appendSqlStringForExpression(rhs, sqlWriter, criteria, criteriaQuery);
    }
    sqlWriter.append(')');
    return sqlWriter.toString();
  }
  
  public void appendSqlStringForExpression(ExpressionBase expr, StringWriter sqlWriter, Criteria criteria, CriteriaQuery criteriaQuery) {
    if (expr instanceof ArithmeticExpression) {
      ExpressionBase nextlhs = ((ArithmeticExpression)expr).getLhs();
      boolean parenthesizeLeft = (nextlhs instanceof ArithmeticExpression);
      ExpressionBase nextrhs = ((ArithmeticExpression)expr).getRhs();
      boolean parenthesizeRight = (nextrhs instanceof ArithmeticExpression);
      
      if (parenthesizeLeft) { sqlWriter.append('('); }
      appendSqlStringForExpression(nextlhs, sqlWriter, criteria, criteriaQuery);
      if (parenthesizeLeft) { sqlWriter.append(')'); }
      
      sqlWriter.append(' ');
      sqlWriter.append(((ArithmeticExpression)expr).getOperator());
      sqlWriter.append(' ');
      
      if (parenthesizeRight) { sqlWriter.append('('); }
      appendSqlStringForExpression(nextrhs, sqlWriter, criteria, criteriaQuery);
      if (parenthesizeRight) { sqlWriter.append(')'); }
    } else if (expr instanceof AbstractPropertyExpression) {
      sqlWriter.append( getSingleColumnForPropertyName( ((AbstractPropertyExpression)expr).propertyFor(options).toString(),
                                                        criteria,
                                                        criteriaQuery ) );
    } else if (expr instanceof ListExpression) {
      List list = (List)((ListExpression)expr).getValue();
      if (treatRhsAsBinaryTuple) {
        appendSqlStringForExpression((ExpressionBase)(list.get(0)), sqlWriter, criteria, criteriaQuery);
        sqlWriter.append(" AND ");
        appendSqlStringForExpression((ExpressionBase)(list.get(1)), sqlWriter, criteria, criteriaQuery);
      } else {
        sqlWriter.append('(');
        for (int i = 0, len = list.size(); i < len; ++i) {
          if (i > 0) { sqlWriter.append(", "); }
          appendSqlStringForExpression((ExpressionBase)(list.get(i)), sqlWriter, criteria, criteriaQuery);
        }
        sqlWriter.append(')');
      }
    } else if (expr instanceof ValueExpression) {
      sqlWriter.append(" ? ");
    }
  }
  
  public static String getSingleColumnForPropertyName(String propertyName, Criteria criteria, CriteriaQuery criteriaQuery) {
    String[] columns = criteriaQuery.getColumnsUsingProjection(criteria, propertyName);
    if (columns.length != 1) {
      throw new RuntimeException("RelationalScope's Hibernate extension ArbitraryExpressionCriterion does not support multi-column properties.");
    }
    return columns[0];
  }
  
  public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery)
  throws HibernateException {
    ArrayList<Type> types = new ArrayList<Type>();
    ArrayList<Object> values = new ArrayList<Object>();
    addTypedValuesForExpression(lhs, types, values);
    if (rhs != null) {
      addTypedValuesForExpression(rhs, types, values);
    }
    TypedValue[] typedValues = new TypedValue[values.size()];
    for (int i = 0, len = values.size(); i < len; ++i) {
      typedValues[i] = new TypedValue(types.get(i), values.get(i), EntityMode.POJO);
    }
    return typedValues;
  }
  
  public void addTypedValuesForExpression(ExpressionBase expr, ArrayList types, ArrayList values) {
    if (expr instanceof ArithmeticExpression) {
      addTypedValuesForExpression(((ArithmeticExpression)expr).getLhs(), types, values);
      addTypedValuesForExpression(((ArithmeticExpression)expr).getRhs(), types, values);
    } else if (expr instanceof ListExpression) {
      for (Object item : (List)((ListExpression)expr).getValue()) {
        addTypedValuesForExpression((ExpressionBase)item, types, values);
      }
    } else if (expr instanceof ValueExpression) {
      Object value = ((ValueExpression)expr).getValue();
      values.add(value);
      if (value != null) {
        types.add(TypeFactory.heuristicType(value.getClass().getName()));
      } else {
        types.add(null);
      }
    } else if (expr instanceof AbstractPropertyExpression) {
      // Ignore this case...there will be no substituted types in this expression.
    }
  }
  
  public String toString() {
    return "(" + lhs.toString() + " " + operator + " " + rhs.toString() + ")";
  }
  
}