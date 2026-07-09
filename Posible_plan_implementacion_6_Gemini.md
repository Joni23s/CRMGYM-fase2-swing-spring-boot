# Posible Plan de Implementación de la Fase 6 (Gemini)

¡Hola! En este documento vas a encontrar un análisis detallado y explicaciones conceptuales paso a paso de la **Fase 6** (Flyway, CI/CD con GitHub Actions y Actuator), especialmente diseñado con explicaciones extra útiles para desarrolladores Junior. 

El objetivo es evaluar si conviene o no aplicar esta fase a nuestra aplicación de escritorio (Swing) y entender a fondo cómo funciona cada tecnología en este contexto.

---

## 🔍 1. ¿Qué es cada componente y cómo impacta en nuestro proyecto?

### 🗄️ A. Flyway Migrations (Control de Versiones de Base de Datos)

#### ¿Qué es?
En el desarrollo de software tradicional, solemos crear un archivo de texto como `script_initialized_db.sql` y lo ejecutamos a mano en la base de datos. Si hacemos un cambio (como agregar una columna a una tabla), tenemos que acordarnos de pasarle el comando a nuestros compañeros de equipo para que lo ejecuten en sus bases de datos locales.

**Flyway** soluciona esto actuando como un "Git" pero para la base de datos. En lugar de un gran script manual, dividimos la base de datos en pequeños archivos SQL numerados (migraciones) que se guardan en la carpeta del proyecto (ej: `V1__crear_tablas.sql`, `V2__agregar_descuento.sql`). 

Cuando la aplicación Spring Boot arranca, Flyway:
1. Revisa una tabla interna que crea él mismo llamada `flyway_schema_history`.
2. Compara qué archivos de migración ya se aplicaron.
3. Si hay archivos nuevos en el código que no están registrados en la base de datos, los ejecuta automáticamente en orden secuencial.

#### Ventajas para un Junior
- **Olvídate de "en mi máquina no anda":** Al clonar el proyecto y arrancarlo, la base de datos se genera sola con la última versión de la estructura y los datos iniciales necesarios para probar.
- **Auditoría:** Sabes exactamente cuándo y quién modificó la estructura de la base de datos.

#### Comportamiento en Spring Boot (ddl-auto)
Hasta ahora veníamos usando `spring.jpa.hibernate.ddl-auto=update`. Esta propiedad le dice a Hibernate: *"Intenta adivinar los cambios de las entidades Java y agrégalos a la base de datos"*. 
- **El peligro:** En entornos reales de producción, Hibernate puede cometer errores, borrar datos por accidente o no sincronizar correctamente índices/constraints complejos.
- **La solución:** Al habilitar Flyway, configuramos `spring.jpa.hibernate.ddl-auto=validate`. Hibernate ya no crea nada; solo "valida" que la base de datos estructurada por Flyway coincida perfectamente con nuestras clases `@Entity` de Java. Si no coinciden, la app no arranca y lanza un error, protegiendo la integridad del sistema.

---

### ⚙️ B. CI/CD con GitHub Actions (Integración Continua)

#### ¿Qué es?
**CI** (Continuous Integration) consiste en automatizar la compilación y pruebas de nuestro código cada vez que subimos cambios a Git. 

**GitHub Actions** es una herramienta integrada en GitHub que levanta una computadora virtual en la nube (un *Runner*), clona nuestro código, instala Java y Maven, y ejecuta el comando de compilación (ej: `mvn clean test` o `mvn clean compile`).

#### Ventajas para un Junior
- **Red de seguridad:** Si modificas algo localmente y te olvidas de compilar, o si rompes un test y subes el cambio por error, el pipeline de GitHub fallará en rojo (`Failed Build`) y te alertará inmediatamente antes de que ese código llegue a la rama principal (`main` o `master`).
- **Seguimiento automatizado:** Garantiza que el código siempre esté en un estado saludable y listo para empaquetarse en un archivo ejecutable `.jar`.

---

### 🩺 C. Spring Boot Actuator (Monitoreo de Salud)

#### ¿Qué es?
**Actuator** es un módulo de Spring Boot que provee herramientas para monitorear el estado interno de la aplicación. Te permite conocer cosas como:
- Si la base de datos está activa y respondiendo (`/health`).
- Cuánta memoria RAM está consumiendo la JVM (`/metrics`).
- Qué beans están cargados en el contexto de Spring.

#### El desafío en una aplicación de Escritorio (Swing)
Normalmente, Actuator expone esta información mediante peticiones HTTP (páginas web locales) en puertos como el `8080`. 

