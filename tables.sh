#!/usr/bin/env bash

mvn resources:resources
cp target/classes/liquibase.tables.properties target/classes/liquibase.properties
mvn liquibase:clearCheckSums liquibase:update