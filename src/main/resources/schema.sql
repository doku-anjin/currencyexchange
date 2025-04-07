# Application Configuration
spring.application.name=currency-exchange-service
server.port=8080
server.servlet.context-path=/api

# H2 Database Configuration
spring.datasource.url=jdbc:h2:mem:currencydb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Initialize database using schema.sql and data.sql
spring.sql.init.mode=always
spring.sql.init.schema-locations=classpath:schema.sql
spring.sql.init.data-locations=classpath:data.sql

# Logging Configuration
logging.level.root=INFO
logging.level.com.cathaybank=DEBUG
logging.level.org.springframework.web=INFO
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# Actuator Configuration
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always

# API Configuration
api.oanda.base-url=https://fxds-public-exchange-rates-api.oanda.com/cc-api
api.oanda.request-timeout=5000

# Scheduler Configuration
scheduler.exchange-rate.cron=0 0 * * * ?  # Hourly sync
scheduler.exchange-rate.enabled=true

# Internationalization
spring.messages.basename=messages
spring.messages.encoding=UTF-8
spring.messages.fallback-to-system-locale=false

# Crypto Configuration
crypto.aes.key=YourSecretKey123
crypto.aes.iv=YourInitVector1