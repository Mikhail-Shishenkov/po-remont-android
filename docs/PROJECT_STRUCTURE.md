# Структура проекта

Проект состоит из одного Android-модуля `app` и использует Gradle Kotlin DSL.

```text
po-remont-android/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── assets/reference/materials.json
│       └── java/com/example/poremont/
│           ├── data/
│           ├── navigation/
│           ├── ui/
│           ├── util/
│           ├── viewmodel/
│           └── MainActivity.kt
├── docs/
├── gradle/wrapper/
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

## Основные компоненты

### Точка входа и навигация

`MainActivity.kt` запускает Compose-интерфейс и корневой граф навигации. `navigation/AppNavHost.kt` связывает главный экран, выбор помещений и этапов, чек-листы, дефекты, сводную панель и разделы справочника.

Параметры помещения, этапа и пункта проверки передаются между экранами через маршруты Navigation Compose.

### Пользовательский интерфейс

Пакет `ui/screens` содержит экраны приложения:

- `MainScreen` — старт проекта;
- `RoomSelectionScreen` и `StageSelectionScreen` — настройка помещений и этапов;
- `DashboardScreen` и `RoomStagesScreen` — обзор прогресса;
- `ChecklistScreen` — прохождение проверок;
- `DefectCreateScreen`, `DefectEditScreen` и `DefectsListScreen` — работа с замечаниями и фотографиями;
- `ReferenceScreen` — каталог, поиск и просмотр справочных материалов.

Оформление Compose-компонентов находится в `ui/theme`.

### Данные

`data/PreferencesManager.kt` хранит конфигурацию проекта, результаты чек-листов и карточки дефектов в локальных настройках приложения. Фотографии с камеры сохраняются во внутреннем каталоге приложения, а изображения из галереи используются через сохранённые URI-разрешения.

`data/AppDatabase.kt`, `data/dao` и `data/entity` образуют слой Room. Модель проекта читается через `Flow`, а `MainViewModel` предоставляет активный проект интерфейсу как `StateFlow`.

### Справочник

`data/reference/ReferenceRepository.kt` загружает исходный каталог из `assets/reference/materials.json`, выполняет поиск и поддерживает локально обновлённую копию. Модели каталога, разделов, текстовых блоков и таблиц находятся в `ReferenceModels.kt`.

### PDF-отчёт

`util/PdfReportGenerator.kt` формирует многостраничный отчёт по дефектам. Генератор добавляет сведения о помещении и этапе, статус, описание и до двух фотографий для каждого замечания.

## Поток данных

```text
Compose screens
    ├── PreferencesManager ── проект, чек-листы, дефекты
    ├── Room database ─────── активный проект
    ├── ReferenceRepository ─ встроенный справочник
    └── PdfReportGenerator ── локальный PDF-отчёт
```

Приложение не требует серверной части: основной пользовательский сценарий и справочные данные доступны локально.

## Сборочная конфигурация

- namespace и application ID: `com.example.poremont`;
- минимальная версия Android: API 24;
- compile SDK и target SDK: API 34;
- Android Gradle Plugin: 8.2.2;
- Kotlin: 1.9.22;
- Gradle Wrapper: 8.2.

Локальные файлы среды и результаты сборки исключаются правилами корневого `.gitignore`.
