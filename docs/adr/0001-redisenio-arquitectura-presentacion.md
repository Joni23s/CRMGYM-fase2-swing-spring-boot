# ADR 0001: Rediseño de la Arquitectura de Presentación (Presenters Ligeros + Componentes Reutilizables)

* **Estado:** Propuesto
* **Fecha:** 2026-07-13
* **Autores:** Antigravity (AI Architect) & ChatGPT (Consultor Externo)
* **Desarrollador Principal:** Jonathan Araujo

---

## 1. Contexto
El proyecto **CRMGYM v2** ha evolucionado de ser un CRUD de escritorio simple a una aplicación robusta que incorpora mejores prácticas de backend y optimización en Swing:
* Implementación de **Thread Safety** mediante `SwingWorker` para evitar bloqueos del Event Dispatch Thread (EDT) durante operaciones de I/O.
* Integración de base de datos MySQL local gestionada con **Flyway** para control de migraciones.
* Mapeo de datos limpio mediante capas de **DTOs** y **Mappers** específicos.
* Uso de caché en memoria para reducir accesos redundantes a la base de datos.
* Automatización de calidad y builds con **GitHub Actions**.
* Profiling de rendimiento integrado usando **VisualVM**.

Sin embargo, la estructura visual en Java Swing sigue un patrón heredado donde los paneles principales (`ClientPanel`, `PlansPanel`, etc.) concentran toda la lógica visual, de control y de negocio.

---

## 2. Problema Identificado (Diagnóstico de Graphify)
La herramienta de análisis estático **Graphify** reveló métricas preocupantes de acoplamiento de salida (**Out-Degree**) en las clases del paquete de vistas (`views.panels`):
* `ClientPanel` → **36 dependencias de salida**
* `HistoricalPanel` → **35 dependencias de salida**
* `PlansPanel` → **35 dependencias de salida**
* `PaymentPanel` → **32 dependencias de salida**

Este nivel crítico de acoplamiento se debe a que estos paneles funcionan como **Smart Views (Vistas Inteligentes)**. Cada panel asume las siguientes responsabilidades simultáneamente:
1. **Construcción y maquetación de UI:** Instanciar manualmente etiquetas, campos de texto, tablas, botones, scrollpanes y bordes utilizando MigLayout.
2. **Escucha de eventos:** Enlazar directamente ActionListeners y lógica de interacción a los botones.
3. **Validación:** Validar los campos de entrada llamando directamente a clases de validación (p. ej., `ClientValidation`).
4. **Consumo de persistencia:** Inyectar servicios de Spring (`ClientService`, `PlanService`) y coordinar llamadas a repositorios.
5. **Mapeo de datos:** Transformar entidades de datos persistidas en DTOs o vectores primitivos para ser renderizados en tablas de Swing.

Este acoplamiento dificulta la legibilidad del código (archivos de más de 700 líneas), impide el testeo unitario de la lógica de negocio asociada a la interfaz y hace que cualquier cambio estético o de negocio requiera modificar la clase visual completa.

---

## 3. Alternativas Evaluadas

### Alternativa A: MVP (Model-View-Presenter) Clásico ("De libro")
Consiste en separar rígidamente cada pantalla en tres partes mediante interfaces: una interfaz `IClientView`, un presentador `ClientPresenter` que coordina todo, y el modelo (`Client`).
* **Pros:** Aislamiento absoluto de Swing; alta facilidad de pruebas unitarias mockeando la interfaz de la vista.
* **Contras:** Introduce una enorme cantidad de boilerplate (múltiples interfaces por cada panel) y complejidad innecesaria para un equipo de desarrollo pequeño o un desarrollador único. Dificulta el flujo rápido de desarrollo en Swing.

### Alternativa B: MVC (Model-View-Controller) Estándar
Seguir el patrón MVC tradicional de Spring.
* **Pros:** Familiaridad conceptual para desarrolladores Java.
* **Contras:** Swing no es un entorno web sin estado (stateless). En aplicaciones de escritorio, los componentes de la vista mantienen su propio estado (selección de fila, datos en campos de texto, focos) de forma nativa, lo que rompe el flujo unidireccional clásico del MVC web.

### Alternativa C: MVVM (Model-View-ViewModel) con Data Binding
El patrón MVVM sincroniza los datos bidireccionalmente entre una vista pasiva y un ViewModel que expone propiedades reactivas.
* **Pros:** Desacoplamiento total; la vista se actualiza automáticamente cuando cambia el ViewModel.
* **Contras:** Java Swing no posee un motor de databinding reactivo nativo potente (a diferencia de JavaFX o frameworks frontend modernos). Implementarlo requeriría dependencias externas pesadas o una cantidad abrumadora de código para observar propiedades manualmente.

