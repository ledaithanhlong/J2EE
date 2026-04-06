package com.nhom3.Jurni_backend.config;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nhom3.Jurni_backend.model.Activity;
import com.nhom3.Jurni_backend.model.Car;
import com.nhom3.Jurni_backend.model.Hotel;
import com.nhom3.Jurni_backend.model.Voucher;
import com.nhom3.Jurni_backend.repository.ActivityRepository;
import com.nhom3.Jurni_backend.repository.CarRepository;
import com.nhom3.Jurni_backend.repository.HotelRepository;
import com.nhom3.Jurni_backend.repository.VoucherRepository;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class DataLoader {

    @Value("${app.data.seed-on-startup:false}")
    private boolean seedOnStartup;

    @Bean
    CommandLineRunner loadData(
            HotelRepository hotelRepo,
            CarRepository carRepo,
            VoucherRepository voucherRepo,
            ActivityRepository activityRepo) {
        return args -> {
            // Check if seeding is enabled
            if (!seedOnStartup) {
                System.out.println("⏭️  Auto-seeding is disabled (app.data.seed-on-startup=false)");
                return;
            }
            
            // Only seed if database is empty
            if (hotelRepo.count() == 0) {
                System.out.println("🌱 Seeding Hotels...");
                seedHotels(hotelRepo);
            }
            if (carRepo.count() == 0) {
                System.out.println("🌱 Seeding Cars...");
                seedCars(carRepo);
            }
            if (voucherRepo.count() == 0) {
                System.out.println("🌱 Seeding Vouchers...");
                seedVouchers(voucherRepo);
            }
            if (activityRepo.count() == 0) {
                System.out.println("🌱 Seeding Activities...");
                seedActivities(activityRepo);
            }
            System.out.println("✅ Data seeding completed!\n");
        };
    }

    private void seedHotels(HotelRepository repo) {
        List<Hotel> hotels = new ArrayList<>();

        hotels.add(createHotel(
                "Khách Sạn Grand Saigon",
                "Thành phố Hồ Chí Minh",
                "Quận 1, 57-59 Đường Đông Du, TP.HCM",
                2500000.0,
                5.0,
                "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800",
                Arrays.asList(
                    "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800",
                    "https://images.unsplash.com/photo-1571896349842-33c89424de2d?w=800"
                ),
                "Khách sạn 5 sao sang trọng tại trung tâm TP.HCM với dịch vụ đẳng cấp quốc tế",
                Arrays.asList(
                    new Hotel.RoomType("standard", 40, 2500000.0, 2, new ArrayList<>()),
                    new Hotel.RoomType("deluxe", 50, 3200000.0, 2, new ArrayList<>()),
                    new Hotel.RoomType("suite", 30, 4800000.0, 4, new ArrayList<>()),
                    new Hotel.RoomType("family", 30, 5200000.0, 6, new ArrayList<>())
                ),
                Arrays.asList("WiFi miễn phí", "Bể bơi", "Spa & Massage", "Nhà hàng 24/7", "Fitness center", "Valet parking"),
                Map.ofEntries(
                    Map.entry("cancel", "Miễn phí hủy trước 48 giờ"),
                    Map.entry("children", "Trẻ em dưới 12 tuổi ở miễn phí"),
                    Map.entry("pets", "Không cho phép thú cưng"),
                    Map.entry("smoking", "Không hút thuốc")
                ),
                Arrays.asList("Nhà thờ Đức Bà - 2 km", "Bến Nhà Rồng - 1.5 km"),
                Arrays.asList("Trạm xe buýt Nguyễn Huệ - 500 m", "Sân bay Tân Sơn Nhất - 7 km")
        ));

        hotels.add(createHotel(
                "Resort Đà Lạt Premium",
                "Tỉnh Lâm Đồng",
                "Đường Hùng Vương, TP. Đà Lạt",
                1800000.0,
                4.5,
                "https://images.unsplash.com/photo-1571896349842-33c89424de2d?w=800",
                Arrays.asList("https://images.unsplash.com/photo-1571896349842-33c89424de2d?w=800"),
                "Resort nghỉ dưỡng cao cấp với view núi rừng tuyệt đẹp",
                Arrays.asList(
                    new Hotel.RoomType("standard", 25, 1800000.0, 2, new ArrayList<>()),
                    new Hotel.RoomType("deluxe", 30, 2300000.0, 2, new ArrayList<>()),
                    new Hotel.RoomType("suite", 15, 3500000.0, 4, new ArrayList<>()),
                    new Hotel.RoomType("family", 10, 4200000.0, 6, new ArrayList<>())
                ),
                Arrays.asList("WiFi miễn phí", "Bể bơi", "Spa", "Nhà hàng", "Golf"),
                Map.ofEntries(
                    Map.entry("cancel", "Miễn phí hủy trước 72 giờ"),
                    Map.entry("children", "Trẻ em dưới 10 tuổi ở miễn phí"),
                    Map.entry("pets", "Cho phép thú cưng (200K/đêm)"),
                    Map.entry("smoking", "Có khu vực hút thuốc")
                ),
                Arrays.asList("Chợ Đà Lạt - 1 km", "Hồ Xuân Hương - 1.5 km"),
                Arrays.asList("Trạm xe Đà Lạt - 2 km")
        ));

        hotels.add(createHotel(
                "Boutique Hotel Hội An",
                "Tỉnh Quảng Nam",
                "Phố cổ Hội An",
                1200000.0,
                4.0,
                "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?w=800",
                Arrays.asList("https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?w=800"),
                "Boutique hotel nhỏ xinh với kiến trúc cổ điển gần phố cổ",
                Arrays.asList(
                    new Hotel.RoomType("standard", 10, 1200000.0, 2, new ArrayList<>()),
                    new Hotel.RoomType("deluxe", 10, 1600000.0, 2, new ArrayList<>()),
                    new Hotel.RoomType("suite", 5, 2300000.0, 4, new ArrayList<>())
                ),
                Arrays.asList("WiFi miễn phí", "Nhà hàng", "Xe đạp miễn phí"),
                Map.ofEntries(
                    Map.entry("cancel", "Miễn phí hủy trước 24 giờ"),
                    Map.entry("children", "Trẻ em dưới 6 tuổi ở miễn phí"),
                    Map.entry("pets", "Không cho phép"),
                    Map.entry("smoking", "Không hút thuốc")
                ),
                Arrays.asList("Phố cổ Hội An - 50m", "Chùa Cầu - 100m"),
                Arrays.asList("Bến xe Hội An - 1 km")
        ));

        hotels.add(createHotel(
                "Beach Resort Nha Trang",
                "Tỉnh Khánh Hòa",
                "22 Trần Phú, Nha Trang",
                2200000.0,
                5.0,
                "https://images.unsplash.com/photo-1551882547-ff40c63fe5fa?w=800",
                Arrays.asList("https://images.unsplash.com/photo-1551882547-ff40c63fe5fa?w=800"),
                "Resort bãi biển 5 sao với bãi biển riêng và view biển tuyệt đẹp",
                Arrays.asList(
                    new Hotel.RoomType("standard", 60, 2200000.0, 2, new ArrayList<>()),
                    new Hotel.RoomType("deluxe", 80, 2800000.0, 2, new ArrayList<>()),
                    new Hotel.RoomType("suite", 40, 4500000.0, 4, new ArrayList<>()),
                    new Hotel.RoomType("family", 20, 5000000.0, 6, new ArrayList<>())
                ),
                Arrays.asList("WiFi miễn phí", "Bể bơi vô cực", "Bãi biển riêng", "Spa", "Dive center"),
                Map.ofEntries(
                    Map.entry("cancel", "Miễn phí hủy trước 48 giờ"),
                    Map.entry("children", "Trẻ em dưới 12 tuổi ở miễn phí"),
                    Map.entry("pets", "Không cho phép"),
                    Map.entry("smoking", "Có khu hút thuốc")
                ),
                Arrays.asList("Biển Nha Trang", "Tháp Bà Ponagar - 3 km"),
                Arrays.asList("Sân bay Cam Ranh - 40 km")
        ));

        hotels.add(createHotel(
                "City Hotel Hà Nội",
                "Thành phố Hà Nội",
                "Phố Hoàn Kiếm",
                1500000.0,
                4.0,
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=800",
                Arrays.asList("https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=800"),
                "Khách sạn 4 sao hiện đại tại trung tâm Hà Nội",
                Arrays.asList(
                    new Hotel.RoomType("standard", 35, 1500000.0, 2, new ArrayList<>()),
                    new Hotel.RoomType("deluxe", 40, 1900000.0, 2, new ArrayList<>()),
                    new Hotel.RoomType("suite", 15, 3000000.0, 4, new ArrayList<>()),
                    new Hotel.RoomType("family", 10, 3500000.0, 6, new ArrayList<>())
                ),
                Arrays.asList("WiFi miễn phí", "Nhà hàng", "Fitness center", "Parking"),
                Map.ofEntries(
                    Map.entry("cancel", "Miễn phí hủy trước 24 giờ"),
                    Map.entry("children", "Trẻ em dưới 10 tuổi ở miễn phí"),
                    Map.entry("pets", "Không cho phép"),
                    Map.entry("smoking", "Không hút thuốc")
                ),
                Arrays.asList("Hồ Hoàn Kiếm - 100m", "Nhà thờ Lớn - 500m"),
                Arrays.asList("Sân bay Nội Bài - 25 km")
        ));

        hotels.add(createHotel(
                "Luxury Hotel Đà Nẵng",
                "Thành phố Đà Nẵng",
                "Sơn Trà, Đà Nẵng",
                2800000.0,
                5.0,
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=800",
                Arrays.asList("https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=800"),
                "Khách sạn 5 sao sang trọng với view biển Mỹ Khê",
                Arrays.asList(
                    new Hotel.RoomType("standard", 50, 2800000.0, 2, new ArrayList<>()),
                    new Hotel.RoomType("deluxe", 60, 3600000.0, 2, new ArrayList<>()),
                    new Hotel.RoomType("suite", 40, 5200000.0, 4, new ArrayList<>()),
                    new Hotel.RoomType("family", 30, 6000000.0, 6, new ArrayList<>())
                ),
                Arrays.asList("WiFi miễn phí", "Bể bơi vô cực", "Spa cao cấp", "Bar & Lounge"),
                Map.ofEntries(
                    Map.entry("cancel", "Miễn phí hủy trước 72 giờ"),
                    Map.entry("children", "Trẻ em dưới 12 tuổi ở miễn phí"),
                    Map.entry("pets", "Không cho phép"),
                    Map.entry("smoking", "Không hút thuốc")
                ),
                Arrays.asList("Bãi biển Mỹ Khê", "Cầu Quay Sơn Trà - 1 km"),
                Arrays.asList("Sân bay Đà Nẵng - 3 km")
        ));

        hotels.add(createHotel(
                "Eco Lodge Cần Thơ",
                "Thành phố Cần Thơ",
                "Quận Ninh Kiều",
                900000.0,
                3.5,
                "https://images.unsplash.com/photo-1551882547-ff40c63fe5fa?w=800",
                Arrays.asList("https://images.unsplash.com/photo-1551882547-ff40c63fe5fa?w=800"),
                "Lodge sinh thái gần sông Hậu với trải nghiệm văn hóa miền Tây",
                Arrays.asList(
                    new Hotel.RoomType("standard", 15, 900000.0, 2, new ArrayList<>()),
                    new Hotel.RoomType("deluxe", 10, 1300000.0, 2, new ArrayList<>()),
                    new Hotel.RoomType("family", 5, 1800000.0, 4, new ArrayList<>())
                ),
                Arrays.asList("WiFi miễn phí", "Tour sông nước", "Nhà hàng", "Xe đạp"),
                Map.ofEntries(
                    Map.entry("cancel", "Miễn phí hủy trước 24 giờ"),
                    Map.entry("children", "Trẻ em ở miễn phí"),
                    Map.entry("pets", "Cho phép"),
                    Map.entry("smoking", "Có khu vực")
                ),
                Arrays.asList("Chợ nổi Cái Răng - 5 km"),
                Arrays.asList("Sân bay Cần Thơ - 10 km")
        ));

        hotels.add(createHotel(
                "Beachfront Hotel Phú Quốc",
                "Tỉnh Kiên Giang",
                "Phú Quốc",
                3200000.0,
                5.0,
                "https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=800",
                Arrays.asList("https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=800"),
                "Resort bãi biển 5 sao với villa riêng và bãi biển tuyệt đẹp",
                Arrays.asList(
                    new Hotel.RoomType("standard", 30, 3200000.0, 2, new ArrayList<>()),
                    new Hotel.RoomType("deluxe", 40, 4200000.0, 2, new ArrayList<>()),
                    new Hotel.RoomType("suite", 25, 5500000.0, 4, new ArrayList<>()),
                    new Hotel.RoomType("family", 25, 6500000.0, 6, new ArrayList<>())
                ),
                Arrays.asList("WiFi miễn phí", "Bể bơi", "Bãi biển riêng", "Spa", "Dive center"),
                Map.ofEntries(
                    Map.entry("cancel", "Miễn phí hủy trước 72 giờ"),
                    Map.entry("children", "Trẻ em dưới 12 tuổi ở miễn phí"),
                    Map.entry("pets", "Không cho phép"),
                    Map.entry("smoking", "Không hút thuốc")
                ),
                Arrays.asList("Bãi Dài Phú Quốc", "Hòn Sao - 2 km"),
                Arrays.asList("Sân bay Phú Quốc - 15 km")
        ));

        repo.saveAll(hotels);
        System.out.println("✓ Seeded " + hotels.size() + " hotels");
    }

    private void seedCars(CarRepository repo) {
        List<Car> cars = new ArrayList<>();

        cars.add(createCar("Toyota", "Camry", "Sedan 4-5 chỗ", 5, 800000.0, "TP.HCM", "https://images.unsplash.com/photo-1621007947382-bb3c3994e3fb?w=800"));
        cars.add(createCar("Honda", "CR-V", "SUV 5-7 chỗ", 7, 1200000.0, "TP.HCM", "https://images.unsplash.com/photo-1619405399517-d4620f4a0ad7?w=800"));
        cars.add(createCar("Mitsubishi", "Xpander", "Minivan 5-7 chỗ", 7, 950000.0, "Hà Nội", "https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=800"));
        cars.add(createCar("Toyota", "Vios", "Sedan 4-5 chỗ", 4, 600000.0, "Đà Nẵng", "https://images.unsplash.com/photo-1552820728-8ac41f1ce891?w=800"));
        cars.add(createCar("Hyundai", "i10", "Hatchback 5 chỗ", 5, 500000.0, "Hà Nội", "https://images.unsplash.com/photo-1570202390220-c7fb3993a612?w=800"));
        cars.add(createCar("Mercedes", "C-Class", "Sedan hạng sang", 5, 2000000.0, "TP.HCM", "https://images.unsplash.com/photo-1618405959076-d1df44f5efbb?w=800"));
        cars.add(createCar("Lexus", "RX", "SUV hạng sang", 7, 2500000.0, "TP.HCM", "https://images.unsplash.com/photo-1606611013016-969c19d27081?w=800"));
        cars.add(createCar("Ford", "Transit", "Van 16 chỗ", 16, 1500000.0, "Hà Nội", "https://images.unsplash.com/photo-1578829185093-d9dcc4e6ecc5?w=800"));

        repo.saveAll(cars);
        System.out.println("✓ Seeded " + cars.size() + " cars");
    }

    private void seedVouchers(VoucherRepository repo) {
        List<Voucher> vouchers = new ArrayList<>();

        vouchers.add(createVoucher("WELCOME", 10, null, 0.0, "2026-12-31T23:59:59Z", 1000));
        vouchers.add(createVoucher("JRNBANMOI", null, 50000.0, 200000.0, "2026-12-31T23:59:59Z", 500));
        vouchers.add(createVoucher("JURNI2025", 20, null, 1000000.0, "2026-12-31T23:59:59Z", 100));
        vouchers.add(createVoucher("SUMMER50", 15, null, 500000.0, "2026-06-30T23:59:59Z", 300));

        repo.saveAll(vouchers);
        System.out.println("✓ Seeded " + vouchers.size() + " vouchers");
    }

    private void seedActivities(ActivityRepository repo) {
        List<Activity> activities = new ArrayList<>();

        activities.add(createActivity(
                "Tour Tham Quan Phố Cổ Hà Nội",
                "Hà Nội",
                350000.0,
                "4 giờ",
                "Khám phá 36 phố phường cổ kính, thưởng thức ẩm thực đường phố và tìm hiểu văn hóa lịch sử Hà Nội",
                "https://images.unsplash.com/photo-1523059623039-a9ed027e7fad?w=800",
                "Văn hóa & Lịch sử",
                Arrays.asList("Hướng dẫn viên chuyên nghiệp", "Bảo hiểm du lịch", "Nước uống"),
                "Nhà hát lớn Hà Nội, 1 Tràng Tiền, Hoàn Kiếm",
                Map.ofEntries(
                    Map.entry("cancel", "Miễn phí hủy trước 48 giờ. Hủy trong vòng 48 giờ: phí 30% giá trị đơn hàng."),
                    Map.entry("change", "Có thể đổi ngày tham gia, vui lòng liên hệ trước ít nhất 24 giờ."),
                    Map.entry("weather", "Tour vẫn diễn ra trong điều kiện thời tiết nhẹ. Hủy miễn phí nếu thời tiết cực đoan."),
                    Map.entry("children", "Trẻ em dưới 5 tuổi miễn phí. Trẻ em 5-12 tuổi giảm 50%.")
                )
        ));

        activities.add(createActivity(
                "Tham Quan Vịnh Hạ Long",
                "Quảng Ninh",
                1200000.0,
                "1 ngày",
                "Trải nghiệm vẻ đẹp kỳ vĩ của Di sản Thế giới UNESCO, tham quan hang động và tắm biển",
                "https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=800",
                "Thiên nhiên & Du lịch",
                Arrays.asList("Tàu tham quan", "Bữa trưa trên tàu", "Hướng dẫn viên", "Bảo hiểm"),
                "Bến tàu Tuần Châu, Hạ Long",
                Map.ofEntries(
                    Map.entry("cancel", "Miễn phí hủy trước 72 giờ. Hủy trong vòng 72 giờ: phí 50% giá trị đơn hàng."),
                    Map.entry("change", "Có thể đổi ngày, vui lòng liên hệ trước 48 giờ."),
                    Map.entry("weather", "Tour có thể bị hủy do thời tiết, hoàn tiền 100% nếu hủy."),
                    Map.entry("children", "Trẻ em dưới 5 tuổi miễn phí. Trẻ em 5-10 tuổi giảm 30%.")
                )
        ));

        activities.add(createActivity(
                "Công Viên Nước Vinpearl",
                "Nha Trang",
                650000.0,
                "Cả ngày",
                "Vui chơi tại công viên nước lớn nhất Việt Nam với hơn 20 trò chơi cảm giác mạnh",
                "https://images.unsplash.com/photo-1544551763-46a013bb70d5?w=800",
                "Giải trí & Vui chơi",
                Arrays.asList("Vé vào cửa", "Sử dụng tất cả trò chơi", "Áo phao", "Két đồ"),
                "Công viên Vinpearl, Đảo Hòn Tre, Nha Trang",
                Map.ofEntries(
                    Map.entry("cancel", "Miễn phí hủy trước 24 giờ. Hủy trong vòng 24 giờ: không hoàn tiền."),
                    Map.entry("change", "Có thể đổi ngày, vui lòng liên hệ trước 12 giờ."),
                    Map.entry("weather", "Tour vẫn diễn ra trong mưa nhẹ. Hủy miễn phí nếu mưa to."),
                    Map.entry("children", "Trẻ em dưới 1m miễn phí. Trẻ em 1m-1.4m giảm 30%.")
                )
        ));

        activities.add(createActivity(
                "Show Diễn Nhạc Nước",
                "Đà Nẵng",
                200000.0,
                "1 giờ",
                "Xem show diễn nhạc nước đầy màu sắc tại Cầu Rồng, một trong những điểm đến nổi tiếng nhất Đà Nẵng",
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800",
                "Giải trí & Vui chơi",
                Arrays.asList("Vé xem show", "Ghế ngồi VIP"),
                "Cầu Rồng, Đà Nẵng",
                Map.ofEntries(
                    Map.entry("cancel", "Miễn phí hủy trước 2 giờ. Hủy trong vòng 2 giờ: không hoàn tiền."),
                    Map.entry("change", "Có thể đổi giờ xem, vui lòng liên hệ trước 1 giờ."),
                    Map.entry("weather", "Show có thể bị hủy do thời tiết, hoàn tiền 100% nếu hủy."),
                    Map.entry("children", "Trẻ em dưới 3 tuổi miễn phí.")
                )
        ));

        activities.add(createActivity(
                "Tour Tham Quan Chùa Một Cột",
                "Hà Nội",
                250000.0,
                "2 giờ",
                "Tham quan biểu tượng văn hóa nổi tiếng của Hà Nội và tìm hiểu lịch sử Phật giáo Việt Nam",
                "https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=800",
                "Văn hóa & Lịch sử",
                Arrays.asList("Hướng dẫn viên", "Vé vào cửa", "Nước uống"),
                "Chùa Một Cột, Đội Cấn, Ba Đình, Hà Nội",
                Map.ofEntries(
                    Map.entry("cancel", "Miễn phí hủy trước 24 giờ."),
                    Map.entry("change", "Có thể đổi giờ, vui lòng liên hệ trước 12 giờ."),
                    Map.entry("weather", "Tour vẫn diễn ra trong mọi điều kiện thời tiết."),
                    Map.entry("children", "Trẻ em dưới 6 tuổi miễn phí.")
                )
        ));

        activities.add(createActivity(
                "Lặn Biển Ngắm San Hô",
                "Phú Quốc",
                850000.0,
                "Nửa ngày",
                "Trải nghiệm lặn biển ngắm san hô đầy màu sắc và các loài cá nhiệt đới",
                "https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=800",
                "Thể thao & Mạo hiểm",
                Arrays.asList("Thiết bị lặn", "Hướng dẫn viên chuyên nghiệp", "Bảo hiểm", "Bữa trưa"),
                "Bến tàu An Thới, Phú Quốc",
                Map.ofEntries(
                    Map.entry("cancel", "Miễn phí hủy trước 48 giờ. Hủy trong vòng 48 giờ: phí 40% giá trị đơn hàng."),
                    Map.entry("change", "Có thể đổi ngày, vui lòng liên hệ trước 24 giờ."),
                    Map.entry("weather", "Tour có thể bị hủy do thời tiết, hoàn tiền 100% nếu hủy."),
                    Map.entry("restricted", "Độ tuổi: tối thiểu 10 tuổi. Không phù hợp cho phụ nữ mang thai.")
                )
        ));

        repo.saveAll(activities);
        System.out.println("✓ Seeded " + activities.size() + " activities");
    }

    // Helper methods
    private Hotel createHotel(String name, String location, String address, Double price, Double rating,
            String imageUrl, List<String> images, String description,
            List<Hotel.RoomType> roomTypes, List<String> amenities,
            Map<String, Object> policies, List<String> attractions, List<String> transport) {
        Hotel hotel = new Hotel();
        hotel.setName(name);
        hotel.setLocation(location);
        hotel.setAddress(address);
        hotel.setPrice(price);
        hotel.setStarRating(rating);
        hotel.setImageUrl(imageUrl);
        hotel.setImages(images);
        hotel.setDescription(description);
        hotel.setRoomTypes(roomTypes);
        hotel.setAmenities(amenities);
        hotel.setPolicies(policies);
        hotel.setNearbyAttractions(attractions);
        hotel.setPublicTransport(transport);
        hotel.setCheckInTime("14:00");
        hotel.setCheckOutTime("12:00");
        hotel.setStatus("approved");
        hotel.setTotalRooms(roomTypes.stream().mapToInt(Hotel.RoomType::getQuantity).sum());
        hotel.setCreatedAt(Instant.now());
        hotel.setUpdatedAt(Instant.now());
        return hotel;
    }

    private Car createCar(String company, String model, String type, Integer seats, Double pricePerDay, String location, String imageUrl) {
        Car car = new Car();
        car.setCompany(company);
        car.setModel(model);
        car.setType(type);
        car.setSeats(seats);
        car.setPricePerDay(pricePerDay);
        car.setLocation(location);
        car.setImageUrl(imageUrl);
        car.setAvailable(true);
        car.setDescription("Xe " + type + " với " + seats + " chỗ ngồi");
        car.setCreatedAt(Instant.now());
        return car;
    }

    private Voucher createVoucher(String code, Integer discountPercent, Double discountAmount,
            Double minSpend, String expiryDate, Integer usageLimit) {
        Voucher voucher = new Voucher();
        voucher.setCode(code);
        voucher.setDiscountPercent(discountPercent);
        voucher.setDiscountAmount(discountAmount);
        voucher.setMinSpend(minSpend);
        voucher.setExpiryDate(Instant.parse(expiryDate));
        voucher.setUsageLimit(usageLimit);
        return voucher;
    }

    private Activity createActivity(String name, String location, Double price, String duration,
            String description, String imageUrl, String category, List<String> includes,
            String meetingPoint, Map<String, Object> policies) {
        Activity activity = new Activity();
        activity.setName(name);
        activity.setLocation(location);
        activity.setPrice(price);
        activity.setDuration(duration);
        activity.setDescription(description);
        activity.setImageUrl(imageUrl);
        activity.setCategory(category);
        activity.setIncludes(includes);
        activity.setHighlights(Arrays.asList()); // Empty by default
        activity.setMeetingPoint(meetingPoint);
        activity.setPolicies(policies);
        activity.setCreatedAt(Instant.now());
        activity.setUpdatedAt(Instant.now());
        return activity;
    }

    /**
     * Public method to seed all data
     * Called by: AdminDataController for manual seeding
     */
    public void seedAll(HotelRepository hotelRepo, CarRepository carRepo, 
                       ActivityRepository activityRepo, VoucherRepository voucherRepo) {
        System.out.println("\n🌱 Starting data seeding...\n");
        
        if (hotelRepo.count() == 0) {
            System.out.println("📦 Seeding Hotels...");
            seedHotels(hotelRepo);
        } else {
            System.out.println("⏭️ Skipping Hotels (already " + hotelRepo.count() + " records)");
        }

        if (carRepo.count() == 0) {
            System.out.println("📦 Seeding Cars...");
            seedCars(carRepo);
        } else {
            System.out.println("⏭️ Skipping Cars (already " + carRepo.count() + " records)");
        }

        if (activityRepo.count() == 0) {
            System.out.println("📦 Seeding Activities...");
            seedActivities(activityRepo);
        } else {
            System.out.println("⏭️ Skipping Activities (already " + activityRepo.count() + " records)");
        }

        if (voucherRepo.count() == 0) {
            System.out.println("📦 Seeding Vouchers...");
            seedVouchers(voucherRepo);
        } else {
            System.out.println("⏭️ Skipping Vouchers (already " + voucherRepo.count() + " records)");
        }

        System.out.println("\n✅ Data seeding completed!\n");
    }
}
