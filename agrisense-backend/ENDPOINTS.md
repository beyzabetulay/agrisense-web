# Agrisense Backend API Endpoints

## Özet
Bu dokümantasyon, Agrisense Backend projesindeki tüm API endpoint'lerini içerir.

**Toplam Endpoint Sayısı: 13**

---

## 1. Alert Endpoints

**Base Path:** `/api/alerts`

### 1.1 Tüm Alert'leri Sorgula
- **HTTP Metod:** `GET`
- **Endpoint:** `/api/alerts`
- **Açıklama:** Tüm alert'leri sayfalanmış şekilde getirir
- **Query Parametreleri:**
  - `status` (opsiyonel): Alert durumuna göre filtrele
  - `page` (opsiyonel): Sayfa numarası
  - `size` (opsiyonel): Sayfa boyutu
- **Response:** 
  - Status: `200 OK`
  - Body: Sayfalanmış AlertResponse listesi
- **Kontroller Sınıfı:** `AlertController.java`

---

## 2. Alert Rule Endpoints

**Base Path:** `/sensors/{sensorId}/rules`

### 2.1 Alert Kuralı Oluştur
- **HTTP Metod:** `POST`
- **Endpoint:** `/sensors/{sensorId}/rules`
- **Açıklama:** Belirtilen sensör için yeni alert kuralı oluşturur
- **Path Parametreleri:**
  - `sensorId` (zorunlu): Sensör ID'si
- **Request Body:** `CreateAlertRuleRequest`
- **Response:** 
  - Status: `201 Created`
  - Body: Oluşturulan `AlertRuleResponse`
- **Kontroller Sınıfı:** `AlertRuleController.java`

### 2.2 Sensör için Aktif Alert Kurallarını Getir
- **HTTP Metod:** `GET`
- **Endpoint:** `/sensors/{sensorId}/rules`
- **Açıklama:** Belirtilen sensör için aktif alert kurallarını getirir
- **Path Parametreleri:**
  - `sensorId` (zorunlu): Sensör ID'si
- **Response:** 
  - Status: `200 OK`
  - Body: `AlertRuleResponse` listesi
- **Kontroller Sınıfı:** `AlertRuleController.java`

### 2.3 Alert Kuralını Güncelle
- **HTTP Metod:** `PUT`
- **Endpoint:** `/sensors/{sensorId}/rules/{ruleId}`
- **Açıklama:** Belirtilen alert kuralını günceller
- **Path Parametreleri:**
  - `sensorId` (zorunlu): Sensör ID'si
  - `ruleId` (zorunlu): Kuralın ID'si
- **Request Body:** `CreateAlertRuleRequest`
- **Response:** 
  - Status: `200 OK`
  - Body: Güncellenen `AlertRuleResponse`
- **Kontroller Sınıfı:** `AlertRuleController.java`

### 2.4 Alert Kuralını Sil
- **HTTP Metod:** `DELETE`
- **Endpoint:** `/sensors/{sensorId}/rules/{ruleId}`
- **Açıklama:** Belirtilen alert kuralını siler
- **Path Parametreleri:**
  - `sensorId` (zorunlu): Sensör ID'si
  - `ruleId` (zorunlu): Kuralın ID'si
- **Response:** 
  - Status: `204 No Content`
- **Kontroller Sınıfı:** `AlertRuleController.java`

---

## 3. Measurement Endpoints

**Base Path:** `/api/measurements`

### 3.1 Yeni Ölçüm Verisi Ekle
- **HTTP Metod:** `POST`
- **Endpoint:** `/api/measurements`
- **Açıklama:** Sensörden yeni ölçüm verisi alır ve işler
- **Request Body:** `CreateMeasurementRequest`
  - `sensorId`: Sensör ID'si
  - `value`: Ölçüm değeri
  - `unit`: Ölçü birimi (opsiyonel, varsayılan: "")
- **Response:** 
  - Status: `202 Accepted`
  - Body: `{"status": "Measurement processed successfully"}`
- **Kontroller Sınıfı:** `MeasurementController.java`

### 3.2 Ölçüm Verilerini Sorgula
- **HTTP Metod:** `GET`
- **Endpoint:** `/api/measurements`
- **Açıklama:** Ölçüm verilerini filtreleme ve sayfalama ile getirir
- **Query Parametreleri:**
  - `fieldId` (opsiyonel): Alan ID'si
  - `from` (opsiyonel): Başlangıç tarihi (ISO 8601 formatı)
  - `to` (opsiyonel): Bitiş tarihi (ISO 8601 formatı)
  - `page` (varsayılan: 1): Sayfa numarası
  - `size` (varsayılan: 50): Sayfa boyutu
