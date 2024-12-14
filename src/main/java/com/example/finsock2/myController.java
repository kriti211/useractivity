package com.example.finsock2;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class myController {
    @Autowired
    UserRepository userRepository;

    public static String currentId;


        //Login API
        @GetMapping("/login")
        public String showLoginForm() {
            return "login";
        }

        @PostMapping("/login")
        public String login(@RequestParam String username, @RequestParam String password, Model model) {
            // Handle the login request
            if (isValidUser(username, password)) {
                return "redirect:/loggedIn";
            } else {
                model.addAttribute("error", "Invalid username or password");
                return "login";
            }
        }
        private boolean isValidUser(String username, String password) {
            // Check if the username and password are valid
            return username.equals("Kaushal") && password.equals("password");
        }

        //to create a user
    @GetMapping("/loggedIn")
    public String createUser(HttpServletRequest request){


        String userAgent = request.getHeader("User-Agent");
        String browserName = "Unknown";
        String osName = "Unknown";

        if (userAgent != null) {
            if (userAgent.contains("MSIE")) {
                browserName = "Internet Explorer";
            } else if (userAgent.contains("Firefox")) {
                browserName = "Mozilla Firefox";
            } else if (userAgent.contains("Chrome")) {
                browserName = "Google Chrome";
            } else if (userAgent.contains("Safari")) {
                browserName = "Apple Safari";
            } else if (userAgent.contains("Opera")) {
                browserName = "Opera";
            }

            Pattern pattern = Pattern.compile("\\((.*?)\\)");
            Matcher matcher = pattern.matcher(userAgent);
            while (matcher.find()) {
                String info = matcher.group(1);
                if (info.contains("Windows")) {
                    osName = "Windows";
                } else if (info.contains("Mac OS X")) {
                    osName = "Mac OS X";
                } else if (info.contains("Linux")) {
                    osName = "Linux";
                }
            }
        }

        WebClient ipClient = WebClient.create("https://api.ipify.org");
        String ip = ipClient.get()

                .retrieve().bodyToMono(String.class).block();


        WebClient client = WebClient
                .builder()
                .baseUrl("http://ip-api.com/json/" + ip)
                .build();
        UserActivity response = client.get()
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(UserActivity.class)
                .block();
        LocalTime loginTime = LocalTime.now();

        UserActivity userActivity = new UserActivity();
        userActivity.setBrowserName(browserName);
        userActivity.setCity(response.getCity());
        userActivity.setCountry(response.getCountry());
        userActivity.setLoginTime(loginTime);
        userActivity.setId(UUID.randomUUID().toString());
        userActivity.setIsp(response.getIsp());
        userActivity.setOsName(osName);
        currentId = userActivity.getId();


        userRepository.save(userActivity);

        return "redirect:/all";

    }

    //To get the list of time log
    @GetMapping("/all")
    public String all(Model model)
    {
        List<UserActivity> userActivityList = userRepository.findAll();
        UserActivity userActivity = userActivityList.get(0);
        model.addAttribute("userActivity",userActivity);
        model.addAttribute("userActivityList",userActivityList);
        return "userDashboard";
    }

    @PostMapping("/logout")
    public String logout(Model model) {
        // Handle the logout request
        UserActivity userActivity = userRepository.findById(currentId).get();
        userActivity.setLogoutTime(LocalTime.now());
        Duration duration = Duration.between(userActivity.getLoginTime(),userActivity.getLogoutTime());
        userActivity.setSessionTime(duration.toString());
        userRepository.save(userActivity);
        return "redirect:/login";

    }
}
