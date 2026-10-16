# 03 — Mantenimiento: CRUD local con Room

> Objetivo: cumplir el CRUD local del proyecto Saba usando Room/SQLite.
> Este módulo debe funcionar aunque no exista conexión a internet.

## 0. Arquitectura

```text
MaintenanceScreen
       ↓ eventos
MaintenanceViewModel
       ↓
MaintenanceRepository
       ↓
MaintenanceDao
       ↓
Room Database
       ↓
SQLite del dispositivo
```

Room **no reemplaza** la API de incidencias. Son dos requisitos distintos.

---

## Paso 1 — Dependencias Room

Agrega versiones compatibles con tu proyecto actual.

Ejemplo de catálogo:

```toml
[versions]
room = "2.8.3"
ksp = "2.3.4"

[libraries]
room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }

[plugins]
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
```

> Las versiones deben alinearse con Kotlin/AGP del proyecto. Si Gradle avisa incompatibilidad, resuelve la matriz de versiones antes de copiar números de otra guía.

En el módulo app:

```kotlin
plugins {
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
}
```

Si usas Version Catalog, agrega también el alias `room-compiler`.

---

## Paso 2 — Entity mínima

`data/local/entity/MaintenanceEntity.kt`:

```kotlin
package com.example.appsaba.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "maintenance")
data class MaintenanceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val parkingName: String,
    val serviceName: String,
    val description: String,
    val scheduledDate: String,
    val status: String,
    val notes: String = ""
)
```

Modelo intencionalmente pequeño para el MVP.

No metas 25 campos si la evaluación solo necesita demostrar CRUD local correctamente.

---

## Paso 3 — DAO

`data/local/dao/MaintenanceDao.kt`:

```kotlin
package com.example.appsaba.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.appsaba.data.local.entity.MaintenanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MaintenanceDao {

    @Query("SELECT * FROM maintenance ORDER BY id DESC")
    fun getAll(): Flow<List<MaintenanceEntity>>

    @Query("SELECT * FROM maintenance WHERE id = :id")
    suspend fun getById(id: Long): MaintenanceEntity?

    @Insert
    suspend fun insert(item: MaintenanceEntity): Long

    @Update
    suspend fun update(item: MaintenanceEntity)

    @Delete
    suspend fun delete(item: MaintenanceEntity)
}
```

Esto ya contiene las cuatro operaciones CRUD:

```text
Create -> @Insert
Read   -> @Query
Update -> @Update
Delete -> @Delete
```

---

## Paso 4 — Database

`data/local/SabaDatabase.kt`:

```kotlin
package com.example.appsaba.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.appsaba.data.local.dao.MaintenanceDao
import com.example.appsaba.data.local.entity.MaintenanceEntity

@Database(
    entities = [MaintenanceEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SabaDatabase : RoomDatabase() {
    abstract fun maintenanceDao(): MaintenanceDao
}
```

---

## Paso 5 — Crear una sola instancia

Para un MVP pequeño puedes crear un proveedor simple:

```kotlin
object DatabaseProvider {
    @Volatile
    private var instance: SabaDatabase? = null

    fun getDatabase(context: Context): SabaDatabase {
        return instance ?: synchronized(this) {
            Room.databaseBuilder(
                context.applicationContext,
                SabaDatabase::class.java,
                "saba.db"
            ).build().also { instance = it }
        }
    }
}
```

La base no debe recrearse cada vez que un Composable recompone.

---

## Paso 6 — Repository

`data/repository/MaintenanceRepository.kt`:

```kotlin
class MaintenanceRepository(
    private val dao: MaintenanceDao
) {
    val items = dao.getAll()

    suspend fun create(item: MaintenanceEntity) = dao.insert(item)
    suspend fun update(item: MaintenanceEntity) = dao.update(item)
    suspend fun delete(item: MaintenanceEntity) = dao.delete(item)
}
```

El ViewModel no necesita saber SQL.

---

## Paso 7 — ViewModel

El ViewModel expone la lista y ejecuta escritura dentro de `viewModelScope`.

Conceptualmente:

```kotlin
class MaintenanceViewModel(
    private val repository: MaintenanceRepository
) : ViewModel() {

    val maintenanceItems = repository.items

    fun create(item: MaintenanceEntity) {
        viewModelScope.launch {
            repository.create(item)
        }
    }

    fun update(item: MaintenanceEntity) {
        viewModelScope.launch {
            repository.update(item)
        }
    }

    fun delete(item: MaintenanceEntity) {
        viewModelScope.launch {
            repository.delete(item)
        }
    }
}
```

---

## Paso 8 — UI mínima para demostrar CRUD

Pantallas posibles:

```text
MaintenanceScreen
  ├── lista de tareas
  ├── botón "Nueva"
  ├── editar elemento
  └── eliminar elemento
```

Para la evaluación importa más que se pueda demostrar:

```text
crear → cerrar/reabrir app → sigue guardado
editar → cambio persiste
eliminar → desaparece de Room
```

que tener una interfaz gigantesca.

---

## Prueba obligatoria

1. Crear mantenimiento ficticio para `Barreras de entrada`.
2. Confirmar que aparece.
3. Cerrar y abrir la app.
4. Confirmar que sigue ahí.
5. Editar la descripción/estado.
6. Reiniciar otra vez.
7. Confirmar persistencia.
8. Eliminar.
9. Confirmar que ya no existe.

Si los datos desaparecen al cerrar la app, no estás demostrando persistencia local real.

---

## Checklist 03

- [ ] Existe `@Entity`.
- [ ] Existe `@Dao`.
- [ ] Existe `@Database`.
- [ ] Create funciona.
- [ ] Read funciona.
- [ ] Update funciona.
- [ ] Delete funciona.
- [ ] Los datos sobreviven a reiniciar la app.
- [ ] Room se usa para mantenimiento, no para guardar contraseñas.
- [ ] El Composable no llama al DAO directamente.
