package com.example.finsock2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalTime;
@Document(collection = "userActivity")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserActivity {
    @Id
    private String id;

    private String isp;

    private String city;

    private LocalTime loginTime;

    private LocalTime logoutTime;

    private String sessionTime;

    private String osName;

    private String browserName;

    private String country;


}
