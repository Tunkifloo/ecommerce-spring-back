# Multi-stage build con imágenes más estables
FROM maven:3.9.5-eclipse-temurin-17 AS build

WORKDIR /app

# Copiar archivos de configuración de Maven
COPY pom.xml .
COPY src ./src

# Compilar la aplicación
RUN mvn clean package -DskipTests

# Imagen final
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Crear usuario no-root
RUN addgroup -g 1001 -S spring && adduser -u 1001 -S spring -G spring

# Copiar el JAR compilado
COPY --from=build /app/target/*.jar app.jar

# Crear directorio para logs
RUN mkdir -p /app/logs && chown -R spring:spring /app

# Cambiar al usuario no-root
USER spring

# Configurar JVM
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseContainerSupport"

# Exponer puerto
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/actuator/health || exit 1

# Comando de inicio
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]