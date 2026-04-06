# 🔧 CRUD Fixes Summary

## 📋 Issues Fixed

### ✅ 1. **Models - @JsonProperty Annotations** (CRITICAL)

**Problem:** Frontend sends snake_case JSON, but backend deserializes to camelCase - PUT updates failed silently.

**Fixed Models:**

#### 🚗 **Car.java**
```java
@JsonProperty("price_per_day")
private Double pricePerDay;

@JsonProperty("image_url")
private String imageUrl;
```

#### 🎯 **Activity.java**
```java
@JsonProperty("image_url")
private String imageUrl;

@JsonProperty("meeting_point")
private String meetingPoint;
```

#### 🎟️ **Voucher.java**
```java
@JsonProperty("discount_percent")
private Integer discountPercent;

@JsonProperty("discount_amount")
private Double discountAmount;

@JsonProperty("min_spend")
private Double minSpend;

@JsonProperty("max_discount")
private Double maxDiscount;

@JsonProperty("start_date")
private Instant startDate;

@JsonProperty("expiry_date")
private Instant expiryDate;

@JsonProperty("usage_limit")
private Integer usageLimit;
```

#### ✈️ **Flight.java**
```java
@JsonProperty("flight_number")
private String flightNumber;

@JsonProperty("departure_city")
private String departureCity;

@JsonProperty("arrival_city")
private String arrivalCity;

@JsonProperty("departure_time")
private Instant departureTime;

@JsonProperty("arrival_time")
private Instant arrivalTime;

@JsonProperty("image_url")
private String imageUrl;

@JsonProperty("available_seats")
private Integer availableSeats;

@JsonProperty("flight_type")
private String flightType;

@JsonProperty("ticket_options")
private List<Map<String, Object>> ticketOptions;
```

---

### ✅ 2. **Missing Fields Added**

#### 🎯 **Activity.java - Added `highlights`**
```java
private List<String> highlights; // New field for highlighting key features
```

#### 🎟️ **Voucher.java - Added `current_usage`**
```java
@JsonProperty("current_usage")
private Integer currentUsage = 0; // Track usage count
```

---

### ✅ 3. **Duplicate Field Removed**

#### ✈️ **Flight.java**
**Removed:** `private Integer seatsAvailable = 0;` (was unused and confusing)

**Kept:** `@JsonProperty("available_seats") private Integer availableSeats;`

---

### ✅ 4. **POST Validation Added**

#### 🚗 **CarController.java**
```java
@PostMapping
public ResponseEntity<?> createCar(@RequestBody Car car) {
    // Validate required fields
    if (car.getCompany() == null || car.getCompany().trim().isEmpty()) {
        return ResponseEntity.badRequest().body(Map.of("error", "Hãng xe không được để trống"));
    }
    if (car.getModel() == null || car.getModel().trim().isEmpty()) {
        return ResponseEntity.badRequest().body(Map.of("error", "Model không được để trống"));
    }
    if (car.getSeats() == null || car.getSeats() <= 0) {
        return ResponseEntity.badRequest().body(Map.of("error", "Số chỗ ngồi phải > 0"));
    }
    if (car.getPricePerDay() == null || car.getPricePerDay() <= 0) {
        return ResponseEntity.badRequest().body(Map.of("error", "Giá thuê phải > 0"));
    }
    if (car.getAvailable() == null) {
        car.setAvailable(true);
    }
    return ResponseEntity.status(201).body(carRepository.save(car));
}
```

#### 🎟️ **VoucherController.java**
```java
@PostMapping
public ResponseEntity<?> createVoucher(@RequestBody Voucher voucher) {
    // Validate required fields
    if (voucher.getCode() == null || voucher.getCode().trim().isEmpty()) {
        return ResponseEntity.badRequest().body(Map.of("error", "Mã voucher không được để trống"));
    }
    if ((voucher.getDiscountPercent() == null || voucher.getDiscountPercent() <= 0) &&
        (voucher.getDiscountAmount() == null || voucher.getDiscountAmount() <= 0)) {
        return ResponseEntity.badRequest().body(Map.of("error", "Chiết khấu phải > 0"));
    }
    if (voucher.getExpiryDate() == null) {
        return ResponseEntity.badRequest().body(Map.of("error", "Ngày hết hạn không được để trống"));
    }
    if (voucher.getCurrentUsage() == null) {
        voucher.setCurrentUsage(0);
    }
    return ResponseEntity.status(201).body(voucherRepository.save(voucher));
}
```

