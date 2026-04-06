# 🌱 Seed Mock Data - Hướng Dẫn

## ✅ Đã Chuẩn Bị
- **DataLoader.java**: Tự động seed dữ liệu khách sạn, xe thuê, voucher khi app khởi động
- Được tạo tại: `Jurni-backend/src/main/java/com/nhom3/Jurni_backend/config/DataLoader.java`

## 📊 Dữ Liệu Được Seed

### � Hoạt Động (6 tour)
1. **Tour Tham Quan Phố Cổ Hà Nội** - 350,000 VND/người - 4 giờ - Văn hóa & Lịch sử
2. **Tham Quan Vịnh Hạ Long** - 1,200,000 VND/người - 1 ngày - Thiên nhiên & Du lịch  
3. **Công Viên Nước Vinpearl** - 650,000 VND/người - Cả ngày - Giải trí & Vui chơi
4. **Show Diễn Nhạc Nước** - 200,000 VND/người - 1 giờ - Giải trí & Vui chơi (Đà Nẵng)
5. **Tour Tham Quan Chùa Một Cột** - 250,000 VND/người - 2 giờ - Văn hóa & Lịch sử (Hà Nội)
6. **Lặn Biển Ngắm San Hô** - 850,000 VND/người - Nửa ngày - Thể thao & Mạo hiểm (Phú Quốc)

**Mỗi hoạt động có:**
- Mô tả chi tiết
- Giá cụ thể
- Thời gian diễn ra
- Danh sách những gì được bao gồm
- Điểm tập trung
- Chính sách hủy, đổi, thời tiết, trẻ em

### �🏨 Khách Sạn (9 cơ sở)
1. Khách Sạn Grand Saigon - TP.HCM - 2,500,000 VND/đêm (5 sao)
2. Resort Đà Lạt Premium - Đà Lạt - 1,800,000 VND/đêm (4.5 sao)
3. Boutique Hotel Hội An - Hội An - 1,200,000 VND/đêm (4 sao)
4. Beach Resort Nha Trang - Nha Trang - 2,200,000 VND/đêm (5 sao)
5. City Hotel Hà Nội - Hà Nội - 1,500,000 VND/đêm (4 sao)
6. Luxury Hotel Đà Nẵng - Đà Nẵng - 2,800,000 VND/đêm (5 sao)
7. Eco Lodge Cần Thơ - Cần Thơ - 900,000 VND/đêm (3.5 sao)
8. Beachfront Hotel Phú Quốc - Phú Quốc - 3,200,000 VND/đêm (5 sao)

**Mỗi khách sạn có:**
- 4 loại phòng (Standard, Deluxe, Suite, Family)
- Tiện nghi đầy đủ
- Chính sách chi tiết
- Điểm tham quan gần đó
- Phương tiện công cộng

### 🚗 Xe Thuê (8 chiếc)
1. Toyota Camry - 4-5 chỗ - 800,000 VND/ngày - TP.HCM - **Ảnh thực từ Unsplash**
2. Honda CR-V - 5-7 chỗ - 1,200,000 VND/ngày - TP.HCM - **Ảnh thực từ Unsplash**
3. Mitsubishi Xpander - 5-7 chỗ - 950,000 VND/ngày - Hà Nội - **Ảnh thực từ Unsplash**
4. Toyota Vios - 4-5 chỗ - 600,000 VND/ngày - Đà Nẵng - **Ảnh thực từ Unsplash**
5. Hyundai i10 - 5 chỗ - 500,000 VND/ngày - Hà Nội - **Ảnh thực từ Unsplash** (rẻ nhất)
6. Mercedes C-Class - 5 chỗ - 2,000,000 VND/ngày - TP.HCM - **Ảnh thực từ Unsplash** (sang trọng)
7. Lexus RX - 5-7 chỗ - 2,500,000 VND/ngày - TP.HCM - **Ảnh thực từ Unsplash** (hạng sang)
8. Ford Transit - 16 chỗ - 1,500,000 VND/ngày - Hà Nội - **Ảnh thực từ Unsplash** (nhóm lớn)

### 🎟️ Voucher (4 mã)
1. **WELCOME** - 10% giảm, limit: 1000
2. **JRNBANMOI** - 50,000 VND cố định, min: 200,000 VND
3. **JURNI2025** - 20% giảm, min: 1,000,000 VND
4. **SUMMER50** - 15% giảm (hè)

## 🚀 Cách Chạy

### Cách 1: IDE (VS Code/IntelliJ)
1. Mở file `JurniBackendApplication.java`
2. Click "Run" (hoặc Ctrl+Shift+D)
3. App sẽ khởi động và tự động seed dữ liệu

### Cách 2: Terminal
```bash
cd d:\WebDoAn\J2EE\Jurni-backend

# Build project
mvn clean package -DskipTests=true

# Run JAR
java -jar target/Jurni-backend-0.0.1-SNAPSHOT.jar
```

### Cách 3: Maven
```bash
cd d:\WebDoAn\J2EE\Jurni-backend
mvn compile exec:java -Dexec.mainClass="com.nhom3.Jurni_backend.JurniBackendApplication"
```

### 📋 Log Ra
Khi app khởi động, bạn sẽ thấy:
```
🌱 Seeding Hotels...
✓ Seeded 9 hotels
🌱 Seeding Cars...
✓ Seeded 8 cars
🌱 Seeding Vouchers...
✓ Seeded 4 vouchers
🌱 Seeding Activities...
✓ Seeded 6 activities
✅ Data seeding completed!
```

## ⚠️ Lưu Ý
- DataLoader chỉ seed **lần đầu tiên** khi DB trống
- Nếu cần seed lại, xóa collection trong MongoDB hoặc reset DB
- Để kiểm tra: 
  - Activities: `GET http://localhost:5000/api/activities`
  - Hotels: `GET http://localhost:5000/api/hotels`
  - Cars: `GET http://localhost:5000/api/cars`
  - Vouchers: `GET http://localhost:5000/api/vouchers`

## 🔄 Seed Lại (Nếu Cần)
1. **MongoDB UI**: Xóa collections `hotels`, `cars`, `vouchers`, `activities`
2. **MongoDB CLI**:
```javascript
db.hotels.deleteMany({})
db.cars.deleteMany({})
db.vouchers.deleteMany({})
db.activities.deleteMany({})
```
3. Khởi động lại app → Dữ liệu sẽ được seed lại

---

✅ **Hoàn tất!** Dữ liệu hoạt động (activities) đã được thêm vào DataLoader!
