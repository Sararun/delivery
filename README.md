# Демо проект к курсу "Domain Driven Design и Clean Architecture на языке Java"
📚 Подробнее о курсе: [microarch.ru/courses/ddd/languages/java](https://microarch.ru/courses/ddd/languages/java?utm_source=gitlab&utm_medium=repository)

---

## Условия использования

Вы можете использовать и модифицировать данный код **в образовательных целях**, при условии сохранения ссылки на курс и оригинального источника.

---

# 🐳 Локальная разработка в Docker (Dev Containers)

Вся среда для разработки описана в `docker-compose.yml` и каталоге `.devcontainer/`.
Поднимаются два контейнера:

| Сервис | Что это       | Порт |
|--------|---------------|------|
| `app`  | Java          |      |
| `db`   | PostgreSQL 16 | 5432 |

## Вариант 1. Через IDE (рекомендуется)

IntelliJ IDEA и VS Code умеют открывать проект прямо внутри контейнера
(«Dev Container») — Java, Maven и БД поднимутся автоматически.

- **IntelliJ IDEA:** *Remote Development → Dev Containers*, указать
  `.devcontainer/devcontainer.json` (либо принять подсказку IDE при открытии проекта).
- **VS Code:** установить расширение *Dev Containers*, затем *Reopen in Container*.

Дальше приложение запускается кнопкой Run или командой `mvn spring-boot:run`
во встроенном терминале.

## Вариант 2. Через терминал (без IDE)

```bash
docker compose up -d
docker compose exec app mvn spring-boot:run
```

Приложение будет доступно на http://localhost:8082
К базе можно подключиться с хоста: `localhost:5432`, имя БД / пользователь / пароль — `delivery`.

# Запросы к БД
```
SELECT * public.assignments;
SELECT * FROM public.couriers;
SELECT * FROM public.orders;
SELECT * public.outbox;
```

# Очистка БД (все кроме справочников)
```
DELETE FROM public.assignments;
DELETE FROM public.couriers;
DELETE FROM public.orders;

DELETE FROM public.outbox;
```

# Генерация HTTP сервера
```
mvn clean compile
```

# Генерация gRPC клиента из Protobuf
```
mvn clean compile
```
# Генерация интеграционных событий Kafka из Protobuf
```
mvn clean compile
```

## Лицензия

Код распространяется под лицензией [MIT](./LICENSE).  
© 2025 microarch.ru