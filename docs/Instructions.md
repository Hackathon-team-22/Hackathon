**Инструкция по запуску приложения и проверке эндпоинтов через Swagger UI**

---

## 1. Подготовка окружения

1. **Установки и требования:**
    - JDK 17+
    - Maven или Gradle
    - PostgreSQL (порт `54320`, БД `fin_monitoring`, логин `user`, пароль `password`)

2. **Запуск PostgreSQL (пример через Docker):**
   ```bash
   docker run -d \
     --name finmon-postgres \
     -p 54320:5432 \
     -e POSTGRES_DB=fin_monitoring \
     -e POSTGRES_USER=user \
     -e POSTGRES_PASSWORD=password \
     postgres:15
   ```

3. **Клонирование репозитория и сборка проекта:**
   ```bash
   git clone <url вашего репо>
   cd financial-monitoring
   mvn clean package   # или ./gradlew build
   ```

4. **Запуск приложения:**
   ```bash
   mvn spring-boot:run
   # или
   java -jar target/finmonitor-0.0.1-SNAPSHOT.jar
   ```
   Приложение стартует на порту **8085**.

---

## 2. Swagger UI: Обзор и работа

Откройте в браузере:
```
http://localhost:8085/swagger-ui/index.html
```

Здесь отображаются все доступные эндпоинты, описание DTO, возможность тестирования `Try it out`.

### Новое: выбор API-групп в селекторе
- Интерфейс теперь разбит по **группам**:
    - `1. Auth`
    - `2. Transactions`
    - `3. Dashboard`
    - `4. Export`
- Каждая группа содержит свои эндпоинты, что упрощает навигацию и тестирование.

---

## 3. Регистрация и авторизация

### POST `/auth/register`
```json
{
  "username": "user1",
  "password": "pass123"
}
```
**Ответ:** `201 Created`

### POST `/auth/login`
```json
{
  "username": "user1",
  "password": "pass123"
}
```
**Ответ:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Ввод токена в Swagger UI
1. Нажмите **Authorize**
2. Введите:
   ```
   Bearer <accessToken>
   ```
3. Нажмите **Authorize** и **Close**

---

## 4. Работа с транзакциями (требуется JWT)

### GET `/transactions`
Фильтрация через параметры запроса:
- `minAmount`, `maxAmount`
- `fromDate`, `toDate` (в формате ISO)
- `transactionTypeId`, `statusId`, `categoryId` (UUID)

Пример:
```bash
curl -X GET "http://localhost:8085/transactions?minAmount=100" \
 -H "Authorization: Bearer <token>"
```

### POST `/transactions`
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
**Ответ:** `201 Created`

### PUT `/transactions/{id}` — обновление
### DELETE `/transactions/{id}` — удаление

---

## 5. Эндпоинты Dashboard

| Метод  | Путь                                       | Описание                               |
|--------|--------------------------------------------|----------------------------------------|
| GET    | `/dashboard/transactions/count`           | Кол-во транзакций по периодам          |
| GET    | `/dashboard/transactions/by-type`         | Дебет/Кредит динамика                  |
| GET    | `/dashboard/transactions/sums`            | Сравнение сумм поступлений и списаний |
| GET    | `/dashboard/transactions/status-count`    | Кол-во проведённых/отменённых          |
| GET    | `/dashboard/transactions/by-bank`         | Статистика по банкам                   |
| GET    | `/dashboard/transactions/by-category`     | Расходы и поступления по категориям    |

`period=week|month|quarter|year`, `type=debit|credit`, `role=sender|receiver`

---

## 6. Экспорт данных в CSV

### GET `/export/transactions`
- Параметры фильтрации — те же, что и для `/transactions`
- **Пример:**
```bash
curl -X GET "http://localhost:8085/export/transactions?minAmount=100" \
 -H "Authorization: Bearer <token>" \
 -H "Accept: text/csv" \
 -o transactions.csv
```

### GET `/export/audit`
- Без параметров
- Выгружает весь аудит:
```bash
curl -X GET http://localhost:8085/export/audit \
 -H "Authorization: Bearer <token>" \
 -o audit_log.csv
```

---

## 7. Дополнительно

- Swagger JSON доступен по адресу:
  ```
  http://localhost:8085/v3/api-docs
  ```
- Можно использовать профили Spring:
  ```bash
  mvn spring-boot:run -Dspring-boot.run.profiles=dev
  ```
- Для тестирования можно использовать Postman / Insomnia / curl.

---


