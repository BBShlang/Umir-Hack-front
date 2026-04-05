# 🏗️ Umir Hack — Backend

> Микросервисная платформа для верификации дипломов — бэкенд часть

[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0-6db33f?logo=springboot)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ed?logo=docker)](https://docs.docker.com/compose/)
[![Kafka](https://img.shields.io/badge/Apache-Kafka-231f20?logo=apachekafka)](https://kafka.apache.org/)

---

## 📋 О ветке

Ветка **`backend`** содержит **бэкенд-микросервисы** платформы верификации дипломов. Архитектура построена на **Spring Boot 4.0** с использованием **PostgreSQL**, **Redis**, **Kafka** и **JWT-аутентификации**.

## 🏛️ Архитектура

```
┌─────────────────────────────────────────────────────────────┐
│                    Docker Compose                           │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌──────────────────┐    Kafka     ┌──────────────────────┐ │
│  │  issuer-service  │ ──────────► │ verification-service │ │
│  │    (port 8081)   │   events    │     (port 8082)      │ │
│  └────────┬─────────┘             └──────────┬───────────┘ │
│           │                                   │             │
│     ┌─────▼──────┐                     ┌─────▼──────┐      │
│     │ PostgreSQL  │                     │ PostgreSQL  │      │
│     │  (port 5433)│                     │  (port 5434)│      │
│     │  issuer_db  │                     │verification │      │
│     └────────────┘                     └────────────┘      │
│                                                             │
│  ┌──────────────────┐         ┌──────────────────┐         │
│  │      Redis       │         │  Kafka + ZK      │         │
│  │   (port 6379)    │         │  (ports 9092)    │         │
│  └──────────────────┘         └──────────────────┘         │
└─────────────────────────────────────────────────────────────┘
```

## 🚀 Быстрый старт

### Способ 1: Docker Compose (рекомендуется)

```bash
cd backend

# Сборка и запуск всех сервисов
docker-compose up -d --build

# Проверка статуса
docker-compose ps

# Просмотр логов
docker-compose logs -f issuer-service
docker-compose logs -f verification-service

# Остановка
docker-compose down
```

### Способ 2: Локальный запуск (для разработки)

```bash
# 1. Запустите инфраструктуру
docker-compose up -d postgres-issuer postgres-verification redis kafka zookeeper

# 2. Соберите сервисы
cd issuer-service
mvnw.cmd clean package -DskipTests

cd ../verification-service
mvnw.cmd clean package -DskipTests

# 3. Запустите сервисы
cd issuer-service
mvnw.cmd spring-boot:run

cd ../verification-service
mvnw.cmd spring-boot:run
```

## 📦 Микросервисы

### 🔐 Issuer Service (порт 8081)

Сервис **выпуска и управления** дипломами.

**Функциональность:**
| Модуль | Описание |
|--------|----------|
| **Auth** | Регистрация/авторизация (студенты, университеты, работодатели), JWT |
| **Certificates** | CRUD дипломов, выпуск, массовый импорт через CSV |
| **Claims** | Запросы на выпуск дипломов (студент → университет) |
| **QR Tokens** | Генерация QR-токенов для верификации |
| **Crypto** | Хеширование, шифрование payload, подписи, управление ключами |
| **Verification** | Внутренний API для verification-service |
| **Student Profile** | Профиль студента, просмотр своих дипломов |

**Ключевые эндпоинты:**
```
POST   /api/auth/register/student      — Регистрация студента
POST   /api/auth/register/university   — Регистрация университета
POST   /api/auth/login                 — Авторизация
POST   /api/certificates/issue         — Выпуск диплома
POST   /api/certificates/bulk-import   — Массовый импорт CSV
GET    /api/certificates/student/me    — Мои дипломы
POST   /api/claims                     — Запрос на выпуск
POST   /api/qr/generate/{certId}      — Генерация QR
GET    /api/verify/internal/{certId}  — Внутренняя верификация
```

**Swagger UI:** `http://localhost:8081/swagger-ui.html`

---

### ✅ Verification Service (порт 8082)

Сервис **верификации** дипломов.

**Функциональность:**
| Модуль | Описание |
|--------|----------|
| **Verification** | Полная проверка диплома (подпись, хеш, TTL, отзыв, токен) |
| **Kafka Consumer** | Подписка на события `certificate.issued` для обновления проекций |
| **Crypto** | Дешифрование payload, верификация подписи, хеширование |
| **Audit** | Логирование всех проверок |

**Проверки при верификации:**
- ✅ **Токен** — валидность QR-токена
- ✅ **Срок действия (TTL)** — не истёк ли токен
- ✅ **Отзыв** — не отозван ли диплом
- ✅ **Хеш полезной нагрузки** — целостность данных
- ✅ **Подпись** — криптографическая подпись университетом

**Ключевые эндпоинты:**
```
POST   /api/verify              — Верификация по certificate ID
POST   /api/verify/qr           — Верификация по QR-токену
```

**Swagger UI:** `http://localhost:8082/swagger-ui.html`

---

## 🔧 Технологии

| Категория | Технология |
|-----------|------------|
| Язык | **Java 21** |
| Фреймворк | **Spring Boot 4.0** |
| БД | **PostgreSQL 16** (2 экземпляра) |
| Миграции | **Flyway** |
| ORM | **Spring Data JPA + Hibernate** |
| Кеширование | **Redis 7** |
| Брокер сообщений | **Apache Kafka 7.5** (Confluent) |
| Аутентификация | **JWT** |
| Документация | **SpringDoc OpenAPI (Swagger)** |
| Криптография | **ECDSA (secp256r1)**, **AES-256-GCM**, **SHA-256** |

## 📁 Структура проекта

```
backend/
├── docker-compose.yml              # Оркестрация всех сервисов
├── pom.xml                         # Родительский POM
│
├── issuer-service/                 # Сервис выпуска дипломов
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/example/issuerservice/
│       ├── auth/                   # Аутентификация и JWT
│       ├── certificate/            # Управление дипломами
│       ├── config/                 # Конфигурация (Security, Kafka, OpenAPI)
│       ├── crypto/                 # Криптография
│       ├── employer/               # Работодатели
│       ├── kafka/                  # Kafka Producer
│       ├── qr/                     # QR-токены
│       ├── security/               # JWT фильтр, UserDetailsService
│       ├── student/                # Студенты
│       ├── university/             # Университеты
│       ├── user/                   # Пользователи и роли
│       └── verification/           # Внутренняя верификация
│
└── verification-service/           # Сервис верификации
    ├── Dockerfile
    ├── pom.xml
    └── src/main/java/com/example/verificationservice/
        ├── client/                 # HTTP клиент к issuer-service
        ├── common/                 # Общие компоненты
        ├── config/                 # CORS, Jackson
        ├── crypto/                 # Дешифрование и верификация
        ├── kafka/                  # Kafka Consumer
        └── verification/           # Основная логика верификации
```

## 🔑 Переменные окружения

### Issuer Service
| Переменная | По умолчанию | Описание |
|-----------|-------------|----------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5433/issuer_db` | URL БД |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | `localhost:29092` | Kafka bootstrap servers |
| `SPRING_DATA_REDIS_HOST` | `localhost` | Redis host |
| `SPRING_DATA_REDIS_PORT` | `6379` | Redis port |
| `APP_CRYPTO_PAYLOAD_SECRET` | *(обязательно)* | Секрет для шифрования payload |
| `JWT_SECRET` | *(default)* | Секрет для JWT |
| `JWT_EXPIRATION_MS` | `86400000` | Время жизни JWT (24ч) |
| `QR_BASE_URL` | `http://localhost:8082/api/verify` | Базовый URL для QR-ссылок |
| `QR_TTL_MINUTES` | `1440` | Время жизни QR-токена (24ч) |

### Verification Service
| Переменная | По умолчанию | Описание |
|-----------|-------------|----------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5434/verification_db` | URL БД |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | `localhost:29092` | Kafka bootstrap servers |
| `SPRING_DATA_REDIS_HOST` | `localhost` | Redis host |
| `ISSUER_BASE_URL` | `http://issuer-service:8081` | URL issuer-service |
| `APP_CRYPTO_PAYLOAD_SECRET` | *(обязательно)* | Секрет для дешифрования |

## 🗄️ Миграции БД

### Issuer DB (Flyway)
| Версия | Описание |
|--------|----------|
| `V1` | Инициализация схемы (users, universities, students, certificates) |
| `V2` | Таблица QR-токенов |
| `V3` | Таблица работодателей |
| `V4` | Удаление QR-токена из certificates |
| `V5` | Запросы на выпуск дипломов (claim requests) |

### Verification DB (Flyway)
| Версия | Описание |
|--------|----------|
| `V1` | Проекции верификации и аудит |

## 🌐 Порты

| Сервис | Порт | Протокол |
|--------|------|----------|
| Issuer Service | **8081** | HTTP/REST |
| Verification Service | **8082** | HTTP/REST |
| PostgreSQL (Issuer) | **5433** | TCP |
| PostgreSQL (Verification) | **5434** | TCP |
| Redis | **6379** | TCP |
| Kafka | **9092** / **29092** | TCP |
| ZooKeeper | **2181** | TCP |

## 🔀 Связанные ветки

| Ветка | Описание |
|-------|----------|
| [`main`](https://github.com/BBShlang/Umir-Hack-front/tree/main) | Фронтенд (Vue 3 + Vite) |
| `backend` | ✅ Вы здесь — бэкенд |
| `master` | Устаревшая ветка (влита в main) |

## 📝 Пример ответа верификации

```json
{
  "isValid": true,
  "message": "Диплом подлинный",
  "reasonCode": "SIGNATURE_VERIFIED",
  "status": "VALID",
  "certificate": {
    "id": "d83f1ab8-9468-4481-9e12-e254a8afb4bc",
    "diplomaNumber": "DIP-2026-010",
    "universityCode": "AITU-07",
    "universityName": "МГУ",
    "graduateName": "К*** К*** К***",
    "specialty": "Информатика",
    "graduationYear": 2026
  },
  "checks": {
    "token": "PASSED",
    "ttl": "PASSED",
    "revocation": "PASSED",
    "payloadHash": "PASSED",
    "signature": "PASSED"
  }
}
```

## 🐛 Troubleshooting

### issuer-service не запускается
```bash
# Пересоберите JAR
cd issuer-service && mvnw.cmd clean package -DskipTests

# Пересоберите контейнер
docker-compose up -d --build issuer-service
```

### Kafka недоступна
```bash
# Проверьте статус
docker ps | grep kafka

# Перезапустите
docker-compose restart kafka
```

### Ошибки миграции БД
```bash
# Очистите и пересоздайте БД
docker-compose down -v
docker-compose up -d --build
```

---

> 📅 Последнее обновление: Апрель 2026
