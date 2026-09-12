package com.SecurityApp.services;

import com.SecurityApp.entities.Session;
import com.SecurityApp.entities.User;
import com.SecurityApp.respositories.SessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SessionService {
    private final SessionRepository sessionRepository;
    private final int SESSION_LIMIT = 2;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public void generateNewSession(User user, String refreshToken) {
        List<Session> userSession = sessionRepository.findByUser(user);
        
        // If session limit reached or exceeded, remove the least recently used session (LRU)
        if (userSession.size() >= SESSION_LIMIT) {
            userSession.sort(Comparator.comparing(Session::getLastUsedAt, Comparator.nullsFirst(Comparator.naturalOrder())));
            Session leastRecentlyUsedSession = userSession.get(0);

            sessionRepository.delete(leastRecentlyUsedSession);
        }
      //if new create new session

        Session newSession = Session.builder()
                .user(user)
                .refreshToken(refreshToken)
                .lastUsedAt(LocalDateTime.now())
                .build();


        sessionRepository.save(newSession);
    }
//    validate session: if the session according to this refresh token  inside my database have or not
    public  void validateSession(String  refreshToken){
        Session session=sessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(()->new RuntimeException("Session not found for this refresh token :" + refreshToken));

       session.setLastUsedAt(LocalDateTime.now());
       sessionRepository.save(session);
    }
    //last time used it

    public void logout(String refreshToken) {
        Session session = sessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        sessionRepository.delete(session);
    }
}
