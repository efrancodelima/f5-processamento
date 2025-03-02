#!/bin/bash

export H2_USERNAME=sa
export H2_PASSWORD=password
export URL_SERV_PAGAMENTO="http://localhost:8081/"

mvn spring-boot:run -Dspring-boot.run.profiles=test -DskipTests
