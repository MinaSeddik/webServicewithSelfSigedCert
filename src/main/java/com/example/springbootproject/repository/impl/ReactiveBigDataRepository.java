package com.example.springbootproject.repository.impl;

import com.example.springbootproject.model.BigRecord;
import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class ReactiveBigDataRepository {

    @Autowired
    private ConnectionFactory connectionFactory;

    public Flux<BigRecord> fetchAllRecords() {

        String selectSql = "SELECT * FROM big_data";

        return Mono.from(connectionFactory.create())
                .flatMapMany(
                        c -> Flux.from(c.createStatement(selectSql)
                                .execute())
                )
                .log()
                .doOnNext(result -> log.info("Receiving Result: " + result.toString()))
                .flatMap(result -> result
                        .map((row, rowMetadata) -> {

//                            rowMetadata.getColumnMetadatas()
//                                    .forEach(columnMetadata -> log.info("column name:{}, type: {}", columnMetadata.getName(), columnMetadata.getJavaType()));


                            String Series_reference = row.get("Series_reference", String.class);
                            Double Period = row.get("Period", Double.class);
                            String Data_value = row.get("Data_value", String.class);
                            String Suppressed = row.get("Suppressed", String.class);
                            String STATUS = row.get("STATUS", String.class);
                            String UNITS = row.get("UNITS", String.class);
                            Integer MAGNTUDE = row.get("MAGNTUDE", Integer.class);
                            String Subject = row.get("Subject", String.class);
                            String Grp = row.get("Grp", String.class);
                            String Series_title_1 = row.get("Series_title_1", String.class);

                            return BigRecord.builder()
                                    .Series_reference(Series_reference)
                                    .Period(Period)
                                    .Data_value(Data_value)
                                    .Suppressed(Suppressed)
                                    .STATUS(STATUS)
                                    .UNITS(UNITS)
                                    .MAGNTUDE(MAGNTUDE)
                                    .Subject(Subject)
                                    .Grp(Grp)
                                    .Series_title_1(Series_title_1)
                                    .build();
                        }));
//                .doOnNext(data -> log.info(": {}", data));

    }

}
