package com.playlist.proxy;

import com.playlist.core.Track;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * RealSubject do padrão Proxy: baixa o áudio de um servidor remoto.

 * Este arquivo não deve ser alterado. Criar uma instância
 * representa abrir conexão com o servidor de mídia, e cada chamada a
 * {@link #readBytes()} representa uma transferência pela rede. Os
 * contadores existem para que o avaliador consiga provar que o seu proxy
 * realmente adia e reaproveita esse trabalho.
 */
public class RemoteAudioStream implements AudioStream {

  private static final AtomicInteger INSTANCES = new AtomicInteger();

  private final Track track;
  private int fetchCount;

  /**
   * Abre a conexão remota para a faixa informada.
   *
   * @param track faixa a ser transmitida.
   */
  public RemoteAudioStream(Track track) {
    if (track == null) {
      throw new IllegalArgumentException("track não pode ser nula");
    }
    this.track = track;
    INSTANCES.incrementAndGet();
  }

  /**
   * Quantidade de conexões remotas abertas desde o último reset.
   *
   * @return o total de instâncias criadas.
   */
  public static int getInstancesCreated() {
    return INSTANCES.get();
  }

  /**
   * Zera o contador de conexões abertas. Usado pelos testes.
   */
  public static void resetInstanceCounter() {
    INSTANCES.set(0);
  }

  @Override
  public String getTrackId() {
    return track.id();
  }

  @Override
  public byte[] readBytes() {
    fetchCount++;
    String payload = "AUDIO::" + track.id() + "::" + track.durationSeconds();
    return payload.getBytes(StandardCharsets.UTF_8);
  }

  /**
   * Quantas vezes o áudio foi efetivamente baixado por esta instância.
   *
   * @return o número de transferências realizadas.
   */
  public int getFetchCount() {
    return fetchCount;
  }
}
