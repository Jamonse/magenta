package com.jsoft.magenta.accounts.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.jsoft.magenta.contacts.Contact;
import com.jsoft.magenta.files.MagentaImage;
import com.jsoft.magenta.projects.domain.Project;
import com.jsoft.magenta.util.validation.annotations.ValidName;
import java.time.LocalDate;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(
    name = "accounts",
    uniqueConstraints = @UniqueConstraint(
        name = "account_name_unique",
        columnNames = "account_name")
)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Account {

  @Id
  @SequenceGenerator(
      name = "account_sequence",
      sequenceName = "account_sequence",
      initialValue = 100
  )
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "account_sequence"
  )
  @Column(name = "account_id", updatable = false)
  private Long id;

  @Column(name = "account_name", nullable = false, length = 50)
  @ValidName
  private String name;

  @Column(name = "created_at", nullable = false, updatable = false)
  @JsonDeserialize(using = LocalDateDeserializer.class)
  @JsonSerialize(using = LocalDateSerializer.class)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private LocalDate createdAt;

  @JoinColumn(
      name = "cover_image",
      foreignKey = @ForeignKey(name = "FK_cimg_account")
  )
  @ManyToOne
  @JsonProperty(access = Access.READ_ONLY)
  private MagentaImage coverImage;

  @JoinColumn(
      name = "logo_image",
      foreignKey = @ForeignKey(name = "FK_limg_account")
  )
  @ManyToOne
  @JsonProperty(access = Access.READ_ONLY)
  private MagentaImage logo;

  @JoinColumn(
      name = "thumbnail_image",
      foreignKey = @ForeignKey(name = "FK_pimg_account")
  )
  @ManyToOne
  @JsonProperty(access = Access.READ_ONLY)
  private MagentaImage profileImage;

  @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
  @JoinColumn(
      name = "account_id",
      referencedColumnName = "account_id",
      foreignKey = @ForeignKey(name = "FK_account_projects")
  )
  @JsonIgnore
  private Set<Project> projects;

  @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
  @JoinColumn(
      name = "account_id",
      referencedColumnName = "account_id",
      foreignKey = @ForeignKey(name = "FK_account_contacts")
  )
  private Set<Contact> contacts;

  @OneToMany(mappedBy = "account", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JsonIgnore
  private Set<AccountAssociation> associations;

  public Account(Long accountId) {
    this.id = accountId;
  }
}
