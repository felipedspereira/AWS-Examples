package com.example.springs3demo;

import jakarta.annotation.*;
import lombok.*;
import org.springframework.stereotype.*;
import software.amazon.awssdk.core.sync.*;
import software.amazon.awssdk.services.s3.*;
import software.amazon.awssdk.services.s3.model.*;

@Service
@RequiredArgsConstructor
public class Handler {
  private final S3Client s3Client;

  @PostConstruct
  public void sendRequest() throws InterruptedException {
    String bucket = "bucket" + System.currentTimeMillis();
    String key = "key";

    createBucket(s3Client, bucket);

    System.out.println("Uploading object...");

    s3Client.putObject(
        PutObjectRequest.builder().bucket(bucket).key(key).build(),
        RequestBody.fromString("Testing with the {sdk-java}"));

    System.out.println("Upload complete");
    System.out.printf("%n");

    System.out.println("will delete bucket within 10s..");

    Thread.sleep(10000);

    cleanUp(s3Client, bucket, key);

    System.out.println("Closing the connection to {S3}");
    s3Client.close();
    System.out.println("Connection closed");
    System.out.println("Exiting...");
  }

  public static void createBucket(S3Client s3Client, String bucketName) {
    try {
      CreateBucketRequest bucket = CreateBucketRequest.builder().bucket(bucketName).build();
      s3Client.createBucket(bucket);

      System.out.println("Creating bucket: " + bucketName);

      s3Client
          .waiter()
          .waitUntilBucketExists(HeadBucketRequest.builder().bucket(bucketName).build());
      System.out.println(bucketName + " is ready.");
      System.out.printf("%n");
    } catch (S3Exception e) {
      System.err.println(e.awsErrorDetails().errorMessage());
      System.exit(1);
    }
  }

  public static void cleanUp(S3Client s3Client, String bucketName, String keyName) {
    System.out.println("Cleaning up...");
    try {
      System.out.println("Deleting object: " + keyName);
      DeleteObjectRequest deleteObjectRequest =
          DeleteObjectRequest.builder().bucket(bucketName).key(keyName).build();
      s3Client.deleteObject(deleteObjectRequest);
      System.out.println(keyName + " has been deleted.");
      System.out.println("Deleting bucket: " + bucketName);
      DeleteBucketRequest deleteBucketRequest =
          DeleteBucketRequest.builder().bucket(bucketName).build();
      s3Client.deleteBucket(deleteBucketRequest);
      System.out.println(bucketName + " has been deleted.");
      System.out.printf("%n");
    } catch (S3Exception e) {
      System.err.println(e.awsErrorDetails().errorMessage());
      System.exit(1);
    }
    System.out.println("Cleanup complete");
    System.out.printf("%n");
  }
}
