# 🏥 API Appointment Management v1

Microservicio encargado de la gestión de citas médicas dentro del sistema de fisioterapia. Permite crear, actualizar, cancelar y consultar citas, integrándose con Kafka mediante eventos Avro y validando usuarios a través del microservicio de autenticación.

---

## 🚀 Tecnologías

- Java 21  
- Quarkus  
- PostgreSQL  
- Apache Kafka  
- Confluent Schema Registry (Avro)  
- Docker  
- Maven  

---

## 🧠 Arquitectura

El microservicio sigue una arquitectura basada en capas:

- **Resource** → Exposición de endpoints REST  
- **Service** → Lógica de negocio  
- **Repository** → Acceso a datos  
- **Messaging** → Publicación y consumo de eventos Kafka  

---

## 📌 Funcionalidades

### 📅 Gestión de Citas
- Crear cita  
- Editar cita  
- Cancelar cita  
- Listar citas del paciente  

### 📚 Catálogos
- Listar especialidades  
- Listar servicios de terapia  
- Listar fisioterapeutas  

### 🔐 Seguridad
- Integración con `api-auth-profile-v1`  
- Validación de usuarios habilitados  

### 📡 Eventos
- Publicación de eventos en Kafka:
  - `APPOINTMENT_CREATED`  
  - `APPOINTMENT_UPDATED`  
  - `APPOINTMENT_CANCELLED`  

---

## 🧪 Endpoints principales

### 🔐 Requieren token

- `GET /appointments`  
- `POST /appointments`  
- `PUT /appointments/{id}`  
- `DELETE /appointments/{id}`  

### 📖 Públicos

- `GET /catalogs/specialties`  
- `GET /catalogs/therapy-services`  
- `GET /catalogs/physiotherapists`  

---

## 🔑 Ejemplo de uso

### Crear cita

```json
{
  "therapyServiceId": "22222222-2222-2222-2222-222222222222",
  "physiotherapistId": "44444444-4444-4444-4444-444444444444",
  "appointmentDate": "2026-04-27T10:00:00",
  "notes": "Dolor lumbar"
}