package se.ashwini.bucketproject;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

@SpringBootApplication
public class BucketprojectApplication implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        Dotenv dotenv = Dotenv.load();

        String bucketName =dotenv.get("BUCKET_NAME");
        String accessKey = dotenv.get("ACCESS_KEY");
        String secretKey = dotenv.get("SECRET_KEY");


                //Scanner scanner = new Scanner(System.in);

                // create S3 client with credentials and region
                S3Client s3Client = S3Client.builder()
                .credentialsProvider(new AwsCredentialsProvider() {
                    @Override
                    public AwsCredentials resolveCredentials() {
                        return AwsBasicCredentials.builder()
                                .accessKeyId(accessKey)
                                .secretAccessKey(secretKey).build();
                    }
                })
                .region(Region.EU_NORTH_1)
                .build();
        Scanner scanner = new Scanner(System.in);



//                GetItemRequest getItemRequest = GetItemRequest.builder()
//                        .tableName("Person")
//                        .key(Map.of("personNummer",AttributeValue.builder().s("19900111-2222").build()))
//                        .build();
//
//                GetItemResponse response = dynamoDbClient.getItem(getItemRequest);
//                Map<String,AttributeValue>returnedItem = response.item();
//                if(returnedItem.isEmpty()){
//                    system.out.println("No item found");
//
//                }else {
//                    String name = returnedItem.get("name").s();
//                    System.out.println("Name:"+name);
//                }
//                PutItemRequest putItemRequest= PutItemRequest.builder()
//                        .tableName("person")
//                        .item(Map.of("pernummer",AttributeValue.builder().s("19900111-2222").build()),
//                        "name",AttributeValue.builder().s("Ashwini").build(),
//                        "isHungry",AttributeValue.builder().s("YES!").build())
//        .build();
//        dynamoDbClient.putItem(putItemRequest);



                // List<String>filnamen= new S3Service().listAll();
               // s3Client s3Client = new S3Client();
               //s3Client.Connect(accessKey,secretKey);
               // List<String> filnamen = s3Client.ListFiles(bucketName);


                while (true) {
                    System.out.println("\n--------S3 Bucket Menu--------");
                    System.out.println("1.List all files");
                    System.out.println("2.Upload a file");
                    System.out.println("3.Download a file");
                    System.out.println("4.Exit");
                    System.out.println("Choose an option:");
                    int choice = Integer.parseInt(scanner.nextLine());
                    switch (choice) {
                        case 1:
                            System.out.println("List of files in the bucket:");
                            ListObjectsV2Request listReq = ListObjectsV2Request.builder()
                                    .bucket(bucketName)
                                    .build();
                            ListObjectsV2Response listRes = s3Client.listObjectsV2(listReq);
                            List<String> fileName = listRes.contents().stream()
                                .map(S3Object::key)
                                    .collect(Collectors.toList());
                            for (String name : fileName) {
                                System.out.println(name);
                            }
                           // fileName.forEach(System.out::println);
                            //lists alla filer som heter "AshwiniShirsat-bucketprojectapplication"
                            break;

                        case 2:
                            System.out.println("Which file do you want to upload?");
                            String filePath = scanner.nextLine();
                            File file = new File(filePath);
                            PutObjectRequest putReq = PutObjectRequest.builder()
                                    .bucket(bucketName)
                                    .key(file.getName())
                                    .build();
                            s3Client.putObject(putReq, file.toPath());
                            System.out.println("File uploaded successfully." + file.getName());
                            break;

                        case 3:
                            System.out.println(" Which file do you want to download?");
                            String fileNameToDownload = scanner.nextLine();
                            System.out.println("Enter the destination path:including filename");
                            String destinationPath = scanner.nextLine();
                            GetObjectRequest getReq = GetObjectRequest.builder()
                                    .bucket(bucketName)
                                    .key(fileNameToDownload)
                                    .build();
                            s3Client.getObject(getReq,Paths.get(destinationPath));
                            System.out.println("File downloaded successfully to " + destinationPath);
                            break;

                        case 4:
                            System.out.println("Exiting the application.");
                            s3Client.close();
                            scanner.close();
                            return;

                    }
                }
    }



public static void main(String[] args) {
        SpringApplication.run(BucketprojectApplication.class, args);
    }
}


