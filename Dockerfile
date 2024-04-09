# Usamos una imagen de OpenJDK como base
FROM openjdk:17-jdk-alpine

# Establecemos el directorio de trabajo en /app
WORKDIR /app

# Copiamos el archivo JAR generado por Maven al contenedor
 COPY target/AHS-API-0.0.1-SNAPSHOT.jar app.jar


# Exponemos el puerto 8080 para que la aplicación Spring Boot sea accesible
EXPOSE 8080

# Comando de entrada para ejecutar la aplicación Spring Boot
CMD ["java", "-jar", "app.jar"]

# Agregar la clase principal al contenedor
COPY src/main/java/com/example/AHSAPI/AhsApiApplication.java /app/src/main/java/com/example/AHSAPI/
