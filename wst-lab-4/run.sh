#!/bin/bash
mkdir -p containers/glassfish/tobeautostarted/
cp embeddable/target/embeddable-1.0.war containers/glassfish/tobeautostarted/
docker-compose up --build