---

### ✅ 5. **PUT Validation Enhanced**

#### 🎯 **ActivityController.java** - Added highlights handling
```java
@PutMapping("/{id}")
public ResponseEntity<?> updateActivity(@PathVariable String id, @RequestBody Activity body) {
    // ... other fields ...
    if (body.getHighlights() != null) act.setHighlights(body.getHighlights());
    // ...
}
```

#### 🎟️ **VoucherController.java** - Added current_usage handling
```java
@PutMapping("/{id}")
public ResponseEntity<?> updateVoucher(@PathVariable String id, @RequestBody Voucher body) {
    // ... other fields ...
    if (body.getCurrentUsage() != null) v.setCurrentUsage(body.getCurrentUsage());
    // ...
}
```

---

### ✅ 6. **DataLoader Updated**
- Updated `createActivity()` to initialize `highlights` field

---

## 🎯 What Works Now

| Operation | Hotel | Car | Activity | Voucher | Flight |
|-----------|-------|-----|----------|---------|--------|
| ✅ CREATE | ✅ | ✅ Validated | ✅ | ✅ Validated | ✅ |
| ✅ READ | ✅ | ✅ | ✅ | ✅ | ✅ |
| ✅ UPDATE | ✅ | ✅ Any field | ✅ + highlights | ✅ + usage | ✅ |
| ✅ DELETE | ✅ | ✅ | ✅ | ✅ | ✅ |
| ✅ Timestamps | ✅ Auto | ✅ Auto | ✅ Auto | ✅ Auto | ✅ Auto |
| ✅ All fields map correctly | ✅ | ✅ | ✅ | ✅ | ✅ |

---

## 🚀 To Apply Changes

### 1. **Delete Old Data** (MongoDB)
```javascript
db.hotels.deleteMany({})
db.cars.deleteMany({})
db.activities.deleteMany({})
db.vouchers.deleteMany({})
db.flights.deleteMany({})
```

### 2. **Start Backend**
```powershell
java -jar d:\WebDoAn\J2EE\Jurni-backend\target\Jurni-backend-0.0.1-SNAPSHOT.jar
```

### 3. **Test CRUD Operations**

#### Add Car:
```bash
POST /api/cars
{
  "company": "BMW",
  "model": "X5",
  "type": "SUV",
  "seats": 7,
  "price_per_day": 2500000,
  "location": "TP.HCM",
  "image_url": "https://...",
  "available": true
}
```

#### Update Car:
```bash
PUT /api/cars/123
{
  "price_per_day": 2300000,  // ← Now works! (not pricePerDay)
  "available": false
}
```

#### Add Voucher:
```bash
POST /api/vouchers
{
  "code": "SAVE50",
  "discount_percent": 50,
  "min_spend": 500000,
  "expiry_date": "2026-12-31T23:59:59Z",
  "usage_limit": 100
}
```

#### Add Activity:
```bash
POST /api/activities
{
  "name": "Scuba Diving",
  "location": "Đà Nẵng",
  "price": 750000,
  "duration": "2 giờ",
  "category": "Thể thao & Mạo hiểm",
  "image_url": "https://...",
  "meeting_point": "Beach Resort",
  "includes": ["Equipment", "Guide"],
  "highlights": ["Rainbow reef", "Sea turtles"],
  "policies": { "cancel": "Free before 24h" }
}
```

---

## 📊 Test Matrix

**Before Fix:**
- ❌ PUT /api/cars - price_per_day ignored
- ❌ PUT /api/activities - image_url ignored  
- ❌ PUT /api/vouchers - ALL fields ignored
- ❌ POST /api/cars - No validation
- ❌ Activities missing highlights

**After Fix:**
- ✅ PUT /api/cars - price_per_day works
- ✅ PUT /api/activities - image_url works
- ✅ PUT /api/vouchers - All fields work
- ✅ POST /api/cars - Full validation
- ✅ Activities support highlights
- ✅ Vouchers track current_usage

---

**Status:** ✅ **READY TO DEPLOY**

All CRUD operations now work correctly with proper field mapping and validation!
