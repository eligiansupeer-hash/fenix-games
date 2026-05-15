# Manual Ejecutable Fenix Games

Este documento es la guia operativa del proyecto `fenix-game`. Fue derivado del PDF `Manual de Construccion Fenix Games.pdf` y convierte sus fases en una secuencia ejecutable, verificable y apta para continuar por microfases.

## Reglas Globales

- Proyecto Android nativo en Kotlin, Jetpack Compose y Clean Architecture con MVI.
- Costo operativo mensual igual a cero: sin Firebase, Supabase, Vercel, Render, Crashlytics, Sentry, autenticacion remota, telemetria cloud ni APIs externas.
- Persistencia local con Room sobre SQLite y DataStore para preferencias.
- Contenido base e importable mediante JSON local y `kotlinx.serialization`.
- Importacion/exportacion mediante Storage Access Framework Android, sin permisos obsoletos de almacenamiento.
- Red local avanzada solo en fases finales mediante Ktor embebido, WebSocket, QR y Foreground Service.
- Git con rama `main`, commits convencionales y repositorio remoto publico `eligiansupeer-hash/fenix-games`.

## Politica de Terminal y Validacion

- Nunca ejecutar dos comandos pesados en paralelo.
- Se consideran pesados: `assembleDebug`, `assembleRelease`, tests Gradle, installs ADB, validaciones ADB, builds CI, descargas grandes y cualquier tarea Gradle de compilacion.
- Los comandos pesados se ejecutan siempre de a uno y se espera su resultado antes del siguiente.
- Comandos livianos de inspeccion pueden ejecutarse en paralelo solo si no invocan Gradle, ADB ni builds.
- Cada fase termina con: build local, prueba funcional minima, actualizacion de `CHANGELOG.md`, commit convencional y push si el remoto esta disponible.

## Dispositivos de QA Fisico

- Xiaomi Redmi 14C por ADB Wi-Fi.
- Samsung A10 por ADB Wi-Fi.
- Las pruebas de red local de fases 8 y 9 deben ejecutarse sin dependencia de internet comercial; se usara Wi-Fi local o hotspot.

## Fase 0 - Fundacion Android

Objetivo: convertir la carpeta actual en un proyecto Android nativo compilable.

Microfases:

1. Inicializar Git local en `main`.
2. Crear gobernanza: `.gitignore`, `README.md`, `CHANGELOG.md`, `LICENSE`.
3. Crear Gradle Wrapper porque la maquina no tiene Gradle global.
4. Configurar Kotlin DSL, `settings.gradle.kts`, build raiz, modulo `app` y `gradle/libs.versions.toml`.
5. Configurar Android SDK local con `compileSdk` y `targetSdk` 35, Java 17 y Kotlin.
6. Activar Jetpack Compose, Material 3 y Navigation Compose.
7. Integrar Room con KSP, DataStore y `kotlinx.serialization`.
8. Crear estructura base: `core`, `domain`, `data`, `ui`, `games`, `diagnostics`.
9. Crear `MainActivity`, tema Material 3, `AppNavigation` y `HomeScreen`.
10. Validar con un unico comando pesado: `.\gradlew.bat assembleDebug`.

Criterio de aceptacion:

- `assembleDebug` termina correctamente.
- La app contiene pantalla inicial "Fenix Games".
- No hay dependencias de nube ni llamadas remotas.

Estado actual: completada localmente. Validacion ejecutada con `.\gradlew.bat assembleDebug --no-daemon --stacktrace` y resultado `BUILD SUCCESSFUL`.

Validacion fisica inicial: APK debug instalada y lanzada por ADB Wi-Fi en dispositivo `2409BRN2CY` (`lake_eea`) y Samsung A10 `SM_A105M` (`a10ub`).

## Fase 1 - Nucleo Offline

Objetivo: crear el motor local minimo para sesiones y contenido.

Microfases:

1. Modelar `GameMode`, `Player`, `Score`, `Round` y `GameSession`.
2. Crear entidades Room para cartas, packs y cartas usadas.
3. Crear DAOs con consultas parametrizadas y filtro anti-repeticion `NOT IN`.
4. Crear `ContentPackManager` para leer JSON desde `assets`.
5. Pre-poblar Room en IO sin bloquear Main Thread.
6. Crear `Randomizer` y `TimerEngine`.
7. Crear `SessionManager` con StateFlow MVI.
8. Validar carga offline de contenido local.

Criterio de aceptacion:

- La app arranca en modo avion con datos locales.
- No se repiten cartas dentro de una sesion activa.
- Build de fase completo con un solo comando pesado.

Estado actual: completada localmente. Validacion ejecutada con `.\gradlew.bat assembleDebug --no-daemon --stacktrace` y resultado `BUILD SUCCESSFUL`. APK instalada y lanzada en Samsung A10 `SM_A105M` y Redmi 14C `2409BRN2CY`; ambos mantuvieron proceso `com.fenixgames` vivo.

