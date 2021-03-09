package com.jsoft.magenta.files;

import lombok.*;

import javax.persistence.*;

@Data
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public abstract class MagentaFile
{
    @Id
    @SequenceGenerator(
            name = "file_sequence",
            sequenceName = "file_sequence",
            initialValue = 100
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "file_sequence"
    )
    @Column(name = "file_id", updatable = false)
    protected Long id;

    @Column(name = "file_type", nullable = false, updatable = false)
    protected String type;

    @Column(name = "file_name", nullable = false)
    protected String name;
}
