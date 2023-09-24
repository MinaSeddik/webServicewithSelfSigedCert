package com.example.springbootproject.repository.impl;

import com.example.springbootproject.domain.Account2;
import com.example.springbootproject.domain.Person2;
import com.example.springbootproject.domain.Person2ResultSet;
import com.example.springbootproject.domain.Person2_;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
@Slf4j
public class JpaCustomPersonRepositoryImpl implements JpaCustomPersonRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /*
        Reference: https://www.baeldung.com/hibernate-criteria-queries
     */
    @Override
    public List<Person2> findUserByEmails(Set<String> emails) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Person2> criteriaQuery = cb.createQuery(Person2.class);
        Root<Person2> person2Root = criteriaQuery.from(Person2.class);

        // to select all records
//        query.select(person2Root);

        Path<String> emailPath = person2Root.get("email");

        List<Predicate> predicates = new ArrayList<>();
        for (String email : emails) {
            predicates.add(cb.like(emailPath, email));
        }
        criteriaQuery.select(person2Root)
                .where(cb.or(predicates.toArray(new Predicate[predicates.size()])))
                .orderBy(cb.asc(person2Root.get("itemName")),
                        cb.desc(person2Root.get("itemPrice")));

        return entityManager.createQuery(criteriaQuery)
                .getResultList();
    }

    @Override
    public List<Person2> findWhatever(Set<String> params) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
        Root<Person2> root = criteriaQuery.from(Person2.class);
        criteriaQuery.select(cb.count(root));

//        entityManager.createQuery(criteriaQuery)
//                .getResultList();
        TypedQuery<Long> query = entityManager.createQuery(criteriaQuery);
        List<Long> itemProjected = query.getResultList();


        CriteriaQuery<Double> criteriaQuery2 = cb.createQuery(Double.class);
        Root<Person2> root2 = criteriaQuery2.from(Person2.class);
        criteriaQuery2.select(cb.avg(root2.get("itemPrice")));
        List<Double> itemProjected2 = entityManager.createQuery(criteriaQuery2)
                .getResultList();


        CriteriaQuery<Double> criteriaQuery3 = cb.createQuery(Double.class);
        Root<Person2> root3 = criteriaQuery3.from(Person2.class);
        criteriaQuery2.select(cb.avg(root2.get("itemPrice")));

        List<Double> itemProjected3 = entityManager.createQuery(criteriaQuery2)
                .getResultList();


//        Create a CriteriaQuery object with the specified result type.
        CriteriaQuery<Person2ResultSet> criteriaQuery6 = cb.createQuery(Person2ResultSet.class);
        Root<Person2> root6 = criteriaQuery6.from(Person2.class);

        //--------------------------------------------------------
        criteriaQuery6.multiselect(root6.get("itemCategory").alias("alias"),
                cb.count(root6  .get("itemCategory")));
        // or
        Expression<Number> priceMinExpression = cb.min(root.get("price"));
        Expression<Number> priceMaxExpression = cb.max(root.get("price"));

        // Create path and parameter expressions:
        Expression<String> path = root.get("name");
        Expression<String> param = cb.parameter(String.class);

        // str [NOT] LIKE pattern
        Predicate p1 = cb.like(path, param);
        Predicate p2 = cb.like(path, "a%");
        Predicate p3 = cb.notLike(path, param);
        Predicate p4 = cb.notLike(path, "a%");
        // additional methods take also an escape character

        // LENGTH(str)
        Expression<Integer> length = cb.length(path);

        // LOCATE(str, substr [, start])
        Expression<Integer> l1 = cb.locate(path, param);
        Expression<Integer> l2 = cb.locate(path, "x");
        Expression<Integer> l3 = cb.locate(path, param, cb.literal(2));
        Expression<Integer> l4 = cb.locate(path, "x", 2);

        // LOWER(str) and UPPER(str)
        Expression<String> lower = cb.lower(path);
        Expression<String> upper = cb.upper(param);

        // TRIM([[LEADING|TRAILING|BOTH] [char] FROM] str)
        Expression<String> t1 = cb.trim(path);
