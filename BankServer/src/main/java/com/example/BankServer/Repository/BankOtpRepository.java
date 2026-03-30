package com.example.BankServer.Repository;

import com.example.BankServer.Modules.BankOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankOtpRepository extends JpaRepository<BankOtp, Long> {

    Optional<BankOtp> findByUserEmailAndOtpAndUsedFalse(String userEmail, String otp);
}