# Guía de Pruebas - Wheel Vault

Esta guía describe cómo ejecutar las pruebas unitarias y de instrumentación en el proyecto Wheel Vault.

## Dependencias de Testing

El proyecto utiliza las siguientes librerías de testing:

### Pruebas Unitarias
- **JUnit 4** - Framework de testing estándar para Java/Kotlin
- **Kotlin Test** - Extensiones de testing para Kotlin
- **Kotlinx Coroutines Test** - Testing de código asíncrono con coroutines
- **Kotest Assertions Core** - Assertions expresivas y legibles
- **MockK** - Framework de mocking para Kotlin

### Pruebas Instrumentadas (Android)
- **AndroidX Test JUnit** - JUnit para Android
- **AndroidX Test Runner** - Runner de tests para Android
- **Espresso Core** - Framework de UI testing
- **Compose UI Test JUnit4** - Testing de Compose UI
- **Kotlinx Coroutines Test** - Testing de coroutines en Android
- **MockK Android** - MockK para Android

## Estructura de Pruebas

```
app/src/
├── test/                          # Pruebas unitarias (JVM)
│   └── kotlin/
│       └── io/github/patrickvillarroel/wheel/vault/
│           ├── data/              # Tests de repositorios y use cases
│           │   ├── BrandRepositoryImplTest.kt
│           │   └── UpdateOnboardingStateUseCaseImplTest.kt
│           └── ui/screen/         # Tests de ViewModels
│               ├── BrandViewModelTest.kt
│               └── CarViewModelTest.kt
│
└── androidTest/                   # Pruebas instrumentadas (Android)
    └── kotlin/
        └── io/github/patrickvillarroel/wheel/vault/
            ├── ExampleInstrumentedTest.kt
            └── ui/screen/
                ├── component/     # Tests de componentes UI
                │   ├── CarCardTest.kt
                │   └── FavoriteIconTest.kt
                └── login/
                    └── LoginContentTest.kt
```

## Ejecutar Pruebas

### Pruebas Unitarias

Las pruebas unitarias se ejecutan en la JVM local y son rápidas. No requieren un dispositivo o emulador Android.

#### Desde Android Studio:
1. Haz clic derecho en `app/src/test`
2. Selecciona "Run 'All Tests'"

#### Desde la línea de comandos:
```bash
# Ejecutar todas las pruebas unitarias
./gradlew test

# Ejecutar pruebas de un módulo específico
./gradlew :app:test

# Ejecutar con reporte de cobertura
./gradlew test jacocoTestReport
```

### Pruebas Instrumentadas

Las pruebas instrumentadas requieren un dispositivo físico o emulador Android en ejecución.

#### Desde Android Studio:
1. Conecta un dispositivo o inicia un emulador
2. Haz clic derecho en `app/src/androidTest`
3. Selecciona "Run 'All Tests'"

#### Desde la línea de comandos:
```bash
# Ejecutar todas las pruebas instrumentadas
./gradlew connectedAndroidTest

# Ejecutar en un dispositivo específico
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=io.github.patrickvillarroel.wheel.vault.ExampleInstrumentedTest
```

## Tipos de Pruebas Implementadas

### 1. Pruebas de ViewModels
- **Archivos**: `CarViewModelTest.kt`, `BrandViewModelTest.kt`
- **Cubren**:
  - Estados de UI (Loading, Success, Error, NotFound)
  - Flujos de datos reactivos con StateFlow
  - Operaciones asíncronas con coroutines
  - Manejo de errores

**Ejemplo**:
```kotlin
@Test
fun `findById should update state to Success when car is found`() = runTest {
    // Given
    val carId = Uuid.random()
    val expectedCar = CarItem(...)
    coEvery { carsRepository.fetch(carId) } returns expectedCar

    // When
    viewModel.findById(carId)
    advanceUntilIdle()

    // Then
    val finalState = viewModel.carDetailState.first()
    finalState.shouldBeInstanceOf<CarViewModel.CarDetailUiState.Success>()
}
```

### 2. Pruebas de Use Cases
- **Archivos**: `UpdateOnboardingStateUseCaseImplTest.kt`
- **Cubren**:
  - Lógica de negocio
  - Interacción con DataStore
  - Operaciones asíncronas

**Ejemplo**:
```kotlin
@Test
fun `updateOnboardingState should save true when called with true`() = runTest {
    // When
    useCase.updateOnboardingState(true)

    // Then
    coVerify { dataStore.edit(any()) }
}
```

### 3. Pruebas de Repositories
- **Archivos**: `BrandRepositoryImplTest.kt`
- **Cubren**:
  - Sincronización entre fuentes de datos locales y remotas
  - Caché de datos
  - Manejo de errores en llamadas de red

