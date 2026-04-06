package com.nhom3.Jurni_backend.controller;

import com.nhom3.Jurni_backend.model.Car;
import com.nhom3.Jurni_backend.repository.CarRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final CarRepository carRepository;

    public CarController(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @GetMapping
    public ResponseEntity<List<Car>> listCars() {
        return ResponseEntity.ok(carRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCar(@PathVariable String id) {
        return carRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createCar(@RequestBody Car car) {
        // Validation
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

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCar(@PathVariable String id, @RequestBody Car body) {
        return carRepository.findById(id).map(car -> {
            if (body.getCompany() != null) car.setCompany(body.getCompany());
            if (body.getModel() != null) car.setModel(body.getModel());
            if (body.getType() != null) car.setType(body.getType());
            if (body.getPricePerDay() != null) car.setPricePerDay(body.getPricePerDay());
            if (body.getSeats() != null) car.setSeats(body.getSeats());
            if (body.getLocation() != null) car.setLocation(body.getLocation());
            if (body.getImageUrl() != null) car.setImageUrl(body.getImageUrl());
            if (body.getAvailable() != null) car.setAvailable(body.getAvailable());
            if (body.getDescription() != null) car.setDescription(body.getDescription());
            if (body.getSpecifications() != null) car.setSpecifications(body.getSpecifications());
            if (body.getAmenities() != null) car.setAmenities(body.getAmenities());
            return ResponseEntity.ok(carRepository.save(car));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCar(@PathVariable String id) {
        return carRepository.findById(id).map(car -> {
            carRepository.delete(car);
            return ResponseEntity.ok(Map.of("ok", true));
        }).orElse(ResponseEntity.notFound().build());
    }
}
