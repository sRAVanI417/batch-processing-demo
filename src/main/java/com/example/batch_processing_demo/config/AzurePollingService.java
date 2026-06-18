@Component
@RequiredArgsConstructor
public class AzurePollingService {

    private final AzureBlobDownloader downloader;

    @Scheduled(
            fixedDelay = 60000)
    public void poll() {

        List<File> files =
                downloader.downloadFiles();

        files.forEach(
                file ->
                        System.out.println(
                                "Downloaded: "
                                        + file.getName()));
    }
}
