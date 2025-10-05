# Инструкция по запуску City Management System

## Быстрый старт

### 1. Запуск базы данных PostgreSQL

```bash
# Запуск PostgreSQL через Docker Compose
docker-compose up -d

# Проверка, что контейнер запущен
docker ps
```

### 2. Настройка переменных окружения

Создайте файл `.env` в корне проекта или установите переменные окружения:

```bash
# Для Windows (PowerShell)
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="postgres"

# Для Linux/Mac
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
```

### 3. Запуск приложения

```bash
# Запуск через Gradle
./gradlew bootRun

# Или через IDE - запустите класс Lab1Application
```

### 4. Открытие приложения

Откройте браузер и перейдите по адресу: http://localhost:8080

## Проверка работы

### Основные функции
1. **Главная страница** - должна отображать таблицу городов (пустую при первом запуске)
2. **Добавление города** - нажмите "Add New City" и заполните форму
3. **Просмотр города** - нажмите на кнопку глаза в таблице
4. **Редактирование** - нажмите на кнопку карандаша
5. **Удаление** - нажмите на кнопку корзины

### Специальные операции
1. Перейдите на страницу "Special Operations"
2. Попробуйте выполнить различные операции:
   - Поиск городов по имени
   - Расчет среднего уровня моря
   - Переселение населения

## Возможные проблемы

### Ошибка подключения к базе данных
```
Connection refused
```
**Решение**: Убедитесь, что PostgreSQL запущен:
```bash
docker-compose ps
docker-compose logs postgres
```

### Ошибка валидации данных
```
Validation failed
```
**Решение**: Проверьте, что все обязательные поля заполнены согласно требованиям:
- Name: не пустое
- Area: > 0
- Population: > 0
- Coordinates: X > -687, Y > -449
- Climate: выбран из списка
- Standard of Living: выбран из списка
- Governor Height: > 0

### Ошибка порта
```
Port 8080 already in use
```
**Решение**: Измените порт в `application.properties`:
```properties
server.port=8081
```

## Структура базы данных

При первом запуске приложение автоматически создаст таблицы:
- `cities` - основная таблица городов
- `coordinates` - координаты городов
- `humans` - информация о губернаторах

## API тестирование

### Через curl

```bash
# Получить список городов
curl http://localhost:8080/api/cities

# Создать новый город
curl -X POST http://localhost:8080/api/cities \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test City",
    "area": 100.5,
    "population": 50000,
    "climate": "OCEANIC",
    "standardOfLiving": "VERY_LOW",
    "coordinates": {
      "x": 100.0,
      "y": 200.0
    },
    "governor": {
      "height": 180.0
    }
  }'

# Получить город по ID
curl http://localhost:8080/api/cities/1

# Удалить город
curl -X DELETE http://localhost:8080/api/cities/1
```

### Через Postman

Импортируйте коллекцию API запросов или создайте запросы вручную:
- Base URL: `http://localhost:8080/api/cities`
- Headers: `Content-Type: application/json`

## Остановка приложения

```bash
# Остановка приложения
Ctrl+C

# Остановка базы данных
docker-compose down

# Остановка с удалением данных
docker-compose down -v
```

## Логи

Логи приложения можно найти в консоли или в файлах логов Spring Boot.

Для отладки включите подробные логи в `application.properties`:
```properties
logging.level.is.lab1=DEBUG
logging.level.org.springframework.web=DEBUG
```
