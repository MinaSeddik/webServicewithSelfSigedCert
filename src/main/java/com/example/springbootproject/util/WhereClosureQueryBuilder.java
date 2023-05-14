package com.example.springbootproject.util;

import com.example.springbootproject.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


@Slf4j
public class WhereClosureQueryBuilder {

    private List<String> clauses = new ArrayList<String>();
    private List<Object> data = new ArrayList<Object>();

    public WhereClosureQueryBuilder(BookSearchCriteria bookSearchCriteria)  {
        this(bookSearchCriteria, false);
    }

    public WhereClosureQueryBuilder(BookSearchCriteria bookSearchCriteria, boolean useAlias) {

        Field[] fields = bookSearchCriteria.getClass().getDeclaredFields();

        for (Field field : fields) {

            log.info("Field: {}", field.getName());
            ColumnName annotation = field.getAnnotation(ColumnName.class);
            if (annotation != null) {

                String columnName = annotation.value();


                field.setAccessible(true);
                Class<?> targetType = field.getType();
                Object value = null;

                try {
                    value = field.get(bookSearchCriteria);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

                log.info("  -> Column Name: {}", columnName);
                log.info("  -> Field Value: {}", value);
                log.info("  -> Field Type: {} , isPrimitiveOrWrapper = {}", targetType, ClassUtils.isPrimitiveOrWrapper(targetType));

                if (value != null) {

                    if (ClassUtils.isPrimitiveOrWrapper(targetType) || targetType.equals(String.class)) {
                        log.info("  -> Handler: handleSingleValue");
                        handleSingleValue(columnName, value);

                    } else if (targetType.equals(BookStatus.class)) {
                        log.info("  -> Handler: handleBookStatusEnumValue");
                        handleBookStatusEnumValue(columnName, value);

                    } else if (Collection.class.isAssignableFrom(targetType)) {
                        log.info("  -> Handler: handleBookCollectionValue");
                        handleBookCollectionValue(columnName, value);

                    } else if (targetType.equals(LocalDate.class)) {
                        log.info("  -> Handler: handleLocalDateValue");
                        handleLocalDateValue(columnName, value);

                    } else if (targetType.equals(LocalDateTime.class)) {
                        log.info("  -> Handler: handleLocalDateTimeValue");
                        handleLocalDateTimeValue(columnName, value);

                    } else if (targetType.equals(DateRange.class)) {
                        log.info("  -> Handler: handleDateRangeValue");
                        handleDateRangeValue(columnName, value);

                    }


                }

            } else {
                ColumnNames columnNamesAnnotation = field.getAnnotation(ColumnNames.class);
                if (columnNamesAnnotation != null) {

                    String[] columnNames = columnNamesAnnotation.value();


                    field.setAccessible(true);
                    Object value = null;
                    try {
                        value = field.get(bookSearchCriteria);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }

                    if (value != null) {
                        handleMultipleValues(columnNames, value);
                    }
                }
            }
        }
    }

    private void handleMultipleValues(String[] columnNames, Object value) {

        int length = columnNames.length;
        if (length > 0) {
            StringBuffer stringBuffer = new StringBuffer("( ");

            for (int i = 0; i < length; i++) {

                stringBuffer.append("(" + columnNames[i] + " = ?)");
                data.add(value);

                stringBuffer.append(i < length - 1 ? " OR " : "");
            }

            stringBuffer.append(" )");
            clauses.add(stringBuffer.toString());

        }

    }

    private void handleSingleValue(String columnName, Object value) {
        String clause = "(" + columnName + " = ?)";

        clauses.add(clause);
        data.add(value);
    }

    private void handleBookStatusEnumValue(String columnName, Object value) {
        BookStatus bookStatus = (BookStatus) value;
        String clause = "(" + columnName + " = ?)";

        String statusValue = bookStatus.name();
        clauses.add(clause);
        data.add(statusValue);
    }

    private void handleBookCollectionValue(String columnName, Object value) {
        Collection<?> collection = (Collection<?>) value;

        String inParameters = String.join(",", Collections.nCopies(collection.size(), "?"));

        String clause = "(" + columnName + " IN (" + inParameters + ") )";

        clauses.add(clause);
        data.addAll(collection);
    }

    private void handleLocalDateValue(String columnName, Object value) {
        LocalDate localDate = (LocalDate) value;
        Date date = Date.valueOf(localDate);

        String clause = "(" + columnName + " = ?)";

        clauses.add(clause);
        data.add(date);
    }

    private void handleDateRangeValue(String columnName, Object value) {
        DateRange dateRange = (DateRange) value;

        LocalDate start = dateRange.getStartDate();
        LocalDate end = dateRange.getEndDate();

        Date from = Date.valueOf(start);
        Date to = Date.valueOf(end);


        String clause = "(" + columnName + " BETWEEN ? AND ?)";

        clauses.add(clause);

        data.add(from);
        data.add(to);
    }

    private void handleLocalDateTimeValue(String columnName, Object value) {
        LocalDateTime localDateTime = (LocalDateTime) value;
        Timestamp timestamp = Timestamp.valueOf(localDateTime);

        String clause = "(" + columnName + " = ?)";

        clauses.add(clause);
        data.add(timestamp);
    }

    public String getSqlClause() {
        return String.join(" AND ", clauses);
    }

    public List<Object> getSqlParameter() {
        return data;
    }
}
