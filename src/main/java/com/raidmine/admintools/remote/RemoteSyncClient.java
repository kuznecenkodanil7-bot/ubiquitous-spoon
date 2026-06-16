package com.raidmine.admintools.remote;

import com.raidmine.admintools.RaidMineAdminTools;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class RemoteSyncClient {
    private final HttpClient client = HttpClient.newHttpClient();

    public void syncAsync() {
        String url = RaidMineAdminTools.getInstance().getConfigManager().get().getRemoteSyncUrl();
        if (url == null || url.isBlank() || !RaidMineAdminTools.getInstance().getConfigManager().get().isAutoUpdateEnabled()) return;

        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(8))
                    .GET()
                    .build();
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() >= 200 && response.statusCode() < 300) {
                            RaidMineAdminTools.getInstance().getDatabaseManager().syncFromRemote(response.body());
                        }
                    })
                    .exceptionally(error -> {
                        RaidMineAdminTools.LOGGER.warn("Remote staff sync failed: {}", error.getMessage());
                        return null;
                    });
        } catch (IllegalArgumentException e) {
            RaidMineAdminTools.LOGGER.warn("Invalid remote sync URL: {}", url);
        }
    }
}