//        Expression<String> t2 = cb.trim(literal(' '), path);
        Expression<String> t3 = cb.trim(' ', path);
        Expression<String> t4 = cb.trim(CriteriaBuilder.Trimspec.BOTH, path);
//        Expression<String> t5 = cb.trim(CriteriaBuilder.Trimspec.LEADING, literal(' '), path);
        Expression<String> t6 = cb.trim(CriteriaBuilder.Trimspec.TRAILING, ' ', path);

        // CONCAT(str1, str2)
        Expression<String> c1 = cb.concat(path, param);
        Expression<String> c2 = cb.concat(path, ".");
        Expression<String> c3 = cb.concat("the", path);

        // SUBSTRING(str, pos [, length])
        Expression<String> s1 = cb.substring(path, cb.literal(2));
        Expression<String> s2 = cb.substring(path, 2);
        Expression<String> s3 =
                cb.substring(path, cb.literal(2), cb.literal(3));
        Expression<String> s4 = cb.substring(path, 2, 3);

        Path<Object> id = root.get("category").get("id");
        Selection<Number> Qtyalias = cb.sum(root.get("quantity")).alias("xxx");
        criteriaQuery6.select(cb.construct(
                Person2ResultSet.class,
                id,
                priceMinExpression,
                priceMaxExpression,
                Qtyalias,

//                register mysql function
//                https://vladmihalcea.com/hibernate-sql-function-jpql-criteria-api-query/
                cb.function("group_concat", String.class, root.get("name")),

                cb.count(root.get("id")),
                cb.avg(root.get("price"))
        ));
        //--------------------------------------------------------

        criteriaQuery6.where((Expression) null);
        criteriaQuery6.groupBy(root.get("itemCategory"));


        List<Person2ResultSet> result = entityManager.createQuery(criteriaQuery6)
                .getResultList();


        // join example
        CriteriaQuery<Person2ResultSet> criteriaQuery7 = cb.createQuery(Person2ResultSet.class);
        Root<Person2> person2Root = criteriaQuery7.from(Person2.class);

        // the next 2 statements are the same
        Join<Person2, Account2> projectJoin = person2Root.join("books", JoinType.LEFT);
//        Join<Person2, Account2> projectJoinAsTypeSafe = person2Root.join(Person2_.ACCOUNTS, JoinType.LEFT);

        criteriaQuery7.groupBy(person2Root.get("firstName"));
//        criteriaQuery7.multiselect(person2Root, person2Root);

        criteriaQuery7.multiselect(
                projectJoin.get(Person2_.ID).alias("projectId"),
                projectJoin.get(Person2_.FIRST_NAME).alias("name"),
                cb.count(root.get("id")).alias("cnt")
        );

        List<Person2ResultSet> result2 = entityManager.createQuery(criteriaQuery7)
                .getResultList();

        // sub-query
//        https://stackoverflow.com/questions/4483576/jpa-2-0-criteria-api-subqueries-in-expressions




        // update
        double newPrice = 12;
        double oldPrice = 10;
        CriteriaUpdate<Person2> criteriaUpdate = cb.createCriteriaUpdate(Person2.class);
        Root<Person2> root4 = criteriaUpdate.from(Person2.class);
        criteriaUpdate.set("itemPrice", newPrice);
        criteriaUpdate.where(cb.equal(root4.get("itemPrice"), oldPrice));
        entityManager.createQuery(criteriaUpdate)
                .executeUpdate();


        // delete
        double targetPrice = 150;
        CriteriaDelete<Person2> criteriaDelete = cb.createCriteriaDelete(Person2.class);
        Root<Person2> root5 = criteriaDelete.from(Person2.class);
        criteriaDelete.where(cb.greaterThan(root5.get("itemPrice"), targetPrice));
        entityManager.createQuery(criteriaUpdate)
                .executeUpdate();


        return null;

    }
}