Sin embargo, nuestra aplicación de escritorio está configurada en `Application.java` como no-web:
```java
.web(WebApplicationType.NONE)
```
Esto significa que Spring Boot arranca en modo "consola/headless", levantando la interfaz Swing pero **sin iniciar un servidor web** (como Tomcat embebido) para ahorrar recursos de tu computadora.
- Si queremos ver las métricas por HTTP (con `/actuator`), tendríamos que agregar la dependencia `spring-boot-starter-web`, lo cual obligará a Spring Boot a abrir un puerto de red y consumir memoria RAM constante solo para servir esas páginas de monitoreo.
- **Alternativa JMX:** Podemos usar Actuator sin HTTP mediante JMX (Java Management Extensions) usando herramientas locales como `jconsole` o `VisualVM`. Es más eficiente en recursos pero requiere configuraciones adicionales de software en tu máquina de desarrollo.

---

## ⚖️ Conclusión: ¿Conviene o no aplicar la Fase 6?

### 🟢 ¿Por qué SÍ convendría aplicarla?
1. **Estabilidad y Orden de Base de Datos:** Dejar de depender de `ddl-auto=update` te prepara para una arquitectura profesional y previene problemas silenciosos de Hibernate con tipos de datos e índices.
2. **Despliegues Futuros:** Si en la Fase 3 del proyecto vas a migrar la aplicación a una interfaz Web con una API REST, tener Flyway y CI/CD listo te ahorrará semanas de trabajo de configuración.
3. **Automatización:** Si trabajas con más personas, la base de datos y la compilación se mantendrán 100% consistentes de manera automática.

### 🔴 ¿Por qué NO convendría aplicarla (o hacerlo parcialmente)?
1. **Actuator HTTP es innecesario:** No tiene sentido agregar peso al proyecto instalando un servidor web Tomcat en una aplicación que corre localmente en tu computadora para administrar el gimnasio. Consume RAM innecesaria para el cliente final.
2. **Sobrecarga de archivos SQL:** Si vas a estar modificando la base de datos constantemente cada 5 minutos en fase de pruebas iniciales, crear archivos de migración de Flyway continuamente (`V3`, `V4`, `V5`) puede volverse tedioso si no estás acostumbrado al flujo.

### 💡 Recomendación del Arquitecto (Enfoque Híbrido)
Se sugiere aplicar la Fase 6 de forma **parcial e inteligente**:
- **SÍ a Flyway:** Para tener la base de datos ordenada, portable y automatizada.
- **SÍ a GitHub Actions:** Para tener la red de seguridad de compilación y pruebas en la nube.
- **NO a Actuator HTTP:** Remover Actuator del `pom.xml` para optimizar el rendimiento de la aplicación y mantener el consumo de memoria de la app al mínimo en la PC del administrador.

---

## 📋 Guía Técnica de Implementación Paso a Paso (Flyway + CI)

Si decides avanzar con la recomendación híbrida, aquí tienes la guía para hacerlo:

### Paso 1: Configurar dependencias en `pom.xml`
1. Remueve la dependencia de `spring-boot-starter-actuator` (si se decide no usar monitoreo).
2. Agrega las dependencias de Flyway dentro de `<dependencies>`:
```xml
<!-- Migraciones de base de datos con Flyway -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-mysql</artifactId>
</dependency>
```

### Paso 2: Configurar `application.properties`
Cambiamos la validación automática para que Hibernate no intente alterar la base de datos:
```properties
# Validar que el esquema coincida con las entidades
spring.jpa.hibernate.ddl-auto=validate

# Opcional: Configurar Flyway para que ejecute migraciones en base de datos vacía
spring.flyway.baseline-on-migrate=true
```

### Paso 3: Organizar los archivos SQL de Migración
Crea las carpetas `src/main/resources/db/migration/` y añade los siguientes archivos:

1. **`V1__Initial_Schema.sql`**: Contendrá las sentencias `CREATE TABLE` del archivo `script_initialized_db.sql` original (sin la sentencia `CREATE DATABASE` inicial para permitir flexibilidad).
2. **`V2__Default_Data.sql`**: Contendrá los `INSERT INTO` para cargar los planes por defecto (`Sin Plan`, `El día`, `Básico`, `Premium`) y los clientes iniciales.

### Paso 4: Crear Workflow de GitHub Actions
Crea el archivo `.github/workflows/maven.yml` con el siguiente contenido base:
```yaml
name: Java CI con Maven

on:
  push:
    branches: [ main, master ]
  pull_request:
    branches: [ main, master ]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
    - name: Clonar repositorio
      uses: actions/checkout@v4

    - name: Configurar JDK 21
      uses: actions/setup-java@v3
      with:
        java-version: '21'
        distribution: 'temurin'
        cache: maven

    - name: Compilar y Probar con Maven
      run: mvn -B clean package
```
*(Nota: Este pipeline compilará el código y ejecutará los tests automáticos en cada push).*
