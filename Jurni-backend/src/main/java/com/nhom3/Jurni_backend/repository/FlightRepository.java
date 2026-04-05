package com.nhom3.Jurni_backend.repository;

import com.nhom3.Jurni_backend.model.Flight;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.Instant;
import java.util.List;

public interface FlightRepository extends MongoRepository<Flight, String> {
    List<Flight> findByDepartureCityAndArrivalCityAndDepartureTimeAfter(
        String departureCity, String arrivalCity, Instant after);
    List<Flight> findByDepartureTimeAfterOrderByDepartureTimeAsc(Instant after);
}
