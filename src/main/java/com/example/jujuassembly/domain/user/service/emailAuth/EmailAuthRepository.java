package com.example.jujuassembly.domain.user.service.emailAuth;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailAuthRepository extends JpaRepository<EmailAuth,Long> {
    void deleteByCreatedAtBefore(LocalDateTime fiveMinAgo);

    Optional<EmailAuth> findTopByNicknameOrderByCreatedAtDesc(String nickname);

  Optional<EmailAuth> findByLoginId(String loginId);

  Optional<EmailAuth> findByNickname(String nickname);

  Optional<EmailAuth> findByEmail(String email);
}
