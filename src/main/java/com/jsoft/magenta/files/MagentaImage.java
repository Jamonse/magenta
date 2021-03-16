package com.jsoft.magenta.files;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "images")
@NoArgsConstructor
public class MagentaImage extends MagentaFile {
    @Column(name = "image_type", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private MagentaImageType imageType;

    @Column(name = "bytes", nullable = false, length = 1000)
    private byte[] bytes;

    public MagentaImage(Long id, String type, String name, MagentaImageType imageType, byte[] bytes) {
        super(id, type, name);
        this.imageType = imageType;
        this.bytes = bytes;
    }
}
