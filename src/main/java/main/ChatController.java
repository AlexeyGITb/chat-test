package main;

import main.model.Message;
import main.model.User;
import main.repos.UserRepository;
import main.repos.MessageRepository;
import main.response.AddMessageResponse;
import main.response.AuthResponse;
import main.response.AllMessagesResponse;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;


@RestController
public class ChatController {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @GetMapping(path = "/api/auth")
    public AuthResponse auth(){
        AuthResponse response = new AuthResponse();
        String sessionId = getSessionId();
        User user = userRepository.getBySessionId(sessionId);
        if(user != null){
            response.setResult(true);
            response.setName(user.getName());
        } else {
            response.setResult(false);
        }
        return response;
    }
    @PostMapping(path = "/api/users")
    public HashMap<String,Boolean>addUser(HttpServletRequest request){
        String name = request.getParameter("name");
        String sessionId = getSessionId();
        User user = new User();
        user.setName(name);
        user.setRegTime(new Date());
        user.setSessionId(sessionId);
        userRepository.save(user);
        HashMap<String,Boolean> response = new HashMap<>();
        response.put("result", true);
        return response;
    }

    @PostMapping(path = "/api/messages")
    public AddMessageResponse addMessage(HttpServletRequest request){
        AddMessageResponse response = new AddMessageResponse();
        String message = request.getParameter("message");
        String sessionId = getSessionId();
        Message m = new Message();
        User u = userRepository.getBySessionId(sessionId);
        m.setText(message);
        m.setPostTime(new Date());
        m.setUser(u);
        messageRepository.save(m);
        response.setResult(true);
        response.setTime(formatter.format(new Date()));
        return response;
    }

    @GetMapping(path = "/api/users")
    public List<String> getUsers(HttpServletRequest request){
        Iterable<User> users = userRepository.findAll();
        LinkedList<String> userNameList = new LinkedList<>();
        for (User u: users) {
            userNameList.add(u.getName());
        }
        return userNameList;
    }

    @GetMapping(path = "/api/messages")
    public List<AllMessagesResponse> getMessages(HttpServletRequest request){
        Iterable<Message> messages = messageRepository.findAll();
        LinkedList<AllMessagesResponse> messagesList = new LinkedList<>();
        for (Message m: messages) {
            AllMessagesResponse messageDTO = new AllMessagesResponse();
            messageDTO.setName(m.getUser().getName());
            messageDTO.setTime(formatter.format(m.getPostTime()));
            messageDTO.setText(m.getText());
            messagesList.add(messageDTO);
        }
        return messagesList;
    }
    private  String getSessionId(){
       return RequestContextHolder.currentRequestAttributes().getSessionId();
    }
}
