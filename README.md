# Hortiña Server

**Hortiña Server** é unha API REST desenvolvida en **Java** con **Spring Boot** que funciona como backend de **Hortiña App**.  
Encárgase da persistencia de datos, autenticación, xeración de tarefas e integración con servizos externos, proporcionando toda a lóxica de negocio necesaria para apoiar o funcionamento da aplicación móbil.

---

## Características xerais, arquitectura e tecnoloxías empregadas

- **Java**  
- **Spring Boot** como framework principal  
- **Spring Web / REST** para a creación de endpoints  
- **Spring Security + JWT** para autenticación e autorización  
- **Spring Data JPA (Hibernate)** para acceso a datos  
- **MySQL** como base de datos relacional  
- **WebClient** para integración con APIs externas  
- **Tarefas programadas con `@Scheduled`** para a planificación automática  
- Arquitectura en capas: Controladores, Servizos, Repositorios, Entidades e Seguridade  

---

## Funcionalidades principais

### Autenticación e xestión de usuarios  
O servidor utiliza **JSON Web Tokens (JWT)** para xestionar a autenticación:

- Rexistro e login mediante email e contrasinal  
- Login mediante **Google Identity Services**  
- Control de acceso mediante filtros de Spring Security  
- Conversión automática de datos do usuario a `MyUserDetails`  

---

### Xestión de cultivos  
A API permite aos usuarios:

- Crear, editar e eliminar cultivos  
- Gardar información sobre o tipo de plantación (semente ou planta)  
- Consultar datos detallados e recibir recomendacións das tarefas asociadas  
- Relacionar cada cultivo cun **PlantProfile** para aplicar regras de mantemento  

---

### Integración coa API **Permapeople**  
O servidor usa **WebClient** para obter información desde Permapeople, unha base de datos aberta con miles de cultivos.

Funcionalidades:

- Buscar plantas por nome  
- Descargar información detallada dun cultivo concreto  
- Transformar os datos en **PlantProfile**, entidade propia do servidor  
- Gardar os perfís para evitar chamadas repetidas á API externa  

---

### Xeración intelixente de tarefas  
O backend inclúe un sistema avanzado de planificación baseado en regras asociadas a cada tipo de planta.

#### Regras de tarefa  
Cada `PlantProfile` define accións recomendadas:

- Frecuencias de rego  
- Necesidades estacionais  
- Fertilización  
- Accións periódicas  

#### TaskGenerationService  
Este servizo xera tarefas automaticamente:

- Ao crear un cultivo novo  
- Ao actualizar as regras  
- Cando o usuario solicita rexenerar as tarefas dun cultivo  

#### TaskSchedulerService  
Un proceso programado con `@Scheduled`:

- Execútase cada día ás 6 da mañá
- Comproba cultivos activos  
- Detecta que tarefas deberían xerarse segundo as regras  
- Evita duplicados comprobando tarefas pendentes equivalentes  
- Engade só as tarefas necesarias  

---

### Xestión manual de tarefas  
O usuario pode:

- Crear tarefas personalizadas para cada cultivo  
- Engadir periodicidade
- Consultar o historial de accións realizadas  

---

## Arquitectura xeral

Android App (desenvolvida en Kotlin) -> Retrofit -> Hortiña Server (desenvolvido en Spring Boot) -> Base de datos de MySQL

---

## Sinatura

Este proxecto foi construído como **traballo de fin de ciclo de FP DAM** con fins educativos.

**Autor:** Alejandro Vázquez Corral  
**Proxecto:** Hortiña Server  
**Ano:** 2025  
**Ciclo:** Ciclo Superior de Desenvolvemento de Aplicacións Multiplataforma  
**Centro:** IES Fernando Wirtz 

