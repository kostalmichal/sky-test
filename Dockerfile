# Dockerfile pro Gradle build
# Použij tento Dockerfile pokud používáš Gradle místo Maven

# První fáze: Build aplikace
FROM gradle:8.7-jdk21 AS build
WORKDIR /app

# Kopírování Gradle souborů pro stažení závislostí
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# Stažení závislostí (bude využito z cache, pokud se build soubory nezmění)
RUN gradle dependencies --no-daemon

# Kopírování zdrojových souborů
COPY src ./src

# Build aplikace s přeskočením testů
RUN gradle build --no-daemon -x test

# Druhá fáze: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Kopírování JAR souboru z build fáze
COPY --from=build /app/build/libs/*.jar app.jar

# Nastavení proměnných prostředí
ENV SPRING_PROFILES_ACTIVE=dev
ENV JAVA_OPTS="-Xms512m -Xmx1024m"

# Nastavení portu
EXPOSE 8080

# Spuštění aplikace
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
