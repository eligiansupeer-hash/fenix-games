# Changelog

Todas las modificaciones notables de este proyecto se documentaran aqui.

El formato sigue Keep a Changelog y el proyecto usa commits convencionales.

## [Unreleased]

### Added

- Manual ejecutable inicial del proyecto.
- Gobernanza base del repositorio.
- Fase 0 Android nativo inicial: Gradle Wrapper, Kotlin DSL, Version Catalogs, Compose, Material 3, Navigation, Room/KSP, DataStore, kotlinx.serialization y pantalla Home.

### Validated

- Fase 0: `.\gradlew.bat assembleDebug --no-daemon --stacktrace` finalizo con `BUILD SUCCESSFUL`.

### Blocked

- Creacion/push del repositorio remoto `eligiansupeer-hash/fenix-game`: la maquina no tiene `gh`, no hay `GH_TOKEN/GITHUB_TOKEN` local y la herramienta GitHub disponible devuelve 404 para el repo.
