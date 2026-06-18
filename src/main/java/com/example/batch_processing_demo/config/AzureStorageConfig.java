@Configuration
public class AzureStorageConfig {

    @Value("${azure.storage.connection-string}")
    private String connectionString;

    @Bean
    public BlobServiceClient blobServiceClient() {

        return new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();
    }
}
