package com.nhom3.Jurni_backend.controller;

import com.nhom3.Jurni_backend.model.Flight;
import com.nhom3.Jurni_backend.repository.FlightRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;


@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightRepository flightRepository;

    public FlightController(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    @GetMapping
    public ResponseEntity<?> listFlights(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        List<Flight> flights;
        Instant now = Instant.now();
        if (from != null && to != null) {
            flights = flightRepository.findByDepartureCityAndArrivalCityAndDepartureTimeAfter(from, to, now);
        } else {
            flights = flightRepository.findByDepartureTimeAfterOrderByDepartureTimeAsc(now);
        }
        return ResponseEntity.ok(flights);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFlight(@PathVariable String id) {
        return flightRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createFlight(@RequestBody Map<String, Object> body) {
        try {
            String airline = (String) body.get("airline");
            String departureCity = (String) body.get("departure_city");
            String arrivalCity = (String) body.get("arrival_city");
            String departureTimeStr = (String) body.get("departure_time");
            String arrivalTimeStr = (String) body.get("arrival_time");

            if (airline == null || departureCity == null || arrivalCity == null
                    || departureTimeStr == null || arrivalTimeStr == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Thiếu thông tin bắt buộc"));
            }
            if (departureCity.equals(arrivalCity)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Thành phố đi và đến không được giống nhau"));
            }

            Instant depTime = Instant.parse(departureTimeStr);
            Instant arrTime = Instant.parse(arrivalTimeStr);
            if (!depTime.isBefore(arrTime)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Giờ đến phải sau giờ khởi hành"));
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> ticketOptions = (List<Map<String, Object>>) body.get("ticket_options");
            double price = parseDouble(body.get("price"), 0);
            int seats = parseInt(body.get("available_seats"), 180);
            String flightType = (String) body.getOrDefault("flight_type", "economy");

            if (ticketOptions != null && !ticketOptions.isEmpty()) {
                price = ticketOptions.stream()
                    .mapToDouble(opt -> parseDouble(opt.get("price"), 0)).min().orElse(price);
                seats = ticketOptions.stream()
                    .mapToInt(opt -> parseInt(opt.get("available_seats"), 0)).sum();
            }

            if (price <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Giá vé phải lớn hơn 0"));
            }

            Flight flight = new Flight();
            flight.setAirline(airline);
            flight.setFlightNumber(body.getOrDefault("flight_number", "FL" + System.currentTimeMillis()).toString());
            flight.setOrigin(departureCity);
            flight.setDestination(arrivalCity);
            flight.setDepartureCity(departureCity);
            flight.setArrivalCity(arrivalCity);
            flight.setDepartureTime(depTime);
            flight.setArrivalTime(arrTime);
            flight.setPrice(price);
            flight.setAvailableSeats(seats);
            flight.setFlightType(flightType);
            flight.setAircraft(body.getOrDefault("aircraft", "Airbus A320").toString());
            flight.setImageUrl((String) body.get("image_url"));
            flight.setTicketOptions(ticketOptions);

            return ResponseEntity.status(201).body(flightRepository.save(flight));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/bulk")
    public ResponseEntity<?> createBulkFlights(@RequestBody Map<String, Object> body) {
        try {
            String airline = (String) body.get("airline");
            String departureCity = (String) body.get("departure_city");
            String arrivalCity = (String) body.get("arrival_city");
            String departureTimeStr = (String) body.get("departure_time");
            double price = parseDouble(body.get("price"), 0);
            int count = parseInt(body.get("count"), 1);
            int intervalHours = parseInt(body.get("interval_hours"), 2);
            int intervalMinutes = parseInt(body.get("interval_minutes"), 10);
            int durationHours = parseInt(body.get("flight_duration_hours"), 2);
            int durationMinutes = parseInt(body.get("flight_duration_minutes"), 30);

            if (airline == null || departureCity == null || arrivalCity == null || departureTimeStr == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Thiếu thông tin bắt buộc"));
            }
            if (departureCity.equals(arrivalCity)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Thành phố đi và đến không được giống nhau"));
            }
            if (count < 1 || count > 50) {
                return ResponseEntity.badRequest().body(Map.of("error", "Số lượng vé phải từ 1 đến 50"));
            }

            Instant baseDepTime = Instant.parse(departureTimeStr);
            long intervalMs = ((long) intervalHours * 60 + intervalMinutes) * 60 * 1000;
            long durationMs = ((long) durationHours * 60 + durationMinutes) * 60 * 1000;
            String prefix = (String) body.get("flight_number_prefix");
            int seats = parseInt(body.get("available_seats"), 180);

            List<Flight> flights = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                Instant dep = baseDepTime.plusMillis(i * intervalMs);
                Instant arr = dep.plusMillis(durationMs);
                Flight f = new Flight();
                f.setAirline(airline);
                f.setFlightNumber(prefix != null ? prefix + String.format("%03d", i + 1) : "FL" + System.currentTimeMillis() + "-" + i);
                f.setOrigin(departureCity);
                f.setDestination(arrivalCity);
                f.setDepartureCity(departureCity);
                f.setArrivalCity(arrivalCity);
                f.setDepartureTime(dep);
                f.setArrivalTime(arr);
                f.setPrice(price);
                f.setAvailableSeats(seats);
                f.setFlightType(body.getOrDefault("flight_type", "economy").toString());
                f.setAircraft(body.getOrDefault("aircraft", "Airbus A320").toString());
                f.setImageUrl((String) body.get("image_url"));
                flights.add(f);
            }

            List<Flight> saved = flightRepository.saveAll(flights);
            return ResponseEntity.status(201).body(Map.of(
                "message", "Đã tạo thành công " + saved.size() + " chuyến bay",
                "flights", saved
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFlight(@PathVariable String id, @RequestBody Map<String, Object> body) {
        return flightRepository.findById(id).map(flight -> {
            if (body.containsKey("airline")) flight.setAirline((String) body.get("airline"));
            if (body.containsKey("departure_city")) {
                flight.setDepartureCity((String) body.get("departure_city"));
                flight.setOrigin((String) body.get("departure_city"));
            }
            if (body.containsKey("arrival_city")) {
                flight.setArrivalCity((String) body.get("arrival_city"));
                flight.setDestination((String) body.get("arrival_city"));
            }
            if (body.containsKey("departure_time"))
                flight.setDepartureTime(Instant.parse((String) body.get("departure_time")));
            if (body.containsKey("arrival_time"))
                flight.setArrivalTime(Instant.parse((String) body.get("arrival_time")));
            if (body.containsKey("price")) flight.setPrice(parseDouble(body.get("price"), flight.getPrice()));
            if (body.containsKey("image_url")) flight.setImageUrl((String) body.get("image_url"));
            if (body.containsKey("flight_type")) flight.setFlightType((String) body.get("flight_type"));
            if (body.containsKey("aircraft")) flight.setAircraft((String) body.get("aircraft"));
            if (body.containsKey("available_seats"))
                flight.setAvailableSeats(parseInt(body.get("available_seats"), flight.getAvailableSeats()));
            if (body.containsKey("ticket_options")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> opts = (List<Map<String, Object>>) body.get("ticket_options");
                flight.setTicketOptions(opts);
                if (opts != null && !opts.isEmpty()) {
                    flight.setPrice(opts.stream().mapToDouble(o -> parseDouble(o.get("price"), 0)).min().orElse(flight.getPrice()));
                    flight.setAvailableSeats(opts.stream().mapToInt(o -> parseInt(o.get("available_seats"), 0)).sum());
                }
            }
            return ResponseEntity.ok(flightRepository.save(flight));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFlight(@PathVariable String id) {
        return flightRepository.findById(id).map(f -> {
            flightRepository.delete(f);
            return ResponseEntity.ok(Map.of("ok", true, "message", "Xóa chuyến bay thành công"));
        }).orElse(ResponseEntity.notFound().build());
    }

    private double parseDouble(Object val, double def) {
        if (val == null) return def;
        try { return Double.parseDouble(val.toString()); } catch (Exception e) { return def; }
    }

    private int parseInt(Object val, int def) {
        if (val == null) return def;
        try { return (int) Double.parseDouble(val.toString()); } catch (Exception e) { return def; }
    }
}
