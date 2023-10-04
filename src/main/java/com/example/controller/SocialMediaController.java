package com.example.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.service.AccountService;
import com.example.service.MessageService;
import com.example.entity.Account;
import com.example.entity.Message;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
@RestController
public class SocialMediaController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private MessageService messageService;

    @PostMapping("/register")
    public ResponseEntity<Object> registerAccount(@RequestBody Account account) {
        // Check if the username is not blank
        if (account.getUsername().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username cannot be blank.");
        }

        // Check if the password is at least 4 characters long
        if (account.getPassword().length() < 4) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password must be at least 4 characters long.");
        }

        // Check if an account with the same username already exists
        if (accountService.doesUsernameExist(account.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists.");
        }

        // Registration successful, save the account to the database
        Account savedAccount = accountService.save(account);
        
        // Return the saved account with the generated account_id
        return ResponseEntity.ok(savedAccount);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody Account account){

        Optional<Account> accOptional  = accountService.doesAccountExist(account.getUsername(), account.getPassword());
        // Check if account eixt, if so return account 
        if(accOptional.isPresent()){
            return ResponseEntity.ok(accOptional.get());
        }
        // If account does not exist unauthorized login
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Account does not exist");
    }

    @PostMapping("/messages")
    public ResponseEntity<Object> createMessage(@RequestBody Message message){

        String message_text = message.getMessage_text();
        // Check message for empty message and message length
        if(message_text.isEmpty() || message_text.length()>255){
            return ResponseEntity.badRequest().body("Mssage cannot be empty or longer than 255 character");
        }
        // Check weather message belongs to an active user
        if(!(accountService.doesAccountExistById(message.getPosted_by()).isPresent())){
            return ResponseEntity.badRequest().body("Message does not belong to active user");
        }
        // Message is valid, post message and return message
        Message savedMessage = messageService.saveMessage(message);
        return ResponseEntity.ok(savedMessage);
    }

    @GetMapping("/messages")
    public List<Message> getAllMessages(){
        List<Message> messages = messageService.getAllMessages();
        return messages;
    }

    @GetMapping("/messages/{message_id}")
    public ResponseEntity<Message> getMessage(@PathVariable("message_id") int messageId) {
        Optional<Message> message = messageService.getMessageById(messageId);
        // If message exist response wiht message, else 200 ok with empty response
        if (message.isPresent()) {
            return ResponseEntity.ok(message.get());
        } else {
            return ResponseEntity.ok().build();
        }
    }

    @GetMapping("/accounts/{account_id}/messages")
    public List<Message> getAllMessagesFromUser(@PathVariable("account_id") int account_id){
        List<Message> messages = messageService.getAllMessagesByUser(account_id);
        return messages;
    }

    @DeleteMapping("/messages/{message_id}")
    public ResponseEntity<?> deleteMessageById(@PathVariable("message_id") int messageId) {
      
        int rowsUpdated = messageService.deleteMessage(messageId);
        // If message exist rowsUpdate=1, else rowsUpdate=0
        if(rowsUpdated > 0){
            return ResponseEntity.ok(rowsUpdated);
        }
        return ResponseEntity.ok(rowsUpdated);  
    }

    @PatchMapping("/messages/{message_id}")
    public ResponseEntity<?> updateMessage(@PathVariable("message_id") int messageId, 
                                           @RequestBody Map<String, String> requestBody) {

        String newMessageText = requestBody.get("message_text");
        int rowsUpdated = messageService.updateMessage(messageId, newMessageText);
        if(rowsUpdated == 1){
            return ResponseEntity.ok(rowsUpdated);
        }
        // update is not successful
        return ResponseEntity.badRequest().build();
    }
}
