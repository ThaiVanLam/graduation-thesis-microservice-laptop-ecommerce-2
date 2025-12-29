package vn.vti.dtn2504.notificationservice.payload;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SendNotificationRequest {
    private String recipient;
    private String msgBody;
    private String subject;
}
