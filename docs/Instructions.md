**Инструкция по запуску приложения и проверке эндпоинтов через Swagger UI**

---

## 1. Подготовка окружения

1. **Установки и требования**
    - JDK 17+
    - Maven (или Gradle)
    - PostgreSQL (порт 54320, БД `fin_monitoring`, юзер `user`/`password`)
2. **Запуск PostgreSQL (пример через Docker)**
   ```bash
   docker run -d \
     --name finmon-postgres \
     -p 54320:5432 \
     -e POSTGRES_DB=fin_monitoring \
     -e POSTGRES_USER=user \
     -e POSTGRES_PASSWORD=password \
     postgres:15
   ```
3. **Клонирование репозитория и сборка**
   ```bash
   git clone <url вашего репо>
   cd financial-monitoring
   mvn clean package   # или ./gradlew build
   ```
4. **Запуск приложения**
    - через Maven:
      ```bash
      mvn spring-boot:run
      ```
    - или через JAR:
      ```bash
      java -jar target/finmonitor-0.0.1-SNAPSHOT.jar
      ```
   Приложение стартует на порту **8085**.

---

## 2. Открытие Swagger UI

В браузере зайдите по адресу:

```
http://localhost:8085/swagger-ui/index.html
```

Здесь отобразятся все доступные эндпоинты, схемы DTO и возможность «Try it out».

---

## 3. Регистрация и получение JWT

1. **POST /auth/register**
    - Путь: `/auth/register`
    - Тело запроса:
      ```json
      {
        "username": "user1",
        "password": "pass123"
      }
      ```
    - Ожидаемый ответ: `201 Created` (пусто)

2. **POST /auth/login**
    - Путь: `/auth/login`
    - Тело запроса:
      ```json
      {
        "username": "user1",
        "password": "pass123"
      }
      ```
    - Ожидаемый ответ:
      ```json
      {
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
      }
      ```

3. **В Swagger UI**
    - Нажмите кнопку **Authorize** (рядом с заголовком API).
    - Введите в поле:
      ```
      Bearer <скопированный_токен>
      ```  
    - Нажмите **Authorize**, затем **Close**. Теперь все защищённые эндпоинты будут доступны.

---

## 4. Работа с транзакциями

Все запросы ниже делать уже **с валидным JWT**.

### 4.1 Получить список транзакций
- **GET** `/transactions`
- Параметры (опционально):
    - `bankSenderId` (UUID)
    - `bankReceiverId` (UUID)
    - `fromDate`, `toDate` (ISO: `2025-04-01T00:00:00`)
    - `statusId` (UUID)
    - `receiverTin` (строка 11 цифр)
    - `minAmount`, `maxAmount` (число)
    - `transactionTypeId`, `categoryId` (UUID)
    - `page`, `size`
- Пример curl:
  ```bash
  curl -X GET "http://localhost:8085/transactions?minAmount=100&maxAmount=500" \
    -H "Authorization: Bearer eyJhbGciOi..." \
    -H "Accept: application/json"
  ```

### 4.2 Создать транзакцию
- **POST** `/transactions`
- Тело:
  ```json
  {
    "partyTypeId": "11111111-1111-1111-1111-111111111111",
    "transactionTypeId": "22222222-2222-2222-2222-222222222222",
    "statusId": "33333333-3333-3333-3333-333333333333",
    "bankSenderId": "44444444-4444-4444-4444-444444444444",
    "bankReceiverId": "55555555-5555-5555-5555-555555555555",
    "accountSender": "ACC123456789012",
    "accountReceiver": "ACC987654321098",
    "categoryId": "66666666-6666-6666-6666-666666666666",
    "timestamp": "2025-04-23T12:00:00",
    "amount": 150.50,
    "receiverTin": "12345678901",
    "receiverPhone": "+71234567890",
    "comment": "Test payment"
  }
  ```
- Пример curl:
  ```bash
  curl -X POST http://localhost:8085/transactions \
    -H "Authorization: Bearer eyJhbGciOi..." \
    -H "Content-Type: application/json" \
    -d '@payload.json'
  ```
- Ожидаемый ответ: `201 Created` с DTO созданной транзакции.

### 4.3 Обновить транзакцию
- **PUT** `/transactions/{id}`
- Тело — аналогично POST, но без параметров `createdByUserId`.

### 4.4 Удалить транзакцию
- **DELETE** `/transactions/{id}`
- Ожидаемый ответ: `204 No Content`

---

## 5. Проверка дашбордов

Эндпоинты формата `/dashboard/transactions/...`, параметр `period` по умолчанию `week`.

| Эндпоинт                                      | Описание                                    | Пример URL                             |
|-----------------------------------------------|---------------------------------------------|----------------------------------------|
| **GET** `/dashboard/transactions/count`       | Динамика числа транзакций                   | `?period=month`                        |
| **GET** `/dashboard/transactions/by-type`     | Динамика по debit/credit                    | `?period=quarter`                      |
| **GET** `/dashboard/transactions/sums`        | Суммы поступлений vs списаний               | (по умолчанию `week`)                  |
| **GET** `/dashboard/transactions/status-count`| Кол-во проведённых и отменённых транзакций  | `?period=year`                         |
| **GET** `/dashboard/transactions/by-bank`     | Статистика по банкам (роль=sender/receiver) | `?role=sender&period=month`            |
| **GET** `/dashboard/transactions/by-category` | Статистика по категориям (type=debit/credit)| `?type=credit&period=week`             |

**Примеры curl**:

```bash
# Динамика по количеству за месяц
curl -X GET "http://localhost:8085/dashboard/transactions/count?period=month" \
  -H "Authorization: Bearer eyJhbGciOi..." \
  -H "Accept: application/json"

# Статусы транзакций за неделю
curl -X GET "http://localhost:8085/dashboard/transactions/status-count" \
  -H "Authorization: Bearer eyJhbGciOi..."
```

