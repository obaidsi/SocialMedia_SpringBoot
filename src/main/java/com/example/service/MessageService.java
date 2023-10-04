package com.example.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.entity.Message;
import com.example.repository.MessageRepository;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

    public Message saveMessage(Message message){
        return messageRepository.save(message);
    }

    public List<Message> getAllMessages(){
        return messageRepository.findAll();
    }

    public Optional<Message> getMessageById(int message_id) {
        return messageRepository.findById(message_id);
    }

    public List<Message> getAllMessagesByUser(int account_id){
        return messageRepository.findByPostedBy(account_id);
    }

    public int deleteMessage(int message_id){
        if(messageRepository.existsById(message_id)){
            messageRepository.deleteById(message_id);
            return 1;
        }
        return 0;
    }

    public int updateMessage(int messageId, String newMessageText) {

        if (newMessageText == null || newMessageText.trim().isEmpty() || newMessageText.length() > 255) {
            // Return 0 to indicate an unsuccessful message update.
            return 0;
        }

        Optional<Message> existingMessageOptional = messageRepository.findById(messageId);
        if(existingMessageOptional.isPresent()){
            Message existingMessage = existingMessageOptional.get();
            existingMessage.setMessage_text(newMessageText);
            messageRepository.save(existingMessage);

        // Return 1 for successful message update
            return 1;
        }
        // Return 0 to indicate an unsuccessful message update.
        return 0;
    }
}
