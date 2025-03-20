package org.example.ubersocketserver.controller;

import org.example.ubersocketserver.dto.RideRequestDto;
import org.example.ubersocketserver.dto.RideResponseDto;
import org.example.ubersocketserver.dto.UpdateBookingRequestDto;
import org.example.ubersocketserver.dto.UpdateBookingResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@RestController
@RequestMapping("/api/socket")
public class DriverRequestController {

    private final SimpMessagingTemplate messagingTemplate;
    private final RestTemplate restTemplate;

    public DriverRequestController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        restTemplate = new RestTemplate();
    }

    @PostMapping("/newride")
    public ResponseEntity<Boolean> raiseRideRequest(@RequestBody RideRequestDto rideRequestDto) {
        sendDriversNewRideRequest(rideRequestDto);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    public void sendDriversNewRideRequest(RideRequestDto rideRequestDto) {
        System.out.println("Sending periodic message");
        messagingTemplate.convertAndSend("/topic/rideRequest",rideRequestDto);
    }

    @MessageMapping("/rideResponse/{driverId}")
    public synchronized void rideResponseHandler(@DestinationVariable String driverId,RideResponseDto responseDto) {
        System.out.println("Rider Response : " + responseDto.getResponse() + "Driver Id : " + driverId);
        UpdateBookingRequestDto requestDto = UpdateBookingRequestDto.builder()
                .status("SCHEDULED")
                .driverId(Optional.of(Long.parseLong(driverId)))
                .build();
        ResponseEntity<UpdateBookingResponseDto> result = this.restTemplate.postForEntity("http://localhost:7478/api/v1/booking/" + responseDto.getBookingId() ,requestDto,UpdateBookingResponseDto.class);
        System.out.println(result.getBody());
        System.out.println(result.getStatusCode());
    }
}
