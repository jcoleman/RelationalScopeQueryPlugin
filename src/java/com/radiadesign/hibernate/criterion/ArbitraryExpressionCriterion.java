package com.radiadesign.hibernate.criterion;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.engine.TypedValue;
import org.hibernate.util.StringHelper;
import java.util.Map;
import com.radiadesign.relationalscope.expression.ExpressionBase;


public class ArbitraryExpressionCriterion implements Criterion {

  private final ExpressionBase lhs;
  private final ExpressionBase rhs;
  private final String operator;
  private final Map options;

  private static final TypedValue[] NO_VALUES = new TypedValue[0];

  protected ArbitraryExpressionCriterion(ExpressionBase _lhs, ExpressionBase _rhs, String _operator, Map _options) {
    lhs = _lhs;
    rhs = _rhs;
    operator = _operator;
    options = _options;
  }

  public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
  throws HibernateException {
    return "(" + this.getLhsStringValue(criteria, criteriaQuery) + " " + operator + " " + this.getRhsStringValue(criteria, criteriaQuery) + ")";
  }
  
  public String getLhsStringValue(Criteria criteria, CriteriaQuery criteriaQuery) {
    return lhs.getSqlStringForHibernate(criteria, criteriaQuery, options);
  }
  
  public String getRhsStringValue(Criteria criteria, CriteriaQuery criteriaQuery) {
    return rhs.getSqlStringForHibernate(criteria, criteriaQuery, options);
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
    return NO_VALUES;
  }

  public String toString() {
    return "(" + lhs.toString() + " " + operator + " " + rhs.toString() + ")";
  }

}