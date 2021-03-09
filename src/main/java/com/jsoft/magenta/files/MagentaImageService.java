package com.jsoft.magenta.files;

import com.jsoft.magenta.exceptions.ImageProcessException;
import com.jsoft.magenta.exceptions.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MagentaImageService
{
    @Value("${application.image.size.cover-width}")
    private int coverImageSizeWidth;

    @Value("${application.image.size.cover-height}")
    private int coverImageSizeHeight;

    @Value("${application.image.size.profile-width}")
    private int profileImageSizeWidth;

    @Value("${application.image.size.profile-height}")
    private int profileImageHeight;

    @Value("${application.image.size.logo-width}")
    private int logoImageSizeWidth;

    @Value("${application.image.size.logo-height}")
    private int logoImageSizeHeight;

    @Value("${application.image.size.thumbnail-width}")
    private int thumbnailImageSizeWidth;

    @Value("${application.image.size.thumbnail-height}")
    private int thumbnailImageSizeHeight;

    private final MagentaImageRepository imageRepository;

    public MagentaImage uploadImage(String name, MultipartFile imageFile, MagentaImageType imageType)
    { // Process image
        MagentaImage magentaImage = processImage(name, imageFile, imageType);
        // Persist and return the saved image
        return this.imageRepository.save(magentaImage);
    }

    public MagentaImage updateImage(Long imageId, String name, MultipartFile imageFile, MagentaImageType imageType)
    {
        isExists(imageId, imageType);
        MagentaImage magentaImage = processImage(name, imageFile, imageType);
        magentaImage.setId(imageId);
        // Persist and return the saved image
        return this.imageRepository.save(magentaImage);
    }

    public void removeImage(Long imageId, MagentaImageType imageType)
    {
        isExists(imageId, imageType);
        this.imageRepository.deleteById(imageId);
    }

    private MagentaImage processImage(String name, MultipartFile imageFile, MagentaImageType imageType)
    { // Convert the image into File
        File file = new File(imageFile.getOriginalFilename());
        int imageWidth = 0;
        int imageHeight = 0;
        switch(imageType)
        { // Set size by requested image type
            case THUMBNAIL:
                imageWidth = thumbnailImageSizeWidth;
                imageHeight = thumbnailImageSizeHeight;
                break;
            case PROFILE:
                imageWidth = profileImageSizeWidth;
                imageHeight = profileImageHeight;
                break;
            case LOGO:
                imageWidth = logoImageSizeWidth;
                imageHeight = logoImageSizeHeight;
                break;
            case COVER:
                imageWidth = coverImageSizeWidth;
                imageHeight = coverImageSizeHeight;
        }

        try { // Format size by matching values
            Thumbnails.of(file).width(imageWidth).height(imageHeight).toFile(file);
        } catch (IOException e) {
            log.error(String.format("Failure during image processing of %s entity", name));
            throw new ImageProcessException(String.format("Failure during image processing of %s entity", name));
        }
        // Prepare custom image object
        MagentaImage magentaImage = new MagentaImage();
        magentaImage.setName(String.format("%s-%s",name , imageType)); // Set custom name
        String extension = imageFile.getOriginalFilename();
        magentaImage.setType(extension.substring(extension.lastIndexOf(".") + 1)); // Get image file type and set
        magentaImage.setImageType(imageType); // Set image type as requested

        try { // Init with file bytes
            byte[] bytes = Files.readAllBytes(file.toPath());
            magentaImage.setBytes(bytes);
        } catch (IOException e) {
            log.error(String.format("Failure during image processing of %s entity", name));
            throw new ImageProcessException(String.format("Failure during image processing of %s entity", name));
        }
        return magentaImage;
    }

    private void isExists(Long imageId, MagentaImageType imageType)
    {
        boolean exist = this.imageRepository.existsByIdAndImageType(imageId, imageType);
        if(!exist)
            throw new NoSuchElementException("Image not found");
    }
}
