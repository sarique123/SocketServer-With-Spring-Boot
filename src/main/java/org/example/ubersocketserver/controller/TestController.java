package org.example.ubersocketserver.controller;

import org.example.ubersocketserver.dto.ChatRequest;
import org.example.ubersocketserver.dto.ChatResponse;
import org.example.ubersocketserver.dto.TestRequest;
import org.example.ubersocketserver.dto.TestResponse;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class TestController {

    private final SimpMessagingTemplate messagingTemplate;

    public TestController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/ping")
    @SendTo("/topic/ping")
    public TestResponse pingCheck(TestRequest request) {
        System.out.println("Received message from client : " + request.getData());
        return new TestResponse("PONG : Received");
    }

//    @Scheduled(fixedDelay = 2000)
//    public void sendPeriodicMessage(){
//        System.out.println("Sending periodic message");
//        messagingTemplate.convertAndSend("/topic/scheduled","Periodic message sent " + System.currentTimeMillis());
//    }

    @MessageMapping("/chat/{room}")
    @SendTo("/topic/message/{room}")
    public ChatResponse chatMessage(@DestinationVariable String room, ChatRequest request){
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        return new ChatResponse(request.getName(), request.getMessage(), time);
    }

    @MessageMapping("/privateChat/{room}/{userId}")
//    @SendTo("/topic/privateMessage/{room}/{userId}")
    public void privateChatMessage(@DestinationVariable String room,@DestinationVariable String userId, ChatRequest request) {
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        ChatResponse response = new ChatResponse(request.getName(), request.getMessage(), time);
        messagingTemplate.convertAndSendToUser(userId,"/queue/privateChat/" + room, response);
    }
}
