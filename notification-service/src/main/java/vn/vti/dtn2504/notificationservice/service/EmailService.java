package vn.vti.dtn2504.notificationservice.service;

import vn.vti.dtn2504.notificationservice.payload.EmailDetails;

public interface EmailService {
    // Method
    // To send a simple email
    void sendSimpleMail(EmailDetails details);
}