## Fase 2 - Caja Negra Fenix

Objetivo: diagnostico offline sin servicios externos.

Microfases:

1. Crear `BlackBoxLogger`.
2. Modelar `DiagnosticEvent`.
3. Persistir logs en `filesDir`.
4. Rotar logs cuando superen 5 MB.
5. Capturar errores no fatales.
6. Instalar `Thread.setDefaultUncaughtExceptionHandler` en `Application`.
7. Registrar navegacion, eventos de juego, errores Room y errores JSON.
8. Crear `DiagnosticScreen`.
9. Exportar diagnostico por `Intent.ACTION_CREATE_DOCUMENT`.
10. Sanitizar datos sensibles antes de exportar.
11. Crear acceso oculto por 7 taps desde Home.

Criterio de aceptacion:

- Un crash artificial genera log local.
- El log puede exportarse por SAF.
- El reporte no contiene datos sensibles en claro.

Estado actual: implementada y validada parcialmente. `assembleDebug` finalizo con `BUILD SUCCESSFUL`; APK instalada y lanzada en Samsung A10 `SM_A105M` y Redmi 14C `2409BRN2CY`. Escritura local confirmada con `run-as com.fenixgames cat files/fenix-blackbox.log` en Samsung A10. Queda pendiente una prueba manual de exportacion SAF desde la pantalla oculta y una prueba de crash artificial supervisada.

## Fases 3 a 7 - Juegos Offline y Contenido Usuario

- Fase 3: "Yo Nunca", "Verdad", "Reto", "Verdad o Reto", "Mimica/Charadas", puntaje basico y `ScoreScreen`.
- Fase 4: modo +18 con `AgeGate`, `ConsentGate`, intensidad, consentimiento por jugador y filtro SQL absoluto.
- Fase 5: Impostor pass-and-play con roles secretos, Fisher-Yates, pantalla tap-to-view, discusion, votacion y resultado.
- Fase 6: Tabu, Ruleta y Trivia con temporizador robusto, Canvas Compose y puntaje por velocidad.
- Fase 7: editor de packs, CRUD local, export/import JSON y `.party`, rollback ante JSON corrupto y SAF.

Cada fase se valida de forma independiente antes de avanzar.

Estado Fase 3 inicial: implementado selector offline para "Yo Nunca", "Verdad" y "Reto" con cambio de modo MVI y mazos locales versionados. Validado con `assembleDebug` exitoso e instalacion/lanzamiento en Samsung A10 `SM_A105M` y Redmi 14C `2409BRN2CY`.

Replan de contenido presencial: la app elimina modo familiar/ninos y unifica Verdad/Reto dentro de `TRUTH_OR_DARE`; Preguntas de amigos/pareja pasan a `QUESTIONS`. Se agregan niveles `TEEN` y `ADULT_1` a `ADULT_6`, plantillas con `{actor}`, `{target}`, `{targetA}`, `{targetB}`, seleccion automatica de participantes, aviso legal antes de iniciar y penalizaciones de juego.

Estado expandido: todos los modos del catalogo estan creados (`Yo Nunca`, `Verdad o Reto`, `Preguntas`, `Ruleta`, `Previa`, `Argento`, `Mimica`, `Impostor`, `Tabu`, `Trivia`). El pack local contiene 1400 tarjetas: 20 por cada modo y cada nivel. El validador `tools/validate_cards.py` confirma conteos, ausencia de redes/material externo y placeholders correctos. Los modos competitivos soportan equipos y puntaje; los modos visuales tienen animacion simple de ruleta, dado o simbolo de juego. Build local generado correctamente; queda pendiente instalar en Samsung A10 y Redmi 14C cuando vuelvan a aparecer en ADB.

## Fases 8 y 9 - Red Local Cero Cloud

- Fase 8: sala local multicelular con permisos Android 14, Ktor embebido, Foreground Service, WebSocket, QR, CameraX/ML Kit, lobby, handshake, heartbeat y cierre ordenado.
- Fase 9: juegos distribuidos con host como verdad unica, payloads privados por WebSocket, sincronizacion MVI, reconexion y manejo de desconectados.

Criterio de aceptacion:

- Xiaomi Redmi 14C y Samsung A10 se conectan por ADB Wi-Fi.
- La sala funciona en Wi-Fi local y hotspot sin internet.
- Los roles secretos no se filtran a clientes no autorizados.

## Checklist por Fase

Antes de cerrar una fase:

- Ejecutar solo una validacion pesada a la vez.
- Revisar que no se agregaron dependencias cloud.
- Confirmar que el build relevante pasa.
- Probar el flujo funcional minimo.
- Actualizar `CHANGELOG.md`.
- Crear commit convencional.
- Push a `origin main` si el remoto esta disponible.