- **Response:** 
  - Status: `200 OK`
  - Body: Sayfalanmış MeasurementResponse listesi
- **Kontroller Sınıfı:** `MeasurementController.java`

---

## 4. Sensor Endpoints

**Base Path:** `/api/sensors`

### 4.1 Yeni Sensör Oluştur
- **HTTP Metod:** `POST`
- **Endpoint:** `/api/sensors`
- **Açıklama:** Yeni bir sensör kaydını oluşturur
- **Request Body:** `CreateSensorRequest`
- **Response:** 
  - Status: `201 Created`
  - Body: Oluşturulan `SensorResponse` (HATEOAS linksleri ile)
- **Kontroller Sınıfı:** `SensorController.java`

### 4.2 Tüm Sensörleri Getir
- **HTTP Metod:** `GET`
- **Endpoint:** `/api/sensors`
- **Açıklama:** Tüm sensörleri listeler
- **Response:** 
  - Status: `200 OK`
  - Body: `SensorResponse` listesi (HATEOAS linksleri ile)
- **Kontroller Sınıfı:** `SensorController.java`

### 4.3 Sensörü ID'ye Göre Getir
- **HTTP Metod:** `GET`
- **Endpoint:** `/api/sensors/{id}`
- **Açıklama:** Belirtilen ID'ye sahip sensörü getirir
- **Path Parametreleri:**
  - `id` (zorunlu): Sensör ID'si
- **Response:** 
  - Status: `200 OK`
  - Body: `SensorResponse`
- **Kontroller Sınıfı:** `SensorController.java`

### 4.4 Sensörü Güncelle
- **HTTP Metod:** `PUT`
- **Endpoint:** `/api/sensors/{id}`
- **Açıklama:** Belirtilen sensörü günceller
- **Path Parametreleri:**
  - `id` (zorunlu): Sensör ID'si
- **Request Body:** `CreateSensorRequest`
- **Response:** 
  - Status: `200 OK`
  - Body: Güncellenen `SensorResponse` (HATEOAS linksleri ile)
- **Kontroller Sınıfı:** `SensorController.java`

### 4.5 Sensörü Sil
- **HTTP Metod:** `DELETE`
- **Endpoint:** `/api/sensors/{id}`
- **Açıklama:** Belirtilen sensörü siler
- **Path Parametreleri:**
  - `id` (zorunlu): Sensör ID'si
- **Response:** 
  - Status: `204 No Content`
- **Kontroller Sınıfı:** `SensorController.java`

---

## Endpoint Özet Tablosu

| # | HTTP | Endpoint | Açıklama |
|---|------|----------|----------|
| 1 | GET | `/api/alerts` | Tüm alert'leri sorgula |
| 2 | POST | `/sensors/{sensorId}/rules` | Alert kuralı oluştur |
| 3 | GET | `/sensors/{sensorId}/rules` | Aktif alert kurallarını getir |
| 4 | PUT | `/sensors/{sensorId}/rules/{ruleId}` | Alert kuralını güncelle |
| 5 | DELETE | `/sensors/{sensorId}/rules/{ruleId}` | Alert kuralını sil |
| 6 | POST | `/api/measurements` | Yeni ölçüm verisi ekle |
| 7 | GET | `/api/measurements` | Ölçüm verilerini sorgula |
| 8 | POST | `/api/sensors` | Yeni sensör oluştur |
| 9 | GET | `/api/sensors` | Tüm sensörleri getir |
| 10 | GET | `/api/sensors/{id}` | Sensörü ID'ye göre getir |
| 11 | PUT | `/api/sensors/{id}` | Sensörü güncelle |
| 12 | DELETE | `/api/sensors/{id}` | Sensörü sil |

---

## Notlar

- Tüm endpoint'ler `application/json` content-type kullanırlar
- HATEOAS (Hypermedia As The Engine Of Application State) uygulaması yapılmıştır
- Sayfalanmış response'lar `PagedResponse` wrapper'ı ile döner
- Alert controller'ında status filtrelemesi `EAlertStatus` enum'u üzerinden yapılır
- Measurement opsiyonel zaman aralığı filtrelemesi destekler

---

**Kontroller Sınıfları Konumu:**
`src/main/java/io/agrisense/adapter/in/web/controller/`
- `AlertController.java`
- `AlertRuleController.java`
- `MeasurementController.java`
- `SensorController.java`
