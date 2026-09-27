@echo off
title EwuarSoft - Servidor Spring Boot con Hibernate
echo ==============================================================
echo       INICIANDO PLATAFORMA EWIARSOFT (SPRING BOOT)
echo ==============================================================
echo Verificando base de datos MySQL en XAMPP (puerto 3306)...
echo.

set MAVEN_CMD="C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.3\plugins\maven-plugin\lib\maven3\bin\mvn.cmd"

if exist %MAVEN_CMD% (
    echo Usando Maven de IntelliJ IDEA...
    %MAVEN_CMD% spring-boot:run
) else (
    echo Usando Maven del sistema...
    mvn spring-boot:run
)

pause