### Alternativa D: Presenters Ligeros + Componentes Reutilizables + Fachadas por Módulo (Elegida)
Propone un enfoque pragmático adaptado al estado actual de CRMGYM:
1. **Componentización de UI:** Extraer los elementos visuales repetitivos (tarjetas de métricas, tablas personalizadas, botones con estilo, badges de estado) a componentes de UI reutilizables y parametrizables.
2. **Presenters/Controllers Ligeros:** Clases concretas de Spring (sin interfaces redundantes) que contienen la lógica de negocio de la interfaz (hilos con `SwingWorker`, orquestación de llamadas a fachadas y validación).
3. **Fachadas Temáticas por Contexto:** Reducir las dependencias de la UI centralizando los servicios relacionados en fachadas modulares (p. ej., `ClientFacade`, `PaymentFacade`).
4. **Theme centralizado:** Consolidar todos los tokens visuales en `Theme.java` para evitar colores y dimensiones mágicas en la UI.

---

## 4. Decisión Adoptada
Adoptar la **Alternativa D (Presenters Ligeros + Componentes Reutilizables + Fachadas)** para realizar la refactorización estructural de la UI en CRMGYM.

### Justificación:
* **Pragmatismo sobre Dogmatismo:** Evita la sobre-ingeniería de interfaces abstractas innecesarias (`IView`, `IPresenter`), manteniendo el código limpio y directo para el tamaño y escala del proyecto.
* **Reducción Orgánica del Acoplamiento:** Al delegar la construcción de tablas, botones y tarjetas a componentes externos reutilizables, los paneles visuales se reducen significativamente de tamaño de forma natural, reduciendo su Out-Degree drásticamente.
* **Consistencia Estética (Tokens de Diseño):** El uso de `Theme.java` garantiza que toda la aplicación se rediseñe o mantenga visualmente consistente de forma centralizada sin duplicar declaraciones estéticas.
* **Seguridad de Hilos Limpia:** Los Presenters ligeros gestionarán el ciclo de vida de los `SwingWorker`, asegurando que las operaciones pesadas sigan fuera del EDT pero manteniendo las vistas totalmente pasivas y libres de lógica de concurrencia.

---

## 5. Consecuencias

### Consecuencias Positivas:
* **Reducción del tamaño de los paneles:** Las clases de vistas pasarán de ~700 líneas a menos de ~300 líneas, enfocadas netamente en la maquetación.
* **Mejora del Out-Degree:** El acoplamiento directo de cada panel visual se reducirá de ~35 a menos de 10 dependencias directas.
* **Mantenibilidad:** La UI se convierte en un ensamblador de componentes parametrizables (`new MetricCard(...)`), facilitando cambios de diseño globales.
* **Testeabilidad:** La lógica de interacción, validación y flujos asíncronos en los Presenters se podrá probar unitariamente con mayor facilidad.

### Consecuencias Negativas / Riesgos:
* **Esfuerzo Inicial de Refactorización:** Requiere tocar las clases de negocio visuales existentes y re-estructurar los flujos de datos.
* **Curva de Aprendizaje:** Requiere consistencia por parte del desarrollador para no volver a inyectar servicios directamente en las nuevas clases visuales.

---

## 6. Roadmap de Migración Gradual

1. **Paso Piloto (PlansPanel):** 
   Implementar el patrón en el panel de Planes por ser el más aislado y con menor riesgo de efectos colaterales.
2. **Componentización de UI:**
   Crear el paquete `views.components` y migrar elementos estáticos (tarjetas, botones y tablas).
3. **Migración del Resto de Paneles:**
   Refactorizar secuencialmente `PaymentPanel`, `ClientPanel` y finalmente `HistoricalPanel`.

---

## 7. Métricas Esperadas post-refactorización

| Métrica | Estado Actual | Objetivo post-migración |
| :--- | :---: | :---: |
| **Out-Degree Promedio de Paneles** | ~34 dependencias | **< 8 dependencias** |
| **Líneas de Código promedio por Panel** | ~700 líneas | **< 300 líneas** |
| **Uso de Colores Mágicos en UI** | Presente en layouts y renders | **0 (100% Theme.java)** |
| **Lógica de Concurrencia (SwingWorker) en Vistas** | Mezclada en listeners visuales | **0 (100% en Presenters)** |
