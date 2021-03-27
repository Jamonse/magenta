package com.jsoft.magenta.security.model;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "refresh_tokens")
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

  @Id
  @Column(updatable = false)
  private String token;

  @Column(name = "expires_at", nullable = false, updatable = false)
  private LocalDateTime expiresAt;

  @Column(name = "email", nullable = false, updatable = false)
  private String email;
}
