# Календарь (Suyziss Calendar)

Современное Android-приложение «Календарь» в дизайне **Material You 3 (Material 3 Expressive)** на Jetpack Compose.

## Возможности

- 📅 Просмотр календаря по **месяцам**, **неделям** и **дням**
- ✏️ Добавление, редактирование и удаление событий
- 💾 Локальное сохранение через **Room**
- 🎨 **Dynamic Color** — тема автоматически подстраивается под обои устройства (Android 12+)
- ✨ Плавные анимации, скруглённые углы и компоненты Material You 3

## Требования

- Android Studio Ladybug | 2024.2.1 или новее
- JDK 17
- Android SDK 35
- minSdk 26 (Android 8.0)

## Сборка и запуск

1. Клонируйте репозиторий:
   ```bash
   git clone https://github.com/suyziss/suyziss-calendar.git
   cd suyziss-calendar
   ```

2. Откройте проект в Android Studio.

3. Дождитесь синхронизации Gradle.

4. Запустите на эмуляторе или устройстве (Run ▶).

### Сборка APK из командной строки

```bash
./gradlew assembleDebug
```

APK появится в:
`app/build/outputs/apk/debug/app-debug.apk`

## GitHub Actions

При пуше в ветку `main` (или вручную через workflow_dispatch) автоматически собирается debug APK и загружается как artifact.

Workflow: `.github/workflows/build.yml`

## Структура проекта

```
app/
├── src/main/java/com/suyziss/calendar/
│   ├── data/          # Room Entity, Dao, Database, Repository
│   ├── ui/
│   │   ├── components/ # EventCard, EventDialog
│   │   ├── screens/    # MonthView, WeekView, DayView, CalendarApp
│   │   └── theme/      # Material 3 Theme + Dynamic Color
│   ├── viewmodel/     # CalendarViewModel
│   └── MainActivity.kt
└── ...
```

## Технологии

- Kotlin 2.0
- Jetpack Compose + Material 3
- Room
- Navigation / ViewModel
- Dynamic Color (Material You)

## Лицензия

MIT
