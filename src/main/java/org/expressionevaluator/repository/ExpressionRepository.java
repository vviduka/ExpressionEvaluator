package org.expressionevaluator.repository;

import org.expressionevaluator.entity.Expression;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ExpressionRepository extends JpaRepository<Expression, Long> {
    Expression findByName(String name);

    void deleteByName(String name);

}
