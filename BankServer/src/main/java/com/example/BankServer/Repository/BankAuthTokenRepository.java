package com.example.BankServer.Repository;

import com.example.BankServer.Modules.BankAuthToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankAuthTokenRepository extends JpaRepository<BankAuthToken, Long> {

    Optional<BankAuthToken> findByToken(String token);
}