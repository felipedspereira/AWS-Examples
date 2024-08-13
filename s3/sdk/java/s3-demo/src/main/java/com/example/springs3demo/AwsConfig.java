package com.example.springs3demo;

import org.springframework.context.annotation.*;
import software.amazon.awssdk.services.s3.*;

@Configuration
public class AwsConfig {

  @Bean
  public S3Client s3Client() {
    return S3Client.builder().build();
  }
}
