# CRMGYM - Sistema de Gestión para Gimnasios 🏋️‍♂️ (Fase 2 - Swing y Spring Boot)

CRMGYM evoluciona en su **Fase 2** integrando **Spring Boot** como framework principal, manteniendo **Hibernate/JPA** para la persistencia de datos y agregando una **interfaz gráfica de usuario con Java Swing**.
Esta fase consolida la arquitectura basada en DAO y permite una experiencia más interactiva y moderna para la gestión de clientes, planes, actividades y pagos del gimnasio.

---

## 📌 Características Principales

### 🤝 Gestión de Clientes

* Registro de clientes con nombre, apellido, DNI, email, teléfono, estado y plan asociado.
* Asociación de clientes con un plan vigente.
* CRUD completo con validaciones y reactivación/baja lógica.

### 📈 Gestión de Planes

* Registro de planes con nombre, días y horas habilitadas, valor y notas.
* CRUD completo con baja lógica.

### 🗓️ Historial de Planes

* Registro automático del historial cuando un cliente se registra o actualiza de plan.
* Consulta de historial por cliente, por plan o por estado.

### 💳 Gestión de Pagos (fase beta)

* Registro de pagos asociados a clientes y planes.
* Seguimiento de pagos pendientes y realizados.
* Funcionalidad en fase beta, con mejoras planificadas para futuras versiones.

### 🖥️ Interfaz Gráfica de Usuario (GUI)

* Desarrollada con **Java Swing**, estilizada con **FlatLaf Dark Theme**.
* Menús interactivos y formularios claros para la gestión de clientes, planes y pagos.
* Visualización de tablas y datos de forma organizada.

### 💾 Persistencia de Datos

* Gestionada con **Spring Data JPA**, utilizando **Hibernate** como proveedor ORM.
* Arquitectura basada en **DAO** para separación de lógica de acceso a datos y lógica de negocio.

## 🛠️ Tecnologías Utilizadas

| Tecnología      | Descripción                    |
| --------------- | ------------------------------ |
| Java 21         | Lenguaje de programación       |
| Spring Boot     | Framework de aplicaciones Java |
| Hibernate / JPA | Persistencia ORM               |
| MySQL           | Base de datos relacional       |
| Swing + FlatLaf | Interfaz gráfica de usuario    |
| Maven           | Gestión de dependencias        |
| Git             | Control de versiones           |

---

## 🚀 Estructura del Proyecto

```
src/
├── controller/        --> Controladores de UI Swing
├── dto/               --> Objetos de transferencia (DTO)
├── mappers/           --> Conversión entre Entity <-> DTO
├── model/             --> Entidades JPA (Hibernate)
├── repository/        --> Interfaces y clases Repository (DAO)
├── service/           --> Servicios con lógica de negocio
├── util/              --> Utilidades (tablas, validaciones, helpers)
├── validations/       --> Validaciones de datos por tipo
└── Main.java          --> Punto de entrada (lanza la GUI)
```

---

## 🛫 Instalación y Ejecución

1. **Clonar el repositorio**

```bash
git clone https://github.com/Joni23s/CRMGYM-fase2-swing-spring-boot.git
cd CRMGYM-fase2-swing-spring-boot
```

2. **Crear la base de datos**

* Ejecutar el script SQL `script_initialized_db.sql` en tu servidor MySQL.
* Este script crea las tablas: `clients`, `plans`, `historical_plans` con sus relaciones y datos iniciales.

3. **Configurar variables de entorno (opcional)**

```bash
export DB_USER=tu_usuario
export DB_PASSWORD=tu_contraseña
```

4. **Ejecutar el proyecto**

* Usando tu IDE favorito o desde terminal:

```bash
mvn clean install
mvn spring-boot:run
```

* La GUI de Swing se abrirá automáticamente para interactuar con la aplicación.

---

## 📋 Ejemplo de Uso

* **Listar Clientes:**
  Abrir menú de Clientes > Listar Clientes > Todos los clientes.

* **Registrar Cliente:**
  Menú de Clientes > Agregar cliente > seguir instrucciones y asociar un plan.

* **Ver Historial por Cliente:**
  Menú de Historial > Historial por Cliente > ingresar DNI.

* **Modificar Plan:**
  Menú de Planes > Modificar Plan > seleccionar ID y actualizar datos.

---

## 📧 Contacto

* **Nombre:** Jonathan Araujo
* **GitHub:** [Joni23s](https://github.com/Joni23s)
* **Email:** [jonathanaraujo232g@gmail.com](mailto:jonathanaraujo232g@gmail.com)
* **LinkedIn:** [Jonathan Araujo](https://www.linkedin.com/in/jonathan-araujo/)

---

## 🔗 Fases Futuras del Proyecto

* **Fase 2 (Actual):** Swing + Spring Boot + GUI + Persistencia robusta
* **Fase 3:** Integración de API REST + Pasarelas de pago
* **Fase 4:** Interfaz web y mejoras de experiencia de usuario

🎉 Gracias por tu interés en **CRMGYM**. ¡Vamos por más! 🚀

