# Evaluación de Arquitectura y Diseño de Dominio (Senior Software Architect Report)

Este informe técnico evalúa la calidad estructural, el diseño del modelo de dominio, el flujo de dependencias y la complejidad de la aplicación **CRMGYM - Fase 2**. Se basa en principios de ingeniería de software como SOLID, DRY, bajo acoplamiento y alta cohesión.

---

## 1. Diseño de Dominio (JPA, DTOs y Mappers)

### Relaciones JPA y Acoplamiento del Modelo
* **Fortaleza - Fetching Optimizado:** La incorporación de `@EntityGraph(attributePaths = {"currentPlan"})` en `ClientRepository` y en `PaymentRepository` (para `findAll` y consultas filtradas) resuelve eficazmente el problema de carga diferida (LAZY) y neutraliza el patrón de consultas N+1 en las operaciones de lectura masiva.
* **Riesgo - Cascade y Eliminación:** La relación de `Client` con `HistoricalPlan` utiliza `cascade = {CascadeType.PERSIST, CascadeType.MERGE}`. Esto es seguro, pero si en el futuro se introduce lógica de borrado físico, la falta de una política clara de ciclo de vida de historiales podría dejar registros huérfanos o causar errores de restricción de clave foránea.
* **Recomendación:** Mantener la política de "borrado lógico" (Soft Delete) mediante el atributo `isActive` en todas las entidades principales (`Client`, `Plan`, `Payment`), evitando eliminaciones físicas directas en JPA.

### Dependencia de Entidades JPA en Presentación
* **Estado Actual:** 
  * **Punto fuerte:** Las tablas y listados visuales (incluyendo el Dashboard unificado) consumen exclusivamente objetos DTO (`ClientDTO`, `PlanDTO`, `PaymentDTO`, `HistoricalPlanDTO`, `DashboardDataDTO`).
  * **Punto de mejora:** Durante las operaciones de **escritura** (`onSave` y `modifyStatus`), los presentadores (`ClientPresenter`, `PaymentPresenter`, `PlansPresenter`) instancian directamente las entidades JPA (`Client`, `Payment`, `Plan`) mediante constructores y patrones Builder, enviándolas listas al servicio.
* **Impacto:** Si la estructura interna del modelo de datos cambia (ej: agregación de nuevos campos obligatorios o cambios en tipos de datos), se deberán modificar tanto la entidad como la capa visual Swing.
* **Propuesta:** Completar el aislamiento. Los presentadores deben pasar DTOs de edición (ej: `ClientDTO`) o parámetros estructurados a los servicios/fachadas, delegando en la capa de negocio la instanciación de las entidades JPA.

---

## 2. Flujo de Dependencias y Direccionalidad

### Dependencias Circulares
* **Estado Actual:** **0 dependencias circulares detectadas.**
  * Anteriormente, existía un acoplamiento directo y potencial ciclo lógico entre `PlanService` y `ClientService` debido a la desactivación de planes.
  * **Estrategia aplicada:** Se implementó un flujo basado en eventos de Spring (`PlanDeactivatedEvent` y `PlanEventsListener`). `PlanService` ahora solo publica la desactivación de forma desacoplada. El listener coordina las acciones necesarias en el contexto de Clientes.
* **Beneficio:** Los contextos están aislados y la dirección de las dependencias es estrictamente unidireccional.

### Direccionalidad y Capas
* **Flujo Top-Down:** Las dependencias fluyen correctamente: 
  `UI (Panels) -> Presenters -> Facades / Services -> Repositories -> Base de Datos`.
* **Aislamiento de la Vista:** Las clases `JPanel` (vistas pasivas) no importan ni conocen a los presentadores. La comunicación se realiza de forma reactiva exponiendo getters de componentes o mediante listeners de eventos Swing (`ActionListener`, `ItemListener`), lo que cumple rigurosamente con el patrón **Model-View-Presenter (MVP) / Passive View**.

### Cadenas de Llamadas Largas (Long Call Chains)
* Las cadenas de llamadas son directas y poco profundas. El presentador invoca un método de un servicio/fachada y este interactúa directamente con el repositorio JPA.
* La introducción de la fachada `DashboardFacade` unificó el punto de contacto para consultas compuestas, evitando que la capa visual realice múltiples llamadas secuenciales e independientes.

---

## 3. Análisis de Complejidad Estructural

A continuación se ordenan las clases con mayor complejidad del sistema y la prioridad recomendada para su refactorización:

| Clase | Dependencias | Responsabilidades | Complejidad | Prioridad de Refactorización | Justificación |
| :--- | :---: | :---: | :---: | :---: | :--- |
| **`ClientPresenter`** | Alta (4) | Mapeo de UI, validación, asincronía y guardado de clientes. | Alta | **Alta (1)** | Concentra demasiada lógica de flujo (instanciación de entidad `Client`, mapeo manual e interacción con validaciones complejas). Debería delegar el mapeo y la creación de la entidad a una fachada. |
| **`PaymentPresenter`** | Alta (3) | Control de flujo de cobro, parsing de importes y fechas, asincronía. | Alta | **Alta (2)** | Contiene conversiones complejas de strings a `BigDecimal`, formateo de monedas y manejo manual de transacciones asíncronas. |
| **`HistoricalPresenter`** | Media (3) | Gestión de filtros acumulativos (RadioButtons de clientes y planes) y búsquedas. | Media | **Media (3)** | Su método `onSearch()` gestiona la lógica de qué filtros de radio button están activos y ejecuta las consultas correspondientes de forma condicional. |
| **`DashboardFacade`** | Media (3) | Consolidación de métricas de negocio del Dashboard. | Media | **Baja (4)** | Su complejidad está justificada ya que actúa como orquestador de métricas. Está bien encapsulada en la capa de servicios. |

---

## 4. Evolución Arquitectónica (Próximos Pasos con Mayor ROI)

Considerando el stack actual (Spring Boot, Swing, Flyway, SwingWorker, DTOs, GitHub Actions y VisualVM), estas son las mejoras prioritarias:

### 1. Migración a Componentes de Formulario Genéricos (ROI: Alto ★★★★★)
* **Impacto:** Mantenibilidad y DRY en la UI.
* **Propuesta:** Actualmente hay código repetido para validar campos de texto, aplicar bordes rojos en caso de error, y limpiar formularios.
* **Solución:** Crear un validador visual genérico (`FormValidator`) que inspeccione anotaciones o reglas y aplique estilos de error de FlatLaf de forma automatizada.

### 2. Retirar Instanciación de Entidades JPA en Presentadores (ROI: Alto ★★★★★)
* **Impacto:** Separación de responsabilidades (SoC).
* **Propuesta:** Mover constructores y builders de entidades JPA de los presentadores hacia la capa de negocio.
* **Solución:** Las firmas de los métodos `save()` de los servicios deben aceptar DTOs (ej: `ClientDTO`). El mapeo y validación de negocio se ejecutan dentro de la transacción de Spring.

### 3. Implementación de Caché de Aplicación (ROI: Medio ★★★★☆)
* **Impacto:** Rendimiento y escalabilidad.
* **Propuesta:** La lista de planes activos se solicita de la base de datos en múltiples pantallas.
* **Solución:** Habilitar `@EnableCaching` de Spring y anotar `PlanService.findByIsActiveDTO(true)` con `@Cacheable("activePlans")`. Invalidar el caché (`@CacheEvict`) únicamente al guardar o modificar planes.

---

## 5. Evaluación Global del Arquitecto (SOLID & Risks)

### Riesgos Técnicos
1. **Concurrency/EDT:** La asincronía se maneja correctamente a través de `AsyncDataLoader` (SwingWorker). Sin embargo, cualquier olvido de un desarrollador al no utilizar `loadData` reintroducirá bloqueos del EDT.
   * *Mitigación:* Se recomienda configurar inspecciones de código estáticas (o reglas de SonarQube) para evitar llamadas directas a clases que terminen en `Repository` o `Service` desde clases en el paquete `presenter` o `views` fuera de un `AsyncDataLoader`.
2. **Parsing en Presentación:** El parsing manual de entradas del usuario (`BigDecimal`, `LocalDate`) dentro de los presentadores expone a la aplicación a errores de formato no controlados en la vista.

### Fortalezas del Proyecto
* **Clean Architecture Swing-Spring:** La integración de un framework empresarial como Spring Boot (para inyección de dependencias, JPA, transacciones y eventos) con una interfaz de escritorio Swing es sumamente limpia y profesional.
* **Soft Coupling (MVP Pasivo):** El uso riguroso de vistas pasivas (Passive View) evita el clásico "acoplamiento espagueti" de Swing, facilitando pruebas unitarias de los presentadores simulando la vista con mocks.
* **Event-Driven Inter-Service Communication:** El uso de eventos de Spring para desacoplar contextos (como la desactivación de planes) muestra madurez de diseño y escalabilidad.
