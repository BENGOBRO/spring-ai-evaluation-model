# Custom Agent Evaluator — многокритериальная оценка качества ответов AI-агента

Стенд для **количественной оценки качества ответов AI-агента**, который вызывает инструменты
доменных сервисов интернет-магазина электроники. Качество ответа измеряется **взвешенной
многокритериальной моделью**, дающей **непрерывный балл** — вещественное число в диапазоне
`[0;1]` (а не бинарный pass/fail). Непрерывность важна: дробный балл позволяет ранжировать
ответы, задавать порог приёмки и строить взвешенные модели, тогда как бинарная оценка различает
лишь «прошло / не прошло». Балл сравнивается в двух конфигурациях: с компонентом-Advisor (повтор
при низком балле) и без него.

## Цель

Формализовать понятие «хороший ответ» агента, вызывающего инструменты, и измерить долю корректных
ответов. Ответ состоит из двух частей — что агент *сделал* (вызов инструмента с параметрами) и что
*сказал* (финальный текст), — отсюда три независимых критерия.

## Модель оценки

| Критерий | Что измеряет | Тип |
|---|---|---|
| **C₁ — Tool accuracy** | выбран ли правильный инструмент | 0/1 |
| **C₂ — Parameter accuracy** | доля верно извлечённых параметров | [0;1] |
| **C₃ — Response relevance** | отвечает ли текст на вопрос (LLM-as-a-judge) | [0;1] |

Итоговый балл: `Score = 0.40·C₁ + 0.35·C₂ + 0.25·C₃`. Ответ корректен при `Score ≥ 0.8`.
Доля корректных: `Accuracy = N_correct / N`. Поскольку C₂ и C₃ дробные, итоговый `Score` —
непрерывная величина, а не набор из двух значений.

## Технологии

- **Java 21**, **Spring Boot 3.5.15**
- **Spring Web** — REST API
- **Spring Data JDBC** + **PostgreSQL**, миграции **Flyway**
- **GigaChat** через `chat.giga:spring-ai-starter-model-gigachat` (Spring AI)
- **Lombok**, **Actuator**, **springdoc OpenAPI / Swagger UI**
- Сборка — **Maven** (wrapper), тесты — **JUnit 5**, **Mockito**, **AssertJ**

## Архитектура

Модульный монолит. Каждый домен — отдельный пакет с сущностью, репозиторием, сервисом
(бизнес-логика), REST-контроллером и tool-адаптером для агента:

```
ru.bengobro.electronic_shop
├── product / order / user / cart / payment   доменные модули
├── agent      ChatClient агента, @Tool-адаптеры, захват вызовов инструментов
├── eval       критерии C1/C2/C3, Score, Advisor с retry
└── web        обработка ошибок, OpenAPI
```

## Предметная область

Модель оценки тестируется на домене **интернет-магазина электроники**. Выбор не случаен:

- **Богатые параметры товаров** (цена, бренд, объём памяти, диагональ экрана, цвет) хорошо
  нагружают критерий C₂ — есть что извлекать и где ошибаться при разборе запроса.
- **Естественная декомпозиция на сервисы** (товары, заказы, пользователи, корзина, оплаты),
  каждый из которых даёт агенту набор инструментов.
- **Понятная номенклатура** — для большинства запросов легко прописать однозначный эталон.

Доменные сущности: `Product`, `User`, `Order`/`OrderItem`, `Cart`/`CartItem`, `Payment`.
Агенту доступны 11 инструментов: `getProducts`, `getProductById`, `getProductCount`,
`createOrder`, `getOrderStatus`, `cancelOrder`, `getUserOrders`, `addToCart`, `getCart`,
`createPayment`, `getUserById`.

### Тестовые данные

Проект поставляется с готовыми данными — поднимать и наполнять руками ничего не нужно:

- **Наполнение БД** — миграция Flyway `V2__seed_data.sql` при старте создаёт 16 товаров
  (разные категории, бренды и параметры), 12 пользователей, 3 заказа и оплату. Идентификаторы
  фиксированы, поэтому на них можно ссылаться в запросах.
- **Золотой датасет** — `src/test/resources/golden-dataset.json`: 60 размеченных запросов с
  эталонными инструментом и параметрами, по категориям (read, write, aggregation,
  complex_filter, ambiguous). Это главный артефакт для оценки. Рядом лежит урезанный
  `golden-dataset-smoke.json` (6 запросов) для быстрой проверки.

## Требования

- JDK 21
- Запущенный PostgreSQL
- Действующий ключ GigaChat

## Конфигурация (переменные окружения)

| Переменная | Назначение |
|---|---|
| `DB_URL` | JDBC-URL, напр. `jdbc:postgresql://localhost:5432/electronic_shop` |
| `DB_USER`, `DB_PASSWORD` | учётные данные БД |
| `GIGACHAT_API_KEY` | ключ авторизации GigaChat |
| `GIGACHAT_CA_BUNDLE_PATH` | путь к CA-сертификатам (для TLS GigaChat) |
| `GIGACHAT_SCOPE` | необязательно, по умолчанию `GIGACHAT_API_PERS` |

Удобно держать их в локальном `*.env` (он в `.gitignore`) и подгружать:

```bash
set -a; source local.env; set +a
```

## Запуск

```bash
# 1. PostgreSQL
docker run --name es-postgres -e POSTGRES_DB=electronic_shop \
  -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 -d postgres:16

# 2. переменные окружения
set -a; source local.env; set +a

# 3. приложение (Flyway применит схему и наполнит данными)
./mvnw spring-boot:run
```

После старта:
- **Swagger UI** — http://localhost:8080/swagger-ui.html
- **OpenAPI** — http://localhost:8080/v3/api-docs
- **Health** — http://localhost:8080/actuator/health

### Примеры запросов

```bash
# REST напрямую
curl "localhost:8080/api/products?maxPrice=80000&category=electronics"

# через агента (выбор инструмента + извлечение параметров)
curl -X POST localhost:8080/api/agent/chat -H 'Content-Type: application/json' \
  -d '{"query":"Покажи товары до 2000 рублей в категории электроника"}'
```

## Тестирование

```bash
# все тесты
./mvnw test

# отдельный класс
./mvnw test -Dtest=ParameterAccuracyEvaluatorTest
```

Юнит- и slice-тесты (`@WebMvcTest`, оценщики) изолированы. Тесты репозиториев (`@DataJdbcTest`)
и полный `@SpringBootTest` обращаются к PostgreSQL, поэтому требуют поднятой БД и переменных
окружения GigaChat.

## Эксперимент

Прогон золотого датасета (60 размеченных запросов) в двух конфигурациях с записью отчётов
в `docs/results/`. Дорогой live-прогон по GigaChat и сброс БД, поэтому включается флагом:

```bash
set -a; source local.env; set +a

# полный прогон (60 запросов × baseline и with-advisor)
./mvnw test -Dtest=ExperimentRunner -Dexperiment=true

# быстрый смоук на 6 запросах
./mvnw test -Dtest=ExperimentRunner -Dexperiment=true -Dexperiment.dataset=/golden-dataset-smoke.json
```

Результаты: `docs/results/summary.md`, `baseline.json`, `with-advisor.json`.
