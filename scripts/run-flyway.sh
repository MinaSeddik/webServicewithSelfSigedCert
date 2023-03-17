#!/bin/bash


mvn flyway:clean
mvn flyway:clean -P local
mvn flyway:clean -P dev
mvn flyway:clean -P qa


mvn flyway:migrate
mvn flyway:migrate -P local
mvn flyway:migrate -P dev
mvn flyway:migrate -P qa

## more commands

#Info: Prints current status/version of a database schema. It prints which migrations are pending, which migrations have been applied, the status of applied migrations, and when they were applied.
#Migrate: Migrates a database schema to the current version. It scans the classpath for available migrations and applies pending migrations.
#Baseline: Baselines an existing database, excluding all migrations, including baselineVersion. Baseline helps to start with Flyway in an existing database. Newer migrations can then be applied normally.
#Validate: Validates current database schema against available migrations.
#Repair: Repairs metadata table.
#Clean: Drops all objects in a configured schema. Of course, we should never use clean on any production database.
