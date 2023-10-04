package com.example.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Account;
import com.example.repository.AccountRepository;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    public Account save(Account account) {
      return accountRepository.save(account);
    }

    public boolean doesUsernameExist(String username) {
        return accountRepository.findByUsername(username).isPresent();
    }

    public Optional<Account> doesAccountExist(String username, String password){
        return accountRepository.findByUsernameAndPassword(username, password);
    }

    public Optional<Account> doesAccountExistById(int id){
        return accountRepository.findById(id);
    }
}
