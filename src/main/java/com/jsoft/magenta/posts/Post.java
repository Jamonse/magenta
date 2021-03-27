package com.jsoft.magenta.posts;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.jsoft.magenta.util.validation.annotations.ValidContent;
import com.jsoft.magenta.util.validation.annotations.ValidTitle;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "posts")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Post {

  @Id
  @SequenceGenerator(
      name = "post_sequence",
      sequenceName = "post_sequence"
  )
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "post_sequence"
  )
  @Column(name = "post_id", updatable = false)
  private Long id;

  @Column(name = "title", length = 50, nullable = false)
  @ValidTitle
  private String title;

  @Column(name = "created_by", length = 50, nullable = false, updatable = false)
  private String createdBy;

  @Column(name = "content", nullable = false)
  @ValidContent
  private String content;

  @Column(name = "image")
  private String image;

  @Column(name = "created_at", nullable = false, updatable = false)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime createdAt;

  @Column(name = "last_updated")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime lastUpdated;
}
