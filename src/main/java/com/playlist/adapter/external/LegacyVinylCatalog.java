package com.playlist.adapter.external;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Sistema externo legado de catálogo de vinis.

 * Este arquivo simula uma dependência de terceiros: você não pode alterá-lo.
 * Ele devolve registros em texto puro, separados por pipe, com a duração em milissegundos e o
 artista no formato
 * "SOBRENOME, NOME" em caixa alta.

 * O layout de cada registro é:
 * {NUMERO_CATALOGO|TITULO|SOBRENOME, NOME|DURACAO_MS|PREMIUM}.
 */
public class LegacyVinylCatalog {

  private static final String DEFAULT_RESOURCE = "/legacy/vinyl-catalog.psv";

  private final List<String> rows;

  /**
   * Cria o catálogo lendo o arquivo de dados embarcado no projeto.
   */
  public LegacyVinylCatalog() {
    this.rows = readResource(DEFAULT_RESOURCE);
  }

  /**
   * Cria o catálogo a partir de uma lista de registros já em memória.
   *
   * @param rows registros no formato legado.
   */
  public LegacyVinylCatalog(List<String> rows) {
    this.rows = List.copyOf(rows);
  }

  /**
   * Devolve todos os registros do catálogo legado.
   *
   * @return um array com os registros crus, no formato do sistema legado.
   */
  public String[] fetchAllRecords() {
    return rows.toArray(new String[0]);
  }

  /**
   * Busca um único registro pelo número de catálogo.
   *
   * @param catalogNumber número de catálogo (ex.: {@code "VNL-0001"}).
   * @return o registro cru correspondente ou {@code null} se não existir.
   */
  public String findRecordByCatalogNumber(String catalogNumber) {
    if (catalogNumber == null) {
      return null;
    }
    String wanted = catalogNumber.trim();
    for (String row : rows) {
      int separator = row.indexOf('|');
      String number = separator < 0 ? row : row.substring(0, separator);
      if (number.trim().equals(wanted)) {
        return row;
      }
    }
    return null;
  }

  private static List<String> readResource(String resource) {
    try (InputStream stream = LegacyVinylCatalog.class.getResourceAsStream(resource)) {
      if (stream == null) {
        throw new IllegalStateException("Arquivo do catálogo legado não encontrado: " + resource);
      }
      BufferedReader reader =
              new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
      List<String> lines = new ArrayList<>();
      String line = reader.readLine();
      while (line != null) {
        String trimmed = line.trim();
        if (!trimmed.isEmpty() && !trimmed.startsWith("#")) {
          lines.add(line);
        }
        line = reader.readLine();
      }
      return List.copyOf(lines);
    } catch (IOException exception) {
      throw new UncheckedIOException(exception);
    }
  }
}
