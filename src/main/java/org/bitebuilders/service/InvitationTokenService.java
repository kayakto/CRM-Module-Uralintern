package org.bitebuilders.service;

import org.bitebuilders.enums.UserRole;
import org.bitebuilders.model.InvitationToken;
import org.bitebuilders.repository.InvitationTokenRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class InvitationTokenService {

    private final InvitationTokenRepository tokenRepository;

    public InvitationTokenService(InvitationTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    // Генерация уникальной ссылки TODO запрос только от админа на регу рук
    public String generateToken(UserRole role) {
        InvitationToken token = new InvitationToken(
                UUID.randomUUID().toString(),
                role,
                new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000) // Срок действия 1 день
        );
        tokenRepository.save(token);

        return token.getToken();
    }

    // Проверка токена
    public Optional<InvitationToken> validateToken(String token) {
        Optional<InvitationToken> invitationToken = tokenRepository.findByToken(token);

        if (invitationToken.isPresent()) {
            InvitationToken tokenEntity = invitationToken.get();

            // Проверяем, не истек ли срок действия
            if (tokenEntity.getExpirationDate().before(new Date()) || tokenEntity.isUsed()) {
                return Optional.empty();
            }

            return Optional.of(tokenEntity);
        }

        return Optional.empty();
    }

    // Отметить токен как использованный
    public void markTokenAsUsed(InvitationToken token) {
        token.setUsed(true);
        tokenRepository.save(token);
    }
}
