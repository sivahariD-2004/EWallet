package com.example.BankServer.Service;



import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class OtpMailService {

    private final JavaMailSender mailSender;

    public OtpMailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtp(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Your Bank OTP");
        message.setText(
                "Your OTP for bank transaction is: " + otp +
                        "\n\nThis OTP is valid for 2 minutes.\n\nDo not share it with anyone."
        );

        mailSender.send(message);
    }
}
