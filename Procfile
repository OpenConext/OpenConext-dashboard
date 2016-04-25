watch: (cd dashboard && grunt watch)
http: (cd dashboard && grunt server)
war: (docker-compose up -d && cd selfservice && mvn spring-boot:run -Drun.jvmArguments="-Dspring.profiles.active=dev -Dspring.datasource.url=jdbc:mysql://$(docker-machine ip)/csa?autoReconnect=true&useSSL=false")
