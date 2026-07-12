# Posible Plan de Implementación de la Fase 6 (Gemini)

¡Hola! En este documento vas a encontrar un análisis detallado y explicaciones conceptuales paso a paso de la **Fase 6** adaptada. Hemos reemplazado el uso de **Spring Boot Actuator** por **VisualVM**, una propuesta mucho más lógica y educativa para el desarrollo de aplicaciones de escritorio (Swing) que aporta un gran valor de aprendizaje para desarrolladores Junior.

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

### 📊 C. VisualVM (Perfilado y Monitoreo Externo de la JVM)

#### ¿Qué es?
**VisualVM** es una herramienta externa y visual que se conecta directamente a la Máquina Virtual de Java (JVM) en la que corre nuestra aplicación. A diferencia de Actuator, **no requiere añadir dependencias de código al proyecto ni levantar un servidor web local (Tomcat)**. 

Se conecta utilizando los puertos de diagnóstico nativos de Java (a través de JMX) y nos muestra en tiempo real gráficos intuitivos sobre lo que pasa "bajo el capó".

#### Ventajas para un Junior
- **Aprender gestión de memoria real:** Verás en tiempo real cómo funciona el *Garbage Collector* (recolector de basura), cómo se llena la memoria *Heap* (donde viven los objetos Java) y cómo se libera.
- **Detección de fugas de memoria (Memory Leaks):** Puedes hacer capturas de memoria (*Heap Dumps*) y ver exactamente qué clases están ocupando espacio y no se están destruyendo.
- **Análisis de Hilos (Threads):** Si la pantalla de la aplicación se congela, VisualVM te muestra qué hilo está bloqueado y en qué línea exacta de código ocurrió el bloqueo.
- **Cero código invasivo:** Mantiene nuestra aplicación Swing 100% limpia y ligera para el cliente final.

---

## ⚖️ Conclusión: ¿Conviene o no aplicar la Fase 6?

A continuación se evalúa cada uno de los tres pilares de esta fase adaptada:

| Componente | ¿Qué aporta? | Costo/Impacto Técnico | ¿Vale la pena? | Recomendación |
| :--- | :--- | :--- | :--- | :--- |
| **1. Flyway Migrations** | - Elimina la inicialización manual vía `script_initialized_db.sql`. <br>- Automatiza la creación/actualización del esquema de BD en entornos locales de desarrollo. <br>- Garantiza control de versiones del esquema SQL. | - Requiere reestructurar el archivo SQL en carpetas específicas (`db/migration/V1__...sql`). <br>- Configurar y probar que el ciclo de vida de Flyway valide las entidades de Hibernate. | **Sí, altamente recomendado.** | Permite automatizar la creación y actualización de tablas tanto en desarrollo como en la PC del administrador sin scripts manuales. |
| **2. CI/CD con GitHub Actions** | - Ejecuta compilación y tests automáticos en la nube con cada Push/PR. <br>- Evita integrar código que no compile o con tests rotos. | - Crear un archivo `.github/workflows/maven.yml`. <br>- El impacto local es nulo; solo configuración de infraestructura. | **Sí, recomendado.** | Aporta una red de seguridad crucial con un esfuerzo de configuración mínimo (solo un archivo YAML). |
| **3. VisualVM (Monitoreo)** | - Monitoreo completo y visual de CPU, RAM, recolección de basura e hilos. <br>- Herramienta estándar de la industria para resolver problemas de rendimiento e hilos en Swing. | - Cero costo en el código. <br>- El desarrollador solo debe descargar e instalar VisualVM en su máquina local para conectarse al proceso Java de la app. | **Sí, excelente para aprender.** | Reemplaza con éxito a Actuator sin recargar la aplicación de escritorio con Tomcat/puertos web locales. Es 100% educativo. |

---

## 📋 Guía Técnica de Implementación Paso a Paso

### 1. Migraciones de Base de Datos (Flyway)

#### Paso 1.1: Configurar dependencias en `pom.xml`
Añade las dependencias de Flyway dentro de la sección `<dependencies>`:
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

#### Paso 1.2: Configurar `application.properties`
Cambiamos `ddl-auto` a `validate` para que Hibernate solo verifique la estructura y no intente alterarla:
```properties
# Validar que el esquema coincida con las entidades
spring.jpa.hibernate.ddl-auto=validate

# Opcional: Configurar Flyway para que ejecute migraciones en base de datos vacía
spring.flyway.baseline-on-migrate=true
```

#### Paso 1.3: Organizar los archivos SQL de Migración
Crea la estructura de carpetas `src/main/resources/db/migration/` y añade los siguientes archivos:

*   **`V1__Initial_Schema.sql`**: Contendrá las sentencias `CREATE TABLE` del archivo `script_initialized_db.sql` original (sin la sentencia `CREATE DATABASE` inicial para permitir flexibilidad).
*   **`V2__Default_Data.sql`**: Contendrá los `INSERT INTO` para cargar los planes por defecto (`Sin Plan`, `El día`, `Básico`, `Premium`) y los clientes de prueba iniciales.

---

### 2. Integración Continua (GitHub Actions)

#### Paso 2.1: Crear archivo de Workflow
Crea la carpeta `.github/workflows/` en la raíz del proyecto y añade el archivo `maven.yml` con el siguiente contenido:
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

---

### 3. Monitoreo e Inspección con VisualVM

#### Paso 3.1: Instalar VisualVM
1. Descarga la última versión de **VisualVM** desde su sitio web oficial ([visualvm.github.io](https://visualvm.github.io/)).
2. Descomprime el archivo `.zip` en una ubicación cómoda de tu disco (ej: `C:\tools\visualvm`).

#### Paso 3.2: Ejecutar tu Aplicación y Conectar VisualVM
1. Inicia la aplicación desde tu entorno de desarrollo o ejecutando `./mvnw spring-boot:run`.
2. Abre la carpeta de VisualVM, entra a `bin` y ejecuta `visualvm.exe`.
3. En la barra lateral izquierda, bajo la categoría **Local**, verás listados todos los procesos Java activos en tu máquina.
4. Busca el proceso que corresponda a nuestro proyecto (normalmente se llamará `com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.Application` o tendrá el ID de proceso asociado a Maven).
5. Haz doble clic sobre él.

#### Paso 3.3: ¿Qué inspeccionar en la interfaz de VisualVM?
*   **Pestaña "Monitor":** Aquí verás gráficos en tiempo real del uso de CPU y del uso de memoria *Heap*. Puedes hacer clic en **"Perform GC"** para forzar manualmente al recolector de basura a limpiar memoria sin usar.
*   **Pestaña "Threads" (Hilos):** Te mostrará todos los hilos en ejecución. Swing maneja el *AWT-EventQueue-0* (el EDT). Podrás ver si ese hilo está en estado "Running" (verde) o si se queda bloqueado (rojo/amarillo) al realizar consultas pesadas, demostrando visualmente el impacto de usar o no usar `SwingWorker`.
*   **Pestaña "Sampler" / "Profiler":** Te permite iniciar un análisis rápido de CPU para descubrir qué consultas SQL o métodos lógicos tardan más milisegundos en responder.
