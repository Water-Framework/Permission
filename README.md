# Permission Module

## Module Goal
Il modulo Permission fornisce il sistema di gestione dei permessi e delle autorizzazioni all'interno del Water Framework. Permette di definire, assegnare e verificare permessi su entità, risorse e azioni, sia a livello di ruolo che di utente, garantendo un controllo granulare e centralizzato delle policy di sicurezza applicativa.

## Module Technical Characteristics

### Tecnologie principali
- **JPA (Jakarta Persistence API):** Gestione della persistenza delle entità Permission.
- **Water Core Modules:** Integrazione con i moduli core, security, validation, registry e service del framework.
- **Spring/OSGi Ready:** Componente utilizzabile sia in ambienti Spring che OSGi.
- **REST API:** Esposizione CRUD e servizi di calcolo permessi tramite JAX-RS.
- **Lombok:** Riduzione del boilerplate.
- **JUnit 5:** Test di integrazione e validazione delle policy.

### Componenti architetturali
- **API Layer (`Permission-api`)**
  - `PermissionApi`: API esterna per operazioni CRUD e calcolo delle mappe permessi.
  - `PermissionSystemApi`: API interna per operazioni privilegiate e bypass del sistema permessi.
  - `PermissionRepository`: Interfaccia repository per la persistenza e query avanzate.
  - `PermissionRestApi`: Interfaccia REST CRUD e servizi di calcolo permessi.
- **Model Layer (`Permission-model`)**
  - `WaterPermission`: Entità JPA che rappresenta un permesso, con supporto a ruoli, utenti, risorse e azioni.
- **Service Layer**
  - Implementazioni dei servizi e repository, con logica di validazione, controllo duplicati e gestione versioni.
- **Test Layer**
  - Test di integrazione su ruoli, utenti, permessi, validazione, duplicati, policy e mappa permessi.

### Caratteristiche chiave
- Gestione permessi a livello di ruolo e utente
- Supporto a permessi specifici per entità e azioni custom
- CRUD completo e API REST per permessi
- Calcolo mappa permessi per utente/loggato
- Validazione, controllo duplicati e gestione versioni
- Policy di accesso configurabili tramite annotazioni e ruoli

## Permission and Security
- **Ruoli e Policy:**
  - `permissionManager`: pieno controllo CRUD e gestione permessi
  - `permissionViewer`: accesso in sola lettura
  - `permissionEditor`: può creare/aggiornare ma non rimuovere
- **Annotazioni di sicurezza:**
  - `@AccessControl` e `@DefaultRoleAccess` per definire policy a livello di entità
  - `@LoggedIn` su tutte le API REST
- **Validazione:**
  - Validazione su campi obbligatori, formati, duplicati e codice malevolo
  - Gestione versioni ottimistiche per update concorrenti
- **Controllo automatico:**
  - Le operazioni CRUD e di calcolo permessi sono protette da controlli automatici tramite il permission system del framework

## How to Use It

### 1. Import del modulo
Aggiungi il modulo Permission e i suoi sottoprogetti al tuo progetto:

```gradle
implementation 'it.water.permission:Permission-api:${waterVersion}'
implementation 'it.water.permission:Permission-model:${waterVersion}'
implementation 'it.water.permission:Permission-service:${waterVersion}'
```

### 2. Esempio di utilizzo API
```java
@Inject
private PermissionApi permissionApi;

// Creazione permesso
WaterPermission perm = new WaterPermission("permName", 1L, "ResourceName", 123L, roleId, userId);
permissionApi.save(perm);

// Calcolo mappa permessi per utente loggato
Map<String, Map<String, Map<String, Boolean>>> map = permissionApi.entityPermissionMap(Map.of("ResourceName", List.of(123L, 456L)));

// CRUD e query
WaterPermission found = permissionApi.find(perm.getId());
permissionApi.update(found);
permissionApi.remove(found.getId());
```

### 3. Esempio REST
```http
POST /permissions
{
  "name": "permName",
  "actionIds": 1,
  "entityResourceName": "ResourceName",
  "resourceId": 123,
  "roleId": 1,
  "userId": 2
}

POST /permissions/map
{
  "ResourceName": [123, 456]
}
```

## Properties and Configurations

### Proprietà principali
- **Nessuna proprietà obbligatoria specifica**: il modulo si integra con le property core del framework e con la configurazione JPA standard.
- **Tabella JPA:** la tabella `WaterPermission` ha unique constraint su `(roleId, userId, entityResourceName, resourceId)`.
- **Azioni custom:** le azioni disponibili sono configurabili tramite il sistema di action del framework.

### Proprietà dai test
- I test utilizzano ruoli e utenti di default (`permissionManager`, `permissionViewer`, `permissionEditor`)
- Le azioni sono gestite tramite `ActionsManager` e `ActionFactory`
- Persistence unit di default: `water-default-persistence-unit`

## How to Customize Behaviours for This Module

### 1. Definire nuove azioni custom
Aggiungi nuove azioni tramite il sistema di action del framework:
```java
public class MyActions extends DefaultActionList {
    public static final ResourceAction<?> CUSTOM_ACTION = new ResourceAction<>("customAction");
}
```

### 2. Estendere la logica di validazione o repository
Estendi i service/repository per aggiungere logica custom:
```java
@FrameworkComponent
public class CustomPermissionService extends PermissionServiceImpl {
    @Override
    public WaterPermission save(WaterPermission entity) {
        // logica custom
        return super.save(entity);
    }
}
```

### 3. Personalizzare le policy di accesso
Utilizza le annotazioni `@AccessControl` e `@DefaultRoleAccess` sulle entità per definire policy custom.

### 4. Override delle API REST
Estendi o sostituisci i controller REST per aggiungere endpoint o logica custom.

### 5. Test personalizzati
Utilizza i test di esempio (`PermissionApiTest`) come base per testare policy, ruoli, azioni e permessi custom.

---

Il modulo Permission fornisce un sistema robusto, estendibile e centralizzato per la gestione dei permessi e delle policy di sicurezza nelle applicazioni Water Framework, con supporto completo a ruoli, utenti, azioni custom e REST API.

