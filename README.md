# О проекте

Этот проект представляет собой сервис
для управления подписками на печатные издания через почтовую службу.

## Технологии

- **Spring Boot 3**
- **Spring Cloud** (Eureka, Gateway, Scheduling, OpenFeign, Circuit Breaker)
- **Spring Security (JWT)**
- **Spring Data JPA**
- **Kafka** (асинхронные события)
- **PostgreSQL** + **Flyway** (миграции БД)
- **MinIO** (хранение изображений)
- **Docker** + **docker-compose)** (контейнеризация)
- **JUnit 5** (тестирование)
- **SpringDoc OpenAPI (Swagger)**
---

## Архитектура

Проект написан на микросервисной архитектуре и состоит из следующих сервисов:

### 1. Gateway Service (`gateway-api`)
- API-шлюз для проксирования запросов между клиентами и микросервисами.
- Балансировка нагрузки через **Spring Cloud Gateway**.
- Документирование API с **SpringDoc OpenAPI**.
- Обрабатывает CORS-запросы.

### 2. Auth Service (`auth-service`)
- Аутентификация и авторизация пользователей с помощью **JWT**.
- Хранение данных о пользователях и ролях в **PostgreSQL**.
- Управление пользователями (регистрация, вход, изменение ролей).
- Используется **Spring Security** и **Spring Data JPA**.

### 3. Subscriber Service (`subscriber-service`)
- Управление подписчиками (создание, обновление, удаление данных).
- Получение событий подписчиков через **Kafka**.
- Хранение данных в **PostgreSQL**.

### 4. Publication Service (`publication-service`)
- Управление печатными изданиями (добавление, обновление, удаление).
- Интеграция с **MinIO** для хранения изображений.
- Использование **Spring Data JPA** и **Spring Security (JWT)**.

### 5. Subscription Service (`subscription-service`)
- Оформление подписок, расчет стоимости.
- Взаимодействие с **Payment Service** и **Payment Service** и другими сервисами через **Kafka**.
- Автоматическая отмена неподтвержденных подписок через **Kafka**.

### 6. Payment Service (`payment-service`)
- Рассчитывает стоимость подписок, скидки, налоги.
- Обрабатывает платежи и частичные возвраты.
- Использует **Kafka** для обмена событиями с `subscription-service`.

### 7. Notification Service (`notification-service`)
- Отправка email-уведомлений подписчикам и почтальонам.
- Использует **Kafka** для обработки событий (`notification-events`).
- Шаблонные письма через **Thymeleaf**.

### 8. Report Service (`report-service`)
- Генерация отчетов в **Excel** на основе подписок.
- Использует **Apache POI** и данные из других сервисов.

---


## Развертывание

### 1. Запуск сервисов через docker-compose
```bash
docker-compose up -d
```

### 2. Swagger UI (доступ через Gateway)
Перейти на [Swagger UI](http://localhost:8765/swagger-ui.html "открыть swagger документацию")


---

## Переменные окружения

### 1. PostgreSQL Credentials

```plaintext
AUTH_DB_NAME=<имя_базы_данных_AuthService>
AUTH_DB_USER=<пользователь_базы_AuthService>
AUTH_DB_PASSWORD=<пароль_базы_AuthService>

SUBSCRIBER_DB_NAME=<имя_базы_данных_SubscriberService>
SUBSCRIBER_DB_USER=<пользователь_базы_SubscriberService>
SUBSCRIBER_DB_PASSWORD=<пароль_базы_SubscriberService>

POSTAL_DB_NAME=<имя_базы_данных_PostalService>
POSTAL_DB_USER=<пользователь_базы_PostalService>
POSTAL_DB_PASSWORD=<пароль_базы_PostalService>

PUBLICATION_DB_NAME=<имя_базы_данных_PublicationService>
PUBLICATION_DB_USER=<пользователь_базы_PublicationService>
PUBLICATION_DB_PASSWORD=<пароль_базы_PublicationService>

SUBSCRIPTION_DB_NAME=<имя_базы_данных_SubscriptionService>
SUBSCRIPTION_DB_USER=<пользователь_базы_SubscriptionService>
SUBSCRIPTION_DB_PASSWORD=<пароль_базы_SubscriptionService>

PAYMENT_DB_NAME=<имя_базы_данных_PaymentService>
PAYMENT_DB_USER=<пользователь_базы_PaymentService>
PAYMENT_DB_PASSWORD=<пароль_базы_PaymentService>

REPORT_DB_NAME=<имя_базы_данных_ReportService>
REPORT_DB_USER=<пользователь_базы_ReportService>
REPORT_DB_PASSWORD=<пароль_базы_ReportService>
```

### 2. Kafka и MinIO
```plaintext
MINIO_ROOT_USER=<root_пользователь_minio>
MINIO_ROOT_PASSWORD=<пароль_minio>
```

### 3. Eureka
```plaintext
EUREKA_SERVER=<URL_сервера_Eureka>
```

### 4. JWT
```plaintext
JWT_SECRET=<случайная_строка_для_JWT>

# Время жизни access-токена (например, 10 часов)
ACCESS_EXPIRATION=36000000

# Время жизни refresh-токена (например, 7 дней)
REFRESH_EXPIRATION=252000000
```

### 5.  Внутренний секретный ключ API
```plaintext
INTERNAL_API_SECRET=<секретный_ключ_API>
```

### 6. Настройки почты
```plaintext
MAIL_USER=<email_почтового_сервиса>
MAIL_PASSWORD=<пароль_почтового_сервиса>
```


