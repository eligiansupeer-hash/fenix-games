# Changelog

Todas las modificaciones notables de este proyecto se documentaran aqui.

El formato sigue Keep a Changelog y el proyecto usa commits convencionales.

## [Unreleased]

### Added

- Manual ejecutable inicial del proyecto.
- Gobernanza base del repositorio.
- Fase 0 Android nativo inicial: Gradle Wrapper, Kotlin DSL, Version Catalogs, Compose, Material 3, Navigation, Room/KSP, DataStore, kotlinx.serialization y pantalla Home.
- Fase 1 nucleo offline: modelos de dominio, Room para packs/cartas/usadas, JSON desde assets, pre-carga local, anti-repeticion y `SessionManager` MVI con StateFlow.
- Fase 2 Caja Negra: `BlackBoxLogger`, crash handler global, rotacion local, sanitizacion, pantalla diagnostica oculta por 7 taps y exportacion SAF.
- Fase 3 inicial: selector offline para "Yo Nunca", "Verdad" y "Reto" con cambio de modo MVI y mazos locales separados.

### Validated

- Fase 0: `.\gradlew.bat assembleDebug --no-daemon --stacktrace` finalizo con `BUILD SUCCESSFUL`.
- Fase 0: APK debug instalada y lanzada por ADB Wi-Fi en dispositivo `2409BRN2CY` (`lake_eea`).
- Fase 0: APK debug instalada y lanzada por ADB Wi-Fi en Samsung A10 `SM_A105M` (`a10ub`).
- Fase 1: `.\gradlew.bat assembleDebug --no-daemon --stacktrace` finalizo con `BUILD SUCCESSFUL`.
- Fase 1: APK debug instalada y lanzada por ADB Wi-Fi en Samsung A10 `SM_A105M` y Redmi 14C `2409BRN2CY`; `pidof com.fenixgames` confirmo proceso vivo en ambos.
- Fase 2: `.\gradlew.bat assembleDebug --no-daemon --stacktrace` finalizo con `BUILD SUCCESSFUL`.
- Fase 2: APK debug instalada y lanzada por ADB Wi-Fi en Samsung A10 `SM_A105M` y Redmi 14C `2409BRN2CY`; `pidof com.fenixgames` confirmo proceso vivo en ambos.
- Fase 2: `run-as com.fenixgames cat files/fenix-blackbox.log` confirmo escritura local del evento `Offline content prepared` en Samsung A10.
- Fase 3 inicial: `.\gradlew.bat assembleDebug --no-daemon --stacktrace` finalizo con `BUILD SUCCESSFUL`.
- Fase 3 inicial: APK debug instalada y lanzada por ADB Wi-Fi en Samsung A10 `SM_A105M` y Redmi 14C `2409BRN2CY`; `pidof com.fenixgames` confirmo proceso vivo en ambos.

### Changed

- Repositorio remoto definitivo actualizado a `eligiansupeer-hash/fenix-games`.
