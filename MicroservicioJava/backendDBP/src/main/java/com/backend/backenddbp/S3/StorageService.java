package com.backend.backenddbp.S3;


import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.databasemigrationservice.model.S3AccessDeniedException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Service
public class StorageService {

    @Value("${AWS_BUCKET_NAME}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3Client;

    private File convercionMultipartFile(MultipartFile file) throws Exception {
        File fileConverted = new File(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            FileOutputStream fos = new FileOutputStream(fileConverted);
            fos.write(file.getBytes());
            fos.close();
        } catch (IOException e) {
            throw new IOException("Error al convertir el archivo", e);
        }
        return fileConverted;
    }

    private void Validacion(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Archivo vacio");
        }
        if (file.getSize() > 500L*1024*1024) {
            throw new IllegalArgumentException("El archivo excedio el tamaño permitido de 500MB");
        }
    }

    private void ValidacionKey(String objectKey) throws Exception {
        if (objectKey.isEmpty()) {
            throw new IllegalArgumentException("El key esta vacio");
        }
        if (objectKey.endsWith("/") || objectKey.startsWith("/") || objectKey.startsWith("\\") ||
                objectKey.endsWith("\\") || !objectKey.substring(objectKey.lastIndexOf(
                        "/") + 1).contains(".")
        ) {
            throw new IllegalArgumentException("Formato invalido");
        }
    }

    public String subiralS3File(MultipartFile file, String objectKey) throws Exception {
        Validacion(file);
        ValidacionKey(objectKey);

        File fileObj = convercionMultipartFile(file);
        try {
            deleteFile(objectKey);

            Map<String, String> userMetadata = new HashMap<>();
            userMetadata.put("x-amz-meta-object-key", objectKey);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setUserMetadata(userMetadata);

            s3Client.putObject(new PutObjectRequest(bucketName, objectKey, fileObj).withMetadata(metadata));
            return objectKey;

        } catch (AmazonS3Exception e) {
            throw new S3AccessDeniedException("\nNo se pudo subir el archivo " + objectKey + " " + e.getErrorCode() + e.getMessage());
        } finally {
            fileObj.delete();
        }
    }

    public String obtenerURL(String objectKey) {

        boolean fileExist = s3Client.doesObjectExist(bucketName, objectKey);
        if (!fileExist) return "";

        Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24*360);

        try {
            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(bucketName, objectKey)
                            .withExpiration(expiration);

            String url = s3Client.generatePresignedUrl(generatePresignedUrlRequest).toString();
            if (url.isEmpty())
                return "";
            return url;
        } catch (SdkClientException e) {
            throw new SdkClientException("\nNo se puedo generar el url" + e.getMessage());
        }
    }

    public void deleteFile(String objectKey) {
        try {
            boolean fileExist = s3Client.doesObjectExist(bucketName, objectKey);
            if (!fileExist)
                return;

            s3Client.deleteObject(bucketName, objectKey);
        } catch (AmazonServiceException e) {
            throw new AmazonServiceException("\nNo se pudo eliminar el archivo" + e.getMessage());
        }
    }


}