# 🛡️ MaqGuard — Sistema de Gestión de Mantenimiento Industrial

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.5-brightgreen?style=for-the-badge&logo=springboot)
![Spring Security](https://img.shields.io/badge/Spring%20Security-✔-brightgreen?style=for-the-badge&logo=springsecurity)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-✔-005F0F?style=for-the-badge&logo=thymeleaf)
![MySQL](https://img.shields.io/badge/MySQL-8.x-blue?style=for-the-badge&logo=mysql)
![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3.3-purple?style=for-the-badge&logo=bootstrap)
![Maven](https://img.shields.io/badge/Maven-3.8+-red?style=for-the-badge&logo=apachemaven)
![Metodología](https://img.shields.io/badge/Metodología-Scrum-0052CC?style=for-the-badge&logo=jira)

> Sistema web para la gestión integral del mantenimiento de maquinaria industrial, control de inventario de repuestos, reportes de fallas y notificaciones en tiempo real.

---

## 👥 Equipo de Desarrollo — Ficha 3114732

| Nombre | Rol |
|--------|-----|
| Marlon García | Desarrollador Backend |
| Laura Vargas | Desarrolladora Full Stack |
| Tatiana Barbosa | Desarrolladora Full Stack |
| Iván Herrera | Desarrollador Backend |
| Óscar Llanos | Desarrollador Frontend |
| Cristian Arciniegas | Desarrollador Full Stack |

**Instructora:** Diana Delgado  
**Programa:** Análisis y Desarrollo de Software — SENA  
**Metodología:** Scrum (Sprint N° 6)  
**Fecha:** 05 de abril de 2026

---

## 📋 Tabla de Contenidos

- [Descripción General](#-descripción-general)
- [Arquitectura MVC](#-arquitectura-mvc)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Tecnologías Utilizadas](#-tecnologías-utilizadas)
- [Módulos del Sistema](#-módulos-del-sistema)
- [Modelo de Datos](#-modelo-de-datos)
- [Roles y Seguridad](#-roles-y-seguridad)
- [Rutas del Sistema](#-rutas-del-sistema)
- [Instalación y Configuración](#-instalación-y-configuración)
- [Exportación a Excel](#-exportación-a-excel)
- [Metodología Scrum Aplicada](#-metodología-scrum-aplicada)

---

## 📖 Descripción General

**MaqGuard** es una aplicación web desarrollada con **Spring Boot + Thymeleaf** orientada a la gestión del ciclo de vida del mantenimiento industrial. El sistema permite a administradores programar, asignar y dar seguimiento a tareas de mantenimiento de maquinaria, mientras que los técnicos pueden consultar sus tareas asignadas, registrar el trabajo realizado y gestionar el uso de repuestos del inventario.

### Funcionalidades principales

- Gestión completa de máquinas (CRUD + alertas de mantenimiento próximo)
- Asignación y seguimiento de órdenes de mantenimiento
- Vista diferenciada por rol: **Administrador** y **Técnico**
- Control de inventario de repuestos con stock mínimo
- Reporte y seguimiento de fallas de maquinaria
- Sistema de notificaciones internas por usuario y rol
- Exportación de reportes a **Excel (.xlsx)** con Apache POI
- Autenticación y autorización con **Spring Security**

---

## 🏗️ Arquitectura MVC

El proyecto implementa el patrón **Model-View-Controller (MVC)** con separación estricta de responsabilidades en tres capas:

```
┌─────────────────────────────────────────────────────────┐
│                  NAVEGADOR (Cliente)                    │
│              Bootstrap 5.3.3 + Thymeleaf                │
└────────────────────────┬────────────────────────────────┘
                         │ HTTP Request
┌────────────────────────▼────────────────────────────────┐
│              CAPA CONTROLLER (MVC - C)                  │
│                                                         │
│  InicioController          → Enrutamiento y roles       │
│  MaquinasController        → CRUD máquinas + alertas    │
│  ManteAdminController      → CRUD mantenimientos        │
│  TecnicoController         → Portal del técnico         │
│  AdminInventarioController → Gestión de repuestos       │
│  ReporteFallasController   → Reporte de fallas          │
│  NotificacionController    → Sistema de notificaciones  │
└────────────────────────┬────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────┐
│              CAPA SERVICE (Lógica de negocio)           │
│                                                         │
│  MantenimientoService  → Lógica de mantenimientos       │
│  InventarioService     → Lógica de inventario           │
│  ReporteFallaService   → Lógica de reportes             │
│  MaquinaService        → Lógica de máquinas             │
└────────────────────────┬────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────┐
│           CAPA REPOSITORY (Acceso a datos JPA)          │
│                                                         │
│  MantenimientoRepository     MaquinasRepository         │
│  UsuarioRepository           NotificacionRepository     │
└────────────────────────┬────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────┐
│              BASE DE DATOS MySQL                        │
│              Nombre: maqguard                           │
└─────────────────────────────────────────────────────────┘
```

### Separación de Responsabilidades aplicada

| Capa | Responsabilidad | Ejemplo en MaqGuard |
|------|----------------|---------------------|
| **Controller** | Recibe peticiones HTTP, prepara el Model, retorna la vista | `TecnicoController` autentica al usuario y delega lógica al Service |
| **Service** | Contiene la lógica de negocio pura | `MantenimientoService.finalizarCompleto()` actualiza máquina + repuestos |
| **Repository** | Solo acceso a datos con JPA | `MantenimientoRepository.findAllConRelaciones()` |
| **Entity** | Representa tablas de la BD | `Mantenimiento`, `Maquina`, `Inventario` |
| **DTO** | Transfiere datos entre capas sin exponer entidades | `MantenimientoDTO`, `MantenimientoDetalleDTO` |

---

## 📁 Estructura del Proyecto

```
src/
├── main/
│   ├── java/
│   │   └── com/proyectogaes/
│   │       ├── controller/
│   │       │   ├── InicioController.java           # Login, dashboard por rol
│   │       │   ├── MaquinasController.java         # CRUD máquinas + alertas + Excel
│   │       │   ├── ManteAdminController.java       # CRUD mantenimientos + Excel
│   │       │   ├── TecnicoController.java          # Portal técnico
│   │       │   ├── AdminInventarioController.java  # CRUD inventario + Excel
│   │       │   ├── ReporteFallasController.java    # CRUD reportes de fallas
│   │       │   └── NotificacionController.java     # Notificaciones por rol
│   │       ├── entity/
│   │       │   ├── Maquina.java                    # Entidad máquinas (@Data Lombok)
│   │       │   ├── Mantenimiento.java              # Entidad mantenimientos
│   │       │   ├── Inventario.java                 # Entidad inventario/repuestos
│   │       │   ├── Usuario.java                    # Entidad usuarios (@Data Lombok)
│   │       │   ├── Rol.java                        # Entidad roles
│   │       │   ├── Notificacion.java               # Entidad notificaciones
│   │       │   ├── ReporteFalla.java               # Entidad reportes de fallas
│   │       │   ├── MantenimientoDTO.java           # DTO vistas del técnico
│   │       │   └── MantenimientoDetalleDTO.java    # DTO vista detalle
│   │       ├── service/
│   │       │   ├── MantenimientoService.java
│   │       │   ├── InventarioService.java
│   │       │   ├── MaquinaService.java
│   │       │   └── ReporteFallaService.java
│   │       └── repository/
│   │           ├── MantenimientoRepository.java
│   │           ├── MaquinasRepository.java
│   │           ├── UsuarioRepository.java
│   │           └── NotificacionRepository.java
│   └── resources/
│       ├── application.properties
│       └── templates/
│           ├── dasboardadmin.html
│           ├── dashboardtecnico.html
│           ├── login.html
│           ├── inicio.html
│           ├── maquinasadmin/        (maquinas, crear, editar, alertas)
│           ├── mantenimientosadmin/  (mantenimientos, crearmanteadmin, editarmanteadmin)
│           ├── tecnico/              (mismantenimientos, historialtec, iniciar, detalle)
│           ├── inventario/           (lista, crear)
│           ├── reportes/             (index, crear, editar, ver)
│           └── notificacionadmin/    (notificaciones)
└── test/
```

---

## 🛠️ Tecnologías Utilizadas

| Tecnología | Versión | Uso en MaqGuard |
|-----------|---------|-----------------|
| Java | 21 | Lenguaje principal |
| Spring Boot | 4.0.5 | Framework base |
| Spring Web MVC | 4.0.5 | Controllers y rutas HTTP |
| Spring Data JPA | 4.0.5 | ORM y acceso a BD |
| Spring Security | 4.0.5 | Autenticación y control de roles |
| Spring Validation | 4.0.5 | Validación de formularios con `@Valid` |
| Thymeleaf | 4.0.5 | Motor de plantillas HTML (MVC - Vista) |
| Thymeleaf Layout Dialect | — | Layouts reutilizables entre vistas |
| Hibernate | 6.x | Implementación JPA |
| MySQL Connector/J | 8.x | Driver de base de datos |
| Lombok | 1.18+ | `@Data`, `@Getter`, `@Setter`, `@NoArgsConstructor` |
| Apache POI (poi-ooxml) | 5.2.3 | Exportación a Excel (.xlsx) |
| Bootstrap (WebJar) | 5.3.3 | Estilos y componentes UI |
| Maven | 3.8+ | Gestión de dependencias y build |

---

## 📦 Módulos del Sistema

### 1. 🏠 Módulo de Inicio y Autenticación
**Controller:** `InicioController`

Gestiona el login y el enrutamiento por rol. Después de autenticarse, el sistema redirige automáticamente al dashboard correspondiente según el rol del usuario (`ROLE_ADMINISTRADOR` o `ROLE_TECNICO`). Implementa cabeceras de caché para evitar el acceso con el botón "Atrás" tras cerrar sesión.

### 2. ⚙️ Módulo de Máquinas
**Controller:** `MaquinasController` | **Ruta base:** `/maquinas`

Gestión completa del parque de maquinaria. La entidad `Maquina` usa `@PrePersist` y `@PreUpdate` para calcular automáticamente la `fechaProximoMantenimiento` sumando el `intervaloMantenimiento` (en días) a la `fechaUltimoMantenimiento`. Las alertas son configurables por cantidad de días con `@RequestParam(defaultValue = "5")`.

### 3. 🔧 Módulo de Mantenimientos (Admin)
**Controller:** `ManteAdminController` | **Ruta base:** `/mantenimientos`

El administrador crea y asigna órdenes de mantenimiento a técnicos mediante relaciones `@ManyToOne` con `Maquina` y `Usuario`. El estado inicial se asigna automáticamente como `"Pendiente"`. Usa `findAllConRelaciones()` del repositorio para obtener datos con JOIN en una sola consulta.

### 4. 👷 Módulo del Técnico
**Controller:** `TecnicoController` | **Rutas:** `/tecnico/*`

Portal exclusivo para técnicos. Obtiene el usuario logueado mediante `Authentication` de Spring Security y filtra datos únicamente de ese técnico. Al finalizar un mantenimiento, el método `finalizarCompleto()` actualiza simultáneamente la descripción del trabajo, la fecha del último mantenimiento, el nuevo intervalo y descuenta los repuestos usados del inventario.

### 5. 📦 Módulo de Inventario
**Controller:** `AdminInventarioController` | **Ruta base:** `/inventario`

Gestión del almacén de repuestos industriales. Usa inyección de dependencias por constructor (`final` + constructor). El formulario de creación y edición reutiliza la misma vista `inventario/crear.html`.

### 6. 🚨 Módulo de Reporte de Fallas
**Controller:** `ReporteFallasController` | **Ruta base:** `/falla`

Registro de fallas detectadas en maquinaria con validación mediante `@Valid` y `BindingResult`. La entidad `ReporteFalla` gestiona fechas automáticamente con `@PrePersist` (asigna `fechaReporte` y estado `"pendiente"`) y `@PreUpdate` (actualiza `fechaActualizacion`).

### 7. 🔔 Módulo de Notificaciones
**Controller:** `NotificacionController` | **Ruta base:** `/notificaciones`

Sistema de notificaciones internas diferenciado por rol: el administrador (rol 1) ve todas las notificaciones del sistema, los técnicos solo las propias. Gestiona el campo `leida` (0/1) con acciones individuales y masivas.

---

## 🗄️ Modelo de Datos

### Diagrama de entidades

```
┌─────────────┐        ┌──────────────────┐        ┌───────────┐
│   usuarios  │        │  mantenimientos  │        │  maquinas │
│─────────────│        │──────────────────│        │───────────│
│ id_usuario  │◄──────►│ id_usuario (FK)  │◄──────►│ id_maquina│
│ nombre      │        │ id_maquina (FK)  │        │ nombre    │
│ usuario     │        │ fecha_mantto     │        │ modelo    │
│ contrasena  │        │ tipo_mantto      │        │ ubicacion │
│ estado      │        │ descripcion      │        │ fecha_ult │
│ id_rol (FK) │        │ costo            │        │ intervalo │
└─────────────┘        │ estado           │        │ fecha_prox│
       │               └──────────────────┘        │ estado    │
       ▼                                           └───────────┘
┌─────────────┐
│     rol     │        ┌──────────────┐    ┌─────────────────┐
│─────────────│        │  inventario  │    │  reporte_falla  │
│ id_rol      │        │──────────────│    │─────────────────│
│descripcion  │        │ id_repuesto  │    │ id_falla        │
└─────────────┘        │ nombre       │    │ titulo          │
                       │ codigo_pieza │    │ descripcion     │
┌──────────────────┐   │ cantidad     │    │ id_usuario (FK) │
│  notificaciones  │   │ ubicacion    │    │ id_maquina (FK) │
│──────────────────│   │ stock_minimo │    │ prioridad       │
│ id_notificacion  │   └──────────────┘    │ estado          │
│ id_usuario (FK)  │                       │ fecha_reporte   │
│ titulo / mensaje │                       └─────────────────┘
│ tipo / leida     │
│ fecha_creacion   │
└──────────────────┘
```

### Script SQL base

```sql
CREATE DATABASE IF NOT EXISTS maqguard;
USE maqguard;

CREATE TABLE rol (
    id_rol INT AUTO_INCREMENT PRIMARY KEY,
    descripcion_rol VARCHAR(50) NOT NULL
);

CREATE TABLE usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    usuario VARCHAR(50) UNIQUE NOT NULL,
    contrasena VARCHAR(255) NOT NULL,
    estado VARCHAR(20) DEFAULT 'activo',
    id_rol INT,
    FOREIGN KEY (id_rol) REFERENCES rol(id_rol)
);

CREATE TABLE maquinas (
    id_maquina BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    modelo VARCHAR(100),
    ubicacion VARCHAR(150),
    fecha_ultimo_mantenimiento DATE,
    intervalo_mantenimiento INT,
    fecha_proximo_mantenimiento DATE,
    estado VARCHAR(50)
);

CREATE TABLE mantenimientos (
    id_mantenimiento BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_maquina BIGINT,
    id_usuario INT,
    fecha_mantenimiento DATE,
    tipo_mantenimiento VARCHAR(100),
    descripcion_trabajo TEXT,
    costo_mantenimiento DOUBLE,
    estado VARCHAR(50),
    FOREIGN KEY (id_maquina) REFERENCES maquinas(id_maquina),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

CREATE TABLE inventario (
    id_repuesto BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    codigo_pieza VARCHAR(50),
    cantidad INT DEFAULT 0,
    ubicacion_almacen VARCHAR(150),
    stock_minimo INT DEFAULT 0
);

CREATE TABLE reporte_falla (
    id_falla BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(150),
    descripcion_falla TEXT,
    id_usuario BIGINT,
    prioridad VARCHAR(20),
    estado VARCHAR(30) DEFAULT 'pendiente',
    fecha_reporte DATETIME,
    fecha_actualizacion DATETIME,
    id_maquina BIGINT
);

CREATE TABLE notificaciones (
    id_notificacion BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT,
    titulo VARCHAR(150),
    mensaje TEXT,
    tipo VARCHAR(50),
    leida INT DEFAULT 0,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

-- Datos iniciales
INSERT INTO rol (descripcion_rol) VALUES ('ADMINISTRADOR'), ('TECNICO');
```

---

## 🔐 Roles y Seguridad

El sistema usa **Spring Security** con BCrypt para el hash de contraseñas y dos roles definidos:

| Rol | Authority | Acceso |
|-----|-----------|--------|
| Administrador | `ROLE_ADMINISTRADOR` | Dashboard admin, gestión de máquinas, todos los mantenimientos, inventario, notificaciones globales |
| Técnico | `ROLE_TECNICO` | Dashboard técnico, solo sus mantenimientos asignados, historial propio |

---

## 🗺️ Rutas del Sistema

| Ruta | Método | Controller | Descripción |
|------|--------|------------|-------------|
| `/` | GET | InicioController | Página de inicio pública |
| `/login` | GET | InicioController | Formulario de login |
| `/inicio` | GET | InicioController | Redirección al dashboard por rol |
| `/maquinas` | GET | MaquinasController | Listar máquinas |
| `/maquinas/crear` | GET | MaquinasController | Formulario nueva máquina |
| `/maquinas/guardar` | POST | MaquinasController | Guardar máquina |
| `/maquinas/editar/{id}` | GET | MaquinasController | Formulario edición |
| `/maquinas/eliminar/{id}` | GET | MaquinasController | Eliminar máquina |
| `/maquinas/alertas` | GET | MaquinasController | Alertas próximo mantenimiento |
| `/maquinas/exportarExcel` | GET | MaquinasController | Descargar Excel de máquinas |
| `/mantenimientos` | GET | ManteAdminController | Listar mantenimientos |
| `/mantenimientos/crear` | GET | ManteAdminController | Formulario nuevo mantenimiento |
| `/mantenimientos/guardar` | POST | ManteAdminController | Guardar mantenimiento |
| `/mantenimientos/editar/{id}` | GET | ManteAdminController | Editar mantenimiento |
| `/mantenimientos/eliminar/{id}` | GET | ManteAdminController | Eliminar mantenimiento |
| `/mantenimientos/exportarExcel` | GET | ManteAdminController | Descargar Excel de mantenimientos |
| `/tecnico/dashboard` | GET | TecnicoController | Dashboard del técnico |
| `/tecnico/mismantenimientos` | GET | TecnicoController | Tareas asignadas al técnico |
| `/tecnico/mismantenimientos/{id}/iniciar` | GET | TecnicoController | Iniciar tarea de mantenimiento |
| `/tecnico/mismantenimientos/{id}/finalizar` | POST | TecnicoController | Finalizar tarea |
| `/tecnico/historial` | GET | TecnicoController | Historial del técnico |
| `/tecnico/historial/{id}` | GET | TecnicoController | Detalle de tarea |
| `/inventario` | GET | AdminInventarioController | Listar inventario |
| `/inventario/nuevo` | GET | AdminInventarioController | Formulario nuevo repuesto |
| `/inventario/guardar` | POST | AdminInventarioController | Guardar repuesto |
| `/inventario/editar/{id}` | GET | AdminInventarioController | Editar repuesto |
| `/inventario/eliminar/{id}` | GET | AdminInventarioController | Eliminar repuesto |
| `/inventario/exportar` | GET | AdminInventarioController | Descargar Excel inventario |
| `/falla` | GET | ReporteFallasController | Listar reportes de fallas |
| `/falla/crear` | GET | ReporteFallasController | Formulario nueva falla |
| `/falla/guardar` | POST | ReporteFallasController | Guardar falla |
| `/falla/ver/{id}` | GET | ReporteFallasController | Ver detalle de falla |
| `/falla/editar/{id}` | GET | ReporteFallasController | Editar falla |
| `/falla/actualizar/{id}` | POST | ReporteFallasController | Actualizar falla |
| `/falla/eliminar/{id}` | GET | ReporteFallasController | Eliminar falla |
| `/notificaciones` | GET | NotificacionController | Ver notificaciones |
| `/notificaciones/marcarLeida/{id}` | GET | NotificacionController | Marcar como leída |
| `/notificaciones/marcarTodasLeidas` | POST | NotificacionController | Marcar todas como leídas |
| `/notificaciones/eliminar/{id}` | GET | NotificacionController | Eliminar notificación |

---

## ⚙️ Instalación y Configuración

### Requisitos previos

- Java JDK 21+
- Maven 3.8+
- MySQL 8.x
- IDE recomendado: IntelliJ IDEA / Eclipse / VS Code

### 1. Clonar el repositorio

```bash
git clone https://github.com/marlongarciacruz/maqguard.git
cd maqguard
```

### 2. Crear la base de datos

```sql
CREATE DATABASE maqguard;
```

Luego ejecuta el script SQL completo de la sección [Modelo de Datos](#-modelo-de-datos).

### 3. Configurar `application.properties`

```properties
spring.application.name=proyectogaes

# Base de Datos MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/maqguard?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=TU_CONTRASEÑA

# JPA / Hibernate
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect

# Thymeleaf
spring.thymeleaf.cache=false
```

> ⚠️ Reemplaza `TU_CONTRASEÑA` con la contraseña de tu MySQL local. En el repositorio la contraseña está vacía por defecto.

### 4. Compilar e instalar dependencias

```bash
mvn clean install
```

### 5. Ejecutar la aplicación

```bash
mvn spring-boot:run
```

La aplicación estará disponible en: **http://localhost:8080**

---

## 📊 Exportación a Excel

MaqGuard implementa exportación a Excel en tres módulos usando **Apache POI 5.2.3**:

| Módulo | Ruta | Archivo generado | Columnas exportadas |
|--------|------|-----------------|---------------------|
| Máquinas | `/maquinas/exportarExcel` | `maquinas_AAAAMMDD.xlsx` | ID, Nombre, Modelo, Ubicación, Últ. Manto, Intervalo, Próx. Manto, Estado |
| Mantenimientos | `/mantenimientos/exportarExcel` | `mantenimientos_AAAAMMDD.xlsx` | ID, Fecha, Máquina, Técnico, Tipo, Descripción, Costo, Estado |
| Inventario | `/inventario/exportar` | `inventario_TIMESTAMP.xlsx` | ID, Nombre, Código pieza, Cantidad, Ubicación, Stock mínimo |

---

## 🔄 Metodología Scrum Aplicada

El desarrollo de MaqGuard se gestiona bajo el marco de trabajo **Scrum**, elegido por su adaptabilidad a los cambios de requerimientos del contexto industrial.

### ¿Por qué Scrum para MaqGuard?

- Los requerimientos de seguridad y monitoreo industrial evolucionan durante el desarrollo
- Permite priorizar funciones críticas (alertas de maquinaria, gestión de usuarios) en Sprints tempranos
- La retroalimentación constante del stakeholder reduce el margen de error
- Facilita la entrega incremental de módulos funcionales (inventario → mantenimientos → técnico)

### Ceremonias implementadas

| Ceremonia | Frecuencia | Propósito |
|-----------|-----------|-----------|
| Sprint Planning | Inicio de cada Sprint | Definir el backlog del Sprint |
| Daily Scrum | Diaria (15 min) | Sincronización del equipo |
| Sprint Review | Final de Sprint | Demo de funcionalidades entregadas |
| Sprint Retrospective | Final de Sprint | Mejora continua del proceso |

### Historias de Usuario — Sprint 6

- **HU-01:** Como administrador, quiero registrar y editar máquinas para mantener actualizado el inventario de equipos
- **HU-02:** Como administrador, quiero asignar mantenimientos a técnicos para distribuir el trabajo
- **HU-03:** Como técnico, quiero ver mis mantenimientos asignados para organizar mi trabajo diario
- **HU-04:** Como técnico, quiero finalizar un mantenimiento registrando los repuestos utilizados
- **HU-05:** Como administrador, quiero recibir alertas de máquinas con mantenimiento próximo
- **HU-06:** Como administrador, quiero exportar reportes a Excel para análisis gerencial
- **HU-07:** Como usuario, quiero recibir notificaciones internas del sistema según mi rol

---

## 📚 Referencias Bibliográficas

- Beck, K. et al. (2001). *Manifiesto por el Desarrollo Ágil de Software*. http://agilemanifesto.org/iso/es/manifesto.html
- Schwaber, K., & Sutherland, J. (2020). *La Guía de Scrum: Las Reglas del Juego*. https://scrumguides.org
- Apache Software Foundation. (2023). *Apache POI: Java API for Microsoft Documents*. https://poi.apache.org/
- Oracle. (2023). *Java SE 21 Documentation*. https://docs.oracle.com/en/java/javase/21/
- VMware. (2024). *Spring Boot Reference Guide (v4.0.5)*. https://docs.spring.io/spring-boot/docs/current/reference/html/
- Spring Framework. (2024). *Spring Security Reference*. https://docs.spring.io/spring-security/reference/
- Anderson, D.J. (2010). *Kanban: Successful Evolutionary Change for Your Technology Business*. Blue Hole Press.

---

## 📄 Licencia

Proyecto académico desarrollado para el **Servicio Nacional de Aprendizaje (SENA)** — Programa Análisis y Desarrollo de Software. Ficha 3114732.

---

> 🛡️ **MaqGuard** — *Protegiendo tu maquinaria, optimizando tu producción.*
