@Service
@RequiredArgsConstructor
public class AzureBlobDownloader {

    private final BlobServiceClient client;

    @Value("${azure.storage.container}")
    private String containerName;

    @Value("${azure.storage.input-folder}")
    private String inputFolder;

    @Value("${local.directory}")
    private String localDirectory;

    public List<File> downloadFiles() {

        BlobContainerClient container =
                client.getBlobContainerClient(containerName);

        List<File> downloadedFiles = new ArrayList<>();

        container.listBlobsByHierarchy(
                inputFolder + "/")

                .forEach(blob -> {

                    String blobName =
                            blob.getName();

                    String fileName =
                            Paths.get(blobName)
                                    .getFileName()
                                    .toString();

                    File localFile =
                            new File(
                                    localDirectory,
                                    fileName);

                    container.getBlobClient(blobName)
                            .downloadToFile(
                                    localFile.getAbsolutePath(),
                                    true);

                    downloadedFiles.add(localFile);
                });

        return downloadedFiles;
    }
}