**Ejemplo**:
```kotlin
@Test
fun `fetch should return brand from room when found locally`() = runTest {
    // Given
    val brandId = Uuid.random()
    val expectedBrand = Brand(...)
    coEvery { roomDataSource.fetch(brandId, false) } returns expectedBrand

    // When
    val result = repository.fetch(brandId, forceRefresh = false)

    // Then
    result shouldBe expectedBrand
}
```

### 4. Pruebas de Componentes UI (Compose)
- **Archivos**: `CarCardTest.kt`, `FavoriteIconTest.kt`
- **Cubren**:
  - Renderizado de componentes
  - Interacciones del usuario (clicks, gestos)
  - Estados visuales y animaciones

**Ejemplo**:
```kotlin
@Test
fun favoriteIcon_togglesStateOnClick() {
    // Given
    var currentState = false

    // When
    composeTestRule.setContent {
        FavoriteIcon(
            isFavorite = false,
            onFavoriteToggle = { newState -> currentState = newState }
        )
    }

    // Then
    composeTestRule.onNodeWithContentDescription("Favorites").performClick()
    assertTrue(currentState)
}
```

### 5. Pruebas de Pantallas Completas
- **Archivos**: `LoginContentTest.kt`
- **Cubren**:
  - Flujos completos de UI
  - Navegación
  - Integración de múltiples componentes

**Ejemplo**:
```kotlin
@Test
fun loginContent_registerButtonClickTriggersCallback() {
    // Given
    var registerClicked = false

    // When
    composeTestRule.setContent {
        LoginContent(
            onRegisterClick = { registerClicked = true },
            ...
        )
    }

    // Then
    composeTestRule.onNodeWithText("Register").performClick()
    assertTrue(registerClicked)
}
```

## Mejores Prácticas

### 1. Nomenclatura de Tests
Usa el formato: `función_condición_resultadoEsperado`
```kotlin
@Test
fun `findById should update state to Error when repository throws exception`()
```

### 2. Patrón Given-When-Then
Estructura tus tests claramente:
```kotlin
@Test
fun myTest() = runTest {
    // Given - Configuración inicial
    val input = createTestInput()

    // When - Acción a probar
    val result = performAction(input)

    // Then - Verificaciones
    result shouldBe expected
}
```

### 3. Usa Kotest Assertions
Son más legibles que las assertions tradicionales:
```kotlin
// ✅ Kotest - Legible
result shouldBe expected
list shouldHaveSize 3

// ❌ JUnit - Menos legible
assertEquals(expected, result)
assertEquals(3, list.size)
```

### 4. Testing de Coroutines
Usa `runTest` y controla el tiempo con `advanceUntilIdle()`:
```kotlin
@Test
fun myCoroutineTest() = runTest {
    viewModel.fetchData()
    advanceUntilIdle() // Avanza el tiempo virtual hasta que todas las coroutines terminen

    val state = viewModel.state.first()
    state.shouldBeInstanceOf<Success>()
}
```

### 5. Mocking con MockK
```kotlin
// Mock relajado - devuelve valores por defecto
val mock = mockk<Repository>(relaxed = true)

// Stub de comportamiento
coEvery { mock.fetch(any()) } returns expectedValue

// Verificación de llamadas
coVerify(exactly = 1) { mock.fetch(any()) }
```

## Configuración del Test Dispatcher

Para tests de ViewModels que usan coroutines, configura el dispatcher de test:

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class MyViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
```

## Troubleshooting

### Error: "Method ... not mocked"
**Solución**: Usa `mockk(relaxed = true)` o define el comportamiento con `every` o `coEvery`.

### Tests de UI fallan en CI/CD
**Solución**: Asegúrate de que el emulador esté configurado correctamente y usa `idlingResources` para operaciones asíncronas.

### Tests de coroutines no terminan
**Solución**: Usa `runTest` en lugar de `runBlocking` y llama a `advanceUntilIdle()`.

### Problemas con DataStore en tests
**Solución**: Usa un DataStore en memoria o mockea el DataStore completamente.

## Recursos Adicionales

- [Testing en Android - Documentación oficial](https://developer.android.com/training/testing)
- [Kotest Documentation](https://kotest.io/)
- [MockK Documentation](https://mockk.io/)
- [Compose Testing Cheatsheet](https://developer.android.com/jetpack/compose/testing-cheatsheet)
- [Coroutines Testing Guide](https://kotlinlang.org/docs/coroutines-testing.html)

## Ejecutar Tests Específicos

```bash
# Ejecutar un test específico
./gradlew test --tests "*.CarViewModelTest"

# Ejecutar tests de un paquete
./gradlew test --tests "io.github.patrickvillarroel.wheel.vault.ui.*"

# Ejecutar con logs detallados
./gradlew test --info

# Generar reporte HTML
./gradlew test
# Ver: app/build/reports/tests/test/index.html
```

## Cobertura de Código

Para generar reportes de cobertura (requiere configuración adicional de JaCoCo):

```bash
./gradlew test jacocoTestReport
# Ver: app/build/reports/jacoco/test/html/index.html
```
