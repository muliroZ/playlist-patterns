package com.playlist.adapter;

import com.playlist.adapter.external.LegacyVinylCatalog;
import com.playlist.core.Track;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter que converte os registros do {@link LegacyVinylCatalog} para
 * {@link Track}.
 */
public class VinylCatalogAdapter implements TrackCatalog {

  private final LegacyVinylCatalog legacyCatalog;

  /**
   * Cria o adapter em cima do sistema legado.
   *
   * @param legacyCatalog catálogo legado a ser adaptado. Não pode ser nulo.
   * @throws IllegalArgumentException se o catálogo for nulo.
   */
  public VinylCatalogAdapter(LegacyVinylCatalog legacyCatalog) {
    if (legacyCatalog == null) {
      throw new IllegalArgumentException();
    }

    this.legacyCatalog = legacyCatalog;
  }

  @Override
  public List<Track> findAll() {
    String[] records = legacyCatalog.fetchAllRecords();
    if (records == null) {
      return List.of();
    }
    return parseRows(records);
  }

  @Override
  public Optional<Track> findById(String id) {
    if (id == null || id.isBlank()) {
      return Optional.empty();
    }
    String record = legacyCatalog.findRecordByCatalogNumber(id);
    if (record == null) {
      return Optional.empty();
    }
    List<Track> tracks = parseRows(new String[] { record });
    return tracks.isEmpty() ? Optional.empty() : Optional.of(tracks.get(0));
  }

  private List<Track> parseRows(String[] rows) {
    List<Track> tracks = new ArrayList<>();
    for (String row : rows) {
      if (row == null) {
        continue;
      }

      String[] splitted = row.split("\\|", -1);
      if (splitted.length != 5) {
        continue;
      }

      String id = splitted[0].trim();
      String rawTitle = splitted[1].trim();
      String rawArtist = splitted[2].trim();
      String rawDuration = splitted[3].trim();

      if (id.isEmpty() || rawTitle.isEmpty()) {
        continue;
      }

      int duration;
      try {
        long durationMs = Long.parseLong(rawDuration);
        if (durationMs < 0) {
          continue;
        }
        duration = (int) (durationMs / 1000);
      } catch (NumberFormatException e) {
        continue;
      }

      String[] artistParts = rawArtist.split(",", 2);
      if (artistParts.length != 2) {
        continue;
      }

      String lastName = capitalize(artistParts[0].trim());
      String firstName = capitalize(artistParts[1].trim());
      String formattedArtist = firstName + " " + lastName;

      String formattedTitle = capitalize(rawTitle);
      boolean isPremium = splitted[4].trim().equalsIgnoreCase("Y");

      tracks.add(new Track(id, formattedTitle, formattedArtist, duration, isPremium));
    }

    return tracks;
  }

  private String capitalize(String word) {
    if (word == null || word.isEmpty()) {
      return "";
    }
    return Arrays.stream(word.split("\\s+"))
        .filter(w -> !w.isEmpty())
        .map(w -> Character.toUpperCase(w.charAt(0)) + w.substring(1).toLowerCase())
        .collect(Collectors.joining(" "));
  }
}
